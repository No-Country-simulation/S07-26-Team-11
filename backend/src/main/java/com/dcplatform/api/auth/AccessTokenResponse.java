package com.dcplatform.api.auth;

public record AccessTokenResponse(String accessToken, String tokenType, long expiresInSeconds) {

    static final long ACCESS_TOKEN_TTL_SECONDS = 24 * 60 * 60; // igual que JwtServiceImpl.generateAccessToken

    static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken, "Bearer", ACCESS_TOKEN_TTL_SECONDS);
    }
}
