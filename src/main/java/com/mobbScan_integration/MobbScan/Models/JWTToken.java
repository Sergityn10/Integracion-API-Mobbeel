package com.mobbScan_integration.MobbScan.Models;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Document(collection = "jwt_tokens")
public class JWTToken {
    @Id
    private String id;

    private String token;
    private String username;
    private Date issuedAt;
    private Date expiration;
    private boolean valid = true;

    // Getters y setters
    // ...
}
