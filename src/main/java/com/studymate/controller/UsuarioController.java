package com.studymate.controller;

import com.studymate.dto.UsuarioDTO;
import com.studymate.model.Usuario;
import com.studymate.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    // Almacenamiento en memoria para actividades
    private static final Map<Long, Map<String, Object>> actividadesEnMemoria = new ConcurrentHashMap<>();
    private static final AtomicLong actividadIdCounter = new AtomicLong(1);

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint funcionando correctamente");
        response.put("received", request);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            System.out.println("=== INICIO REGISTRO ===");
            System.out.println("DTO recibido: " + usuarioDTO.toString());
            
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
            System.out.println("Usuario creado: " + nuevoUsuario.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario creado exitosamente");
            response.put("usuario", nuevoUsuario);
            response.put("status", "SUCCESS");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("ERROR EN REGISTRO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al crear usuario: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id);
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuariosActivos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtiene la actividad reciente del usuario
     */
    @GetMapping("/actividad")
    public ResponseEntity<Map<String, Object>> obtenerActividad() {
        Map<String, Object> response = new HashMap<>();
        // Convertir a lista y ordenar por fecha descendente (más reciente primero)
        List<Map<String, Object>> actividadesOrdenadas = actividadesEnMemoria.values()
            .stream()
            .sorted((a1, a2) -> {
                String fecha1 = (String) a1.get("fecha");
                String fecha2 = (String) a2.get("fecha");
                return fecha2.compareTo(fecha1); // Orden descendente
            })
            .collect(java.util.stream.Collectors.toList());
        
        response.put("actividades", actividadesOrdenadas);
        response.put("message", "Actividad obtenida correctamente");
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Agregar una nueva actividad
     */
    @PostMapping("/actividad")
    public ResponseEntity<Map<String, Object>> agregarActividad(@RequestBody Map<String, Object> request) {
        try {
            String tipo = (String) request.get("tipo");
            String descripcion = (String) request.get("descripcion");
            
            if (tipo == null || descripcion == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tipo y descripción son obligatorios");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Crear actividad en memoria
            Long id = actividadIdCounter.getAndIncrement();
            Map<String, Object> actividad = new HashMap<>();
            actividad.put("id", id);
            actividad.put("tipo", tipo);
            actividad.put("descripcion", descripcion);
            actividad.put("fecha", java.time.LocalDateTime.now().toString());
            
            actividadesEnMemoria.put(id, actividad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("actividad", actividad);
            response.put("message", "Actividad agregada exitosamente");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al agregar actividad: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }
}