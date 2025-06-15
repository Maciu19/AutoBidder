package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User syncUserOnLogin(Jwt jwt) {
        String keycloackId = jwt.getSubject();

        return userRepository.findByKeycloackId(keycloackId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloackId);
                    newUser.setEmail(jwt.getClaimAsString("email"));
                    newUser.setFirstName(jwt.getClaimAsString("given_name"));
                    newUser.setLastName(jwt.getClaimAsString("family_name"));

                    return userRepository.save(newUser);
                });
    }
}
