package com.example.taskreminder.model;

import java.util.Objects;

/**
 * Task model representing a reminder task.
 */
public class Task {
    private Long id;
    private String title;
    private String description;
    private Long dueTimestamp; // epoch milliseconds
    private String email; // email to notify
    private String status; // PENDING or COMPLETED
    private Long createdAt; // epoch milliseconds
    private Long completedAt; // epoch milliseconds, null if not completed

    public Task() {
    }

    public Task(Long id, String title, String description, Long dueTimestamp, 
                String email, String status, Long createdAt, Long completedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueTimestamp = dueTimestamp;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDueTimestamp() {
        return dueTimestamp;
    }

    public void setDueTimestamp(Long dueTimestamp) {
        this.dueTimestamp = dueTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Long completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueTimestamp=" + dueTimestamp +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }
}

