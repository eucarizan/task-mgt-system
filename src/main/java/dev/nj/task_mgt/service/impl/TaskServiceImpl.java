package dev.nj.task_mgt.service.impl;

import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.TaskRepository;
import dev.nj.task_mgt.repository.UserRepository;
import dev.nj.task_mgt.service.TaskService;
import dev.nj.task_mgt.web.dto.NewTicketDto;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Task createTask(NewTicketDto dto, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Task task = new Task(dto.title(), dto.description(), user);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll(Sort.by(Sort.Order.desc("created")));
    }

    @Override
    public List<Task> getTasksByAuthor(String author) {
        return taskRepository.findByAuthorIgnoreCaseOrderByCreatedDesc(author);
    }
}
