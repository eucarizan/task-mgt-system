package dev.nj.task_mgt.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nj.task_mgt.dictionaries.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Status status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime created;

    private User author;

    public Task() {}

    public Task(String title, String description, User author) {
        this.title = title;
        this.description = description;
        this.status = Status.CREATED;
        this.created = LocalDateTime.now();
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

    public LocalDateTime getCreated() {
        return created;
    }

    public String getAuthor() {
        return author.getEmail();
    }
}
