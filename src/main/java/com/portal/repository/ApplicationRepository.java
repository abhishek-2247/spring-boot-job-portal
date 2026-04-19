package com.portal.repository;

import com.portal.model.Application;
import com.portal.model.Job;
import com.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Application.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUser(User user);

    List<Application> findByJob(Job job);

    Optional<Application> findByUserAndJob(User user, Job job);

    boolean existsByUserAndJob(User user, Job job);

    List<Application> findByStatus(String status);

    long countByStatus(String status);

    /** JPQL: count applications for a given job — used for stats */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.jobId = :jobId")
    long countByJobId(Long jobId);

    /**
     * Eagerly load all applications with user and job details for admin view.
     * JOIN FETCH prevents LazyInitializationException when open-in-view=false.
     */
    @Query("SELECT a FROM Application a JOIN FETCH a.user JOIN FETCH a.job j LEFT JOIN FETCH j.employer")
    List<Application> findAllWithDetails();
}
