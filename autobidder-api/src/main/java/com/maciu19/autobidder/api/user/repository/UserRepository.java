package com.maciu19.autobidder.api.user.repository;

import com.maciu19.autobidder.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.keycloakId = ?1")
    Optional<User> findByKeycloackId(String keycloakId);
}
