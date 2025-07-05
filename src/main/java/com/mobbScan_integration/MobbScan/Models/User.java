package com.mobbScan_integration.MobbScan.Models;


import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;
    private String password;

    @OneToMany
    private Set<JWTToken> apiKeys;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y setters
    public String getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

