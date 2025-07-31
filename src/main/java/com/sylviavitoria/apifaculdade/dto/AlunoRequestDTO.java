package com.sylviavitoria.apifaculdade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlunoRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    private String email;
    
    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
}
