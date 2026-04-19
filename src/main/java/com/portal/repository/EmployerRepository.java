package com.portal.repository;

import com.portal.model.Employer;
import com.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Spring Data JPA repository for Employer.
 */
@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

    Optional<Employer> findByUser(User user);

    /**
     * Eagerly loads the employer's jobs to avoid LazyInitializationException
     * in the dashboard when open-in-view is false.
     */
    @Query("SELECT e FROM Employer e LEFT JOIN FETCH e.jobs WHERE e.user = :user")
    Optional<Employer> findByUserWithJobs(@Param("user") User user);

    boolean existsByUser(User user);
}
