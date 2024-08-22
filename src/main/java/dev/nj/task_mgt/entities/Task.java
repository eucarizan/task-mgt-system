package dev.nj.task_mgt.entities;

import dev.nj.task_mgt.dictionaries.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Status status;

    private User author;

    public Task() {}

    public Task(String title, String description, User author) {
        this.title = title;
        this.description = description;
        this.status = Status.CREATED;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public User getAuthor() {
        return author;
    }
}
