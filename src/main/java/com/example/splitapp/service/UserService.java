package com.example.splitapp.service;

import com.example.splitapp.entity.User;
import com.example.splitapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOrCreateByName(String name) {
        return userRepository.findByName(name)
                .orElseGet(() -> userRepository.save(new User(name)));
    }
}