package com.studymate.controller;

import com.studymate.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    /**
     * Chatbot académico
     */
    @PostMapping("/chatbot")
    public ResponseEntity<Map<String, Object>> chatbot(@RequestBody Map<String, String> request) {
        String pregunta = request.get("pregunta");
        String contexto = request.get("contexto");
        
        if (pregunta == null || pregunta.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "La pregunta es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> respuesta = aiService.chatbot(pregunta, contexto);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Generar plan de estudio
     */
    @PostMapping("/plan-estudio")
    public ResponseEntity<Map<String, Object>> generarPlanEstudio(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> materias = (List<String>) request.get("materias");
        Integer horasDisponibles = (Integer) request.get("horasDisponibles");

        if (materias == null || materias.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Las materias son obligatorias");
            return ResponseEntity.badRequest().body(error);
        }

        if (horasDisponibles == null || horasDisponibles <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Las horas disponibles deben ser mayores a 0");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> plan = aiService.generarPlanEstudio(materias, horasDisponibles);
        return ResponseEntity.ok(plan);
    }

    /**
     * Resumir PDF
     */
    @PostMapping("/resumir-pdf")
    public ResponseEntity<Map<String, Object>> resumirPDF(@RequestBody Map<String, String> request) {
        String contenidoPDF = request.get("contenido");

        if (contenidoPDF == null || contenidoPDF.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "El contenido del PDF es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> resumen = aiService.resumirPDF(contenidoPDF);
        return ResponseEntity.ok(resumen);
    }

    /**
     * Resumir PDF desde archivo
     */
    @PostMapping("/resumir-pdf-archivo")
    public ResponseEntity<Map<String, Object>> resumirPDFArchivo(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "El archivo PDF es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        if (!archivo.getContentType().equals("application/pdf")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "El archivo debe ser un PDF");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Map<String, Object> resumen = aiService.resumirPDFDesdeArchivo(archivo);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al procesar el archivo PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Health check para IA
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "AI Service is running");
        response.put("services", List.of("chatbot", "plan-estudio", "resumir-pdf"));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Probar configuración de OpenRouter
     */
    @PostMapping("/test-openrouter")
    public ResponseEntity<Map<String, Object>> testOpenRouter(@RequestBody Map<String, String> request) {
        String testMessage = request.get("message");
        
        if (testMessage == null || testMessage.trim().isEmpty()) {
            testMessage = "Hola, ¿cómo estás?";
        }

        try {
            String response = aiService.testOpenRouterConnection(testMessage);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", testMessage);
            result.put("response", response);
            result.put("timestamp", System.currentTimeMillis());
            result.put("status", "success");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error probando OpenRouter: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
