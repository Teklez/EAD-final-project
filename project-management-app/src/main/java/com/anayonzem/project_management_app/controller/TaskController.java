package com.anayonzem.project_management_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anayonzem.project_management_app.model.Task;
import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;
import org.springframework.ui.Model;

import com.anayonzem.project_management_app.service.ChatGptApiService;
import com.anayonzem.project_management_app.service.TaskService;
import com.anayonzem.project_management_app.service.ProjectService;
import com.anayonzem.project_management_app.repository.UserRepository;
import com.anayonzem.project_management_app.service.EmailService;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ChatGptApiService chatGptApiService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/project/{id}/tasks")
    public String getTasks(@PathVariable("id") Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<User> members = project.getTeamMembers();
        model.addAttribute("members", members);
        model.addAttribute("projectId", projectId);
        model.addAttribute("tasks", taskService.getTasksByProjectId(projectId));
        return "tasks";
    }

    @GetMapping("/project/{id}/tasks/add/chat")
    public String showChatTaskForm(@PathVariable("id") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "chatTask";
    }

    @GetMapping("/project/{projectId}/tasks/{taskId}")
    public String viewTaskDetails(@PathVariable Long taskId, @PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<User> members = project.getTeamMembers();
        model.addAttribute("members", members);
        Task task = taskService.getTaskById(taskId);
        model.addAttribute("task", task);
        model.addAttribute("projectId", projectId);
        return "taskDetail";
    }

    @GetMapping("/project/{id}/tasks/add")
    public String addTask(@PathVariable("id") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "addTask";
    }

    @PostMapping("/project/{id}/tasks/add")
    public String addManualTask(@PathVariable("id") Long projectId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setProject(project);
        taskService.saveTask(task);
        return "redirect:/project/" + projectId + "/tasks";
    }

    @PostMapping("/project/{id}/tasks/add/chat")
    public String addChatTask(@PathVariable("id") Long projectId, @RequestParam String prompt) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Task task = chatGptApiService.getTaskObject(prompt);
        task.setProject(project);
        taskService.saveTask(task);
        return "redirect:/project/" + projectId + "/tasks";
    }

    @PostMapping("/tasks/update/status/{taskId}")
    public String updateStatus(@PathVariable Long taskId, @RequestParam String status, @RequestParam Long projectId) {
        Task task = taskService.getTaskById(taskId);
        task.setStatus(status);
        taskService.saveTask(task);
        return "redirect:/project/" + projectId + "/tasks/" + taskId;
    }

    @PostMapping("/tasks/update/priority/{taskId}")
    public String updatePriority(@PathVariable Long taskId, @RequestParam String priority,
            @RequestParam Long projectId) {
        Task task = taskService.getTaskById(taskId);
        task.setPriority(priority);
        taskService.saveTask(task);
        return "redirect:/project/" + projectId + "/tasks/" + taskId;
    }

    @PostMapping("/tasks/delete/{taskId}")
    public String deleteTask(@PathVariable Long taskId, @RequestParam Long projectId) {

        System.out.println("========================================================");
        System.out.println("taskId: " + taskId);
        System.out.println("projectId: " + projectId);
        taskService.deleteTask(taskId);
        return "redirect:/project/" + projectId + "/tasks";
    }

    @PostMapping("/tasks/assign/{taskId}")
    public String assignTask(@PathVariable Long taskId, @RequestParam String assignee, @RequestParam Long projectId) {
        Task task = taskService.getTaskById(taskId);
        User user = userRepository.findByEmail(assignee)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        task.getMembers().add(user);
        taskService.saveTask(task);
        taskService.saveTask(task);
        String invitationLink = "http://localhost:8080/project/" + projectId + "/tasks/" + taskId;
        emailService.sendTaskAssignmentNotification(assignee, user.getName(), task.getTitle(), project.getName(), "",
                invitationLink);
        return "redirect:/project/" + projectId + "/tasks/" + taskId;
    }
}
