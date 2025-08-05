package com.sylviavitoria.apifaculdade.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotaRequestDTO {

    @Schema(description = "Nota 1 do aluno (0.0 a 10.0)", example = "8.5")
    @DecimalMin(value = "0.0", message = "Nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.0", message = "Nota deve ser menor ou igual a 10")
    private BigDecimal nota1;

    @Schema(description = "Nota 2 do aluno (0.0 a 10.0)", example = "7.0")
    @DecimalMin(value = "0.0", message = "Nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.0", message = "Nota deve ser menor ou igual a 10")
    private BigDecimal nota2;
}