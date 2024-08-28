package dev.nj.task_mgt.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.config.WebSecurityConfig;
import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.UserRepository;
import dev.nj.task_mgt.service.TaskService;
import dev.nj.task_mgt.service.impl.UserDetailsServiceImpl;
import dev.nj.task_mgt.web.controller.TaskController;
import dev.nj.task_mgt.web.dto.NewTaskDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$..author").value("user@example.com"));

        verify(userRepository).findByEmail(any(String.class));
        verify(taskService).getTasks();
    }

    @Test
    public void createTask() throws Exception {
        String email = "create1@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        Task task = new Task("title slice 1", "description slice 1", user);
        NewTaskDto taskDto = new NewTaskDto("title slice 1", "description slice 1");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskService.createTask(any(NewTaskDto.class), any(UserDetails.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("create1@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..author").value("create1@test.com"));

        verify(userRepository).findByEmail(any(String.class));
        verify(taskService).createTask(any(NewTaskDto.class), any(UserDetails.class));
    }

    @Test
    public void createTaskNullTitleFail() throws Exception {
        String email = "create2@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        NewTaskDto taskDto = new NewTaskDto(null, "description fail 1");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("create2@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isBadRequest());

        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    public void createTaskBlankTitleFail() throws Exception {
        String email = "create3@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        NewTaskDto taskDto = new NewTaskDto("", "description fail 1");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("create3@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isBadRequest());

        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    public void createTaskNullDescriptionFail() throws Exception {
        String email = "create4@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        NewTaskDto taskDto = new NewTaskDto("title fail 1", null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("create4@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isBadRequest());

        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    public void createTaskBlankDescriptionFail() throws Exception {
        String email = "create5@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        NewTaskDto taskDto = new NewTaskDto("title fail 1", "");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("create5@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskDto)))
                .andExpect(status().isBadRequest());

        verify(userRepository).findByEmail(any(String.class));
    }

    // todo-06: get tasks sorted by creation

    // todo-07: get tasks by author


    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
