package com.ecommerce.sb_ecom.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // stateless JWT API, not cookie/session based
                // Applies the CorsConfigurationSource bean from CorsConfig. Without this line
                // here, that bean is registered but Spring Security never consults it, so a
                // cross-origin browser request is still blocked (or, worse, silently allowed
                // through by a servlet-container-level CORS filter with no method/header
                // restrictions - explicit is safer than relying on that).
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JSON error bodies for both "not signed in" (401) and "signed in but not
                // allowed" (403), matching the rest of the API's ErrorResponse shape instead
                // of Spring Security's default empty 403.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // public: auth endpoints + product/category browsing
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                        // catalog management (create/update/delete products & categories)
                        .requestMatchers("/api/admin/**").hasAnyRole("SELLER", "ADMIN")

                        // customer / seller self-service profile & address management
                        // (more specific matchers must come before the general /api/users/** rule below)
                        .requestMatchers("/api/users/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/users/sellers/**").hasAnyRole("SELLER", "ADMIN")

                        // raw user administration (list/patch/delete any user account)
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // cart / checkout — any authenticated customer
                        .requestMatchers("/api/customers/carts/**").hasAnyRole("CUSTOMER", "ADMIN")

                        // everything else just needs to be logged in. Per-resource ownership
                        // (does this cart/order/address id belong to the caller?) is enforced
                        // in each controller via CurrentUser - see its class comment.
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
