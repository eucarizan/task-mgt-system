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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Test
    public void getTaskSortedByCreation() throws Exception {
        String email = "get3@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        List<Task> tasks = List.of(
                new Task("task list 1", "description list 1", user),
                new Task("task list 2", "description list 2", user),
                new Task("task list 3", "description list 3", user)
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(taskService.getTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("get3@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].author").value("get3@test.com"))
                .andExpect(jsonPath("$[0].status").value("CREATED"));

        verify(userRepository).findByEmail(any(String.class));
        verify(taskService).getTasks();
    }

    @Test
    public void getTaskSortedByCreationUserDifferentThanAuthor() throws Exception {
        String email = "get4@test.com";
        User user = new User(email, passwordEncoder.encode("password"));
        User authUser = new User("diff@test.com", passwordEncoder.encode("password"));
        List<Task> tasks = List.of(
                new Task("task list 1", "description list 1", user),
                new Task("task list 2", "description list 2", user),
                new Task("task list 3", "description list 3", user)
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(authUser));
        when(taskService.getTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("diff@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].author").value("get4@test.com"))
                .andExpect(jsonPath("$[0].status").value("CREATED"));

        verify(userRepository).findByEmail(any(String.class));
        verify(taskService).getTasks();
    }

    @Test
    public void getTasksByAuthor() throws Exception {
        User user1 = new User("get5@test.com", passwordEncoder.encode("password"));
        User user2 = new User("get6@test.com", passwordEncoder.encode("password"));
        Task task1 = new Task("task list author 1", "description list author 1", user1);
        Task task2 = new Task("task list author 2", "description list author 2", user2);
        Task task3 = new Task("task list author 3", "description list author 3", user1);

        when(userRepository.findByEmail("get5@test.com")).thenReturn(Optional.of(user1));
        when(taskService.getTasksByAuthor("get5@test.com")).thenReturn(List.of(task1, task3));

        mockMvc.perform(get("/api/tasks?author=get5@test.com")
                        .with(httpBasic("get5@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].author").value("get5@test.com"));

        when(userRepository.findByEmail("get6@test.com")).thenReturn(Optional.of(user2));
        when(taskService.getTasksByAuthor("get6@test.com")).thenReturn(List.of(task2));

        mockMvc.perform(get("/api/tasks?author=get6@test.com")
                        .with(httpBasic("get6@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].author").value("get6@test.com"));

        verify(userRepository, times(2)).findByEmail(any(String.class));
        verify(taskService, times(2)).getTasksByAuthor(any(String.class));
    }

    @Test
    public void getTasksByNotExistingAuthor() throws Exception {
        User user = new User("get7@test.com", passwordEncoder.encode("password"));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(taskService.getTasksByAuthor(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks?author=unknown")
                        .with(httpBasic("get7@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userRepository).findByEmail(anyString());
        verify(taskService).getTasksByAuthor(anyString());
    }

    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
