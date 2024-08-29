package dev.nj.task_mgt.entities;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"USER\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @OneToMany(mappedBy = "user")
    private Set<Task> tasks;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.tasks = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }
}
