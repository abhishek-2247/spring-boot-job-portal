package com.portal.service;

import com.portal.dto.RegistrationForm;
import com.portal.model.Employer;
import com.portal.model.Role;
import com.portal.model.Skill;
import com.portal.model.User;
import com.portal.repository.EmployerRepository;
import com.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

/**
 * UserService — handles registration, profile updates, and Spring Security integration.
 *
 * Implements UserDetailsService so Spring Security can authenticate via email+password.
 * The loadUserByUsername method is called internally during login form submission.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security hook — loads user credentials by email.
     * Maps our Role enum to Spring's GrantedAuthority (ROLE_SEEKER, ROLE_EMPLOYER, ROLE_ADMIN).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("No account found for: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Register a new user from the registration form.
     * Handles BCrypt hashing and Employer profile creation if role = EMPLOYER.
     *
     * @throws IllegalArgumentException if email taken or passwords don't match
     */
    public User registerUser(RegistrationForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + form.getEmail());
        }
        if (!form.passwordsMatch()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
            .name(form.getName())
            .email(form.getEmail())
            .passwordHash(passwordEncoder.encode(form.getPassword()))
            .location(form.getLocation())
            .role(form.getRole())
            .build();

        user = userRepository.save(user);
        log.info("Registered new user: {} ({})", user.getEmail(), user.getRole());

        // Create employer profile if role is EMPLOYER
        if (form.getRole() == Role.EMPLOYER) {
            Employer employer = Employer.builder()
                .user(user)
                .companyName(form.getCompanyName() != null ? form.getCompanyName() : "My Company")
                .industry(form.getIndustry())
                .website(form.getWebsite())
                .build();
            employerRepository.save(employer);
            log.info("Created employer profile for: {}", user.getEmail());
        }

        return user;
    }

    /**
     * Get the full User entity by email with skills eagerly loaded — used in dashboard/profile.
     * Uses JOIN FETCH to prevent LazyInitializationException when open-in-view is disabled.
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailWithSkills(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    /**
     * Update the seeker's skill set.
     */
    public void updateSkills(User user, Set<Skill> newSkills) {
        user.setSkills(newSkills);
        userRepository.save(user);
        log.info("Updated skills for user: {}", user.getEmail());
    }
}
