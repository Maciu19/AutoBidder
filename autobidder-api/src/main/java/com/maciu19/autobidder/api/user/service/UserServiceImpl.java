package com.maciu19.autobidder.api.user.service;

import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken)) {
            throw new IllegalStateException("User is not authenticated or authentication token is not a JWT.");
        }

        Jwt jwt = ((JwtAuthenticationToken) auth).getToken();
        return getCurrentUser(jwt);
    }

    @Override
    public User getCurrentUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloackId(keycloakId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in local database. Sync filter may have failed."));
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
