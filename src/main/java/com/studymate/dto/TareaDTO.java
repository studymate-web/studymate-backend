package com.studymate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TareaDTO {
    private Long id;
    
    @NotBlank(message = "El título de la tarea es obligatorio")
    private String titulo;
    
    private String descripcion;
    private LocalDateTime fechaVencimiento;
    private String prioridad = "MEDIA"; // BAJA, MEDIA, ALTA
    private Boolean completada = false;
    private Long usuarioId;
    private Long materiaId;
}
