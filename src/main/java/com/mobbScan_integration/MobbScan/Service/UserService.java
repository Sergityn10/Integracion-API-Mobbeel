package com.mobbScan_integration.MobbScan.Service;


import com.mobbScan_integration.MobbScan.Models.User;
import com.mobbScan_integration.MobbScan.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

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
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoded);
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user;
    }
}

