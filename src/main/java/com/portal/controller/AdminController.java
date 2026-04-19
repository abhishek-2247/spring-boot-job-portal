package com.portal.controller;

import com.portal.repository.ApplicationRepository;
import com.portal.repository.JobRepository;
import com.portal.repository.UserRepository;
import com.portal.service.AdminService;
import com.portal.service.JobAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminController — admin panel for platform management, stats, and moderation.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobAlertService jobAlertService;

    /** Admin dashboard with platform stats */
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("jobs",  jobRepository.findAllWithEmployer());
        model.addAttribute("applications", applicationRepository.findAllWithDetails());
        return "admin/index";
    }

    /** Close a job listing */
    @PostMapping("/jobs/{id}/close")
    public String closeJob(@PathVariable Long id, RedirectAttributes ra) {
        adminService.closeJob(id);
        ra.addFlashAttribute("successMsg", "Job #" + id + " has been closed.");
        return "redirect:/admin";
    }

    /** Delete a user */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        adminService.deleteUser(id);
        ra.addFlashAttribute("successMsg", "User #" + id + " has been deleted.");
        return "redirect:/admin";
    }

    /** Update application status */
    @PostMapping("/applications/{id}/status")
    public String updateAppStatus(@PathVariable Long id,
                                  @RequestParam String status,
                                  RedirectAttributes ra) {
        adminService.updateApplicationStatus(id, status);
        ra.addFlashAttribute("successMsg", "Application #" + id + " status → " + status);
        return "redirect:/admin";
    }

    /** Manually trigger job alerts (for demo/viva) */
    @PostMapping("/trigger-alerts")
    public String triggerAlerts(RedirectAttributes ra) {
        jobAlertService.triggerAlertsManually();
        ra.addFlashAttribute("successMsg",
            "Job alerts triggered! Check console logs for JobAlert-N thread output.");
        return "redirect:/admin";
    }
}
