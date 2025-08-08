package com.studymate.service;

import com.studymate.dto.UsuarioDTO;
import com.studymate.model.Usuario;
import com.studymate.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Crear nuevo usuario
    public Usuario crearUsuario(UsuarioDTO usuarioDTO) {
        try {
            System.out.println("=== SERVICE: Creando usuario ===");
            System.out.println("Email a verificar: " + usuarioDTO.getEmail());
            
            // Verificar si el email ya existe
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            
            System.out.println("Email disponible, convirtiendo DTO...");
            Usuario usuario = convertirDTOaEntidad(usuarioDTO);
            usuario.setActivo(true);
            
            System.out.println("Guardando en base de datos...");
            Usuario savedUser = usuarioRepository.save(usuario);
            System.out.println("Usuario guardado con ID: " + savedUser.getId());
            
            return savedUser;
        } catch (Exception e) {
            System.err.println("ERROR EN SERVICE: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndActivoTrue(email);
    }

    // Verificar si existe usuario por email
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // Actualizar usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario (desactivar)
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // Listar todos los usuarios activos
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo)
                .toList();
    }

    // Método auxiliar para convertir DTO a entidad
    private Usuario convertirDTOaEntidad(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido() != null ? dto.getApellido() : "Sin apellido");
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        // Establecer fechaRegistro manualmente para asegurar que se guarde
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        return usuario;
    }
}