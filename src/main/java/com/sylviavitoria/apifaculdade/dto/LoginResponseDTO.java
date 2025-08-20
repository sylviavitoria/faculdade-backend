package com.sylviavitoria.apifaculdade.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tipo;
    private Long expiresIn;
    private UsuarioResponseDTO usuario;
}
