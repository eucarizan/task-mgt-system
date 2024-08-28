package dev.nj.task_mgt.service;

import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.web.dto.NewTaskDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TaskService {
    Task createTask(NewTaskDto dto, UserDetails userDetails);

    List<Task> getTasks();

    List<Task> getTasksByAuthor(String author);
}
