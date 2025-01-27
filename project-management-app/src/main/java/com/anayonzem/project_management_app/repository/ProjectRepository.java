package com.anayonzem.project_management_app.repository;

import com.anayonzem.project_management_app.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}