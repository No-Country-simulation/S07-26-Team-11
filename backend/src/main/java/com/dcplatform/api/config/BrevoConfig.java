package com.dcplatform.api.config;

import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import sendinblue.ApiClient;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;

@Configuration
public class BrevoConfig {

    @Value("${app.email.api-key:}")
    private String apiKey;

    @Bean
    public TransactionalEmailsApi transactionalEmailsApi() {
        ApiClient defaultClient = sendinblue.Configuration.getDefaultApiClient();

        // Configurar la autenticación mediante la API Key
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        return new TransactionalEmailsApi(defaultClient);
    }
}
