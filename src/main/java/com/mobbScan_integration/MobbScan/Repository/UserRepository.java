package com.mobbScan_integration.MobbScan.Repository;


import com.mobbScan_integration.MobbScan.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}


