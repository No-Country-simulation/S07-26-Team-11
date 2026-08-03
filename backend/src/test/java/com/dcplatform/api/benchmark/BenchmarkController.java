package com.dcplatform.api.benchmark;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/benchmark")
@Tag(name = "Benchmark", description = "Instrumento y scoring de eficiencia del Data Center")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @PostMapping
    @Operation(summary = "Procesar evaluación de benchmark", description = "Calcula el scoring y percentiles en el servidor sin exponer los pesos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación procesada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<BenchmarkResponse> evaluate(@Valid @RequestBody BenchmarkRequest request) {
        BenchmarkResponse response = benchmarkService.processBenchmark(request);
        return ResponseEntity.ok(response);
    }
}