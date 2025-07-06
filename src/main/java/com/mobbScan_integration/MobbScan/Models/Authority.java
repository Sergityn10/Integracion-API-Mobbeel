package com.mobbScan_integration.MobbScan.Models;

import com.mobbScan_integration.MobbScan.Models.AuthorityId;
import com.mobbScan_integration.MobbScan.Models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "authority"}) )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @EmbeddedId
    private AuthorityId id;

    @MapsId("username")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "username",
            nullable = false,
            referencedColumnName = "username",
            foreignKey = @ForeignKey(name = "FK_authorities_users")
    )
    private User user;



}
