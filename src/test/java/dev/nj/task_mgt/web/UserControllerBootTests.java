package dev.nj.task_mgt.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.config.WebSecurityConfig;
import dev.nj.task_mgt.exceptions.UserAlreadyExistsException;
import dev.nj.task_mgt.service.UserService;
import dev.nj.task_mgt.web.controller.UserController;
import dev.nj.task_mgt.web.dto.NewUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {
        TaskManagementSystemApplication.class,
        WebSecurityConfig.class
})
public class UserControllerBootTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void register() throws Exception {
        NewUserDto user = new NewUserDto("alice@email.com", "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk());

        verify(userService).registerUser(user);
    }

    @Test
    public void registerBlankEmailFail() throws Exception {
        NewUserDto user = new NewUserDto("", "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerNulLEmailFail() throws Exception {
        NewUserDto user = new NewUserDto(null, "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerBadEmailFail() throws Exception {
        NewUserDto user = new NewUserDto(" ", "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerMalformedEmailFail() throws Exception {
        NewUserDto user = new NewUserDto("address@email.", "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerBlankPasswordFail() throws Exception {
        NewUserDto user = new NewUserDto("address@email.com", "");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerNullPasswordFail() throws Exception {
        NewUserDto user = new NewUserDto("address@email.com", null);
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerBadPasswordFail() throws Exception {
        NewUserDto user = new NewUserDto("address@email.com", " ");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerLessThanMinLengthPasswordFail() throws Exception {
        NewUserDto user = new NewUserDto("address@email.com", "12345");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAlreadyExistingEmailFail() throws Exception {
        doThrow(new UserAlreadyExistsException()).when(userService).registerUser(any(NewUserDto.class));

        NewUserDto user = new NewUserDto("existing@email.com", "password");
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isConflict());

        verify(userService).registerUser(user);
    }

    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
