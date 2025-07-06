package com.mobbScan_integration.MobbScan.controller;


import com.mobbScan_integration.MobbScan.DTO.response.JwtAuthenticationResponse;
import com.mobbScan_integration.MobbScan.DTO.request.LoginRequest;
import com.mobbScan_integration.MobbScan.Models.JWTToken;
import com.mobbScan_integration.MobbScan.Models.User;
import com.mobbScan_integration.MobbScan.Repository.JwtTokenRepository;
import com.mobbScan_integration.MobbScan.Security.JwtTokenProvider;


import com.mobbScan_integration.MobbScan.Service.JwtTokenService;
import com.mobbScan_integration.MobbScan.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final JwtTokenRepository tokenRepository;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserService userService, JwtTokenRepository tokenRepository, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        userService.saveUser(request.username(), request.password());
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        boolean valid = tokenProvider.validateToken(token);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 2) Buscar en Mongo si existe ese JWT y está marcado como válido
        Optional<JWTToken> maybeJwtEntity = tokenRepository.findByToken(token);
        if (maybeJwtEntity.isEmpty() || !maybeJwtEntity.get().isValid()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token not found or already revoked"));
        }

        JWTToken jwtEntity = maybeJwtEntity.get();

        // 3) Obtener el username tanto del JWT como de la entidad (por seguridad)
        String usernameFromToken = tokenProvider.getUsernameFromJWT(token);
        String usernameFromEntity = jwtEntity.getUser().getUsername();
        if (!usernameFromToken.equals(usernameFromEntity)) {
            // Los datos no coinciden: posible manipulación
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token username mismatch"));
        }

        // 4) Recuperar información adicional del usuario (por ejemplo, roles, email, etc)
        // Supongamos que UserService tiene un método getUserByUsername que devuelve un DTO o entidad
        User userDto = userService.getUserByUsername(usernameFromToken);
        if (userDto == null) {
            // El usuario pudo haber sido borrado luego de emitir el token
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User no longer exists"));
        }

        // 5) Devolver respuesta con datos del usuario
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", usernameFromToken,
                "userDetails", userDto
        ));

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{apiKey}")
    public ResponseEntity<?> authenticateApiKey(@PathVariable("apiKey") String apiKey) {
        User user = jwtTokenService.findUserByApikey(apiKey);
        System.out.println("user: " + user.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(Map.of("user", user));
        }
    }

}
