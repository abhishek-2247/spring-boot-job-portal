package com.portal.service;

import com.portal.dto.ScoredJob;
import com.portal.model.Role;
import com.portal.model.User;
import com.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * JobAlertService — handles sending daily job alerts to seekers asynchronously.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobAlertService {

    private final UserRepository userRepository;
    private final JobMatchingService matchingService;

    /**
     * Scheduled trigger — runs every day at 9:00 AM.
     * Cron: second minute hour day-of-month month day-of-week
     *
     * This runs on Spring's scheduler thread → immediately delegates to @Async.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyAlerts() {
        log.info("[Scheduler] Triggering daily job alerts at 9 AM...");
        List<User> seekers = userRepository.findAllByRole(Role.SEEKER);
        log.info("[Scheduler] Found {} seekers to notify", seekers.size());
        seekers.forEach(this::sendAlertAsync);
    }

    /**
     * @Async — runs on a JobAlert-N thread from the ThreadPoolTaskExecutor.
     *
     * Each user's matching is independent, so they can run in parallel.
     * Returns CompletableFuture<Void> to signal completion.
     *
     * @param user the seeker to send alerts to
     */
    @Async
    public CompletableFuture<Void> sendAlertAsync(User user) {
        String threadName = Thread.currentThread().getName();
        log.debug("[{}] Computing matches for: {}", threadName, user.getEmail());

        try {
            List<ScoredJob> matches = matchingService.getMatchedJobs(user);

            if (!matches.isEmpty()) {
                // MOCK email — replace with JavaMailSender in production
                log.info("[{}] JOB ALERT → {} | {} matched jobs: {}",
                    threadName,
                    user.getEmail(),
                    matches.size(),
                    matches.stream()
                        .limit(3)
                        .map(sj -> sj.getJob().getTitle() + " (" + sj.getFormattedScore() + ")")
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("None"));
            } else {
                log.debug("[{}] No matches for {} — no alert sent", threadName, user.getEmail());
            }
        } catch (Exception e) {
            log.error("[{}] Failed to process alert for {}: {}", threadName, user.getEmail(), e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Manually trigger alerts — exposed via admin panel for demo purposes.
     */
    public void triggerAlertsManually() {
        log.info("[Admin] Manual alert trigger initiated");
        sendDailyAlerts();
    }
}
