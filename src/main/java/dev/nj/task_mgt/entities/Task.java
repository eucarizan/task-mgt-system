package dev.nj.task_mgt.entities;

import dev.nj.task_mgt.dictionaries.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;

    private String author;

    public Task() {}

    public Task(String title, String description, User author) {
        this.title = title;
        this.description = description;
        this.status = Status.CREATED;
        this.created = LocalDateTime.now();
        this.user = author;
        this.user.addTask(this);
        this.author = user.getEmail();
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

    public User getUser() {
        return user;
    }

    public String getAuthor() {
        return author;
    }
}
