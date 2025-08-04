package com.sylviavitoria.apifaculdade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisciplinaRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O código é obrigatório")
    private String codigo;

    private Long professorId;
}

