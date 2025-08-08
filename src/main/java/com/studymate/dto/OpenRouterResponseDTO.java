package com.studymate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO para las respuestas de OpenRouter API
 */
@Data
public class OpenRouterResponseDTO {
    
    /**
     * ID de la respuesta
     */
    private String id;
    
    /**
     * Tipo de objeto
     */
    private String object;
    
    /**
     * Timestamp de creación
     */
    private Long created;
    
    /**
     * Modelo usado
     */
    private String model;
    
    /**
     * Lista de opciones de respuesta
     */
    private List<Choice> choices;
    
    /**
     * Información de uso
     */
    private Usage usage;
    
    /**
     * Clase interna para representar una opción de respuesta
     */
    @Data
    public static class Choice {
        /**
         * Índice de la opción
         */
        private Integer index;
        
        /**
         * Mensaje de la respuesta
         */
        private Message message;
        
        /**
         * Razón de finalización
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }
    
    /**
     * Clase interna para representar un mensaje
     */
    @Data
    public static class Message {
        /**
         * Rol del mensaje
         */
        private String role;
        
        /**
         * Contenido del mensaje
         */
        private String content;
    }
    
    /**
     * Clase interna para representar información de uso
     */
    @Data
    public static class Usage {
        /**
         * Tokens de prompt
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        /**
         * Tokens de completación
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        /**
         * Total de tokens
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
