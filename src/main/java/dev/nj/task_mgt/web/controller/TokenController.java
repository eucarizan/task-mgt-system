package dev.nj.task_mgt.web.controller;

import dev.nj.task_mgt.service.AccessTokenService;
import dev.nj.task_mgt.web.dto.AccessTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final AccessTokenService tokenService;

    public TokenController(AccessTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenDto> requestToken(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tokenService.requestToken(userDetails));
    }
}
