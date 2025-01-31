package com.anayonzem.project_management_app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;
import com.anayonzem.project_management_app.repository.ProjectRepository;
import com.anayonzem.project_management_app.repository.UserRepository;
import com.anayonzem.project_management_app.service.ChatGptApiService;
import com.anayonzem.project_management_app.service.EmailService;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGptApiService chatGptApiService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/project")
    public String getProject(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Project> userProjects = projectRepository.findByTeamLead(user);
        model.addAttribute("projects", userProjects);
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

    @GetMapping("/chatProject")
    public String showChatProjectForm() {
        return "chatProject";
    }

    @PostMapping("/chatProject")
    public String createProjectFromChat(@RequestParam String prompt, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Project project = chatGptApiService.getProjectObject(prompt);
        project.setTeamLead(user);
        projectRepository.save(project);
        return "redirect:/project";
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
    public String addProject(@ModelAttribute Project project, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        project.setTeamLead(user);
        project.getTeamMembers().add(user);
        projectRepository.save(project);
        return "redirect:/project";
    }

    @PostMapping("/project/{id}/delete")
    public String deleteProject(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLead().equals(user)) {
            System.out.println(
                    "Userrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr: "
                            + user);
            throw new RuntimeException("You are not authorized to delete this project");
        }

        projectRepository.deleteById(id);
        return "redirect:/project";
    }

}
