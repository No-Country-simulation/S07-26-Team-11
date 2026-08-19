package com.dcplatform.api.magiclink.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

   
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> emailBuckets = new ConcurrentHashMap<>();

    public boolean tryConsumeIp(String ip) {
        Bucket bucket = ipBuckets.computeIfAbsent(ip, this::createIpBucket);
        return bucket.tryConsume(1);
    }

    public boolean tryConsumeEmail(String email) {
        Bucket bucket = emailBuckets.computeIfAbsent(email.toLowerCase(), this::createEmailBucket);
        return bucket.tryConsume(1);
    }

    private Bucket createIpBucket(String key) {
        
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofHours(1))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createEmailBucket(String key) {

        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofHours(1))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }
}
