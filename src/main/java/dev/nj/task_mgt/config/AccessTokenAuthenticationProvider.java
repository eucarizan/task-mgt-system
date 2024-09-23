package dev.nj.task_mgt.config;

import dev.nj.task_mgt.entities.AccessToken;
import dev.nj.task_mgt.entities.AccessTokenAuthentication;
import dev.nj.task_mgt.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenRepository repository;

    public AccessTokenAuthenticationProvider(TokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(dontRollbackOn = BadCredentialsException.class)
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = authentication.getCredentials().toString();

        AccessToken accessToken = repository
                .findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid access token"));

        repository.deleteById(accessToken.getId());

        if (new Date().after(accessToken.getExpiresAt())) {
            throw new BadCredentialsException("Invalid access token.");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenAuthentication.class.equals(authentication);
    }
}
