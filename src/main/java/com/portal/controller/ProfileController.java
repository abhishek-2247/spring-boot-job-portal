package com.portal.controller;

import com.portal.model.Skill;
import com.portal.model.User;
import com.portal.repository.SkillRepository;
import com.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ProfileController — seeker's profile and skill tag management.
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;
    private final SkillRepository skillRepository;

    /** Display profile page with skill management */
    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());
        List<Skill> allSkills = skillRepository.findAllByOrderBySkillNameAsc();

        model.addAttribute("user", user);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("userSkillIds",
            user.getSkills().stream().map(Skill::getSkillId).toList());

        return "profile";
    }

    /** Update seeker's skill tags */
    @PostMapping("/skills")
    public String updateSkills(@RequestParam(required = false) List<Long> skillIds,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(auth.getName());

        Set<Skill> selectedSkills = new HashSet<>();
        if (skillIds != null && !skillIds.isEmpty()) {
            selectedSkills.addAll(skillRepository.findAllById(skillIds));
        }

        userService.updateSkills(user, selectedSkills);
        redirectAttributes.addFlashAttribute("successMsg",
            "Skills updated! You now have " + selectedSkills.size() + " skill(s).");
        log.info("Skills updated for {}: {} skills", user.getEmail(), selectedSkills.size());
        return "redirect:/profile";
    }
}
