package com.anayonzem.project_management_app;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.anayonzem.project_management_app.model.Task;
import com.anayonzem.project_management_app.service.ChatGptApiService;

@SpringBootApplication
public class ProjectManagementAppApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(ProjectManagementAppApplication.class, args);

    }

    @Bean
    ChatClient openAIChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

}
