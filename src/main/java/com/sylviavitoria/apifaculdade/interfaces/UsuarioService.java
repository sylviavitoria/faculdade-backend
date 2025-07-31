package com.sylviavitoria.apifaculdade.interfaces;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;

public interface UsuarioService {
    AlunoResponseDTO criarAluno(AlunoRequestDTO alunoRequestDTO);
    UsuarioResponseDTO criarAdmin(UsuarioRequestDTO usuarioRequestDTO);
}