package com.mobbScan_integration.MobbScan.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {

    List<Authority> findByUserUsername(String username);

    void deleteByUserUsername(String username);
}
