package com.dcplatform.api.benchmark;

import org.springframework.stereotype.Service;

@Service
public class BenchmarkService {

    public BenchmarkResponse processBenchmark(BenchmarkRequest request) {
        // Lógica de cálculo interna (los pesos y métricas no se exponen al cliente)
        double pue = request.pue();
        
        // Simulación de scoring basado en el PUE (menor PUE es mejor eficiencia)
        double score = Math.max(0.0, Math.min(100.0, 100.0 - ((pue - 1.2) * 50.0)));
        
        String category;
        String recommendation;
        
        if (score >= 85.0) {
            category = "World-Class Efficiency";
            recommendation = "El Data Center opera con niveles óptimos de eficiencia energética.";
        } else if (score >= 60.0) {
            category = "Standard Operation";
            recommendation = "Existen oportunidades de mejora en la gestión térmica y de carga.";
        } else {
            category = "High Optimization Potential";
            recommendation = "Se recomienda una auditoría integral para reducir el consumo y optimizar el PUE.";
        }

        return new BenchmarkResponse(
                score,
                pue,
                category,
                recommendation
        );
    }
}