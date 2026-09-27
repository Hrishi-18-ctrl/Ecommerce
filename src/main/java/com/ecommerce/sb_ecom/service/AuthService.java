package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.AuthResponse;
import com.ecommerce.sb_ecom.DTO.LoginRequest;
import com.ecommerce.sb_ecom.DTO.RegisterRequest;
import com.ecommerce.sb_ecom.DTO.SellerRegisterRequest;
import com.ecommerce.sb_ecom.enums.UserRole;
import com.ecommerce.sb_ecom.exceptions.EmailAlreadyExistsException;
import com.ecommerce.sb_ecom.exceptions.InvalidCredentialsException;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.Customer;
import com.ecommerce.sb_ecom.model.Seller;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.CustomerRepository;
import com.ecommerce.sb_ecom.repository.SellerRepository;
import com.ecommerce.sb_ecom.repository.UserRepository;
import com.ecommerce.sb_ecom.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Public self-registration always creates a CUSTOMER. (RegisterRequest has no role
     * field, so a client cannot ask for SELLER/ADMIN here - see RegisterRequest.)
     *
     * A Customer profile and an empty Cart are created in the same transaction as the
     * User, so the response can return customerId/cartId immediately and the storefront
     * never has to make a separate "create my customer profile" call before it can show
     * a cart. Admin accounts are still created through the ADMIN-only /api/users
     * endpoints (or the dev seeder) - see registerSeller below for the seller equivalent
     * of this method.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("An account with email " + normalizedEmail + " already exists");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // never store raw passwords
        user.setRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        Customer savedCustomer = customerRepository.save(customer);

        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        cart.setTotalItems(0);
        cart.setTotalPrice(0.0);
        cart.setFinalPrice(0.0);
        cart.setDiscountAmount(0.0);
        Cart savedCart = cartRepository.save(cart);
        savedCustomer.setCart(savedCart);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getRole(),
                savedCustomer.getId(), savedCart.getId(), null, savedCustomer.getFirstName(), savedCustomer.getLastName());
    }

    /**
     * Public self-registration for a seller account - the SELLER equivalent of
     * register() above. Creates the User (role=SELLER) and the Seller profile in one
     * transaction, same pattern as customer registration, minus the cart (sellers don't
     * have one). This is what makes POST /api/auth/register/seller a genuine self-serve
     * "become a seller" flow rather than something only an admin can do.
     */
    @Transactional
    public AuthResponse registerSeller(SellerRegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("An account with email " + normalizedEmail + " already exists");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.SELLER);
        User savedUser = userRepository.save(user);

        Seller seller = new Seller();
        seller.setUser(savedUser);
        seller.setFirstName(request.getFirstName().trim());
        seller.setLastName(request.getLastName().trim());
        Seller savedSeller = sellerRepository.save(seller);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getRole(),
                null, null, savedSeller.getId(), savedSeller.getFirstName(), savedSeller.getLastName());
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Deliberately the same message as "user not found" above, so the API
            // never reveals whether a given email is registered.
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        // One login endpoint serves all three roles. A CUSTOMER has no seller profile,
        // a SELLER has no customer profile, and ADMIN has neither - so at most one of
        // these two lookups ever finds anything, and both leave their fields null
        // rather than fail the login when they don't.
        Optional<Customer> customer = customerRepository.findByUserId(user.getId());
        Optional<Seller> seller = sellerRepository.findByUserId(user.getId());

        String customerId = customer.map(Customer::getId).orElse(null);
        String cartId = customer.map(c -> c.getCart() != null ? c.getCart().getId() : null).orElse(null);
        String sellerId = seller.map(Seller::getId).orElse(null);
        String firstName = customer.map(Customer::getFirstName)
                .or(() -> seller.map(Seller::getFirstName)).orElse(null);
        String lastName = customer.map(Customer::getLastName)
                .or(() -> seller.map(Seller::getLastName)).orElse(null);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole(),
                customerId, cartId, sellerId, firstName, lastName);
    }
}
