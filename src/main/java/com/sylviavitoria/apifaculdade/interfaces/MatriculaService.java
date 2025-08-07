package com.sylviavitoria.apifaculdade.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;

public interface MatriculaService {
    MatriculaResponseDTO criarMatricula(MatriculaRequestDTO matriculaRequestDTO);
    MatriculaResponseDTO atualizarNotas(Long id, NotaRequestDTO notaRequestDTO);
    void deletarMatricula(Long id);
    MatriculaResponseDTO buscarMatriculaPorId(Long id);
    Page<MatriculaResponseDTO> listarMatriculas(int page, int size, List<String> sort);

}