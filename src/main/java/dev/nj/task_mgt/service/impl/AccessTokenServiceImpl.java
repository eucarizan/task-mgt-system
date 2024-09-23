package dev.nj.task_mgt.service.impl;

import dev.nj.task_mgt.entities.AccessToken;
import dev.nj.task_mgt.repository.TokenRepository;
import dev.nj.task_mgt.service.AccessTokenService;
import dev.nj.task_mgt.web.dto.AccessTokenDto;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final TokenRepository repository;

    public AccessTokenServiceImpl(TokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public AccessTokenDto requestToken() {
        var bytes = KeyGenerators.secureRandom(10).generateKey();
        var hexString = new BigInteger(1, bytes).toString(16);

        var token = new AccessToken();
        token.setToken(hexString);
        long hour = 60L * 60 * 1000;
        var timestamp = System.currentTimeMillis() + hour;
        token.setExpiresAt(new Date(timestamp));
        repository.save(token);

        return new AccessTokenDto(hexString);
    }
}
