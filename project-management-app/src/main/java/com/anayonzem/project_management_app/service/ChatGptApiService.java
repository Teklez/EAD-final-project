package com.anayonzem.project_management_app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.Task;

@Service
public class ChatGptApiService {
   
    private final ChatClient chatClient;

    public ChatGptApiService (ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Task getTaskObject(String prompt) {
        String context = "You are a very helpful assistant that will change a given random description of a task into a very good and nicely formatted json form of a task with name, description, deadline, and priority .";
        String contextualPromString = prompt + context;
        return chatClient.prompt(contextualPromString)
                .call() 
                .entity(Task.class); 
    }

    public Project getProjectObject(String prompt) {
        String context = "You are a very helpful assistant that will change a given random description of a project into a very good and nicely formatted json form of a project with name, description, deadline, and priority .";
        String contextualPromString = prompt + context;
        return chatClient.prompt(contextualPromString)
                .call() 
                .entity(Project.class); 
    }

}

