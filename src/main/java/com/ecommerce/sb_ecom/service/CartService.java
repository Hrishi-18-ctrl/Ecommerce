package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.CartItemResponse;
import com.ecommerce.sb_ecom.DTO.CartPageResponse;
import com.ecommerce.sb_ecom.DTO.CartRequest;
import com.ecommerce.sb_ecom.DTO.CartResponse;
import com.ecommerce.sb_ecom.DTO.UpdateItemQuantityRequest;
import com.ecommerce.sb_ecom.exceptions.*;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.CartItem;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.repository.CartItemRepository;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    // NOTE on totalItems: it is kept as the SUM of item quantities in the cart
    // (not the count of distinct products), consistently across add/update/delete/order,
    // to match what "how many items are in my cart" means to a shopper.

//    ADD ITEM TO CART
    @Transactional
    public CartItemResponse addItemToCart(CartRequest cartRequest) {
        Cart cart = cartRepository.findById(cartRequest.getCartId())
                .orElseThrow(() -> new CartNotFoundException("cart with id " + cartRequest.getCartId() + " not found"));

        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product with id " + cartRequest.getProductId() + " not found"));

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cartRequest.getCartId(), cartRequest.getProductId());
        int alreadyInCart = existingCartItem.map(CartItem::getQuantity).orElse(0);
        int requestedTotal = alreadyInCart + cartRequest.getQuantity();

        // Best-effort check for a responsive UI. This does NOT lock the row, so it can
        // still race with another checkout; the authoritative, locked check happens in
        // OrderService.placeOrder at the moment of purchase.
        if (product.getStockQuantity() < requestedTotal) {
            throw new InsufficientStockException(
                    "Only " + product.getStockQuantity() + " unit(s) of \"" + product.getName() + "\" available");
        }

        double addedValue = product.getPrice() * cartRequest.getQuantity();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());

            cart.setTotalItems(cart.getTotalItems() + cartRequest.getQuantity());
            cart.setTotalPrice(cart.getTotalPrice() + addedValue);
            cart.setFinalPrice(cart.getTotalPrice());
            cartRepository.save(cart);
            cartItem = cartItemRepository.save(cartItem);
        } else {
            cart.setTotalItems(cart.getTotalItems() + cartRequest.getQuantity());
            cart.setTotalPrice(cart.getTotalPrice() + addedValue);
            cart.setFinalPrice(cart.getTotalPrice());
            Cart savedCart = cartRepository.save(cart);

            cartItem = new CartItem();
            cartItem.setCart(savedCart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartRequest.getQuantity());
            cartItem = cartItemRepository.save(cartItem);
        }

        return toItemResponse(cartItem, product);
    }


//  DELETE ITEM FROM CART
    @Transactional
    public void deleteItemFromCart(String cartId, String cartItemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("cart with id " + cartId + " not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("product with id " + cartItem.getProduct().getId() + " not found"));

        double removedValue = product.getPrice() * cartItem.getQuantity();

        cart.setTotalItems(cart.getTotalItems() - cartItem.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() - removedValue);
        cart.setFinalPrice(cart.getTotalPrice());

        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }


//       UPDATE QUANTITY OF ITEM IN A CART
    @Transactional
    public CartItemResponse updateQuantityOfItem(UpdateItemQuantityRequest updateItemQuantityRequest, String cartId, String cartItemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("cart with id " + cartId + " not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("product with id " + cartItem.getProduct().getId() + " not found"));

        if (updateItemQuantityRequest.getAddQuantity() != null && updateItemQuantityRequest.getRemoveQuantity() != null) {
            throw new InvalidRequestException("cannot add and remove quantity in the same request");
        }

        if (updateItemQuantityRequest.getAddQuantity() != null) {
            Integer addQuantity = updateItemQuantityRequest.getAddQuantity();

            if (product.getStockQuantity() < cartItem.getQuantity() + addQuantity) {
                throw new InsufficientStockException(
                        "Only " + product.getStockQuantity() + " unit(s) of \"" + product.getName() + "\" available");
            }

            cartItem.setQuantity(cartItem.getQuantity() + addQuantity);
            cart.setTotalItems(cart.getTotalItems() + addQuantity);
            cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * addQuantity));
            cart.setFinalPrice(cart.getTotalPrice());
            cartRepository.save(cart);

            return toItemResponse(cartItemRepository.save(cartItem), product);
        }

        if (updateItemQuantityRequest.getRemoveQuantity() != null) {
            Integer removeQuantity = updateItemQuantityRequest.getRemoveQuantity();

            if (removeQuantity >= cartItem.getQuantity()) {
                throw new InvalidRequestException(
                        "removeQuantity must be less than the item's current quantity (" + cartItem.getQuantity()
                                + "); to remove it entirely, delete the cart item instead");
            }

            cartItem.setQuantity(cartItem.getQuantity() - removeQuantity);
            cart.setTotalItems(cart.getTotalItems() - removeQuantity);
            cart.setTotalPrice(cart.getTotalPrice() - (product.getPrice() * removeQuantity));
            cart.setFinalPrice(cart.getTotalPrice());
            cartRepository.save(cart);

            return toItemResponse(cartItemRepository.save(cartItem), product);
        }

        throw new InvalidRequestException("either addQuantity or removeQuantity must be provided");
    }


//    GET CART BY CUSTOMER ID
    public CartResponse getCartByCustomerId(String customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found against customer"));
        return toCartResponse(cart);
    }


    //GET ALL CARTS
    public CartPageResponse getAllCarts(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber , pageSize);
        Page<Cart> page = cartRepository.findAll(pageable);

        List<CartResponse> cartResponseList = page.getContent().stream().map(this::toCartResponse).toList();

        CartPageResponse response = new CartPageResponse();
        response.setContent(cartResponseList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }

    // Was: response.setCartItems(cart.getCartItems()) - serializing the raw CartItem
    // entity. CartItem.product is @JsonIgnore'd, so every line arrived on the client
    // with no name, price, image or stock - just an id and a quantity. This maps each
    // line into CartItemResponse instead, pulling those fields off the product.
    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            items.add(toItemResponse(item, item.getProduct()));
        }

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setCartItems(items);
        response.setTotalItems(cart.getTotalItems());
        response.setTotalPrice(cart.getTotalPrice());
        response.setFinalPrice(cart.getFinalPrice());
        response.setDiscountAmount(cart.getDiscountAmount());
        return response;
    }

    private CartItemResponse toItemResponse(CartItem item, Product product) {
        return new CartItemResponse(item.getId(), item.getQuantity(), product.getId(), product.getName(),
                product.getPrice(), product.getImageUrl(), product.getStockQuantity());
    }
}
