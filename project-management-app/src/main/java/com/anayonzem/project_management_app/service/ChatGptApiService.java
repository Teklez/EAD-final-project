package com.anayonzem.project_management_app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.anayonzem.project_management_app.model.Project;
import com.anayonzem.project_management_app.model.Task;

import java.util.concurrent.TimeUnit;

@Service
public class ChatGptApiService {

    private final ChatClient chatClient;

    public ChatGptApiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Converts a task description into a Task object.
     */
    public Task getTaskObject(String prompt) {
        String context = "You are a very helpful assistant that transforms a given task description into JSON. The JSON must have fields: 'title' (string), 'description' (string), 'deadline' (ISO 8601 format), 'createdAt' (ISO 8601 format),'status' ('To Do','In Progress', 'Completed') and 'priority' (High, Medium, or Low).";
        String contextualPrompt = context + " " + prompt;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return chatClient.prompt(contextualPrompt)
                        .call()
                        .entity(Task.class);
            } catch (Exception e) {
                logError("Error during task processing. Attempt " + attempt, e);

                // Retry only if transient issue
                if (attempt < 3) {
                    backoff(attempt); // Backoff before retrying
                } else {
                    throw new RuntimeException("Failed to process task after multiple attempts: " + e.getMessage(), e);
                }
            }
        }
        return null; // Should never reach here
    }

    /**
     * Converts a project description into a Project object.
     */
    public Project getProjectObject(String prompt) {
        String context = "You are a very helpful assistant that transforms a given project description into JSON. The JSON must have fields: 'name', 'description', 'deadline', and 'priority' and make the description longer.";
        String contextualPrompt = context + " " + prompt;

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Project project = chatClient.prompt(contextualPrompt)
                        .call()
                        .entity(Project.class);

                // Validate project response
                if (project.getName() == null || project.getDescription() == null) {
                    throw new RuntimeException("Invalid project data received from OpenAI");
                }

                return project; // Success
            } catch (Exception e) {
                logError("Error during project processing. Attempt " + attempt, e);

                // Retry logic with exponential backoff
                if (attempt < 3) {
                    backoff(attempt);
                } else {
                    throw new RuntimeException("Failed to process project after multiple attempts: " + e.getMessage(),
                            e);
                }
            }
        }
        return null; // Should never reach here
    }

    /**
     * Logs error details for debugging purposes.
     */
    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }

    /**
     * Implements exponential backoff for retries.
     */
    private void backoff(int attempt) {
        try {
            long delay = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }
}
