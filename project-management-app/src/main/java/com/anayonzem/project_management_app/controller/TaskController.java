package com.anayonzem.project_management_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
public class TaskController {

    @GetMapping("/project/{id}/tasks")
    public String getTasks(@PathVariable("id") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "tasks";
    }

    @GetMapping("/project/{id}/tasks/add")
    public String addTask() {
        return "addTask";
    }

}
