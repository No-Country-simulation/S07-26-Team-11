package com.dcplatform.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingPathVariableException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura cuando mandan un parámetro con formato inválido en la URL (ej: UUID con letras "hola") -> Devuelve 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detailMessage = String.format("El parámetro '%s' tiene un formato inválido.", ex.getName());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
        problemDetail.setTitle("Solicitud Incorrecta");
        problemDetail.setType(URI.create("https://api.dcplatform.com/errors/bad-request"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // Captura cuando falta un parámetro obligatorio en la URL -> Devuelve 400 Bad Request
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ProblemDetail> handleMissingPathVariable(MissingPathVariableException ex) {
        String detailMessage = String.format("Falta el parámetro obligatorio en la ruta: '%s'.", ex.getVariableName());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
        problemDetail.setTitle("Solicitud Incorrecta");
        problemDetail.setType(URI.create("https://api.dcplatform.com/errors/bad-request"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}