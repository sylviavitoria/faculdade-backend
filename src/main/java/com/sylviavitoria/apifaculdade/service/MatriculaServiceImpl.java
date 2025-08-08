package com.sylviavitoria.apifaculdade.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.MatriculaService;
import com.sylviavitoria.apifaculdade.mapper.MatriculaMapper;
import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Disciplina;
import com.sylviavitoria.apifaculdade.model.Matricula;
import com.sylviavitoria.apifaculdade.repository.AlunoRepository;
import com.sylviavitoria.apifaculdade.repository.DisciplinaRepository;
import com.sylviavitoria.apifaculdade.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaMapper matriculaMapper;
    private final LogService logService;

    @Override
    @Transactional
    public MatriculaResponseDTO criarMatricula(MatriculaRequestDTO matriculaRequestDTO) {
        log.info("Criando matrícula para aluno {} na disciplina {}", 
                matriculaRequestDTO.getAlunoId(), matriculaRequestDTO.getDisciplinaId());

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            if (matriculaRepository.existsByAlunoIdAndDisciplinaId(
                    matriculaRequestDTO.getAlunoId(), matriculaRequestDTO.getDisciplinaId())) {
                throw new BusinessException("Aluno já está matriculado nesta disciplina");
            }

            Aluno aluno = alunoRepository.findById(matriculaRequestDTO.getAlunoId())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

            Disciplina disciplina = disciplinaRepository.findById(matriculaRequestDTO.getDisciplinaId())
                    .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

            Matricula matricula = new Matricula();
            matricula.setAluno(aluno);
            matricula.setDisciplina(disciplina);

            Matricula matriculaSalva = matriculaRepository.save(matricula);

            logInfo("Matrícula criada com sucesso: Aluno " + aluno.getNome() + " na disciplina " + disciplina.getNome() + 
                    " (ID: " + matriculaSalva.getId() + ")", "criarMatricula", emailUsuarioLogado, "CREATE_MATRICULA");

            return matriculaMapper.toDTO(matriculaSalva);
        } catch (Exception e) {
            logError("Erro ao criar matrícula: " + e.getMessage(), "criarMatricula", emailUsuarioLogado, "CREATE_MATRICULA_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public MatriculaResponseDTO atualizarNotas(Long id, NotaRequestDTO notaRequestDTO) {
        log.info("Atualizando notas da matrícula {}", id);

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            Matricula matricula = matriculaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada"));

            if (notaRequestDTO.getNota1() != null) {
                matricula.setNota1(notaRequestDTO.getNota1());
            }
            if (notaRequestDTO.getNota2() != null) {
                matricula.setNota2(notaRequestDTO.getNota2());
            }

            matricula.calcularMediaEStatus();
            Matricula matriculaAtualizada = matriculaRepository.save(matricula);

            logInfo("Notas atualizadas com sucesso para matrícula ID: " + id + 
                    " - Aluno: " + matricula.getAluno().getNome() + 
                    " - Disciplina: " + matricula.getDisciplina().getNome(), 
                    "atualizarNotas", emailUsuarioLogado, "UPDATE_NOTAS");

            return matriculaMapper.toDTO(matriculaAtualizada);
        } catch (Exception e) {
            logError("Erro ao atualizar notas da matrícula ID " + id + ": " + e.getMessage(), 
                    "atualizarNotas", emailUsuarioLogado, "UPDATE_NOTAS_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public void deletarMatricula(Long id) {
        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            Matricula matricula = matriculaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada"));

            String nomeAluno = matricula.getAluno().getNome();
            String nomeDisciplina = matricula.getDisciplina().getNome();

            matriculaRepository.delete(matricula);

            logInfo("Matrícula deletada com sucesso: Aluno " + nomeAluno + 
                    " da disciplina " + nomeDisciplina + " (ID: " + id + ")", 
                    "deletarMatricula", emailUsuarioLogado, "DELETE_MATRICULA");
        } catch (Exception e) {
            logError("Erro ao deletar matrícula ID " + id + ": " + e.getMessage(), 
                    "deletarMatricula", emailUsuarioLogado, "DELETE_MATRICULA_ERROR");
            throw e;
        }
    }

    @Override
    public MatriculaResponseDTO buscarMatriculaPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada"));
        return matriculaMapper.toDTO(matricula);
    }

    @Override
    public Page<MatriculaResponseDTO> listarMatriculas(int page, int size, List<String> sort) {
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("dataMatricula").descending());
        }

        return matriculaRepository.findAll(pageable)
                .map(matriculaMapper::toDTO);
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