# Task Management System

- [Task Management System](#task-management-system)
  - [Learning outcomes](#learning-outcomes)
  - [About](#about)
  - [Stages](#stages)
    - [1: Registering users](#1-registering-users)
    - [2: Creating tasks](#2-creating-tasks)
    - [3: Authenticating with JWT](#3-authenticating-with-jwt)

## Learning outcomes
You will practice building a REST API, learn how to validate input and customize server responses, practice applying JWT authentication and learn about creating complex database queries using Spring Data.

## About
Have you ever used any task management systems like Jira or Trello? This project is the perfect starting point to build a small yet feature rich application on your own. In this project, you will develop a task management system that allows users to create, update, and manage tasks. You'll implement JWT authentication to protect your system and create aggregate views of tasks. This will give you a good practice in creating advance CRUD applications and provide you with the necessary skills to create a useful web service for your portfolio and daily use.

## Stages
### 1: Registering users
<details>
<summary>Create the endpoint to register new users and secure the other endpoint to make it accessible to authenticated users only.</summary>

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

### 2: Creating tasks
<details>
<summary>Add the functionality to publish new tasks and list and filter existing tasks.</summary>

#### 2.1 Description
In the last stage, you enabled users to register on the Task Management System. They should now be able to create tasks and view those created by others. Additionally, you need to implement a function that allows filtering tasks by their author for user convenience.

A task should be a straightforward object encompassing a title, which should never be blank or empty, a description explaining the task's essence, and a status indicating its current state. The task should also hold information about the user who created it and who will own the task.

Any authenticated user should have the ability to create a task and view a list of all tasks in the system. To make it user-friendly, arrange the task list so that the most recent tasks show up first.

#### 2.2 Objectives
- Create the `POST /api/tasks` endpoint to accept a JSON request body with new task details:
    ```json
    {
      "title": <string, not null, not blank>,
      "description": <string, not null, not blank>
    }
    ```

    If the request body is valid, the endpoint should respond with `200 OK` status code and a JSON response body:
    ```json
    {
      "id": <string>,
      "title": <string>,
      "description": <string>,
      "status": "CREATED",
      "author": <string>
    }
    ```

    The `id` field is a string representation of the task's unique identifier. This doesn't mean that the identifier must be a string, but the response body should present it as a string value to not depend on the database implementation. The `author` field should feature the author email in lowercase.
    If the request body is not valid, the endpoint should return a `400 BAD REQUEST` status code.

- Modify the `GET /api/tasks` endpoint to respond with `200 OK` status code and return a JSON array of all created tasks, or an empty array if no tasks exist. Each array's element should follow the same format as mentioned above:
    ```json
    [
      {
        "id": <string>,
        "title": <string>,
        "description": <string>,
        "status": "CREATED",
        "author": <string>
      },
      // other tasks
    ]
    ```

    The array should display newer tasks first.

- The `GET /api/tasks` endpoint should also accept an optional `author` parameter to return an array of tasks authored by a particular user. Treat this parameter as case insensitive:
    ```
    GET /api/tasks?author=address@domain.net
    ```

- Only authenticated users should have access to the tasks endpoints. If accessed by unauthorized users, respond with a `401 UNAUTHORIZED` status code.

- Store all data in the database.

#### 2.3 Examples
**Example 1.** *request to 'POST /api/tasks' endpoint by a registered user with valid credentials (username=user1@mail.com):*

*Request body:*
```json
{
  "title": "new task",
  "description": "a task for anyone"
}
```

*Response code:* `200 OK`

*Response body:*
```json
{
  "id": "1",
  "title": "new task",
  "description": "a task for anyone",
  "status": "CREATED",
  "author": "user1@mail.com"
}
```

**Example 2.** *request to 'POST /api/tasks' endpoint with an invalid request body by an authenticated user:*

*Request body:*
```json
{
  "title": "",
  "description": "a task for anyone"
}
```

*Response code:* `400 BAD REQUEST`

**Example 3.** *request to 'POST /api/developers/signup' endpoint with invalid credentials:*

*Request body:*
```json
{
  "title": "new task",
  "description": "a task for anyone"
}
```

*Response code:* `401 UNAUTHORIZED`

**Example 4.** *request to 'GET /api/tasks' endpoint with valid user credentials:*

*Response code:* `200 OK`

*Response body:*
```json
[
  {
    "id": "1",
    "title": "new task",
    "description": "a task for anyone",
    "status": "CREATED",
    "author": "user1@mail.com"
  }
]
```

**Example 5.** *request to 'GET /api/tasks?author=USER1@mail.com' with valid user credentials:*

*Response code:* `200 OK`

*Response body:*
```json
[
  {
    "id": "1",
    "title": "new task",
    "description": "a task for anyone",
    "status": "CREATED",
    "author": "user1@mail.com"
  }
]
```

</details>

<hr/>

### 3: Authenticating with JWT
<details>
<summary>Introduce JWT authentication to let users use tokens to authenticate to access the API.</summary>

#### 3.1 Description
Great job so far! It's now time to enhance the sign-in process to improve user experience. Instead of making a user supply their login details for every request, you should let them give the details only once to receive an authentication token. They can then use this token for future sign ins.

This will make the user experience better without compromising security.

The normal way to put such token-based sign ins into action is by using JWT. However, the tests won't make you use the JWT format, and you're free to use any opaque token, as long as the service can recognize the user by that token.

To put the bearer token sign in to use, you need to create an endpoint for users to sign in and get their tokens. This endpoint should be available only to registered users using basic HTTP sign ins. Any other endpoint, apart from the `POST /api/accounts`, should be available if a request has the Authorization header with a functioning bearer token:

```
Authorization: Bearer <token value>
```

The rest of the service should work as it did in the previous stage.

#### 3.2 Objectives
- Create the `POST /api/auth/token` endpoint that should be accessible using basic HTTP sign in. If the user signs in successfully, the endpoint should respond with the status code `200 OK` and a JSON response body:
    ```json
    {
      "token": <string>
    }
    ```

    The `token` field contains a string representation of the access token provided to the user. The token should have a reasonable expiration time, allowing the user to utilize the token without frequently signing in again. If the user does not provide valid details, the endpoint should respond with a `401 UNAUTHORIZED` status code.

- Update the security settings so that any other secure endpoint can be accessed using the bearer token sign in with the granted access tokens.
- From now on, any requests to the API will be tested using access tokens that will be added to the `Authorization` header:
    ```json
    Authorization: Bearer <string token value>
    ```

    But remember, in this project you don't need to turn off the basic HTTP sign in completely.
- All other endpoints should work the same way as they did in the previous stage.

#### 3.3 Examples
**Example 1.** *request to the `POST /api/auth/token` endpoint by a registered user with valid details (username=user2@mail.com):*

*Response code:* `200 OK`

*Response body:*
```json
{
  "token": "eyJraWQiOiJkM2E4YWY0NC0xNzc3LTRmOTAtYjc5Yy03NDRkMTI4MDQxNGQiLCJhbGciOiJSUzI1NiJ9
            .eyJzdWIiOiJ1c2VyMkBtYWlsLmNvbSIsImV4cCI6MTcwMzA4MTIyNiwiaWF0IjoxNzAzMDc3NjI2LCJ
            zY29wZSI6WyJST0xFX1VTRVIiXX0.g069OwkofwRPa1vuIU-Vc30UYXzmJihlg9KzxQ7LqJzKsJJO4_o
            sSVwq7eqniiYurIBToXiW_PttteuOOps6ryDZXKqg3FBoHEiRLoUgn9vNgRydFOBo1WwB_fHxOB0xFW2
            RrGbDlWpFs9F_8ap-O9BHf74VU4L1HRn6vTA7yhtqfBdAZscPY6XCVjUdPwXMQnNqJy2vOTdFNd1-V9X
            X5GEFbndXMyTsQAKfhTnjdn151unbzYnllUwtb4xtRfpCLr47KuVSGrOTfDbBbjx91SB2i0wfq46b5ty
            lCOMR7nsMWuhBxV8oqlIICLPokEejB8jAVcZXcsxSqQz9AHopGg"
}
```

**Example 2.** *request to the `POST /api/tasks` endpoint with the token from Example 1:*

*Request body:*
```json
{
  "title": "second task",
  "description": "another task"
}
```

*Response code:* `200 OK`

*Response body:*
```json
{
  "id": "2",
  "title": "second task",
  "description": "another task",
  "status": "CREATED",
  "author": "user2@mail.com"
}
```

**Example 3.** *request to the `GET /api/tasks` endpoint with the token from Example 1:*

*Response code:* `200 OK`

*Response body:*
```json
[
  {
    "id": "2",
    "title": "second task",
    "description": "another task",
    "status": "CREATED",
    "author": "user2@mail.com"
  },
  {
    "id": "1",
    "title": "new task",
    "description": "a task for anyone",
    "status": "CREATED",
    "author": "user1@mail.com"
  }
]
```

**Example 4.** *request to the `GET /api/tasks` with an expired access token:*

*Response code:* `401 UNAUTHORIZED`

</details>

<hr/>

[<<](https://github.com/eucarizan/hs-java-backend/blob/main/README.md)
<!--
<summary>Allow users to assign their tasks to other users and change the task statuses.</summary>
<summary>Allow users to post comments to tasks and see all the comments for each task.</summary>
-->
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
s_\(Example \d\.\) \(\w\+\) \(request to\) \(/[a-zA-Z0-9/?=@\.]\+\)\+ \(.*:\)_**\1** *\3 \'\2 \4\' \5*_gc

# format request body
s_\(Request body:\)\n_\*\1\*\r```json_gc

# format response code after response body
s_}\n\n\(Response code:\)\(.*\)_}\r```\r\r*\1\* `\2`_gc

# format response code not after response body
s_\n\(Response code:\) \(.*\)_\r*\1\* `\2`_gc

# format response body
s_\(Response body:\)\n_\*\1\*\r```json_gc
-->
