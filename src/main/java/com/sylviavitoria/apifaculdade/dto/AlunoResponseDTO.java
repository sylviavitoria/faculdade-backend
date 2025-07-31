package com.sylviavitoria.apifaculdade.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AlunoResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String matricula;
}
