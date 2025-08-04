package com.sylviavitoria.apifaculdade.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
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

    @Override
    @Transactional
    public MatriculaResponseDTO criarMatricula(MatriculaRequestDTO matriculaRequestDTO) {
        log.info("Criando matrícula para aluno {} na disciplina {}", 
                matriculaRequestDTO.getAlunoId(), matriculaRequestDTO.getDisciplinaId());

        if (matriculaRepository.existsByAlunoIdAndDisciplinaId(
                matriculaRequestDTO.getAlunoId(), matriculaRequestDTO.getDisciplinaId())) {
            throw new BusinessException("Aluno já está matriculado nesta disciplina");
        }

        Aluno aluno = alunoRepository.findById(matriculaRequestDTO.getAlunoId())
                .orElseThrow(() -> new BusinessException("Aluno não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(matriculaRequestDTO.getDisciplinaId())
                .orElseThrow(() -> new BusinessException("Disciplina não encontrada"));

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setDisciplina(disciplina);

        Matricula matriculaSalva = matriculaRepository.save(matricula);
        return matriculaMapper.toDTO(matriculaSalva);
    }

    @Override
    @Transactional
    public MatriculaResponseDTO atualizarNotas(Long id, NotaRequestDTO notaRequestDTO) {
        log.info("Atualizando notas da matrícula {}", id);

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matrícula não encontrada"));

        if (notaRequestDTO.getNota1() != null) {
            matricula.setNota1(notaRequestDTO.getNota1());
        }
        if (notaRequestDTO.getNota2() != null) {
            matricula.setNota2(notaRequestDTO.getNota2());
        }

        matricula.calcularMediaEStatus();
        Matricula matriculaAtualizada = matriculaRepository.save(matricula);
        return matriculaMapper.toDTO(matriculaAtualizada);
    }

    @Override
    @Transactional
    public void deletarMatricula(Long id) {
        if (!matriculaRepository.existsById(id)) {
            throw new BusinessException("Matrícula não encontrada");
        }
        matriculaRepository.deleteById(id);
    }

    @Override
    public MatriculaResponseDTO buscarMatriculaPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Matrícula não encontrada"));
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
}