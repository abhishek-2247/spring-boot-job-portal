package com.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * User entity — the central actor in the system.
 * A User can be a SEEKER (applies for jobs), EMPLOYER (posts jobs), or ADMIN.
 *
 * Demonstrates:
 *  - @ManyToMany with user_skills join table (Hibernate)
 *  - @OneToMany with cascade (applications)
 *  - @Enumerated for Role
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Skills owned by this user (SEEKER proficiency set).
     * ManyToMany — a skill can belong to many users; a user can have many skills.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_skills",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    /**
     * Applications submitted by this seeker.
     * OneToMany — cascade ALL so removing a user removes their applications.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    // Avoid circular toString via Lombok
    @Override
    public String toString() {
        return "User{userId=" + userId + ", email='" + email + "', role=" + role + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
