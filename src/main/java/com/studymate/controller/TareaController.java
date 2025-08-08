package com.studymate.controller;

import com.studymate.model.Tarea;
import com.studymate.service.TareaService;
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
@RequestMapping("/tareas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaService tareaService;
    
    // Almacenamiento temporal en memoria
    private static final Map<Long, Map<String, Object>> tareasEnMemoria = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Obtener todas las tareas del usuario
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTareas() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("tareas", new java.util.ArrayList<>(tareasEnMemoria.values()));
            response.put("message", "Tareas obtenidas correctamente");
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener tareas: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Crear una nueva tarea
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTarea(@Valid @RequestBody Map<String, Object> request) {
        try {
            String titulo = (String) request.get("titulo");
            String descripcion = (String) request.get("descripcion");
            String materia = (String) request.get("materia");
            String fechaVencimiento = (String) request.get("fechaVencimiento");
            String prioridad = (String) request.get("prioridad");

            if (titulo == null || titulo.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El título es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear tarea en memoria
            Long id = idCounter.getAndIncrement();
            Map<String, Object> tarea = new HashMap<>();
            tarea.put("id", id);
            tarea.put("titulo", titulo);
            tarea.put("descripcion", descripcion);
            tarea.put("materia", materia);
            tarea.put("fechaVencimiento", fechaVencimiento);
            tarea.put("prioridad", prioridad);
            tarea.put("completada", false);
            tarea.put("fechaCreacion", java.time.LocalDateTime.now().toString());

            tareasEnMemoria.put(id, tarea);

            Map<String, Object> response = new HashMap<>();
            response.put("tarea", tarea);
            response.put("message", "Tarea creada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al crear tarea: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener una tarea específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerTarea(@PathVariable Long id) {
        try {
            if (!tareasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tarea no encontrada");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> tarea = tareasEnMemoria.get(id);
            return ResponseEntity.ok(tarea);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener tarea: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una tarea existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarTarea(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            if (!tareasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tarea no encontrada");
                return ResponseEntity.notFound().build();
            }

            String titulo = (String) request.get("titulo");
            String descripcion = (String) request.get("descripcion");
            String materia = (String) request.get("materia");
            String fechaVencimiento = (String) request.get("fechaVencimiento");
            String prioridad = (String) request.get("prioridad");

            Map<String, Object> tarea = tareasEnMemoria.get(id);
            tarea.put("titulo", titulo);
            tarea.put("descripcion", descripcion);
            tarea.put("materia", materia);
            tarea.put("fechaVencimiento", fechaVencimiento);
            tarea.put("prioridad", prioridad);
            tarea.put("fechaActualizacion", java.time.LocalDateTime.now().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("tarea", tarea);
            response.put("message", "Tarea actualizada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al actualizar tarea: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Marcar tarea como completada
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Map<String, Object>> completarTarea(@PathVariable Long id) {
        try {
            if (!tareasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tarea no encontrada");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> tarea = tareasEnMemoria.get(id);
            tarea.put("completada", true);
            tarea.put("fechaCompletada", java.time.LocalDateTime.now().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("tarea", tarea);
            response.put("message", "Tarea marcada como completada");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al completar tarea: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una tarea
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarTarea(@PathVariable Long id) {
        try {
            if (!tareasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tarea no encontrada");
                return ResponseEntity.notFound().build();
            }

            tareasEnMemoria.remove(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tarea eliminada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al eliminar tarea: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }
}