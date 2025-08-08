package com.studymate.controller;

import com.studymate.model.Nota;
import com.studymate.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/notas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotaController {

    private final NotaService notaService;
    
    // Almacenamiento temporal en memoria
    private static final Map<Long, Map<String, Object>> notasEnMemoria = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Obtener todas las notas del usuario
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerNotas() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("notas", new java.util.ArrayList<>(notasEnMemoria.values()));
            response.put("message", "Notas obtenidas correctamente");
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener notas: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva nota
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearNota(@Valid @RequestBody Map<String, Object> request) {
        try {
            String titulo = (String) request.get("titulo");
            String contenido = (String) request.get("contenido");
            String materia = (String) request.get("materia");

            if (titulo == null || titulo.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El título es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear nota en memoria
            Long id = idCounter.getAndIncrement();
            Map<String, Object> nota = new HashMap<>();
            nota.put("id", id);
            nota.put("titulo", titulo);
            nota.put("contenido", contenido);
            nota.put("materia", materia);
            nota.put("fechaCreacion", java.time.LocalDateTime.now().toString());

            notasEnMemoria.put(id, nota);

            Map<String, Object> response = new HashMap<>();
            response.put("nota", nota);
            response.put("message", "Nota creada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al crear nota: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener una nota específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerNota(@PathVariable Long id) {
        try {
            if (!notasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Nota no encontrada");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> nota = notasEnMemoria.get(id);
            return ResponseEntity.ok(nota);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener nota: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una nota existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarNota(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            if (!notasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Nota no encontrada");
                return ResponseEntity.notFound().build();
            }

            String titulo = (String) request.get("titulo");
            String contenido = (String) request.get("contenido");
            String materia = (String) request.get("materia");

            Map<String, Object> nota = notasEnMemoria.get(id);
            nota.put("titulo", titulo);
            nota.put("contenido", contenido);
            nota.put("materia", materia);
            nota.put("fechaActualizacion", java.time.LocalDateTime.now().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("nota", nota);
            response.put("message", "Nota actualizada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al actualizar nota: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una nota
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarNota(@PathVariable Long id) {
        try {
            if (!notasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Nota no encontrada");
                return ResponseEntity.notFound().build();
            }

            notasEnMemoria.remove(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Nota eliminada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al eliminar nota: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }
}