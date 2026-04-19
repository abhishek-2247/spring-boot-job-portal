package com.portal.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Raw JDBC repository — demonstrates PreparedStatement usage (vs Statement).
 *
 * WHY PreparedStatement?
 *  1. SQL Injection prevention — parameters are sent separately from the query template.
 *  2. Pre-compilation — the DB engine compiles the plan once and reuses it.
 *  3. Cleaner code — no manual string escaping needed.
 *
 * Used for custom reporting where JPA's overhead is unnecessary.
 */
@Repository
@Slf4j
public class JobStatsRepository {

    private final DataSource dataSource;

    public JobStatsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Count total applications for a specific job using raw JDBC.
     * Demonstrates PreparedStatement with a single parameterized query.
     *
     * @param jobId the job to count applications for
     * @return count of applications
     */
    public int countApplicationsForJob(Long jobId) {
        String sql = "SELECT COUNT(*) FROM applications WHERE job_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, jobId);   // Safe: parameter bound, not concatenated
            ResultSet rs = ps.executeQuery();
            int count = rs.next() ? rs.getInt(1) : 0;
            log.debug("Applications for job {}: {}", jobId, count);
            return count;

        } catch (SQLException e) {
            log.error("JDBC error counting applications for job {}: {}", jobId, e.getMessage());
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * SDG KPI: Placement Rate = (HIRED count / total seeker count) × 100
     *
     * Queryable via raw JDBC to demonstrate multi-table SQL aggregation.
     *
     * @return placement rate as a percentage (0.0–100.0)
     */
    public double getPlacementRate() {
        String hiredSql   = "SELECT COUNT(*) FROM applications WHERE status = 'HIRED'";
        String seekersSql = "SELECT COUNT(*) FROM users WHERE role = 'SEEKER'";

        try (Connection conn = dataSource.getConnection()) {

            int hired;
            try (PreparedStatement ps = conn.prepareStatement(hiredSql);
                 ResultSet rs = ps.executeQuery()) {
                hired = rs.next() ? rs.getInt(1) : 0;
            }

            int totalSeekers;
            try (PreparedStatement ps = conn.prepareStatement(seekersSql);
                 ResultSet rs = ps.executeQuery()) {
                totalSeekers = rs.next() ? rs.getInt(1) : 0;
            }

            if (totalSeekers == 0) return 0.0;
            double rate = (double) hired / totalSeekers * 100.0;
            log.info("SDG Placement Rate: {}% ({} hired / {} seekers)", String.format("%.1f", rate), hired, totalSeekers);
            return rate;

        } catch (SQLException e) {
            log.error("JDBC error computing placement rate: {}", e.getMessage());
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Get total applications across all jobs — admin dashboard stat.
     */
    public int getTotalApplicationCount() {
        String sql = "SELECT COUNT(*) FROM applications";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            log.error("JDBC error counting total applications: {}", e.getMessage());
            return 0;
        }
    }
}
