package com.maciu19.autobidder.api.user.mapper;

import com.maciu19.autobidder.api.user.dto.UserDto;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getKeycloakId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getLastSyncedAt(),
                user.getCreatedDate(),
                user.getLastModifiedDate()
        );
    }
}
