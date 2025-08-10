package com.studymate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MateriaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    private String codigo;
    private String descripcion;
    private Integer creditos;
    private String color = "#007bff";
    private Long usuarioId;
}
