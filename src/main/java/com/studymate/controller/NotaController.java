package com.studymate.controller;

import com.studymate.model.Materia;
import com.studymate.model.Nota;
import com.studymate.model.Usuario;
import com.studymate.service.JwtService;
import com.studymate.service.MateriaService;
import com.studymate.service.NotaService;
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
@RequestMapping("/notas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotaController {

    private final NotaService notaService;
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
    public ResponseEntity<Map<String, Object>> obtenerNotas(HttpServletRequest request) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            List<Nota> notas = notaService.buscarPorUsuario(usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("notas", notas);
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearNota(HttpServletRequest request, @Valid @RequestBody Nota nota) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            if (nota.getMateria() != null && nota.getMateria().getId() != null) {
                Materia materia = materiaService.buscarPorId(nota.getMateria().getId())
                        .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
                if (!materia.getUsuario().getId().equals(usuarioId)) {
                    throw new RuntimeException("La materia no pertenece al usuario");
                }
                nota.setMateria(materia);
            }
            Nota creada = notaService.crearNota(nota, usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("nota", creada);
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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerNota(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Nota nota = notaService.buscarPorId(id).orElse(null);
            if (nota == null || !nota.getUsuario().getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Nota no encontrada"));
            }
            return ResponseEntity.ok(nota);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al obtener nota: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarNota(HttpServletRequest request, @PathVariable Long id,
            @Valid @RequestBody Nota nota) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);

            // Log para debugging
            System.out.println("=== Actualizando nota ===");
            System.out.println("ID de nota: " + id);
            System.out.println("Usuario ID: " + usuarioId);
            System.out.println("Título: " + nota.getTitulo());
            System.out.println("Contenido: " + nota.getContenido());
            System.out.println("Materia: " + (nota.getMateria() != null ? nota.getMateria().getId() : "null"));

            if (nota.getId() == null)
                nota.setId(id);
            Nota actualizada = notaService.actualizarNota(nota, usuarioId);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            e.printStackTrace(); // Log del error completo
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al actualizar nota: " + e.getMessage(),
                    "status", "ERROR",
                    "error", e.getClass().getSimpleName()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNota(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            notaService.eliminarNota(id, usuarioId);
            return ResponseEntity.ok(Map.of(
                    "message", "Nota eliminada exitosamente",
                    "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al eliminar nota: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }
}