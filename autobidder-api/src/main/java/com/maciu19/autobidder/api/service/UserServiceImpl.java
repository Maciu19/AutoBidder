package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User syncUser(Jwt jwt) {
        String keycloackId = jwt.getSubject();

        User user = userRepository.findByKeycloackId(keycloackId)
                .orElseGet(() -> createNewUser(jwt));

        if (user.getLastSyncedAt() == null || user.getLastSyncedAt().isBefore(LocalDateTime.now().minusHours(48))) {
            user.setFirstName(jwt.getClaimAsString("given_name"));
            user.setLastName(jwt.getClaimAsString("family_name"));
            user.setLastSyncedAt(LocalDateTime.now());

            user = userRepository.save(user);
        }

        return user;
    }

    private User createNewUser(Jwt jwt) {
        User newUser = new User();
        newUser.setKeycloakId(jwt.getSubject());
        newUser.setEmail(jwt.getClaimAsString("email"));
        newUser.setFirstName(jwt.getClaimAsString("given_name"));
        newUser.setLastName(jwt.getClaimAsString("family_name"));
        newUser.setLastSyncedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}
