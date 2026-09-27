package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.*;
import com.ecommerce.sb_ecom.enums.OrderStatus;
import com.ecommerce.sb_ecom.config.RazorpayConfig;
import com.ecommerce.sb_ecom.enums.PaymentMethod;
import com.ecommerce.sb_ecom.enums.PaymentStatus;
import com.ecommerce.sb_ecom.exceptions.*;
import com.ecommerce.sb_ecom.model.*;
import com.ecommerce.sb_ecom.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentVerificationService paymentVerificationService;
    private final RazorpayConfig razorpayConfig;

    @Transactional
    public OrderResponse placeOrder(PaymentMethod paymentMethod, OrderRequest orderRequest) {

        if (paymentMethod != PaymentMethod.UPI
                && paymentMethod != PaymentMethod.CARD
                && paymentMethod != PaymentMethod.CASH) {
            throw new InvalidPaymentMethodException("Invalid payment method: " + paymentMethod);
        }

        Cart cart = cartRepository.findById(orderRequest.getCartId())
                .orElseThrow(() -> new CartNotFoundException("cart with id " + orderRequest.getCartId() + " not found"));

        CustomerAddress customerAddress = customerAddressRepository
                .findByIdAndCustomerId(orderRequest.getCustomerAddressId(), cart.getCustomer().getId())
                .orElseThrow(() -> new CustomerAddressNotFoundException(
                        "customer address with id " + orderRequest.getCustomerAddressId() + " not found"));

        if (cart.getTotalItems() == null || cart.getTotalItems() == 0) {
            throw new CartEmptyException("Cart is empty, cannot place order");
        }

        List<CartItem> cartItemList = cart.getCartItems();

        // --- Lock and validate stock for every product BEFORE writing anything.
        // findByIdForUpdate takes a row lock held until this transaction commits,
        // so two concurrent orders for the same product's last unit cannot both pass
        // this check and both succeed (prevents overselling).
        for (CartItem cartItem : cartItemList) {
            Product lockedProduct = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "product with id " + cartItem.getProduct().getId() + " not found"));

            if (lockedProduct.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Only " + lockedProduct.getStockQuantity() + " unit(s) of \"" + lockedProduct.getName()
                                + "\" left in stock, but " + cartItem.getQuantity() + " were requested");
            }
        }

        // A reference id for Razorpay's "receipt" field, purely for human
        // cross-referencing - deliberately NOT the same value as Order.id.
        String internalReferenceId = UUID.randomUUID().toString();

        // Razorpay (like most gateways) takes amounts in the smallest currency
        // unit - paise for INR, hence the *100. Currency is hardcoded for now;
        // revisit if this project ever needs multi-currency support.
        long amountInPaise = Math.round(cart.getFinalPrice() * 100);
        String currency = "INR";

        // --- Payment is verified server-side; the client cannot dictate success. See
        // PaymentVerificationService for why this replaced trusting OrderRequest fields.
        // For CASH this resolves immediately as SUCCEEDED. For CARD/UPI, this only
        // creates a Razorpay order and returns a PENDING Payment - actual success/failure
        // is not known yet and arrives later via confirmPayment() and/or the webhook.
        Payment payment = paymentVerificationService.initiatePayment(paymentMethod, amountInPaise, currency, internalReferenceId);

        boolean paymentAlreadyConfirmed = paymentMethod == PaymentMethod.CASH;

        Customer customer = customerRepository.findById(cart.getCustomer().getId())
                .orElseThrow(() -> new CustomerNotFoundException("customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setPayment(payment);
        order.setTotalAmount(cart.getFinalPrice());
        order.setOrderStatus(paymentAlreadyConfirmed ? OrderStatus.PLACED : OrderStatus.PENDING_PAYMENT);
        // Was never set, so every order's orderDate came back null. Set once, here, at
        // creation - orders don't move to a different date when their payment resolves
        // later, so confirmPayment() deliberately does not touch this.
        order.setOrderDate(LocalDate.now());

        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setLine1(customerAddress.getLine1());
        orderAddress.setLine2(customerAddress.getLine2());
        orderAddress.setCity(customerAddress.getCity());
        orderAddress.setState(customerAddress.getState());
        orderAddress.setCountry(customerAddress.getCountry());
        orderAddress.setPinCode(customerAddress.getPinCode());
        order.setAddress(orderAddress);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setFinalPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemList.add(orderItemRepository.save(orderItem));

            // Stock was already validated (and locked) above, so decrementing here
            // is safe - but only do it now for payment methods that are already
            // confirmed (CASH). For CARD/UPI the payment isn't confirmed yet, so
            // decrementing now would remove stock for orders that may never
            // actually be paid for. That decrement happens later, when the
            // webhook/confirm step marks the order PLACED.
            if (paymentAlreadyConfirmed) {
                Product product = cartItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            }
        }

        savedOrder.getOrderItems().clear();
        savedOrder.getOrderItems().addAll(orderItemList);

        cart.setTotalItems(0);
        cart.setFinalPrice(0.0);
        cart.setTotalPrice(0.0);
        cart.setDiscountAmount(0.0);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("Order {} placed for customer {} - total {}", savedOrder.getId(), customer.getId(), savedOrder.getTotalAmount());

        OrderResponse response = new OrderResponse();
        response.setOrderId(savedOrder.getId());
        response.setOrderItems(orderItemList.stream().map(this::toItemResponse).toList());
        response.setOrderAddressId(savedOrder.getAddress().getId());
        response.setOrderTotal(savedOrder.getTotalAmount());
        response.setPaymentMethod(savedOrder.getPayment().getPaymentMethod().toString());
        response.setPaymentId(savedOrder.getPayment().getId());
        response.setOrderStatus(savedOrder.getOrderStatus().toString());

        // Only populated for CARD/UPI - frontend needs both to open Razorpay
        // Checkout. razorpayKeyId is Razorpay's PUBLIC key (safe to expose,
        // unlike keySecret) and is required by their JS widget.
        if (!paymentAlreadyConfirmed) {
            response.setRazorpayOrderId(savedOrder.getPayment().getPgOrderId());
            response.setRazorpayKeyId(razorpayConfig.getKeyId());
        }

        return response;
    }

    /**
     * Called after the frontend's Razorpay Checkout flow completes, with the
     * three values Razorpay hands back to it. This is one of two ways an
     * order can move out of PENDING_PAYMENT - the other being the webhook
     * (not yet built), which remains the reliable source of truth in case
     * this call never arrives.
     *
     * Idempotent: calling this more than once for an already-resolved order
     * is a safe no-op, since either this endpoint or the webhook may fire
     * first, and both may eventually fire for the same order.
     */
    @Transactional
    public OrderResponse confirmPayment(String orderId, PaymentConfirmationRequest confirmationRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("order with id " + orderId + " not found"));

        Payment payment = order.getPayment();

        // Already resolved (by this endpoint or the webhook) - nothing to do.
        // Prevents double stock decrement if both this call and the webhook
        // arrive for the same order.
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            log.info("Order {} already in status {}, ignoring duplicate confirmPayment call",
                    order.getId(), order.getOrderStatus());
            return buildOrderResponse(order);
        }

        // The razorpay_order_id the client is presenting MUST match the one
        // we generated for THIS order's Payment - otherwise a client could
        // replay a genuine signature from a different (e.g. cheaper) order
        // against this one.
        if (!confirmationRequest.getRazorpayOrderId().equals(payment.getPgOrderId())) {
            throw new PaymentVerificationException(
                    "razorpayOrderId does not match the payment initiated for order " + orderId);
        }

        boolean verified = paymentVerificationService.confirmPayment(
                confirmationRequest.getRazorpayOrderId(),
                confirmationRequest.getRazorpayPaymentId(),
                confirmationRequest.getRazorpaySignature());

        if (verified) {
            payment.setPgPaymentId(confirmationRequest.getRazorpayPaymentId());
            payment.setPaymentStatus(PaymentStatus.SUCCEEDED);
            payment.setPgStatus("captured");
            payment.setPgResponseMessage("Payment verified via signature check");
            order.setOrderStatus(OrderStatus.PLACED);

            // Deferred from placeOrder() for CARD/UPI - only decrement stock
            // now that payment is actually confirmed. Re-locks each product
            // row, same as placeOrder, since time has passed and other
            // orders may have moved stock in the meantime.
            for (OrderItem orderItem : order.getOrderItems()) {
                Product lockedProduct = productRepository.findByIdForUpdate(orderItem.getProduct().getId())
                        .orElseThrow(() -> new ProductNotFoundException(
                                "product with id " + orderItem.getProduct().getId() + " not found"));
                lockedProduct.setStockQuantity(lockedProduct.getStockQuantity() - orderItem.getQuantity());
            }

            log.info("Payment confirmed for order {}", order.getId());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setPgStatus("failed");
            payment.setPgResponseMessage("Signature verification failed");
            order.setOrderStatus(OrderStatus.PAYMENT_FAILED);

            log.warn("Payment signature verification failed for order {}", order.getId());
        }

        orderRepository.save(order);

        return buildOrderResponse(order);
    }

    // GET /api/users/customers/orders - the caller's own order history, newest first.
    // There was previously no way for a signed-in customer to list their past orders.
    public OrderPageResponse listOrdersForCustomer(String customerId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> page = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId, pageable);

        OrderPageResponse response = new OrderPageResponse();
        response.setContent(page.getContent().stream().map(this::toSummaryResponse).toList());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());
        return response;
    }

    // GET /api/users/customers/orders/{orderId} - one order's full detail. Ownership
    // (does this order belong to the caller?) is checked by the controller before this
    // is called, via CurrentUser.assertOwnsOrder.
    public OrderSummaryResponse getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("order with id " + orderId + " not found"));
        return toSummaryResponse(order);
    }

    private OrderSummaryResponse toSummaryResponse(Order order) {
        OrderAddress a = order.getAddress();
        OrderAddressResponse address = new OrderAddressResponse(
                a.getLine1(), a.getLine2(), a.getCity(), a.getState(), a.getCountry(), a.getPinCode());

        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setOrderId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setOrderStatus(order.getOrderStatus().toString());
        response.setOrderTotal(order.getTotalAmount());
        response.setPaymentMethod(order.getPayment().getPaymentMethod().toString());
        response.setPaymentId(order.getPayment().getId());
        response.setAddress(address);
        response.setItems(order.getOrderItems().stream().map(this::toItemResponse).toList());

        // Only useful while the order can still be paid for.
        if (order.getOrderStatus() == OrderStatus.PENDING_PAYMENT) {
            response.setRazorpayOrderId(order.getPayment().getPgOrderId());
            response.setRazorpayKeyId(razorpayConfig.getKeyId());
        }
        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        Product product = item.getProduct();
        return new OrderItemResponse(item.getId(), product.getId(), product.getName(), product.getImageUrl(),
                item.getQuantity(), item.getFinalPrice());
    }

    private OrderResponse buildOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setOrderItems(order.getOrderItems().stream().map(this::toItemResponse).toList());
        response.setOrderAddressId(order.getAddress().getId());
        response.setOrderTotal(order.getTotalAmount());
        response.setPaymentMethod(order.getPayment().getPaymentMethod().toString());
        response.setPaymentId(order.getPayment().getId());
        response.setOrderStatus(order.getOrderStatus().toString());
        return response;
    }
}
