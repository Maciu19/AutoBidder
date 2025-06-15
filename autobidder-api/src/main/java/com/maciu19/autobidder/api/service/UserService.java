package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    User syncUserOnLogin(Jwt jwt);
}
