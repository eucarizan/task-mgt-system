package dev.nj.task_mgt.client;

import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.entities.Task;
import dev.nj.task_mgt.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TaskManagementSystemApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TasksClientTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String URL = "/api/tasks";

    @Test
    public void get_tasks_using_invalid_user_should_return_401() {
        ResponseEntity<Void> responseEntity
                = restTemplate.withBasicAuth("user@domain.net", "password")
                .getForEntity(URL, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void get_tasks_using_valid_user_should_succeed() {
        User user = new User("address@domain.net", "password");
        ResponseEntity<Void> registerResponseEntity
                = restTemplate.postForEntity("/api/accounts", user, Void.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> responseEntity
                = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .getForEntity(URL, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void create_task_using_invalid_user_should_return_401() {
        User invalidUser = new User("user@domain.net", "password");
        Task task = new Task("invalid task 1", "invalid task description 1", invalidUser);
        ResponseEntity<Void> responseEntity
                = restTemplate.withBasicAuth(invalidUser.getEmail(), invalidUser.getPassword())
                .postForEntity(URL, task, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_task_using_valid_user_should_succeed() {
        User user = new User("test1@test.com", "password");
        ResponseEntity<Void> registerResponseEntity
                = restTemplate.postForEntity("/api/accounts", user, Void.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Task task = new Task("test task 1", "test description 1", user);
        ResponseEntity<Task> responseEntity
                = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .postForEntity(URL, task, Task.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getStatus().toString()).isEqualTo("CREATED");
        assertThat(responseEntity.getBody().getAuthor()).isEqualTo(user.getEmail());
    }

    @Test
    public void get_list_of_tasks_using_valid_user_should_succeed() {
        User user = new User("test2@test.com", "password");
        ResponseEntity<Void> registerResponseEntity
                = restTemplate.postForEntity("/api/accounts", user, Void.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Task task1 = new Task("test2 task 1", "test2 description 1", user);
        Task task2 = new Task("test2 task 2", "test2 description 2", user);
        ResponseEntity<Void> task1ResponseEntity
                = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .postForEntity(URL, task1, Void.class);
        assertThat(task1ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> task2ResponseEntity
                = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .postForEntity(URL, task2, Void.class);
        assertThat(task2ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Task[] tasks = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .getForObject(URL, Task[].class);
        assertThat(tasks.length == 2).isEqualTo(true);
        assertThat(tasks[0].getId()).isEqualTo(2);
    }

    @Test
    public void get_list_of_tasks_by_author_using_valid_user_should_succeed() {
        User user1 = new User("test3_1@test.com", "password");
        User user2 = new User("test3_2@test.com", "password");
        ResponseEntity<Void> register1ResponseEntity
                = restTemplate.postForEntity("/api/accounts", user1, Void.class);
        assertThat(register1ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> register2ResponseEntity
                = restTemplate.postForEntity("/api/accounts", user2, Void.class);
        assertThat(register2ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Task task1 = new Task("test3 task 1", "test3 description 1", user1);
        Task task2 = new Task("test3 task 2", "test3 description 2", user2);
        Task task3 = new Task("test3 task 3", "test3 description 3", user1);
        ResponseEntity<Void> task1ResponseEntity
                = restTemplate.withBasicAuth(user1.getEmail(), user1.getPassword())
                .postForEntity(URL, task1, Void.class);
        assertThat(task1ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> task2ResponseEntity
                = restTemplate.withBasicAuth(user2.getEmail(), user2.getPassword())
                .postForEntity(URL, task2, Void.class);
        assertThat(task2ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> task3ResponseEntity
                = restTemplate.withBasicAuth(user1.getEmail(), user1.getPassword())
                .postForEntity(URL, task3, Void.class);
        assertThat(task3ResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Task[] tasks = restTemplate.withBasicAuth(user1.getEmail(), user1.getPassword())
                .getForObject(URL + "?author=" + user1.getEmail(), Task[].class);
        assertThat(tasks.length == 2).isEqualTo(true);
    }
}
