package com.portal.controller;

import com.portal.dto.RegistrationForm;
import com.portal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController — handles login and registration pages.
 * Login form submission is processed by Spring Security (not here).
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /** Public home page redirect */
    @GetMapping("/")
    public String home() {
        return "redirect:/jobs";
    }

    /** Display login page */
    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model) {
        if (error != null)   model.addAttribute("errorMsg",  "Invalid email or password.");
        if (logout != null)  model.addAttribute("infoMsg",   "You have been logged out.");
        if (expired != null) model.addAttribute("infoMsg",   "Your session has expired.");
        return "login";
    }

    /** Display registration form */
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    /** Process registration form submission */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute RegistrationForm registrationForm,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (!registrationForm.passwordsMatch()) {
            model.addAttribute("errorMsg", "Passwords do not match.");
            return "register";
        }
        try {
            userService.registerUser(registrationForm);
            redirectAttributes.addFlashAttribute("successMsg",
                "Account created successfully! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "register";
        }
    }
}
