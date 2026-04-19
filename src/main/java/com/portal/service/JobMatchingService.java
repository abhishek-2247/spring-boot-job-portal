package com.portal.service;

import com.portal.dto.ScoredJob;
import com.portal.model.Job;
import com.portal.model.Skill;
import com.portal.model.User;
import com.portal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JobMatchingService — the core intelligence of the portal.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobMatchingService {

    private final JobRepository jobRepository;

    /**
     * Returns jobs ranked by skill match percentage for the given user.
     * Only returns jobs with score > 0 (at least one matching skill).
     *
     * @param user the seeker whose skills are used for scoring
     * @return sorted list of ScoredJob (highest match first)
     */
    public List<ScoredJob> getMatchedJobs(User user) {
        // Step 1: Get user's skill IDs
        Set<Long> userSkillIds = user.getSkills().stream()
            .map(Skill::getSkillId)
            .collect(Collectors.toSet());

        if (userSkillIds.isEmpty()) {
            log.debug("User {} has no skills — returning empty recommendations", user.getEmail());
            return Collections.emptyList();
        }

        // Step 2: Load all open jobs with employer eagerly fetched (prevents LazyInitializationException)
        List<Job> activeJobs = jobRepository.findByStatusWithEmployer("OPEN");

        log.debug("Scoring {} active jobs for user {} with {} skills",
            activeJobs.size(), user.getEmail(), userSkillIds.size());

        // Step 3–5: Score + filter + sort
        return activeJobs.stream()
            .map(job -> {
                Set<Long> jobSkillIds = job.getRequiredSkills().stream()
                    .map(Skill::getSkillId)
                    .collect(Collectors.toSet());

                if (jobSkillIds.isEmpty()) {
                    return new ScoredJob(job, 0.0);
                }

                // Count intersection
                long matchedCount = jobSkillIds.stream()
                    .filter(userSkillIds::contains)
                    .count();

                double score = (double) matchedCount / jobSkillIds.size() * 100.0;
                return new ScoredJob(job, score);
            })
            .filter(sj -> sj.getScore() > 0)
            .sorted(Comparator.comparingDouble(ScoredJob::getScore).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Get all open jobs (unscored) — for public listing page.
     */
    public List<Job> getAllActiveJobs() {
        return jobRepository.findByStatusWithEmployer("OPEN");
    }

    /**
     * Filter open jobs by location.
     */
    public List<Job> getJobsByLocation(String location) {
        return jobRepository.findByStatusAndLocationWithEmployer("OPEN", location);
    }
}
