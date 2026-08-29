package com.dcplatform.api.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {
	@Value("${gemini.api.key}")
	private String apiKey;

	@Value("${gemini.ai.model}")
	private String aiModel;

	@Value("${gemini.ai.temperature}")
	private double temperature;

	@Bean
	public ChatModel chatLanguageModel() {
		return GoogleAiGeminiChatModel.builder()
				.apiKey(apiKey)
				.modelName(aiModel)
				.temperature(temperature)
				.build();
	}
}
