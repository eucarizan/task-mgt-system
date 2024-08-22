package dev.nj.task_mgt.service;

import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.web.dto.NewTicketDto;

import java.util.List;

public interface TaskService {
    Task createTask(NewTicketDto dto, User user);
    List<Task> getTasks();
    List<Task> getTasksByAuthor(String author);
}
