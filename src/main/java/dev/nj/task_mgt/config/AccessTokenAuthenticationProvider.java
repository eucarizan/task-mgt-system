package dev.nj.task_mgt.config;

import dev.nj.task_mgt.entities.AccessToken;
import dev.nj.task_mgt.entities.AccessTokenAuthentication;
import dev.nj.task_mgt.entities.User;
import dev.nj.task_mgt.repository.TokenRepository;
import dev.nj.task_mgt.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public AccessTokenAuthenticationProvider(TokenRepository repository,
                                             UserDetailsService userDetailsService,
                                             UserRepository userRepository) {
        this.tokenRepository = repository;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Transactional(dontRollbackOn = BadCredentialsException.class)
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = authentication.getCredentials().toString();

        AccessToken accessToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid access token"));

        if (LocalDateTime.now().isAfter(accessToken.getExpiresAt())) {
            tokenRepository.deleteById(accessToken.getId());
            throw new BadCredentialsException("Invalid access token.");
        }

        User user = getUserFromAccessToken(accessToken);

        AccessTokenAuthentication auth = new AccessTokenAuthentication(token);
        auth.setUserDetails(userDetailsService.loadUserByUsername(user.getEmail()));
        auth.setAuthenticated(true);

        return auth;
    }

    private User getUserFromAccessToken(AccessToken token) {
        return userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenAuthentication.class.equals(authentication);
    }
}
