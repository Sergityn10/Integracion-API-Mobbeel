package com.mobbScan_integration.MobbScan.Repository;

import com.mobbScan_integration.MobbScan.Models.JWTToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JWTToken, String> {
    Optional<JWTToken> findByToken(String token);
}
