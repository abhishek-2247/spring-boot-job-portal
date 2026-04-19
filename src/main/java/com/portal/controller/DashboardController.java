package com.portal.controller;

import com.portal.model.Role;
import com.portal.model.User;
import com.portal.repository.EmployerRepository;
import com.portal.service.AdminService;
import com.portal.service.ApplicationService;
import com.portal.service.JobMatchingService;
import com.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController — routes users to their role-appropriate dashboard view.
 * A single /dashboard URL renders different content based on role via Thymeleaf sec:authorize.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ApplicationService applicationService;
    private final JobMatchingService jobMatchingService;
    private final EmployerRepository employerRepository;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("user", user);

        boolean isSeeker   = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SEEKER"));
        boolean isEmployer = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYER"));
        boolean isAdmin    = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isSeeker) {
            model.addAttribute("applications", applicationService.getApplicationsForUser(user));
            model.addAttribute("recommendations", jobMatchingService.getMatchedJobs(user));
        }
        if (isEmployer) {
            employerRepository.findByUserWithJobs(user).ifPresent(emp -> {
                model.addAttribute("employer", emp);
                model.addAttribute("postedJobs", emp.getJobs());
            });
        }
        if (isAdmin) {
            model.addAttribute("stats", adminService.getDashboardStats());
        }

        return "dashboard";
    }
}
