package com.anayonzem.project_management_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ProjectController {

    @GetMapping("/project")
    public String getProject() {
        return "project";
    }

}
