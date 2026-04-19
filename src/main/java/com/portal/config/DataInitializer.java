package com.portal.config;

import com.portal.model.*;
import com.portal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Seeds the database with demo data on startup (H2 dev mode).
 * Implements CommandLineRunner — runs after the application context is ready.
 *
 * Creates:
 *  - 20 skills across 4 categories
 *  - 1 Admin user
 *  - 2 Employers with company profiles + 4 job postings
 *  - 2 Seekers with skill sets and sample applications
 */
// @Component // Disabled so it doesn't auto-seed data on MySQL
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Seeding demo data...");

        // === SKILLS ===
        Skill java    = saveSkill("Java",         "Backend");
        Skill spring  = saveSkill("Spring Boot",  "Backend");
        Skill python  = saveSkill("Python",       "Backend");
        Skill django  = saveSkill("Django",       "Backend");
        Skill js      = saveSkill("JavaScript",   "Frontend");
        Skill react   = saveSkill("React",        "Frontend");
        Skill html    = saveSkill("HTML/CSS",     "Frontend");
        Skill mysql   = saveSkill("MySQL",        "Database");
        Skill mongodb = saveSkill("MongoDB",      "Database");
        Skill redis   = saveSkill("Redis",        "Database");
        Skill docker  = saveSkill("Docker",       "DevOps");
        Skill k8s     = saveSkill("Kubernetes",   "DevOps");
        Skill git     = saveSkill("Git",          "DevOps");
        Skill aws     = saveSkill("AWS",          "Cloud");
        Skill ml      = saveSkill("Machine Learning", "AI/ML");
        Skill sql     = saveSkill("SQL",          "Database");
        Skill node    = saveSkill("Node.js",      "Backend");
        Skill ts      = saveSkill("TypeScript",   "Frontend");
        Skill linux   = saveSkill("Linux",        "DevOps");
        Skill rest    = saveSkill("REST APIs",    "Backend");

        // === ADMIN ===
        if (!userRepository.existsByEmail("admin@portal.com")) {
            userRepository.save(User.builder()
                .name("System Admin")
                .email("admin@portal.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .location("Remote")
                .build());
        }

        // === EMPLOYER 1 — TechNova Solutions ===
        User empUser1 = saveEmployerUser("hr@technova.com", "TechNova HR", "Bangalore");
        Employer employer1 = saveEmployer(empUser1, "TechNova Solutions", "Software", "https://technova.example.com");

        // Job 1 — Full Stack Developer
        Job job1 = saveJob("Full Stack Developer",
            "Build and maintain web applications using Java Spring Boot and React. Work in an agile team delivering microservices solutions.",
            "Bangalore", "₹8L – ₹15L", employer1,
            java, spring, react, mysql, git);

        // Job 2 — DevOps Engineer
        Job job2 = saveJob("DevOps Engineer",
            "Manage CI/CD pipelines, containerization with Docker & Kubernetes, and cloud infrastructure on AWS.",
            "Remote", "₹10L – ₹18L", employer1,
            docker, k8s, aws, linux, git);

        // === EMPLOYER 2 — DataPulse Analytics ===
        User empUser2 = saveEmployerUser("talent@datapulse.com", "DataPulse Talent", "Hyderabad");
        Employer employer2 = saveEmployer(empUser2, "DataPulse Analytics", "Data Science", "https://datapulse.example.com");

        // Job 3 — Python ML Engineer
        Job job3 = saveJob("Python ML Engineer",
            "Develop machine learning models using Python and deploy them as REST APIs. Experience with pandas, sklearn required.",
            "Hyderabad", "₹12L – ₹22L", employer2,
            python, ml, sql, rest, docker);

        // Job 4 — Frontend Developer
        Job job4 = saveJob("Frontend Developer",
            "Create stunning UI components with React and TypeScript. Collaborate with designers to implement pixel-perfect interfaces.",
            "Pune", "₹6L – ₹12L", employer2,
            react, ts, html, js, git);

        // === SEEKER 1 — Arjun Sharma (Java dev) ===
        User seeker1 = saveSeeker("arjun@seekers.com", "Arjun Sharma", "Bangalore",
            java, spring, mysql, git, rest, docker);

        // === SEEKER 2 — Priya Nair (Frontend dev) ===
        User seeker2 = saveSeeker("priya@seekers.com", "Priya Nair", "Pune",
            react, js, html, ts, git);

        // === Sample Applications ===
        applicationRepository.save(Application.builder()
            .user(seeker1).job(job1).status("REVIEWED").build());
        applicationRepository.save(Application.builder()
            .user(seeker1).job(job3).status("PENDING").build());
        applicationRepository.save(Application.builder()
            .user(seeker2).job(job4).status("HIRED").build());

        log.info("""
            ╔══════════════════════════════════════════╗
            ║  Demo Data Seeded Successfully!           ║
            ╠══════════════════════════════════════════╣
            ║  Admin:    admin@portal.com / admin123    ║
            ║  Seeker 1: arjun@seekers.com / pass123   ║
            ║  Seeker 2: priya@seekers.com / pass123   ║
            ║  Employer: hr@technova.com / pass123     ║
            ╚══════════════════════════════════════════╝
            """);
    }

    private Skill saveSkill(String name, String category) {
        return skillRepository.findBySkillNameIgnoreCase(name)
            .orElseGet(() -> skillRepository.save(
                Skill.builder().skillName(name).category(category).build()));
    }

    private User saveEmployerUser(String email, String name, String location) {
        return userRepository.findByEmail(email).orElseGet(() ->
            userRepository.save(User.builder()
                .name(name).email(email)
                .passwordHash(passwordEncoder.encode("pass123"))
                .role(Role.EMPLOYER).location(location)
                .build()));
    }

    private Employer saveEmployer(User user, String company, String industry, String website) {
        return employerRepository.findByUser(user).orElseGet(() ->
            employerRepository.save(Employer.builder()
                .user(user).companyName(company).industry(industry).website(website)
                .build()));
    }

    private Job saveJob(String title, String description, String location,
                        String salary, Employer employer, Skill... skills) {
        // Save job without skills first, then add skills via mutable HashSet
        Job job = Job.builder()
            .title(title).description(description)
            .location(location).salaryRange(salary)
            .status("OPEN").employer(employer)
            .build();
        job = jobRepository.save(job);
        // Now safely add managed skill references
        java.util.Set<Skill> skillSet = new java.util.HashSet<>(java.util.Arrays.asList(skills));
        job.setRequiredSkills(skillSet);
        return jobRepository.save(job);
    }

    private User saveSeeker(String email, String name, String location, Skill... skills) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            // Save user without skills first, then link
            User u = User.builder()
                .name(name).email(email)
                .passwordHash(passwordEncoder.encode("pass123"))
                .role(Role.SEEKER).location(location)
                .build();
            u = userRepository.save(u);
            // Add managed skill references via mutable HashSet
            u.setSkills(new java.util.HashSet<>(java.util.Arrays.asList(skills)));
            return userRepository.save(u);
        });
    }
}
