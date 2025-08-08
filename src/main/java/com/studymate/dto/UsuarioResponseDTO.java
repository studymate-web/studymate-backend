package com.studymate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de usuario (sin contraseña)
 */
@Data
public class UsuarioResponseDTO {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    
    private LocalDateTime fechaRegistro;
    
    private Boolean activo;
}
