package com.studymate.controller;

import com.studymate.dto.MateriaDTO;
import com.studymate.model.Materia;
import com.studymate.model.Usuario;
import com.studymate.service.JwtService;
import com.studymate.service.MateriaService;
import com.studymate.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MateriaController {

    private final MateriaService materiaService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    private Long getUsuarioIdDesdeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerMaterias(HttpServletRequest request) {
        Long usuarioId = getUsuarioIdDesdeToken(request);
        List<Materia> materias = materiaService.buscarPorUsuario(usuarioId);
        Map<String, Object> response = new HashMap<>();
        response.put("materias", materias);
        response.put("message", "Materias obtenidas correctamente");
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearMateria(HttpServletRequest request,
            @Valid @RequestBody MateriaDTO materiaDTO) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Materia materia = materiaService.crearMateria(materiaDTO, usuarioId);
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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMateria(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Materia materia = materiaService.buscarPorId(id).orElse(null);
            if (materia == null || !materia.getUsuario().getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Materia no encontrada"));
            }
            return ResponseEntity.ok(materia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al obtener materia: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMateria(HttpServletRequest request, @PathVariable Long id,
            @Valid @RequestBody MateriaDTO materiaDTO) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            Materia existente = materiaService.buscarPorId(id).orElse(null);
            if (existente == null || !existente.getUsuario().getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Materia no encontrada"));
            }
            materiaDTO.setId(id);
            Materia actualizada = materiaService.actualizarMateria(materiaDTO);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al actualizar materia: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMateria(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long usuarioId = getUsuarioIdDesdeToken(request);
            materiaService.eliminarMateria(id, usuarioId);
            return ResponseEntity.ok(Map.of(
                    "message", "Materia eliminada exitosamente",
                    "status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error al eliminar materia: " + e.getMessage(),
                    "status", "ERROR"));
        }
    }
}