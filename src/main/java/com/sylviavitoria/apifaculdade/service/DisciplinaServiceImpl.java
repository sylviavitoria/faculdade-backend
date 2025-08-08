package com.sylviavitoria.apifaculdade.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
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
    private final LogService logService;

    @Override
    @Transactional
    public DisciplinaResponseDTO criarDisciplina(DisciplinaRequestDTO disciplinaRequestDTO) {
        log.info("Iniciando criação de disciplina: {}", disciplinaRequestDTO.getNome());

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            if (disciplinaRepository.existsByCodigo(disciplinaRequestDTO.getCodigo())) {
                throw new BusinessException("Já existe uma disciplina com esse código");
            }

            Disciplina disciplina = disciplinaMapper.toEntity(disciplinaRequestDTO);

            if (disciplinaRequestDTO.getProfessorId() != null) {
                Professor professor = professorRepository.findById(disciplinaRequestDTO.getProfessorId())
                        .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
                disciplina.setProfessor(professor);
            }

            Disciplina disciplinaSalva = disciplinaRepository.save(disciplina);

            logInfo("Disciplina criada com sucesso: " + disciplinaSalva.getNome() + " (ID: " + disciplinaSalva.getId()
                    + ")",
                    "criarDisciplina", emailUsuarioLogado, "CREATE_DISCIPLINA");

            return disciplinaMapper.toDTO(disciplinaSalva);
        } catch (Exception e) {
            logError("Erro ao criar disciplina: " + e.getMessage(), "criarDisciplina", emailUsuarioLogado,
                    "CREATE_DISCIPLINA_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public DisciplinaResponseDTO atualizarDisciplina(Long id, DisciplinaRequestDTO disciplinaRequestDTO) {
        String emailUsuarioLogado = getEmailUsuarioLogado();
        
        try {
            Disciplina disciplina = disciplinaRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Disciplina não encontrada"));

            if (!disciplina.getCodigo().equals(disciplinaRequestDTO.getCodigo()) &&
                    disciplinaRepository.existsByCodigo(disciplinaRequestDTO.getCodigo())) {
                throw new BusinessException("Já existe uma disciplina com esse código");
            }

            disciplinaMapper.updateEntity(disciplinaRequestDTO, disciplina);

            if (disciplinaRequestDTO.getProfessorId() != null) {
                Professor professor = professorRepository.findById(disciplinaRequestDTO.getProfessorId())
                        .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
                disciplina.setProfessor(professor);
            }

            Disciplina disciplinaAtualizada = disciplinaRepository.save(disciplina);
            
            logInfo("Disciplina atualizada com sucesso: " + disciplinaAtualizada.getNome() + " (ID: " + disciplinaAtualizada.getId() + ")",
                    "atualizarDisciplina", emailUsuarioLogado, "UPDATE_DISCIPLINA");

            return disciplinaMapper.toDTO(disciplinaAtualizada);
        } catch (Exception e) {
            logError("Erro ao atualizar disciplina: " + e.getMessage(), "atualizarDisciplina", emailUsuarioLogado, "UPDATE_DISCIPLINA_ERROR");
            throw e;
        }
    }


    @Override
    @Transactional
    public void deletarDisciplina(Long id) {
        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            if (!disciplinaRepository.existsById(id)) {
                throw new EntityNotFoundException("Disciplina não encontrada");
            }

            Disciplina disciplina = disciplinaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

            disciplinaRepository.deleteById(id);

            logInfo("Disciplina deletada com sucesso: " + disciplina.getNome() + " (ID: " + disciplina.getId() + ")",
                    "deletarDisciplina", emailUsuarioLogado, "DELETE_DISCIPLINA");
        } catch (Exception e) {

            logError("Erro ao deletar disciplina: " + e.getMessage(), "deletarDisciplina", emailUsuarioLogado,
                    "DELETE_DISCIPLINA_ERROR");
            throw e;
        }
    }

    @Override
    public DisciplinaResponseDTO buscarDisciplinaPorId(Long id) {
        return disciplinaRepository.findById(id)
                .map(disciplinaMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
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

    private void logInfo(String message, String method, String user, String action) {
        logService.saveLog("INFO", message, this.getClass().getSimpleName(), method, user, action);
    }

    private void logError(String message, String method, String user, String action) {
        logService.saveLog("ERROR", message, this.getClass().getSimpleName(), method, user, action);
    }

    private String getEmailUsuarioLogado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
