package com.portal.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Skill entity — represents a technical or domain skill (e.g., Java, SQL, React).
 * Shared between Users (what they have) and Jobs (what is required).
 */
@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @Column(nullable = false, unique = true)
    private String skillName;

    private String category; // e.g., Frontend, Backend, Database, DevOps

    // Used for display purposes
    @Override
    public String toString() {
        return skillName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return Objects.equals(skillId, skill.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
}
