package dev.nj.task_mgt.client;

import dev.nj.task_mgt.TaskManagementSystemApplication;
import dev.nj.task_mgt.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TaskManagementSystemApplication.class},
webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserClientTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void register_using_valid_credentials_should_succeed() {
        User user = new User("address@domain.net", "password");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity("/api/accounts", user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
