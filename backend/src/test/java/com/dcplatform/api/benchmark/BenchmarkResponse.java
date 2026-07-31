package com.dcplatform.api.benchmark;

public record BenchmarkResponse(
        double overallScore,
        double efficiencyRating,
        String category,
        String recommendation
) {}