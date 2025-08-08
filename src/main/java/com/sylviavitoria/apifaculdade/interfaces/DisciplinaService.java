package com.sylviavitoria.apifaculdade.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;

public interface DisciplinaService {
    DisciplinaResponseDTO criarDisciplina(DisciplinaRequestDTO disciplinaRequestDTO);
    DisciplinaResponseDTO atualizarDisciplina(Long id, DisciplinaRequestDTO disciplinaRequestDTO);
    void deletarDisciplina(Long id);
    DisciplinaResponseDTO buscarDisciplinaPorId(Long id);
    Page<DisciplinaResponseDTO> listarDisciplinas(int page, int size, List<String> sort);

}
