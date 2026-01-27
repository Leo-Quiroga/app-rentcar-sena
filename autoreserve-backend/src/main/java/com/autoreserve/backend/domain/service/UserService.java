package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Inyección por constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    public User save(User user) {
        return userRepository.save(user);
    }

    // READ - all
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // READ - by id
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // READ - by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // DELETE
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
