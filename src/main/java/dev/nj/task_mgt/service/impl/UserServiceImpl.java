package dev.nj.task_mgt.service.impl;

import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.exceptions.UserAlreadyExistsException;
import dev.nj.task_mgt.repository.UserRepository;
import dev.nj.task_mgt.service.UserService;
import dev.nj.task_mgt.web.dto.NewUserDto;
import dev.nj.task_mgt.web.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @Override
    public void registerUser(NewUserDto newUserDto) {
        User user = userMapper.toEntity(newUserDto);
        if (userRepository.existByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException();
        }
        userRepository.save(user);
    }
}
