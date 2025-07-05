package com.mobbScan_integration.MobbScan.Models;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Tokens")
public class JWTToken {
    @Id
    private String id;

    private String token;
    private String username;
    private Date issuedAt;
    private Date expiration;
    private boolean valid = true;

    @ManyToOne
    private User user;

}
