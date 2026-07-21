package com.dcplatform.api.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint minimo para verificar que el despliegue responde.
 * Es lo primero que debe funcionar en staging, antes que cualquier logica de negocio.
 */
@RestController
@RequestMapping("/api/v1/public")
public class HealthController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "service", "dcplatform-api",
                "timestamp", Instant.now().toString());
    }
}
