package dev.nj.task_mgt.client;

import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        ResponseEntity<Void> postResponseEntity
                = restTemplate.postForEntity("/api/accounts", user, Void.class);
        assertThat(postResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> responseEntity
                = restTemplate.withBasicAuth(user.getEmail(), user.getPassword())
                .getForEntity(URL, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
