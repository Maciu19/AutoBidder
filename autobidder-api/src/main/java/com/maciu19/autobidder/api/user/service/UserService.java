package com.maciu19.autobidder.api.user.service;

import com.maciu19.autobidder.api.user.model.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    User getCurrentUser();

    User getCurrentUser(Jwt jwt);

    User syncUser(Jwt jwt);
}
