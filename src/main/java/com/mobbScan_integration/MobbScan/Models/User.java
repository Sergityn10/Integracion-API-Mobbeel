package com.mobbScan_integration.MobbScan.Models;


import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "users")
public class User {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @Column(nullable = false, length = 150)
    private String username;               // único gracias al UniqueConstraint

    @Column(nullable = false, length = 250)
    private String password;               // almacena el hash (bcrypt u otro)

    @OneToMany
    private Set<JWTToken> apiKeys;

}

