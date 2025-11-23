package com.bms.config;

import com.bms.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/users/me/**").authenticated()
                        .requestMatchers("/api/v1/users/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/v1/accounts/all").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/v1/transactions/all").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/v1/payments/all").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/v1/loans/all").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/v1/loans/*/approve").hasRole("EMPLOYEE")
                        .requestMatchers("/api/v1/loans/*/reject").hasRole("EMPLOYEE")
                        .requestMatchers("/api/v1/loans/*/disburse").hasRole("EMPLOYEE")
                        .requestMatchers("/api/v1/cards/all").hasAnyRole("ADMIN", "EMPLOYEE")
                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
