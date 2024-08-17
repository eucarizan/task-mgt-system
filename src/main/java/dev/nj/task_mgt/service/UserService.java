package dev.nj.task_mgt.service;

import dev.nj.task_mgt.web.dto.NewUserDto;

public interface UserService {
    void registerUser(NewUserDto newUserDto);
}
