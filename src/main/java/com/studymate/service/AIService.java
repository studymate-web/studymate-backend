package com.studymate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Servicio que maneja las funcionalidades de IA del sistema StudyMate
 * Integrado con OpenRouter API para respuestas de IA real
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final OpenRouterService openRouterService;
    private final Random random = new Random();

    /**
     * Chatbot académico que responde preguntas educativas usando OpenRouter
     * @param pregunta La pregunta del usuario
     * @param contexto Contexto adicional para la respuesta
     * @return Respuesta estructurada con información académica
     */
    public Map<String, Object> chatbot(String pregunta, String contexto) {
        try {
            // Construir prompt del sistema
            String systemPrompt = """
                Eres un asistente académico experto llamado StudyMate AI. Tu objetivo es ayudar a estudiantes universitarios con sus dudas académicas.
                
                Instrucciones:
                - Responde de manera clara, educativa y amigable
                - Proporciona ejemplos prácticos cuando sea posible
                - Si no sabes algo, sé honesto y sugiere recursos adicionales
                - Usa un tono motivacional y alentador
                - Mantén las respuestas concisas pero informativas
                
                Contexto adicional: """ + (contexto != null ? contexto : "Sin contexto adicional");
            
            // Enviar petición a OpenRouter
            String respuesta = openRouterService.sendSimpleRequest(pregunta, systemPrompt);
            
            Map<String, Object> result = new HashMap<>();
            result.put("respuesta", respuesta);
            result.put("pregunta", pregunta);
            result.put("contexto", contexto);
            result.put("timestamp", System.currentTimeMillis());
            result.put("modelo", "OpenRouter AI");
            
            return result;
        } catch (Exception e) {
            log.error("Error en el chatbot", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error en el chatbot: " + e.getMessage());
            return error;
        }
    }

    /**
     * Genera un plan de estudio personalizado basado en materias y horas disponibles usando OpenRouter
     * @param materias Lista de materias a incluir en el plan
     * @param horasDisponibles Horas totales disponibles por semana
     * @return Plan de estudio estructurado
     */
    public Map<String, Object> generarPlanEstudio(List<String> materias, int horasDisponibles) {
        try {
            // Construir prompt del sistema
            String systemPrompt = """
                Eres un experto en planificación académica y técnicas de estudio. Tu objetivo es crear planes de estudio personalizados y efectivos.
                
                Instrucciones:
                - Crea planes de estudio estructurados y realistas
                - Distribuye el tiempo de manera equilibrada entre materias
                - Incluye técnicas de estudio efectivas (Pomodoro, repaso espaciado, etc.)
                - Proporciona consejos específicos para cada materia
                - Incluye tiempo para descanso y actividades de repaso
                - Usa formato claro y fácil de seguir
                """;
            
            // Construir mensaje del usuario
            String materiasStr = String.join(", ", materias);
            String userMessage = String.format("""
                Necesito un plan de estudio semanal personalizado con las siguientes características:
                
                Materias: %s
                Horas disponibles por semana: %d
                
                Por favor, crea un plan detallado que incluya:
                1. Distribución de horas por materia
                2. Horarios recomendados
                3. Técnicas de estudio específicas
                4. Consejos para optimizar el aprendizaje
                5. Objetivos semanales por materia
                
                Responde en formato estructurado y fácil de leer.
                """, materiasStr, horasDisponibles);
            
            // Enviar petición a OpenRouter
            String plan = openRouterService.sendSimpleRequest(userMessage, systemPrompt);
            
            Map<String, Object> result = new HashMap<>();
            result.put("plan", plan);
            result.put("materias", materias);
            result.put("horasDisponibles", horasDisponibles);
            result.put("timestamp", System.currentTimeMillis());
            result.put("modelo", "OpenRouter AI");
            
            return result;
        } catch (Exception e) {
            log.error("Error generando plan de estudio", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error generando plan: " + e.getMessage());
            return error;
        }
    }

    /**
     * Genera un resumen de contenido académico usando OpenRouter
     * @param contenidoPDF Contenido a resumir
     * @return Resumen estructurado del contenido
     */
    public Map<String, Object> resumirPDF(String contenidoPDF) {
        try {
            // Construir prompt del sistema
            String systemPrompt = """
                Eres un experto en análisis y síntesis de contenido académico. Tu objetivo es crear resúmenes claros, concisos y estructurados.
                
                Instrucciones:
                - Identifica los puntos clave y conceptos principales
                - Organiza la información de manera lógica
                - Incluye definiciones importantes y ejemplos relevantes
                - Mantén un tono académico pero accesible
                - Destaca las ideas más importantes
                - Usa formato estructurado con títulos y subtítulos
                - Incluye palabras clave relevantes
                """;
            
            // Construir mensaje del usuario
            String userMessage = String.format("""
                Por favor, crea un resumen ejecutivo del siguiente contenido académico:
                
                CONTENIDO:
                %s
                
                El resumen debe incluir:
                1. Puntos clave principales
                2. Conceptos fundamentales
                3. Ideas importantes
                4. Palabras clave relevantes
                
                Responde en formato estructurado y fácil de leer.
                """, contenidoPDF);
            
            // Enviar petición a OpenRouter
            String resumen = openRouterService.sendSimpleRequest(userMessage, systemPrompt);
            
            Map<String, Object> result = new HashMap<>();
            result.put("resumen", resumen);
            result.put("contenidoOriginal", contenidoPDF);
            result.put("timestamp", System.currentTimeMillis());
            result.put("modelo", "OpenRouter AI");
            
            return result;
        } catch (Exception e) {
            log.error("Error resumiendo PDF", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error resumiendo PDF: " + e.getMessage());
            return error;
        }
    }

    /**
     * Prueba la conexión con OpenRouter
     * @param testMessage Mensaje de prueba
     * @return Respuesta de prueba
     */
    public String testOpenRouterConnection(String testMessage) {
        try {
            String systemPrompt = """
                Eres un asistente de prueba para verificar la conexión con OpenRouter API.
                Responde de manera simple y amigable.
                """;
            
            return openRouterService.sendSimpleRequest(testMessage, systemPrompt);
        } catch (Exception e) {
            log.error("Error probando conexión con OpenRouter", e);
            return "Error de conexión: " + e.getMessage();
        }
    }

    /**
     * Resumir PDF desde archivo usando OpenRouter
     * @param archivo Archivo PDF a procesar
     * @return Resumen estructurado del contenido del PDF
     */
    public Map<String, Object> resumirPDFDesdeArchivo(MultipartFile archivo) {
        try {
            // Extraer texto real del PDF usando Apache PDFBox
            String contenidoPDF = extraerTextoDePDF(archivo);
            
            if (contenidoPDF.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "No se pudo extraer texto del archivo PDF");
                return error;
            }
            
            log.info("Texto extraído del PDF: {} caracteres", contenidoPDF.length());
            
            // Usar el método existente para resumir
            return resumirPDF(contenidoPDF);
        } catch (Exception e) {
            log.error("Error al procesar archivo PDF", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al procesar archivo PDF: " + e.getMessage());
            return error;
        }
    }

    /**
     * Extrae texto de un archivo PDF usando Apache PDFBox
     * @param archivo Archivo PDF
     * @return Texto extraído del PDF
     */
    private String extraerTextoDePDF(MultipartFile archivo) throws Exception {
        try (PDDocument document = PDDocument.load(archivo.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            
            String texto = stripper.getText(document);
            
            // Limpiar el texto extraído
            texto = limpiarTextoPDF(texto);
            
            return texto;
        }
    }

    /**
     * Limpia el texto extraído del PDF
     * @param texto Texto original del PDF
     * @return Texto limpio
     */
    private String limpiarTextoPDF(String texto) {
        if (texto == null) return "";
        
        return texto
            .replaceAll("\\s+", " ") // Reemplazar múltiples espacios con uno solo
            .replaceAll("\\n\\s*\\n", "\n\n") // Limpiar líneas vacías múltiples
            .trim();
    }

}
