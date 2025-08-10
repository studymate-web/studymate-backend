package com.studymate.controller;

import com.studymate.model.Materia;
import com.studymate.model.Tarea;
import com.studymate.model.Usuario;
import com.studymate.service.JwtService;
import com.studymate.service.MateriaService;
import com.studymate.service.TareaService;
import com.studymate.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tareas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaService tareaService;
    private final UsuarioService usuarioService;
    private final MateriaService materiaService;
    private final JwtService jwtService;

    private Long getUsuarioIdDesdeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        return usuario.getId();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTareas(HttpServletRequest request) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            List<Tarea> tareas = tareaService.buscarPorUsuario(usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("tareas", tareas);
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTarea(HttpServletRequest request, @Valid @RequestBody Tarea tarea) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            // Si viene materia con id, validar pertenencia
            if (tarea.getMateria() != null && tarea.getMateria().getId() != null) {
                Materia materia = materiaService.buscarPorId(tarea.getMateria().getId())
                        .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
                if (!materia.getUsuario().getId().equals(usuarioId)) {
                    throw new RuntimeException("La materia no pertenece al usuario");
                }
                tarea.setMateria(materia);
            }
            Tarea creada = tareaService.crearTarea(tarea, usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("tarea", creada);
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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTarea(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Tarea tarea = tareaService.buscarPorId(id).orElse(null);
            if (tarea == null || !tarea.getUsuario().getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Tarea no encontrada"));
            }
            return ResponseEntity.ok(tarea);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al obtener tarea: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTarea(HttpServletRequest request, @PathVariable Long id,
            @Valid @RequestBody Tarea tarea) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            if (tarea.getId() == null)
                tarea.setId(id);
            Tarea actualizada = tareaService.actualizarTarea(tarea, usuarioId);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al actualizar tarea: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<?> completarTarea(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Tarea tarea = tareaService.marcarCompletada(id, usuarioId);
            return ResponseEntity.ok(Map.of(
                    "tarea", tarea,
                    "message", "Tarea marcada como completada",
                    "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al completar tarea: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTarea(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            tareaService.eliminarTarea(id, usuarioId);
            return ResponseEntity.ok(Map.of(
                    "message", "Tarea eliminada exitosamente",
                    "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al eliminar tarea: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }
}