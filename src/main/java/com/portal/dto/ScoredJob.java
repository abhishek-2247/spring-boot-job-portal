package com.portal.dto;

import com.portal.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO used by the skill-matching algorithm.
 * Wraps a Job with a computed match score (0–100%).
 *
 * Score = (matched skills / total required skills) × 100
 *
 * This is the core output of JobMatchingService — sorted descending by score.
 */
@Data
@AllArgsConstructor
public class ScoredJob {

    private Job job;

    /** Match percentage: 0.0 to 100.0 */
    private double score;

    /** Convenience: formatted score string e.g. "75%" */
    public String getFormattedScore() {
        return String.format("%.0f%%", score);
    }

    /** Returns a CSS class based on score band for visual coloring */
    public String getScoreClass() {
        if (score >= 80) return "score-high";
        if (score >= 50) return "score-medium";
        return "score-low";
    }
}
