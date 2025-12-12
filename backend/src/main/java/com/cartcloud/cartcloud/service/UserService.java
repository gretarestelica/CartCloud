package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.User;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

  
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

   
    public User getUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
}



    public User createUser(User user) {
        // default values
        if (user.getRole() == null) user.setRole("CUSTOMER");
        if (user.getAccountStatus() == null) user.setAccountStatus("ACTIVE");
        return userRepository.save(user);
    }

  
    public User updateUser(Long id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    existing.setPassword(updated.getPassword());
                    existing.setRole(updated.getRole());
                    existing.setAccountStatus(updated.getAccountStatus());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

   
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(password));
    }
}
