package dev.nj.task_mgt.web.controller;

import dev.nj.task_mgt.service.UserService;
import dev.nj.task_mgt.web.dto.NewUserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid NewUserDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.ok().build();
    }
}
