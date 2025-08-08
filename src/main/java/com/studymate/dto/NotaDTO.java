package com.studymate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotaDTO {
    private Long id;
    
    @NotBlank(message = "El título de la nota es obligatorio")
    private String titulo;
    
    private String contenido;
    private String color = "#ffffff";
    private Boolean favorita = false;
    private Long usuarioId;
    private Long materiaId;
}
