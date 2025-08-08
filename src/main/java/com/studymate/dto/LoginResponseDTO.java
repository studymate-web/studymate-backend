package com.studymate.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String message;
    private String status;
    private UsuarioDTO usuario;
}
