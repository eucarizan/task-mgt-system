package dev.nj.task_mgt.service;

import dev.nj.task_mgt.web.dto.AccessTokenDto;

public interface AccessTokenService {
    AccessTokenDto requestToken();
}
