package com.sylviavitoria.apifaculdade.dto;

import com.sylviavitoria.apifaculdade.enums.TipoUsuario;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private TipoUsuario tipo;
}
