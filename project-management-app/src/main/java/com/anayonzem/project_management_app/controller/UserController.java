package com.anayonzem.project_management_app.controller;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;
import com.anayonzem.project_management_app.repository.ProjectRepository;
import com.anayonzem.project_management_app.repository.UserRepository;
import com.anayonzem.project_management_app.service.EmailService;
import com.anayonzem.project_management_app.service.ProjectService;

import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String root(Authentication authentication) {
        return "redirect:/dashboard";
    }

    @GetMapping("/index")
    public String index(Authentication authentication) {
        return "index";
    }

    @GetMapping("/dashboard")
    public String project(Authentication authentication, Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> userProjects = projectRepository.findByTeamLead(user);
        model.addAttribute("projects", userProjects);
        model.addAttribute("user", user);

        return "dashboard";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @Transactional
    @PostMapping("/signup")
    public String signUp(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            Model model) {
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Email already registered");
            return "signup";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("TEAM_LEAD");
        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/find")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(Map.of("exists", true, "name", user.getName()));
        } else {
            return ResponseEntity.ok(Map.of("exists", false));
        }
    }

    @GetMapping("/members")
    public String getMembersPage(@RequestParam Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        return "members";
    }

    @GetMapping("/members/add")
    public String showNewMemberForm(@RequestParam Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        return "addMember";
    }

    @PostMapping("/projects/{projectId}/addMember")
    @Transactional
    public String addTeamMemberToProject(@PathVariable Long projectId, @RequestParam String memberEmail) {
        // Find the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Find the user
        User user = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add the user to the project
        project.getTeamMembers().add(user);
        projectRepository.save(project);
        String invitationLink = "http://localhost:8080/project/" + project.getId();
        emailService.sendProjectMemebrNotification(memberEmail, user.getName(), project.getName(), invitationLink);
        return "redirect:/project/" + projectId;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/email/invite")
    public String sendProjectInvitationEmail(@RequestParam String memberEmail) {
        emailService.sendPlatformInvitation(memberEmail, "Yoni", "https://localhost:8080/signup");
        return "redirect:/project";
    }

}