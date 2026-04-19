package com.portal.repository;

import com.portal.model.Role;
import com.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for User.
 * Auto-generates CRUD + custom queries from method names.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by email — used by Spring Security UserDetailsService */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email with skills eagerly loaded via JOIN FETCH.
     * Prevents LazyInitializationException when open-in-view is disabled.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.skills WHERE u.email = :email")
    Optional<User> findByEmailWithSkills(@Param("email") String email);

    /** Check if email is already registered */
    boolean existsByEmail(String email);

    /** Get all users with a specific role — e.g., all SEEKERs for alerts */
    List<User> findAllByRole(Role role);

    /** Count users by role — for admin stats */
    long countByRole(Role role);
}
