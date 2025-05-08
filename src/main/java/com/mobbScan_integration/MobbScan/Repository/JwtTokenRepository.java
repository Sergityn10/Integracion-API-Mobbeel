package com.mobbScan_integration.MobbScan.Repository;

import com.mobbScan_integration.MobbScan.Models.JWTToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JwtTokenRepository extends MongoRepository<JWTToken, String> {
    Optional<JWTToken> findByToken(String token);
}
