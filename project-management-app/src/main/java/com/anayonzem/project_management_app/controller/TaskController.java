package com.anayonzem.project_management_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

    @GetMapping("/tasks")
    public String getTasks() {
        return "tasks";
    }

    @GetMapping("/tasks/add")
    public String addTask() {
        return "addTask";
    }

}
