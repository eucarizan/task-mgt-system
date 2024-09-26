package dev.nj.task_mgt.service;

import dev.nj.task_mgt.web.dto.AccessTokenDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AccessTokenService {
    AccessTokenDto requestToken(UserDetails userDetails);
}
