package com.dcplatform.api.shared;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Un unico formato de error para toda la API (RFC 9457).
 * Regla: nunca exponer trazas ni mensajes internos al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("/errors/validation"));
        problem.setTitle("Datos de entrada invalidos");
        problem.setInstance(URI.create(request.getRequestURI()));

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", String.valueOf(fieldError.getDefaultMessage())))
                .toList();
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApi(ApiException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatus());
        problem.setType(URI.create("/errors/" + ex.getType()));
        problem.setTitle(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        // TODO: registrar la excepcion completa en el log con el traceId,
        //       pero nunca devolverla al cliente.
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("/errors/internal"));
        problem.setTitle("Error interno del servidor");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED.value());
        problem.setType(URI.create("/errors/auth"));
        problem.setTitle("Autenticación");
        problem.setDetail("El token de acceso es inválido, ha expirado o no fue proporcionado");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN.value());
        problem.setType(URI.create("/errors/auth"));
        problem.setTitle("Acceso no permitido");
        problem.setDetail("No se tienen los privilegios necesarios para acceder a esta ruta");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
