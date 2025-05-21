package com.maciu19.autobidder.api.controllers;

import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    List<User> findAll() {
        return userRepository.findAll();
    }
}
