# Task Management System

- [Task Management System](#task-management-system)
  - [Learning outcomes](#learning-outcomes)

## Learning outcomes
You will practice building a REST API, learn how to validate input and customize server responses, practice applying JWT authentication and learn about creating complex database queries using Spring Data.

## About
Have you ever used any task management systems like Jira or Trello? This project is the perfect starting point to build a small yet feature rich application on your own. In this project, you will develop a task management system that allows users to create, update, and manage tasks. You'll implement JWT authentication to protect your system and create aggregate views of tasks. This will give you a good practice in creating advance CRUD applications and provide you with the necessary skills to create a useful web service for your portfolio and daily use.

## Stages
### 1: Registering users
<details>
<summary>Create the service structure (API)</summary>

#### 1.1 Description
In a task management system, you'll often be working with multiple users. Therefore, it's useful to begin by setting up the user registration process and API access. It's important you store user data in a database right from the start and set up the project accordingly.

You should provide a REST API for users. Now, you need to create two endpoints, one for user registration and another one for testing access control. Using Spring Security, you can secure these endpoints. To carry out tests, you should set up Spring Security correctly:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .httpBasic(Customizer.withDefaults()) // enable basic HTTP authentication
            .authorizeHttpRequests(auth -> auth
                    // other matchers
                    .requestMatchers("/error").permitAll() // expose the /error endpoint
                    .requestMatchers("/actuator/shutdown").permitAll() // required for tests
                    .requestMatchers("/h2-console/**").permitAll() // expose H2 console
            )
            .csrf(AbstractHttpConfigurer::disable) // allow modifying requests from tests
            .sessionManagement(sessions ->
                    sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
            )
            .build();
    }
```

Store all data in the H2 database on disk. Don't forget to include these lines in the `application.properties` file:
```
spring.datasource.url=jdbc:h2:file:../tms_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa

spring.jpa.hibernate.ddl-auto=update

spring.h2.console.enabled=true
spring.h2.console.settings.trace=false
spring.h2.console.settings.web-allow-others=false
```

#### 1.2 Objectives
- Set up a `POST /api/accounts` endpoint that takes a unique email and password as a JSON object in this format:
  ```json
  {
    "email": <unique string>,
    "password": <string>
  }
  ```
  and responds with a `200 OK status` code. Check the request body. If the `email` or `password` are empty, blank, or missing, the endpoint should respond with a `400 BAD REQUEST` status code. The endpoint should also respond with a `400 BAD REQUEST` status code if the email format is invalid. Additionally, ensure that the password is at least 6 characters long. If the password is shorter than this, the endpoint should respond with a `400 BAD REQUEST` status code.

- Guarantee each user has a unique email address. If a registration request includes an email address already in the system, the user registration endpoint should respond with a `409 CONFLICT` status code. Treat email addresses as case-insensitive, so `address@domain.net` and `ADDRESS@DOMAIN.NET` are regarded as the same address.

- Create a `GET /api/tasks` endpoint that responds with a `200 OK` status code.

- Enable security and require users to authenticate using basic HTTP authentication to access the `GET /api/tasks` endpoint. If a user tries to access it without providing a valid username and password, the service should respond with a `401 UNAUTHORIZED` status code.

- Ensure that all data remains even after the server restarts.

#### 1.3 Examples
**Example 1.** *request to `POST /accounts` endpoint:*

*Request body:*
```json
{
  "email": "address@domain.net",
  "password": "password"
}
```

*Response code:* `200 OK`

**Example 2.** *request to `POST /accounts` endpoint with an invalid request body:*

*Request body:*
```json
{
  "email": "address",
  "password": ""
}
```

*Response code:* `400 BAD REQUEST`

**Example 3.** *request to `POST /accounts` endpoint with an email address that's already taken:*

*Request body:*
```json
{
  "email": "ADDRESS@DOMAIN.NET",
  "password": "password"
}
```

*Response code:* `409 CONFLICT`

**Example 4.** *request to `GET /tasks` endpoint with correct credentials:*

*Response code:* `200 OK`

**Example 5.** *request to `GET /tasks` endpoint with incorrect credentials:*

*Response code:* `401 UNAUTHORIZED`

</details>

<hr/>

[<<](https://github.com/eucarizan/hs-java-backend/blob/main/README.md)
<!--
:%s/\(Sample \(Input\|Output\) \d:\)\n\(.*\)/```\r\r**\1**\r```\3/gc

###
<details>
<summary></summary>

#### Description

#### Examples

</details>

<hr/>
-->
<!--
# format example
s_\(Example \d\.\) \(\w\+\) \(request to\) \(/\w\+\)\+ \(.*:\)_**\1** *\3 \'\2 \4\' \5*_gc

# format request body
s_\(Request body:\)\n_\*\1\*\r```_gc

# format response code after response body
s_}\n\n\(Response code:\)\(.*\)_}\r```\r\r*\1\* `\2`_gc

# format response code not after response body
s_\n\(Response code:\)\(.*\)_\r*\1\* `\2`_gc
-->
