package com.studymate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para OpenRouter API
 */
@Configuration
@ConfigurationProperties(prefix = "openrouter")
@Data
public class OpenRouterConfig {
    
    /**
     * URL base de la API de OpenRouter
     */
    private String baseUrl = "https://openrouter.ai/api/v1";
    
    /**
     * API Key de OpenRouter
     */
    private String apiKey;
    
    /**
     * Modelo de IA a usar (por defecto: gpt-3.5-turbo)
     */
    private String model = "openai/gpt-3.5-turbo";
    
    /**
     * Temperatura para la generación (0.0 a 2.0)
     */
    private Double temperature = 0.7;
    
    /**
     * Máximo número de tokens en la respuesta
     */
    private Integer maxTokens = 1000;
}
