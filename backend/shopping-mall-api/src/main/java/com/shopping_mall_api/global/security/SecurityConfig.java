package com.shopping_mall_api.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.AuthenticationException;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSec) throws Exception {
        httpSec
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization filter
                .authorizeHttpRequests(auth -> auth
                        // ADMIN API
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users", "/carts", "/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/products/{productId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}",
                                                            "/orders/{orderId}").hasRole("ADMIN")

                        // Authenticated User
                        .requestMatchers(HttpMethod.POST, "/carts",
                                                          "/orders", "/orders/toss/payment/auth").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/me",
                                                         "/carts/me",
                                                         "/orders/me", "/orders/{orderId}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/me",
                                                           "/carts/me",
                                                           "/orders/{orderId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/logout", "/users/me",
                                                            "/carts/items/{productId}", "/carts/me").authenticated()

                        // Permit All
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/login", "/users/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/{productId}").permitAll()

                        .anyRequest().hasRole("ADMIN")
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(this::handleAuthenticationFailure))

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return httpSec.build();
    }

    private void handleAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> errorBody = ApiResponse.error("Invalid or missing access token");
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
