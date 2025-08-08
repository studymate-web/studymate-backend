package com.studymate.controller;

import com.studymate.model.Materia;
import com.studymate.service.MateriaService;
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
@RequestMapping("/materias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MateriaController {

    private final MateriaService materiaService;
    
    // Almacenamiento temporal en memoria
    private static final Map<Long, Map<String, Object>> materiasEnMemoria = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Obtener todas las materias del usuario
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerMaterias() {
        Map<String, Object> response = new HashMap<>();
        response.put("materias", materiasEnMemoria.values());
        response.put("message", "Materias obtenidas correctamente");
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    /**
     * Crear una nueva materia
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearMateria(@Valid @RequestBody Map<String, Object> request) {
        try {
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            String profesor = (String) request.get("profesor");
            String horario = (String) request.get("horario");
            String color = (String) request.get("color");

            if (nombre == null || nombre.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El nombre es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear materia en memoria
            Long id = idCounter.getAndIncrement();
            Map<String, Object> materia = new HashMap<>();
            materia.put("id", id);
            materia.put("nombre", nombre);
            materia.put("descripcion", descripcion);
            materia.put("profesor", profesor);
            materia.put("horario", horario);
            materia.put("color", color);
            materia.put("activa", true);
            materia.put("fechaCreacion", java.time.LocalDateTime.now().toString());

            materiasEnMemoria.put(id, materia);
            
            Map<String, Object> response = new HashMap<>();
            response.put("materia", materia);
            response.put("message", "Materia creada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al crear materia: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener una materia específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerMateria(@PathVariable Long id) {
        try {
            if (!materiasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Materia no encontrada");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> materia = materiasEnMemoria.get(id);
            return ResponseEntity.ok(materia);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener materia: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar una materia existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarMateria(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            if (!materiasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Materia no encontrada");
                return ResponseEntity.notFound().build();
            }

            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            String profesor = (String) request.get("profesor");
            String horario = (String) request.get("horario");
            String color = (String) request.get("color");

            Map<String, Object> materia = materiasEnMemoria.get(id);
            materia.put("nombre", nombre);
            materia.put("descripcion", descripcion);
            materia.put("profesor", profesor);
            materia.put("horario", horario);
            materia.put("color", color);
            materia.put("fechaActualizacion", java.time.LocalDateTime.now().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("materia", materia);
            response.put("message", "Materia actualizada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al actualizar materia: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Desactivar una materia
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarMateria(@PathVariable Long id) {
        try {
            if (!materiasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Materia no encontrada");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> materia = materiasEnMemoria.get(id);
            materia.put("activa", false);
            materia.put("fechaDesactivacion", java.time.LocalDateTime.now().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("materia", materia);
            response.put("message", "Materia desactivada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al desactivar materia: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar una materia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarMateria(@PathVariable Long id) {
        try {
            if (!materiasEnMemoria.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Materia no encontrada");
                return ResponseEntity.notFound().build();
            }

            materiasEnMemoria.remove(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Materia eliminada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al eliminar materia: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }
}