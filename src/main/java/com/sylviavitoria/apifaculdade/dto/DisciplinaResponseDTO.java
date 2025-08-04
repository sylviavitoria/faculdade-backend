package com.sylviavitoria.apifaculdade.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisciplinaResponseDTO {
    private Long id;
    private String nome;
    private String codigo;
    private ProfessorResponseDTO professor; 
}
