package com.portal.repository;

import com.portal.model.Job;
import com.portal.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for Job.
 *
 * Demonstrates both:
 *  - Method-name derived queries (findByStatus, findByLocationContainingIgnoreCase)
 *  - @Query JPQL for complex joins (findBySkillsIn)
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Eagerly loads the employer for a specific job detail view.
     * Prevents LazyInitializationException when open-in-view=false.
     */
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer WHERE j.jobId = :jobId")
    Optional<Job> findByIdWithEmployer(@Param("jobId") Long jobId);

    /**
     * Eagerly loads all jobs with their employers for the admin dashboard.
     */
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer")
    List<Job> findAllWithEmployer();

    /** All open jobs — used for the public listing and matching */
    List<Job> findByStatus(String status);

    /**
     * All open jobs with employer eagerly loaded.
     * Prevents LazyInitializationException when open-in-view=false and template accesses job.employer.
     */
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer WHERE j.status = :status")
    List<Job> findByStatusWithEmployer(@Param("status") String status);

    /** Location-based filtering (case-insensitive contains) */
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer WHERE j.status = :status AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> findByStatusAndLocationWithEmployer(@Param("status") String status, @Param("location") String location);

    /** Location-based filtering (case-insensitive contains) */
    List<Job> findByStatusAndLocationContainingIgnoreCase(String status, String location);

    /** JPQL: jobs that require at least one of the given skills */
    @Query("SELECT DISTINCT j FROM Job j JOIN j.requiredSkills s WHERE s IN :skills AND j.status = 'OPEN'")
    List<Job> findBySkillsIn(@Param("skills") Set<Skill> skills);

    /** Jobs posted by a specific employer */
    List<Job> findByEmployer_EmployerId(Long employerId);

    /** Count jobs by status — for admin stats */
    long countByStatus(String status);
}
