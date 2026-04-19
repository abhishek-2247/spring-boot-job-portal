package com.portal.config;

import com.portal.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration.
 *
 * Role-based URL authorization:
 *  - /admin/**   → ADMIN only
 *  - /employer/**→ EMPLOYER only
 *  - /jobs/apply → SEEKER only
 *  - /login, /register, /jobs → public
 *
 * Session management: max 1 concurrent session per user.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // H2 console — dev only
                .requestMatchers("/h2-console/**").permitAll()
                // Static assets
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // Public pages
                .requestMatchers("/", "/register", "/login", "/jobs", "/jobs/*").permitAll()
                // Role-restricted
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/jobs/post", "/jobs/post/**").hasRole("EMPLOYER")
                .requestMatchers("/jobs/apply/**", "/profile/**").hasRole("SEEKER")
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement(sm -> sm
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            )
            // Allow H2 console iframes (dev only)
            .headers(headers -> headers
                .frameOptions(fo -> fo.sameOrigin())
            )
            // Disable CSRF for H2 console (dev only — re-enable for production)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
