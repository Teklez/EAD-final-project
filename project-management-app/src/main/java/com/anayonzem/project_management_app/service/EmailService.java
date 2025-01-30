package com.anayonzem.project_management_app.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    private static final String PROJECT_PATH = "src/main/resources/templates/project_invitation.html";
    private static final String TASK_PATH = "src/main/resources/templates/task_reminder.html";

    public void sendProjectInvitation(String recipientEmail, String name, String projectName, String invitationLink, String companyName) {
        String subject = "You're Invited to Join the Project: " + projectName;
        String emailContent = loadHtmlTemplate(name, projectName, invitationLink, companyName);
        sendEmail(recipientEmail, subject, emailContent);
    }

    public void sendTaskAssignmentNotification(String recipientEmail, String name, String taskName, String projectName, String companyName, String taskLink) {
        String subject = "New Task Assigned: " + taskName;
        String contentText = loadHtmlTemplate_task(name, taskName, projectName, companyName, taskLink);
        sendEmail(recipientEmail, subject, contentText);
    }

    private void sendEmail(String recipientEmail, String subject, String htmlContent ) {
        System.out.println("Sending email to: " + recipientEmail);
        Email from = new Email(fromEmail);
        Email to = new Email(recipientEmail);
        Content content = new Content("text/html", htmlContent); 
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);
        // mail.
        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);
        // mail.addContent(new Content("text/plain", "This is a fallback text email."));
        // mail.addContent(new Content("text/html", htmlContent));

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            System.out.println("Email Sent! Status Code: " + response.getStatusCode());
        } catch (IOException ex) {
            System.err.println("Error sending email: " + ex.getMessage());
        }
    }

    private String loadHtmlTemplate(String name, String projectName, String invitationLink, String companyName) {
        try {
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(PROJECT_PATH)));
            return htmlTemplate
                    .replace("{{NAME}}", name)
                    .replace("{{PROJECT_NAME}}", projectName)
                    .replace("{{INVITATION_LINK}}", invitationLink)
                    .replace("{{COMPANY_NAME}}", companyName);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error loading email template.";
        }
    }

    private String loadHtmlTemplate_task(String name, String taskName, String projectName, String companyName, String taskLink) {
        try {
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(TASK_PATH)));
            return htmlTemplate
                    .replace("{{NAME}}", name)
                    .replace("{{TASK_NAME}}", taskName)
                    .replace("{{PROJECT_LINK}}", projectName)
                    .replace("{{COMPANY_NAME}}", companyName)
                    .replace("{{TASK_LINK}}", taskLink);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error loading email template.";
        }
    }
}
