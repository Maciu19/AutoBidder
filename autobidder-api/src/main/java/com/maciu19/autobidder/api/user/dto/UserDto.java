package com.maciu19.autobidder.api.user.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto (
        UUID id,
        String keycloakId,
        String email,
        String firstName,
        String lastName,
        LocalDateTime lastSyncedAt,
        Instant createdDate,
        Instant lastModifiedDate) { }
