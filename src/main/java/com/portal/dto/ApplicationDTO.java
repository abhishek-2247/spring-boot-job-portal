package com.portal.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Flat DTO for showing application info on the dashboard.
 * Avoids lazy-loading issues by pre-flattening nested entities.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDTO {
    private Long appId;
    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private String status;
    private LocalDateTime appliedAt;

    /** Returns a display-friendly date string */
    public String getFormattedDate() {
        if (appliedAt == null) return "—";
        return appliedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /** Returns a CSS badge class based on status */
    public String getStatusClass() {
        return switch (status) {
            case "HIRED"    -> "badge-hired";
            case "REVIEWED" -> "badge-reviewed";
            case "REJECTED" -> "badge-rejected";
            default         -> "badge-pending";
        };
    }
}
