package com.ecommerce.sb_ecom.config;

import com.ecommerce.sb_ecom.enums.UserRole;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Creates exactly one ADMIN account on startup if none exists yet, so there's a way
 * into the system at all now that public self-registration always creates a CUSTOMER
 * (see RegisterRequest / AuthService — that used to accept a client-chosen role, which
 * let anyone register as ADMIN; this seeder replaces that hole with a fixed, known
 * account instead).
 *
 * Sign in with these credentials at /login, then use that account's token in Postman
 * (or curl) to call the seller/admin endpoints directly — there's no UI for them yet.
 * From there you can create a Seller profile (POST /api/users/sellers) and products
 * (POST /api/admin/products) for testing.
 *
 * Configure real credentials via environment variables before running this anywhere
 * other than your own machine:
 *   app.seed-admin.email=${SEED_ADMIN_EMAIL:admin@haat.local}
 *   app.seed-admin.password=${SEED_ADMIN_PASSWORD:changeme123}
 * Delete this class (or set app.seed-admin.enabled=false) once you have a real admin
 * account and don't need auto-seeding anymore.
 */
@Configuration
@RequiredArgsConstructor
public class DevAdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevAdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-admin.enabled:true}")
    private boolean enabled;

    @Value("${app.seed-admin.email:admin@haat.local}")
    private String seedEmail;

    @Value("${app.seed-admin.password:changeme123}")
    private String seedPassword;

    @Override
    public void run(String... args) {
        if (!enabled) return;

        boolean anyAdminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == UserRole.ADMIN);
        if (anyAdminExists) return;

        User admin = new User();
        admin.setEmail(seedEmail.trim().toLowerCase());
        admin.setPassword(passwordEncoder.encode(seedPassword));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        log.warn("Seeded a first ADMIN account: {} / (password from app.seed-admin.password). "
                + "Sign in once, then set app.seed-admin.enabled=false.", admin.getEmail());
    }
}
