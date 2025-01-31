    package com.anayonzem.project_management_app.model;

    import jakarta.persistence.*;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    public class Project {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        @Lob
        private String description;

        @ManyToOne
        @JoinColumn(name = "team_lead_id")
        private User teamLead;

        @OneToMany(mappedBy = "project")
        private List<Task> tasks;

        @ManyToMany
        @JoinTable(name = "project_team_members", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
        private List<User> teamMembers = new ArrayList<>();

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public User getTeamLead() {
            return teamLead;
        }

        public void setTeamLead(User teamLead) {
            this.teamLead = teamLead;
        }

        public List<User> getTeamMembers() {
            return teamMembers;
        }

        public void setTeamMembers(List<User> teamMembers) {
            this.teamMembers = teamMembers;
        }

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }
    }