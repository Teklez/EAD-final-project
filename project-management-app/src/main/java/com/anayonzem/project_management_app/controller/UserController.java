package com.anayonzem.project_management_app.controller;

import com.anayonzem.project_management_app.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private List<User> users = new ArrayList<>(); 

    // Root URL Handler
    @GetMapping("/")
    public String root(HttpSession session) {
        // Check if user is authenticated (simulated using session)
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/dashboard"; 
        } else {
            return "redirect:/signup"; 
        }
    }

    // Sign-Up Page
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Handle Sign-Up
    @PostMapping("/signup")
    public String signUp(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            HttpSession session, Model model) {
        // Check if user already exists
        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            model.addAttribute("error", "Email already registered");
            return "signup";
        }

        // Create new user (default role is TEAM_LEAD)
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); 
        user.setRole("TEAM_LEAD"); 
        users.add(user);

        // Automatically log in the user after sign-up
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    // Login Page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Handle Login
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        // Find user by email and password
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            session.setAttribute("user", user); 
            return "redirect:/dashboard"; 
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // Dashboard (for logged-in users)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; 
        }

        model.addAttribute("message", "Welcome to the Dashboard, " + user.getName() + "!");
        return "dashboard";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user"); 
        return "redirect:/login";
    }
}