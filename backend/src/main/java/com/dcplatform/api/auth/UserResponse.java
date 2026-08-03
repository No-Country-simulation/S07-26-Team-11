package com.dcplatform.api.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String email, OffsetDateTime createdAt) {

    static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}
