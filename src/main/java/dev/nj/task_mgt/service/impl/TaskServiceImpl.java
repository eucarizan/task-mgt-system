package dev.nj.task_mgt.service.impl;

import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.TaskRepository;
import dev.nj.task_mgt.service.TaskService;
import dev.nj.task_mgt.web.dto.NewTicketDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(NewTicketDto dto, User user) {
        Task task = new Task(dto.title(), dto.description(), user);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAllOrderByCreatedDesc();
    }

    @Override
    public List<Task> getTasksByAuthor(String author) {
        return taskRepository.findByAuthorIgnoreCase(author);
    }
}
