package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> { }
