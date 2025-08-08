package com.sylviavitoria.apifaculdade.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;

public interface ProfessorService {
    ProfessorResponseDTO criarProfessor(ProfessorRequestDTO professorRequestDTO);
    ProfessorResponseDTO atualizarProfessor(Long id, ProfessorRequestDTO professorRequestDTO);
    void deletarProfessor(Long id);
    ProfessorResponseDTO buscarProfessorPorId(Long id);
    Page<ProfessorResponseDTO> listarProfessores(int page, int size, List<String> sort);
    ProfessorResponseDTO buscarProfessorLogado();
}