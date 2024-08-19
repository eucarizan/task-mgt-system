package dev.nj.task_mgt.web.mapper;

import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.web.dto.NewUserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(NewUserDto dto) {
        return new User(
                dto.email().toLowerCase(),
                dto.password());
    }
}
