package dev.nj.task_mgt.web.controller;

import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.service.TaskService;
import dev.nj.task_mgt.web.dto.NewTicketDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@RequestParam Optional<String> author) {
        return author.map(s ->
                        ResponseEntity.ok(taskService.getTasksByAuthor(s)))
                .orElseGet(() -> ResponseEntity.ok(taskService.getTasks()));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid NewTicketDto dto,
                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.createTask(dto, user));
    }
}
