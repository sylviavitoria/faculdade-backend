package com.sylviavitoria.apifaculdade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sylviavitoria.apifaculdade.enums.StatusMatricula;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MatriculaResponseDTO {
    private Long id;
    private AlunoResponseDTO aluno;
    private DisciplinaResponseDTO disciplina;
    private BigDecimal nota1;
    private BigDecimal nota2;
    private StatusMatricula status;
    private LocalDateTime dataMatricula;
}