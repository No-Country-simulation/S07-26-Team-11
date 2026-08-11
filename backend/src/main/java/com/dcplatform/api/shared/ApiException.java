package com.dcplatform.api.shared;

import org.springframework.http.HttpStatus;

/**
 * Excepcion de negocio. El manejador global la traduce al formato
 * RFC 9457 Problem Details definido en docs/API.md
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String type;

    public ApiException(HttpStatus status, String type, String message) {
        super(message);
        this.status = status;
        this.type = type;
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "not-found", message);
    }

    public static ApiException businessRule(String message) {
        return new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "business-rule", message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, "conflict", message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "auth", message);
    }
       public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "bad-request", message);
    }
    public static ApiException ratLimited(String message) {
        return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "rate-limited", message);
    }
    public static ApiException noConsent(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, "no-consent", message);
    }


    public HttpStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
