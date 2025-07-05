package com.mobbScan_integration.MobbScan.Models;

import com.mobbScan_integration.MobbScan.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor

public class AuthorityId implements Serializable {

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", length = 100, nullable = false)
    private Role authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorityId)) return false;
        AuthorityId that = (AuthorityId) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authority);
    }
}
