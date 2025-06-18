package com.maciu19.autobidder.api.user;

import com.maciu19.autobidder.api.user.dto.UserDto;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(
                userMapper.toDto(userService.getCurrentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);

        return user
                .map(value -> ResponseEntity.ok(
                        userMapper.toDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
