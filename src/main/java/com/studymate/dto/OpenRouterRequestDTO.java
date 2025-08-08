package com.studymate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO para las peticiones a OpenRouter API
 */
@Data
public class OpenRouterRequestDTO {
    
    /**
     * Modelo de IA a usar
     */
    private String model;
    
    /**
     * Lista de mensajes en el formato de chat
     */
    private List<Message> messages;
    
    /**
     * Temperatura para la generación (0.0 a 2.0)
     */
    private Double temperature;
    
    /**
     * Máximo número de tokens en la respuesta
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    /**
     * Clase interna para representar un mensaje
     */
    @Data
    public static class Message {
        /**
         * Rol del mensaje (system, user, assistant)
         */
        private String role;
        
        /**
         * Contenido del mensaje
         */
        private String content;
    }
}
