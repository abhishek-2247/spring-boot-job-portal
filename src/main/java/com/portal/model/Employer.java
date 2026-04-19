package com.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Employer entity — extends a User with company-specific details.
 * An EMPLOYER role User has an associated Employer profile.
 *
 * OneToOne with User: one employer account per user.
 * OneToMany with Job: an employer can post multiple jobs.
 */
@Entity
@Table(name = "employers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employerId;

    @Column(nullable = false)
    private String companyName;

    private String industry;

    private String website;

    private String companyDescription;

    /**
     * The user account associated with this employer.
     * OneToOne — unique per user.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Jobs posted by this employer.
     * OneToMany — cascade ALL so removing employer also removes their jobs.
     */
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();

    @Override
    public String toString() {
        return "Employer{id=" + employerId + ", company='" + companyName + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employer)) return false;
        Employer employer = (Employer) o;
        return Objects.equals(employerId, employer.employerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employerId);
    }
}
