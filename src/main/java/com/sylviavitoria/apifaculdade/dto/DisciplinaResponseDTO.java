package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO de resposta com dados da disciplina")
public class DisciplinaResponseDTO {

    @Schema(description = "ID da disciplina", example = "1")
    private Long id;

    @Schema(description = "Nome da disciplina", example = "Algoritmos")
    private String nome;

    @Schema(description = "Código da disciplina", example = "ALG101")
    private String codigo;

    @Schema(description = "Dados do professor responsável")
    private ProfessorResponseDTO professor; 
}
