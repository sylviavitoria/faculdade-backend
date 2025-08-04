package com.sylviavitoria.apifaculdade.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.interfaces.DisciplinaService;
import com.sylviavitoria.apifaculdade.mapper.DisciplinaMapper;
import com.sylviavitoria.apifaculdade.model.Disciplina;
import com.sylviavitoria.apifaculdade.model.Professor;
import com.sylviavitoria.apifaculdade.repository.DisciplinaRepository;
import com.sylviavitoria.apifaculdade.repository.ProfessorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisciplinaServiceImpl implements DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaMapper disciplinaMapper;

    @Override
    @Transactional
    public DisciplinaResponseDTO criarDisciplina(DisciplinaRequestDTO disciplinaRequestDTO) {
        log.info("Iniciando criação de disciplina: {}", disciplinaRequestDTO.getNome());

        if (disciplinaRepository.existsByCodigo(disciplinaRequestDTO.getCodigo())) {
            throw new BusinessException("Já existe uma disciplina com esse código");
        }

        Disciplina disciplina = disciplinaMapper.toEntity(disciplinaRequestDTO);

        if (disciplinaRequestDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(disciplinaRequestDTO.getProfessorId())
                    .orElseThrow(() -> new BusinessException("Professor não encontrado"));
            disciplina.setProfessor(professor);
        }

        Disciplina disciplinaSalva = disciplinaRepository.save(disciplina);
        return disciplinaMapper.toDTO(disciplinaSalva);
    }

    @Override
    @Transactional
    public DisciplinaResponseDTO atualizarDisciplina(Long id, DisciplinaRequestDTO disciplinaRequestDTO) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Disciplina não encontrada"));

        if (!disciplina.getCodigo().equals(disciplinaRequestDTO.getCodigo()) &&
                disciplinaRepository.existsByCodigo(disciplinaRequestDTO.getCodigo())) {
            throw new BusinessException("Já existe uma disciplina com esse código");
        }

        disciplinaMapper.updateEntity(disciplinaRequestDTO, disciplina);

        if (disciplinaRequestDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(disciplinaRequestDTO.getProfessorId())
                    .orElseThrow(() -> new BusinessException("Professor não encontrado"));
            disciplina.setProfessor(professor);
        }

        return disciplinaMapper.toDTO(disciplinaRepository.save(disciplina));
    }

    @Override
    @Transactional
    public void deletarDisciplina(Long id) {
        if (!disciplinaRepository.existsById(id)) {
            throw new BusinessException("Disciplina não encontrada");
        }
        disciplinaRepository.deleteById(id);
    }

    @Override
    public DisciplinaResponseDTO buscarDisciplinaPorId(Long id) {
        return disciplinaRepository.findById(id)
                .map(disciplinaMapper::toDTO)
                .orElseThrow(() -> new BusinessException("Disciplina não encontrada"));
    }

    @Override
    public Page<DisciplinaResponseDTO> listarDisciplinas(int page, int size, List<String> sort) {
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("nome"));
        }

        return disciplinaRepository.findAll(pageable)
                .map(disciplinaMapper::toDTO);
    }
}
