package com.anayonzem.project_management_app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;
import com.anayonzem.project_management_app.repository.ProjectRepository;
import com.anayonzem.project_management_app.repository.UserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/project")
    public String getProject() {
        return "project";
    }

    @GetMapping("/project/{id}")
    public String viewProject(@PathVariable Long id, Model model, Principal principal) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);

        return "projectDetails";
    }

    @GetMapping("/addProject")
    public String showAddProjectForm(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("users", userRepository.findAll());
        return "addProject";
    }

    @PostMapping("/project/add")
    public String addProject(@ModelAttribute Project project) {

        projectRepository.save(project);
        return "redirect:/project";
    }

}
