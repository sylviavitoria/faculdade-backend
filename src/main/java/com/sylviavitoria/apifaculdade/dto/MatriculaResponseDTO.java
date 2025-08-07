package com.sylviavitoria.apifaculdade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sylviavitoria.apifaculdade.enums.StatusMatricula;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MatriculaResponseDTO {

    @Schema(description = "ID da matrícula", example = "1001")
    private Long id;

    @Schema(description = "Dados do aluno matriculado")
    private AlunoResponseDTO aluno;

    @Schema(description = "Dados da disciplina matriculada")
    private DisciplinaResponseDTO disciplina;

    @Schema(description = "Nota 1 do aluno na disciplina", example = "8.5")
    private BigDecimal nota1;
    
    @Schema(description = "Nota 2 do aluno na disciplina", example = "7.0")
    private BigDecimal nota2;

    @Schema(description = "Status da matrícula", example = "APROVADA")
    private StatusMatricula status;

    @Schema(description = "Data e hora da matrícula", example = "2023-10-01T10:15:30")
    private LocalDateTime dataMatricula;
}