package com.mobbScan_integration.MobbScan.Service;


import com.mobbScan_integration.MobbScan.Models.User;
import com.mobbScan_integration.MobbScan.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void saveUser(String username, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        userRepository.save(new User(username, encoded));
    }
}

