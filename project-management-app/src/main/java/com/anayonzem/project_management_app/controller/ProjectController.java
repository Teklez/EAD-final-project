package com.anayonzem.project_management_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.repository.ProjectRepository;
import com.anayonzem.project_management_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/addProject")
    public String showAddProjectForm(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "addProject";
    }

    @PostMapping("/project/add")
    public String addProject(@ModelAttribute Project project) {
        projectRepository.save(project);
        return "redirect:/project";
    }

}
