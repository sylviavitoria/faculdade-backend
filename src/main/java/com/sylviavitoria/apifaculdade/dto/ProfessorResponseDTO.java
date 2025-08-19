package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfessorResponseDTO {

    @Schema(description = "ID do professor", example = "1")
    private Long id;

    @Schema(description = "Nome completo do professor", example = "Maria Silva")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Email do professor", example = "maria.silva@universidade.com")
    @NotBlank(message = "O email é obrigatório")
    private String email;
    
}