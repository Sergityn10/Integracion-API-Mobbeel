package com.mobbScan_integration.MobbScan.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "authorities")              // ← Nombre de la colección
@CompoundIndex(name = "username_authority_uq",     // ← Nombre del índice (opcional)
        def = "{'username': 1, 'authority': 1}",
        unique = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @EmbeddedId
    private AuthorityId id;

//    @MapsId("username")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public User user;



}
