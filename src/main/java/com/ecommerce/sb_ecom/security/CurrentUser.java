package com.ecommerce.sb_ecom.security;

import com.ecommerce.sb_ecom.enums.UserRole;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.CustomerAddressRepository;
import com.ecommerce.sb_ecom.repository.CustomerRepository;
import com.ecommerce.sb_ecom.repository.OrderRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import com.ecommerce.sb_ecom.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Answers "who is calling, and is this their data?".
 *
 * The role checks in SecurityConfig only say *what kind* of user may call an endpoint.
 * They don't stop customer A from passing customer B's cart/order/address id. These
 * guards do, and are called at the top of each controller method that takes such an id.
 * ADMIN accounts bypass ownership checks.
 */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    private UserPrincipal principal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        throw new AccessDeniedException("Sign in to continue.");
    }

    public boolean isAdmin() {
        return principal().getUser().getRole() == UserRole.ADMIN;
    }

    public void requireAdmin() {
        if (!isAdmin()) throw deny();
    }

    /** The signed-in user's customer id. Throws for accounts without a customer profile. */
    public String customerId() {
        return customerRepository.findByUserId(principal().getId())
                .map(customer -> customer.getId())
                .orElseThrow(() -> new AccessDeniedException("This account has no customer profile."));
    }

    public void assertOwnsCustomer(String customerId) {
        if (isAdmin()) return;
        if (customerId == null || !customerId().equals(customerId)) throw deny();
    }

    public void assertOwnsCart(String cartId) {
        if (isAdmin()) return;
        if (cartId == null || !cartRepository.existsByIdAndCustomerId(cartId, customerId())) throw deny();
    }

    public void assertOwnsAddress(String addressId) {
        if (isAdmin()) return;
        if (addressId == null || customerAddressRepository.findByIdAndCustomerId(addressId, customerId()).isEmpty()) {
            throw deny();
        }
    }

    public void assertOwnsOrder(String orderId) {
        if (isAdmin()) return;
        if (orderId == null || !orderRepository.existsByIdAndCustomerId(orderId, customerId())) throw deny();
    }

    /** The signed-in user's seller id. Throws for accounts without a seller profile. */
    public String sellerId() {
        return sellerRepository.findByUserId(principal().getId())
                .map(seller -> seller.getId())
                .orElseThrow(() -> new AccessDeniedException("This account has no seller profile."));
    }

    /**
     * Does the signed-in seller own this product? Used before a seller is allowed to
     * update or delete it - without this, any seller could edit or delete any other
     * seller's product just by knowing its id, since /api/admin/products/** previously
     * only checked "is this caller a seller at all", not "is this their product".
     */
    public void assertOwnsProduct(String productId) {
        if (isAdmin()) return;
        String sellerId = sellerId();
        boolean owns = productRepository.findById(productId)
                .map(product -> product.getSeller() != null && sellerId.equals(product.getSeller().getId()))
                .orElse(false);
        if (!owns) throw deny();
    }

    private AccessDeniedException deny() {
        return new AccessDeniedException("You don't have access to that.");
    }
}
