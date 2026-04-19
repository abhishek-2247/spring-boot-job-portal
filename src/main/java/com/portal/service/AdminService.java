package com.portal.service;

import com.portal.model.Role;
import com.portal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

/**
 * AdminService — computes platform-wide statistics for the admin dashboard.
 * Uses both JPA (count queries) and raw JDBC (JobStatsRepository) for the SDG KPI.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobStatsRepository jobStatsRepository;

    /**
     * Collects all admin dashboard statistics.
     * SDG KPI: Placement Rate computed via raw JDBC.
     *
     * @return map of stat name to value
     */
    public Map<String, Object> getDashboardStats() {
        long totalSeekers   = userRepository.countByRole(Role.SEEKER);
        long totalEmployers = userRepository.countByRole(Role.EMPLOYER);
        long openJobs       = jobRepository.countByStatus("OPEN");
        long closedJobs     = jobRepository.countByStatus("CLOSED");
        long totalApps      = applicationRepository.count();
        long hiredCount     = applicationRepository.countByStatus("HIRED");
        double placementRate = jobStatsRepository.getPlacementRate();

        return Map.of(
            "totalSeekers",   totalSeekers,
            "totalEmployers", totalEmployers,
            "openJobs",       openJobs,
            "closedJobs",     closedJobs,
            "totalApps",      totalApps,
            "hiredCount",     hiredCount,
            "placementRate",  String.format("%.1f", placementRate)
        );
    }

    @Transactional
    public void closeJob(Long jobId) {
        jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus("CLOSED");
            jobRepository.save(job);
            log.info("Admin closed job: {}", jobId);
        });
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Admin deleted user: {}", userId);
    }

    @Transactional
    public void updateApplicationStatus(Long appId, String status) {
        applicationRepository.findById(appId).ifPresent(app -> {
            app.setStatus(status);
            applicationRepository.save(app);
        });
    }
}
