package com.dcplatform.api.calculator;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {

    @PostMapping("/estimate")
    public ResponseEntity<CalculatorResponse> estimate(@Valid @RequestBody CalculatorRequest request) {
        BigDecimal carga = request.getCargaTermicaKw();
        
        // Lógica de estimación básica
        BigDecimal consumoEstimadoKwh = carga.multiply(new BigDecimal("720")); 
        BigDecimal costoEstimado = consumoEstimadoKwh.multiply(new BigDecimal("0.15")); 

        // Creamos la respuesta usando el constructor con los dos argumentos
        CalculatorResponse response = new CalculatorResponse(consumoEstimadoKwh, costoEstimado);

        return ResponseEntity.ok(response);
    }
}