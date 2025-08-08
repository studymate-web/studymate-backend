package com.studymate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.config.OpenRouterConfig;
import com.studymate.dto.OpenRouterRequestDTO;
import com.studymate.dto.OpenRouterResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para interactuar con OpenRouter API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenRouterService {

    private final OpenRouterConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Envía una petición a OpenRouter API
     * @param messages Lista de mensajes para el chat
     * @param systemPrompt Prompt del sistema (opcional)
     * @return Respuesta de la IA
     */
    public String sendRequest(List<String> messages, String systemPrompt) {
        try {
            log.info("Configuración OpenRouter: API Key={}, Model={}, Temperature={}, MaxTokens={}", 
                config.getApiKey() != null ? "Configurada" : "No configurada",
                config.getModel(),
                config.getTemperature(),
                config.getMaxTokens());
            
            // Validar configuración
            if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
                log.warn("OpenRouter API Key no configurada, usando respuestas simuladas");
                return "Lo siento, el servicio de IA no está configurado correctamente. Por favor, contacta al administrador.";
            }

            // Construir la petición
            OpenRouterRequestDTO request = new OpenRouterRequestDTO();
            request.setModel(config.getModel());
            request.setTemperature(config.getTemperature());
            request.setMaxTokens(config.getMaxTokens());

            // Construir mensajes
            List<OpenRouterRequestDTO.Message> requestMessages = new ArrayList<>();
            
            // Agregar prompt del sistema si existe
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                OpenRouterRequestDTO.Message systemMessage = new OpenRouterRequestDTO.Message();
                systemMessage.setRole("system");
                systemMessage.setContent(systemPrompt);
                requestMessages.add(systemMessage);
            }

            // Agregar mensajes del usuario
            for (String message : messages) {
                OpenRouterRequestDTO.Message userMessage = new OpenRouterRequestDTO.Message();
                userMessage.setRole("user");
                userMessage.setContent(message);
                requestMessages.add(userMessage);
            }

            request.setMessages(requestMessages);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getApiKey());
            headers.set("HTTP-Referer", "https://studymate-ai.com");
            headers.set("X-Title", "StudyMate AI");

            // Crear entidad HTTP
            HttpEntity<OpenRouterRequestDTO> entity = new HttpEntity<>(request, headers);

            // Hacer la petición
            String url = config.getBaseUrl() + "/chat/completions";
            log.info("Enviando petición a OpenRouter: URL={}, Request={}", url, request);
            
            ResponseEntity<OpenRouterResponseDTO> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                OpenRouterResponseDTO.class
            );

            // Procesar respuesta
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OpenRouterResponseDTO responseBody = response.getBody();
                log.info("Respuesta de OpenRouter recibida: {}", responseBody);
                if (responseBody.getChoices() != null && !responseBody.getChoices().isEmpty()) {
                    String content = responseBody.getChoices().get(0).getMessage().getContent();
                    log.info("Contenido de la respuesta: {}", content);
                    return content;
                } else {
                    log.error("No se encontraron choices en la respuesta: {}", responseBody);
                }
            } else {
                log.error("Error en respuesta de OpenRouter: Status={}, Body={}", 
                    response.getStatusCode(), response.getBody());
            }

            return "Lo siento, hubo un error al procesar tu solicitud. Por favor, intenta de nuevo.";

        } catch (Exception e) {
            log.error("Error al comunicarse con OpenRouter API", e);
            return "Lo siento, hubo un error de conexión con el servicio de IA. Por favor, intenta de nuevo más tarde.";
        }
    }

    /**
     * Envía una petición simple a OpenRouter
     * @param message Mensaje del usuario
     * @param systemPrompt Prompt del sistema (opcional)
     * @return Respuesta de la IA
     */
    public String sendSimpleRequest(String message, String systemPrompt) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        return sendRequest(messages, systemPrompt);
    }
}
