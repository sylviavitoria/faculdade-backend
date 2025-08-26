package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO de requisição para criação/atualização de disciplina")
public class DisciplinaRequestDTO {

    @Schema(description = "Nome da disciplina", example = "Algoritmos", required = true)
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Código único da disciplina", example = "ALG101", required = true)
    @NotBlank(message = "O código é obrigatório")
    private String codigo;

    @Schema(description = "ID do professor responsável", example = "1")
    @NotNull(message = "O ID do professor é obrigatório")
    private Long professorId;
}

