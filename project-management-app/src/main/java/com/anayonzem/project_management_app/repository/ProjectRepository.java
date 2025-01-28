package com.anayonzem.project_management_app.repository;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findById(Long id);

    List<Project> findByTeamLead(User teamLead);

    List<Project> findAll();

}