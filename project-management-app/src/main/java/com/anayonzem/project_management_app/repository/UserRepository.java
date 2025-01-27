package com.anayonzem.project_management_app.repository;

import com.anayonzem.project_management_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndRole(String email, String role); // Find team lead by email and role
}