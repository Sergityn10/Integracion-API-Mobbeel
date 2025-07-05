package com.mobbScan_integration.MobbScan.Models;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuthorityRepository extends MongoRepository<Authority, AuthorityId> {

    List<Authority> findByUserUsername(String username);

    void deleteByUserUsername(String username);
}
