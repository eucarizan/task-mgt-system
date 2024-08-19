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

    private static final String URL = "/api/accounts";

    // TODO-02a: test invalid email format
    @Test
    public void register_invalid_email_should_return_400() {
        User user = new User("addrestATdomain.net", "password");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity(URL, user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // TODO-02b: test email should not be blank
    @Test
    public void register_blank_email_should_return_400() {
        User user = new User("", "password");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity(URL, user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // TODO-02c: test password should not be blank
    @Test
    public void register_blank_password_should_return_400() {
        User user = new User("address2@domain.net", "");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity(URL, user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // TODO-02d: test email already taken
    @Test
    public void register_already_existing_email_should_return_409() {
        User user = new User("existing@domain.net", "password");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity(URL, user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        User newUser = new User("existing@domain.net", "password");
        responseEntity = restTemplate.postForEntity(URL, newUser, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void register_using_valid_credentials_should_succeed() {
        User user = new User("address@domain.net", "password");
        ResponseEntity<Void> responseEntity
                = restTemplate.postForEntity(URL, user, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
