package com.anayonzem.project_management_app.repository;

import com.anayonzem.project_management_app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}