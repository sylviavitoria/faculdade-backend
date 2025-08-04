package com.sylviavitoria.apifaculdade.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotaRequestDTO {
    @DecimalMin(value = "0.0", message = "Nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.0", message = "Nota deve ser menor ou igual a 10")
    private BigDecimal nota1;

    @DecimalMin(value = "0.0", message = "Nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.0", message = "Nota deve ser menor ou igual a 10")
    private BigDecimal nota2;

}