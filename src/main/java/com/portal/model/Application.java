package com.portal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Application entity — records a SEEKER applying to a JOB.
 *
 * Status lifecycle: PENDING → REVIEWED → HIRED | REJECTED
 *
 * The HIRED count is used by AdminService to compute the SDG KPI:
 *   Placement Rate = (HIRED count / total seekers) × 100
 *
 * Demonstrates:
 *  - @ManyToOne to both User and Job
 *  - @CreationTimestamp for auto timestamp
 */
@Entity
@Table(name = "applications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appId;

    /**
     * The seeker who applied.
     * ManyToOne — a user can apply to many jobs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The job being applied to.
     * ManyToOne — a job can receive many applications.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    /** PENDING | REVIEWED | HIRED | REJECTED */
    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    /** Automatically set to current UTC timestamp on insert. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;

    @Override
    public String toString() {
        return "Application{appId=" + appId + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Application)) return false;
        Application other = (Application) o;
        return Objects.equals(appId, other.appId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId);
    }
}
