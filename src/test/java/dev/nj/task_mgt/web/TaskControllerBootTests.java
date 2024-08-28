package dev.nj.task_mgt.web;

import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.config.WebSecurityConfig;
import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.UserRepository;
import dev.nj.task_mgt.service.TaskService;
import dev.nj.task_mgt.service.impl.UserDetailsServiceImpl;
import dev.nj.task_mgt.web.controller.TaskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ContextConfiguration(classes = {
        TaskManagementSystemApplication.class,
        WebSecurityConfig.class,
        UserDetailsServiceImpl.class
})
public class TaskControllerBootTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskService taskService;

    @Test
    public void getTasksForbidden() throws Exception {
        when(userRepository.findByEmail("address@domain.net")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("address@domain.net", "password")))
                .andExpect(status().isUnauthorized());

        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    public void getTasks() throws Exception {
        String email = "user@example.com";
        User user = new User(email, passwordEncoder.encode("password"));
        Task task = new Task("new task", "a task for anyone", user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskService.getTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("user@example.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..title").value("new task"))
                .andExpect(jsonPath("$..description").value("a task for anyone"))
                .andExpect(jsonPath("$..status").value("CREATED"))
                .andExpect(jsonPath("$..author").value("user@example.com"))
        ;

        verify(userRepository).findByEmail(any(String.class));
        verify(taskService).getTasks();
    }

    // TODO-01: get tasks sorted by creation
    // TODO-02: get tasks by author
    // TODO-03: post endpoint valid request body
    // TODO-04: post endpoint invalid request body
}
