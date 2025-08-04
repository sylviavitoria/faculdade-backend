package com.sylviavitoria.apifaculdade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatriculaRequestDTO {
    @NotNull(message = "ID do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "ID da disciplina é obrigatório")
    private Long disciplinaId;
}