package dev.nj.task_mgt.entities;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiresAt;

    private Long userId;

    public AccessToken() {}

    public AccessToken(Long userId) {
        this.userId = userId;
        generateToken();
        generateExpiry();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    private void generateToken() {
        var bytes = KeyGenerators.secureRandom(10).generateKey();
        var hexString = new BigInteger(1, bytes).toString(16);
        setToken(hexString);
    }

    private void generateExpiry() {
        setExpiresAt(LocalDateTime.now().plusHours(1));
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getUserId() {
        return userId;
    }
}
