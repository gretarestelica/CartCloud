package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.User;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("CUSTOMER");
        }
        if (user.getAccountStatus() == null || user.getAccountStatus().isBlank()) {
            user.setAccountStatus("ACTIVE");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    if (updated.getName() != null) existing.setName(updated.getName());

                    if (updated.getEmail() != null && !updated.getEmail().equals(existing.getEmail())) {
                        if (userRepository.existsByEmail(updated.getEmail())) {
                            throw new RuntimeException("Email already exists");
                        }
                        existing.setEmail(updated.getEmail());
                    }

                    if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(updated.getPassword()));
                    }

                    if (updated.getRole() != null && !updated.getRole().isBlank()) {
                        existing.setRole(updated.getRole());
                    }

                    if (updated.getAccountStatus() != null && !updated.getAccountStatus().isBlank()) {
                        existing.setAccountStatus(updated.getAccountStatus());
                    }

                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
