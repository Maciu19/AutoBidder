package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.keycloakId = ?1")
    Optional<User> findByKeycloackId(String keycloakId);
}
