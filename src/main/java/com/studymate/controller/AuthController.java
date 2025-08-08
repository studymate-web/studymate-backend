package com.studymate.controller;

import com.studymate.dto.LoginRequestDTO;
import com.studymate.dto.LoginResponseDTO;
import com.studymate.dto.UsuarioDTO;
import com.studymate.dto.UsuarioResponseDTO;
import com.studymate.model.Usuario;
import com.studymate.service.JwtService;
import com.studymate.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para manejar la autenticación y autorización de usuarios
 * Incluye endpoints para registro y login
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Convierte un Usuario a UsuarioResponseDTO
     */
    private UsuarioResponseDTO convertirAUsuarioResponse(Usuario usuario) {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(usuario.getId());
        responseDTO.setNombre(usuario.getNombre());
        responseDTO.setApellido(usuario.getApellido());
        responseDTO.setEmail(usuario.getEmail());
        responseDTO.setFechaRegistro(usuario.getFechaRegistro());
        responseDTO.setActivo(usuario.getActivo());
        return responseDTO;
    }

    /**
     * Endpoint de prueba para verificar que el servidor esté funcionando
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Backend funcionando correctamente");
        response.put("status", "SUCCESS");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba para verificar el endpoint /me sin autenticación
     */
    @GetMapping("/test-me")
    public ResponseEntity<Map<String, Object>> testMe() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint /me accesible");
        response.put("status", "SUCCESS");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba para registro simple
     */
    @PostMapping("/test-registro")
    public ResponseEntity<Map<String, Object>> testRegistro(@RequestBody Map<String, String> request) {
        try {
            System.out.println("=== TEST REGISTRO ===");
            System.out.println("Datos recibidos: " + request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Datos recibidos correctamente");
            response.put("received", request);
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR EN TEST: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error: " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param usuarioDTO Datos del usuario a registrar
     * @return Respuesta con token JWT y datos del usuario
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            System.out.println("=== INICIO REGISTRO ===");
            System.out.println("DTO recibido: " + usuarioDTO.toString());
            
            // Verificar si el email ya existe
            if (usuarioService.existePorEmail(usuarioDTO.getEmail())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "El email ya está registrado");
                response.put("status", "ERROR");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("Email disponible, encriptando contraseña...");
            // Encriptar contraseña
            usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            
            System.out.println("Creando usuario...");
            Usuario usuario = usuarioService.crearUsuario(usuarioDTO);
            System.out.println("Usuario creado: " + usuario.getId());
            
            System.out.println("Generando token JWT...");
            // Generar token JWT
            String token = jwtService.generateToken(usuario.getEmail());
            System.out.println("Token generado correctamente");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("token", token);
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido() != null ? usuario.getApellido() : "Sin apellido",
                "email", usuario.getEmail(),
                "fechaRegistro", usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : LocalDateTime.now().toString(),
                "activo", usuario.getActivo()
            ));
            response.put("status", "SUCCESS");
            
            System.out.println("Respuesta preparada, enviando...");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("ERROR EN REGISTRO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al registrar usuario: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Autentica un usuario existente
     * @param loginRequest Datos de login (email y password)
     * @return Respuesta con token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Buscar usuario
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(loginRequest.getEmail());
            if (usuarioOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Usuario no encontrado");
                response.put("status", "ERROR");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Usuario usuario = usuarioOpt.get();
            
            // Generar token JWT
            String token = jwtService.generateToken(usuario.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("token", token);
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "email", usuario.getEmail(),
                "fechaRegistro", usuario.getFechaRegistro().toString(),
                "activo", usuario.getActivo()
            ));
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Credenciales inválidas");
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Obtiene información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioActual(HttpServletRequest request) {
        try {
            // Extraer token del header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Token no proporcionado");
                response.put("status", "ERROR");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7); // Remover "Bearer "
            
            // Validar token y extraer email
            String email = jwtService.extractUsername(token);
            if (email == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Token inválido - no se pudo extraer email");
                response.put("status", "ERROR");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (!jwtService.isTokenValid(token, email)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Token inválido");
                response.put("status", "ERROR");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
            if (usuarioOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Usuario no encontrado para email: " + email);
                response.put("status", "ERROR");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Usuario usuario = usuarioOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario obtenido correctamente");
            response.put("status", "SUCCESS");
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "email", usuario.getEmail(),
                "fechaRegistro", usuario.getFechaRegistro().toString(),
                "activo", usuario.getActivo()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener usuario: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("status", "ERROR");
            e.printStackTrace(); // Para ver el stack trace en los logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
