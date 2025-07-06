package com.mobbScan_integration.MobbScan.Models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apikeys")
public class JWTToken {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    /**
     * Lo ideal es referenciar al usuario con @ManyToOne.
     * Mientras migras, puede seguir como String:
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "issued_at", nullable = false)
    private Date issuedAt;

    @Column(nullable = false)
    private Date expiration;

    @Column(nullable = false)
    private boolean valid = true;

}
