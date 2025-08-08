package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfessorRequestDTO {

    @Schema(description = "Nome completo do professor", example = "Maria Silva")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Email do professor", example = "maria.silva@universidade.com")
    @NotBlank(message = "O email é obrigatório")
    private String email;

    @Schema(description = "Senha para login", example = "123")
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}