package com.mobbScan_integration.MobbScan.Service;


import com.mobbScan_integration.MobbScan.Models.JWTToken;
import com.mobbScan_integration.MobbScan.Repository.JwtTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    private final JwtTokenRepository repository;

    public JwtTokenService(JwtTokenRepository repository) {
        this.repository = repository;
    }

    public void saveToken(String token, String username, Date issuedAt, Date expiration) {
        JWTToken jwt = new JWTToken();
        jwt.setToken(token);
        jwt.setUsername(username);
        jwt.setIssuedAt(issuedAt);
        jwt.setExpiration(expiration);
        jwt.setValid(true);
        repository.save(jwt);
    }

    public boolean isTokenValid(String token) {
        return repository.findByToken(token)
                .map(JWTToken::isValid)
                .orElse(false);
    }

    public void invalidateToken(String token) {
        repository.findByToken(token).ifPresent(jwt -> {
            jwt.setValid(false);
            repository.save(jwt);
        });
    }
}
