package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatriculaRequestDTO {

    @Schema(description = "ID do aluno a ser matriculado", example = "1", required = true)
    @NotNull(message = "ID do aluno é obrigatório")
    private Long alunoId;

    @Schema(description = "ID da disciplina para matrícula", example = "1", required = true)
    @NotNull(message = "ID da disciplina é obrigatório")
    private Long disciplinaId;
}