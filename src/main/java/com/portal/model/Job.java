package com.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Job entity — a job listing posted by an Employer.
 *
 * Demonstrates:
 *  - @ManyToOne to Employer (job_id → employer_id FK)
 *  - @ManyToMany with job_skills join table
 *  - @OneToMany to Application
 */
@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private String salaryRange;

    /** OPEN or CLOSED */
    @Column(nullable = false)
    @Builder.Default
    private String status = "OPEN";

    /**
     * The employer who posted this job.
     * ManyToOne — many jobs can belong to one employer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    /**
     * Skills required for this job — used in the matching algorithm.
     * ManyToMany — a skill can be required by many jobs.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> requiredSkills = new HashSet<>();

    /**
     * Applications submitted for this job.
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @Override
    public String toString() {
        return "Job{jobId=" + jobId + ", title='" + title + "', status='" + status + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }
}
