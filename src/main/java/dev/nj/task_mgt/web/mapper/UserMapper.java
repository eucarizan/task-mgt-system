package dev.nj.task_mgt.web.mapper;

import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.web.dto.NewUserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final PasswordEncoder encoder;

    public UserMapper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public User toEntity(NewUserDto dto) {
        return new User(
                dto.email().toLowerCase(),
                encoder.encode(dto.password()));
    }
}
