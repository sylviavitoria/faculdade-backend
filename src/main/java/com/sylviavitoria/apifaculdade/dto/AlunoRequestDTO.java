package com.sylviavitoria.apifaculdade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO de requisição para criação/atualização de aluno")
public class AlunoRequestDTO {

    @Schema(description = "Nome do aluno", example = "João da Silva", required = true)
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "E-mail do aluno", example = "joao.silva@email.com", required = true)
    @NotBlank(message = "O email é obrigatório")
    private String email;

    @Schema(description = "Número de matrícula", example = "2023001234", required = true)
    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @Schema(description = "Senha do aluno", example = "123", required = true)
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
}
