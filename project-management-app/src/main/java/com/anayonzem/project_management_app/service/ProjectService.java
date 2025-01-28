package com.anayonzem.project_management_app.service;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;
import com.anayonzem.project_management_app.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // Fetch a project by its ID
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    // Fetch all projects
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // Fetch projects by team lead
    public List<Project> findByTeamLead(User teamLead) {
        return projectRepository.findByTeamLead(teamLead);
    }

    // Add a new project
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    // Update an existing project
    public Project updateProject(Long id, Project updatedProject) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isPresent()) {
            Project project = existingProject.get();
            project.setName(updatedProject.getName());
            project.setDescription(updatedProject.getDescription());
            project.setTeamLead(updatedProject.getTeamLead());
            project.setTasks(updatedProject.getTasks());
            return projectRepository.save(project);
        }
        throw new RuntimeException("Project not found with ID: " + id);
    }

    // Delete a project
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }
}
