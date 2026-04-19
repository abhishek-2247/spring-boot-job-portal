package com.portal.controller;

import com.portal.model.*;
import com.portal.repository.EmployerRepository;
import com.portal.repository.SkillRepository;
import com.portal.service.ApplicationService;
import com.portal.service.JobMatchingService;
import com.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.portal.repository.JobRepository;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JobController — handles job listing, recommendations, posting, and applications.
 */
@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobMatchingService jobMatchingService;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    /** Public job listing with optional skill filter */
    @GetMapping
    public String listJobs(@RequestParam(required = false) String location,
                           @RequestParam(required = false) String skillId,
                           Model model,
                           Authentication auth) {

        List<Job> jobs;
        if (location != null && !location.isBlank()) {
            jobs = jobMatchingService.getJobsByLocation(location.trim());
        } else {
            jobs = jobMatchingService.getAllActiveJobs();
        }

        // Pass user's applied job IDs (if logged in) so Apply button shows "Applied"
        if (auth != null) {
            User user = userService.findByEmail(auth.getName());
            boolean isSeeker = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_SEEKER"));
            if (isSeeker) {
                // Get titles of jobs the seeker has already applied to
                Set<String> appliedTitles = applicationService.getApplicationsForUser(user).stream()
                    .map(app -> app.getJobTitle())
                    .collect(Collectors.toSet());
                // Map those titles to job IDs from the current listing
                Set<Long> appliedIds = jobs.stream()
                    .filter(j -> appliedTitles.contains(j.getTitle()))
                    .map(Job::getJobId)
                    .collect(Collectors.toSet());
                model.addAttribute("appliedIds", appliedIds);
                model.addAttribute("isSeeker", true);
            }
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("skills", skillRepository.findAllByOrderBySkillNameAsc());
        model.addAttribute("location", location);
        return "jobs/list";
    }

    /** Job detail page */
    @GetMapping("/{jobId}")
    public String jobDetail(@PathVariable Long jobId, Model model, Authentication auth) {
        Job job = jobRepository.findByIdWithEmployer(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        model.addAttribute("job", job);

        if (auth != null) {
            User user = userService.findByEmail(auth.getName());
            boolean isSeeker = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_SEEKER"));
            model.addAttribute("isSeeker", isSeeker);
        }
        return "jobs/detail";
    }

    /** Skill-matched recommendations for logged-in seeker */
    @GetMapping("/recommendations")
    public String recommendations(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("scoredJobs", jobMatchingService.getMatchedJobs(user));
        model.addAttribute("userSkills", user.getSkills());
        return "jobs/recommendations";
    }

    /** Employer: show job posting form */
    @GetMapping("/post")
    public String showPostForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("allSkills", skillRepository.findAllByOrderBySkillNameAsc());
        return "jobs/post";
    }

    /** Employer: process job posting */
    @PostMapping("/post")
    public String postJob(@ModelAttribute Job job,
                          @RequestParam(required = false) List<Long> skillIds,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(auth.getName());
        Employer employer = employerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("Employer profile not found"));

        job.setEmployer(employer);
        job.setStatus("OPEN");

        if (skillIds != null) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(skillIds));
            job.setRequiredSkills(skills);
        }

        jobRepository.save(job);
        redirectAttributes.addFlashAttribute("successMsg", "Job posted successfully!");
        log.info("Job posted: {} by {}", job.getTitle(), user.getEmail());
        return "redirect:/dashboard";
    }

    /** Seeker: apply to a job */
    @PostMapping("/apply/{jobId}")
    public String applyToJob(@PathVariable Long jobId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(auth.getName());
        try {
            applicationService.apply(user, jobId);
            redirectAttributes.addFlashAttribute("successMsg", "Application submitted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/jobs/" + jobId;
    }
}
