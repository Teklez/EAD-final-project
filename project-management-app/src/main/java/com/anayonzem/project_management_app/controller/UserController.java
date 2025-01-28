package com.anayonzem.project_management_app.controller;

import com.anayonzem.project_management_app.model.User;
import com.anayonzem.project_management_app.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String root(Authentication authentication) {
        return "redirect:/dashboard";
    }

    @GetMapping("/index")
    public String index(Authentication authentication) {
        return "index";
    }

    @GetMapping("/dashboard")
    public String project(Authentication authentication) {
        return "dashboard";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @Transactional
    @PostMapping("/signup")
    public String signUp(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            Model model) {
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Email already registered");
            return "signup";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("TEAM_LEAD");
        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

}