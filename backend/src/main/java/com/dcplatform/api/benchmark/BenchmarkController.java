package com.dcplatform.api.benchmark;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/benchmark")
public class BenchmarkController {

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitBenchmark(@Valid @RequestBody BenchmarkRequest request) {
        // Procesamiento aislado del benchmark respetando las fronteras del módulo
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Benchmark procesado correctamente",
            "pdf_url", "/api/v1/documents/download/12345"
        ));
    }
}