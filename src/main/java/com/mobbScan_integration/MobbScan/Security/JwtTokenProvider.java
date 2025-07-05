package com.mobbScan_integration.MobbScan.Security;

import com.mobbScan_integration.MobbScan.Service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; // Para Spring Boot 3+
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationInMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private SecretKey jwtSecretKey;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostConstruct
    public void init() {
        // Esta es la forma recomendada de generar una SecretKey a partir de tu string secreto
        // para algoritmos HMAC-SHA. Asegúrate que jwtSecretString sea suficientemente largo y aleatorio.
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        logger.info("JWT Secret Key inicializada.");
    }

//    public String generateToken(Authentication authentication) {
//        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        String authorities = userPrincipal.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .claim("auth", authorities) // Puedes nombrar esta claim como prefieras (e.g., "roles", "scopes")
//                .setIssuer(jwtIssuer)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, jwtSecretKey) // Asegúrate que el algoritmo coincida con la fortaleza de tu clave
//                .compact();
//    }
public String generateToken(Authentication authentication) {
    UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

    String jwt = Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .setIssuer(jwtIssuer)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
            .compact();

    jwtTokenService.saveToken(jwt, userPrincipal.getUsername(), now, expiryDate);

    return jwt;
}

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseClaimsJws(token);

            return jwtTokenService.isTokenValid(token);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

    // Los siguientes métodos son útiles si necesitas validar o parsear el token fuera del flujo estándar
    // de Spring Security (que usa JwtDecoder).

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .requireIssuer(jwtIssuer) // Validar el emisor
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}
