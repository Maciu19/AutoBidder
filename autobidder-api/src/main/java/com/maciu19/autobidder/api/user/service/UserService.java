package com.maciu19.autobidder.api.user.service;

import com.maciu19.autobidder.api.user.model.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> getUserById(UUID userId);

    User getCurrentUser();

    User getCurrentUser(Jwt jwt);

    User syncUser(Jwt jwt);

    List<User> findAll();
}
