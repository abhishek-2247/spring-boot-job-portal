package com.portal.dto;

import com.portal.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Form-backing object for the /register page.
 * Validated by Spring Validation (@Valid in controller).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationForm {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    private String location;

    @NotNull(message = "Please select a role")
    private Role role;

    // For EMPLOYER registrations
    private String companyName;
    private String industry;
    private String website;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
