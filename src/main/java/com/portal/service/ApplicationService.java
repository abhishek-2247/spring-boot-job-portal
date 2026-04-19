package com.portal.service;

import com.portal.dto.ApplicationDTO;
import com.portal.model.*;
import com.portal.repository.ApplicationRepository;
import com.portal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApplicationService — handles job application creation and status management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    /**
     * Submit a job application for the given seeker.
     * Prevents duplicate applications via existsByUserAndJob check.
     */
    public Application apply(User user, Long jobId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (applicationRepository.existsByUserAndJob(user, job)) {
            throw new IllegalStateException("You have already applied to this job");
        }
        if (!"OPEN".equals(job.getStatus())) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }

        Application app = Application.builder()
            .user(user)
            .job(job)
            .status("PENDING")
            .build();

        Application saved = applicationRepository.save(app);
        log.info("Application submitted: user={} job={}", user.getEmail(), job.getTitle());
        return saved;
    }

    /**
     * Update application status — used by employers to REVIEW/HIRE/REJECT.
     */
    public void updateStatus(Long appId, String newStatus) {
        Application app = applicationRepository.findById(appId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found: " + appId));
        app.setStatus(newStatus);
        applicationRepository.save(app);
        log.info("Application {} status updated to {}", appId, newStatus);
    }

    /**
     * Get all applications for a user as DTOs (lazy-load safe).
     */
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsForUser(User user) {
        return applicationRepository.findByUser(user).stream()
            .map(app -> ApplicationDTO.builder()
                .appId(app.getAppId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getEmployer() != null
                    ? app.getJob().getEmployer().getCompanyName() : "—")
                .jobLocation(app.getJob().getLocation())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * Get all applications for a specific job — for employer's view.
     */
    @Transactional(readOnly = true)
    public List<Application> getApplicationsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        return applicationRepository.findByJob(job);
    }
}
