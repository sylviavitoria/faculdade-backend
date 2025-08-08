package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "DTO de resposta com dados do aluno")
public class AlunoResponseDTO {

    @Schema(description = "ID do aluno", example = "1")
    private Long id;

    @Schema(description = "Nome do aluno", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do aluno", example = "joao.silva@email.com")
    private String email;

    @Schema(description = "Matrícula do aluno", example = "2023001234")
    private String matricula;
}
