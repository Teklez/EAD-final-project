package com.anayonzem.project_management_app;

import com.anayonzem.project_management_app.service.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

@SpringBootApplication
public class ProjectManagementAppApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProjectManagementAppApplication.class, args);
        // Get the EmailService bean from the context
        // EmailService emailService = context.getBean(EmailService.class);
        // Call email sending methods
        // emailService.sendProjectInvitation("amanueltsehay11@gmail.com", "Amanuel",
        // "Awesome Project", "http://localhost:8080/signup", "Example Company");
        // emailService.sendTaskAssignmentNotification("yonatanalebachew7@gmail.com",
        // "Yonathan", "Awesome Task", "Great Project", "Example Company",
        // "http://localhost:8080/signup");
    }

    @Bean
    ChatClient openAIChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}
