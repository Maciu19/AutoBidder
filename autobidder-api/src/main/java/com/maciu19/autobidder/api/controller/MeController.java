package com.maciu19.autobidder.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public Map<String, String> getMyInfo(@AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");
        String userId = jwt.getSubject();

        return Collections.singletonMap("response", "Hello, " + username + "! Your User ID is: " + userId);
    }
}
