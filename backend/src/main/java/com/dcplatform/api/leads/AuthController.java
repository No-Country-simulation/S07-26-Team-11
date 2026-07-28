package com.dcplatform.api.leads;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/request-access")
    public ResponseEntity<Map<String, String>> requestAccess(@Valid @RequestBody AuthRequest request) {
        // Aquí se procesa o guarda el email del operador en la base de datos
        // Por ahora retornamos una respuesta exitosa simulando el envío o registro
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Acceso solicitado correctamente para el correo: " + request.getEmail()
        ));
    }
}