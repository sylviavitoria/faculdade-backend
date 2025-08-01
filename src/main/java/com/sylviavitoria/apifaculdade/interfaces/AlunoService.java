package com.sylviavitoria.apifaculdade.interfaces;

import java.util.List;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;

public interface AlunoService {
    AlunoResponseDTO criarAluno(AlunoRequestDTO alunoRequestDTO);
    AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoRequestDTO);
    void deletarAluno(Long id);
    AlunoResponseDTO buscarAlunoPorId(Long id);
    List<AlunoResponseDTO> listarAlunos();
    AlunoResponseDTO buscarAlunoLogado();
}