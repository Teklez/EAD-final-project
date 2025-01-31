package com.anayonzem.project_management_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anayonzem.project_management_app.model.Task;
import com.anayonzem.project_management_app.model.Project;
import org.springframework.ui.Model;

import com.anayonzem.project_management_app.service.ChatGptApiService;
import com.anayonzem.project_management_app.service.TaskService;
import com.anayonzem.project_management_app.service.ProjectService;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ChatGptApiService chatGptApiService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/project/{id}/tasks")
    public String getTasks(@PathVariable("id") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("tasks", taskService.getTasksByProjectId(projectId));
        return "tasks";
    }

    @GetMapping("/project/{id}/tasks/add")
    public String addTask(@PathVariable("id") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "addTask";
    }

    @PostMapping("/project/{id}/tasks/add")
    public String addTask(@PathVariable("id") Long projectId, @RequestParam String prompt) {
        // task.setProject(projectId);

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
        return "redirect:/project/" + projectId + "/tasks";
    }

    @PostMapping("/tasks/update/priority/{taskId}")
    public String updatePriority(@PathVariable Long taskId, @RequestParam String priority,
            @RequestParam Long projectId) {
        Task task = taskService.getTaskById(taskId);
        task.setPriority(priority);
        taskService.saveTask(task);
        return "redirect:/project/" + projectId + "/tasks";
    }

    @PostMapping("/tasks/delete/{taskId}")
    public String deleteTask(@PathVariable Long taskId, @RequestParam Long projectId) {

        System.out.println("========================================================");
        System.out.println("taskId: " + taskId);
        System.out.println("projectId: " + projectId);
        taskService.deleteTask(taskId);
        return "redirect:/project/" + projectId + "/tasks";
    }
}
