package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MeController {

    private UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        return userService.syncUserOnLogin(jwt);
    }
}
