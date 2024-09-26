package dev.nj.task_mgt.service.impl;

import dev.nj.task_mgt.entities.AccessToken;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.TokenRepository;
import dev.nj.task_mgt.repository.UserRepository;
import dev.nj.task_mgt.service.AccessTokenService;
import dev.nj.task_mgt.web.dto.AccessTokenDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public AccessTokenServiceImpl(TokenRepository repository, UserRepository userRepository) {
        this.tokenRepository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public AccessTokenDto requestToken(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AccessToken token = new AccessToken(user.getId());
        tokenRepository.save(token);

        return new AccessTokenDto(token.getToken());
    }
}
