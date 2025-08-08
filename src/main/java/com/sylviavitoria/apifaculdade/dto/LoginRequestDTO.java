package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO para requisição de login do usuário")
public class LoginRequestDTO {

    @Schema(description = "Email do usuário", example = "admin@exemplo.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "123", required = true)
    private String senha;
}
