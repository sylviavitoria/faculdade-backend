package com.sylviavitoria.apifaculdade.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;
import com.sylviavitoria.apifaculdade.interfaces.MatriculaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MatriculaResponseDTO> criarMatricula(@Valid @RequestBody MatriculaRequestDTO matriculaRequestDTO) {
        MatriculaResponseDTO matricula = matriculaService.criarMatricula(matriculaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }

    @PutMapping("/{id}/notas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public ResponseEntity<MatriculaResponseDTO> atualizarNotas(
            @PathVariable Long id,
            @Valid @RequestBody NotaRequestDTO notasRequestDTO) {
        MatriculaResponseDTO matricula = matriculaService.atualizarNotas(id, notasRequestDTO);
        return ResponseEntity.ok(matricula);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id) {
        matriculaService.deletarMatricula(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    public ResponseEntity<MatriculaResponseDTO> buscarMatricula(@PathVariable Long id) {
        MatriculaResponseDTO matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(matricula);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<MatriculaResponseDTO>> listarMatriculas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort) {
        Page<MatriculaResponseDTO> matriculas = matriculaService.listarMatriculas(page, size, sort);
        return ResponseEntity.ok(matriculas);
    }

    // @GetMapping("/aluno/{alunoId}")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ALUNO')")
    // public ResponseEntity<List<MatriculaResponseDTO>> buscarMatriculasPorAluno(@PathVariable Long alunoId) {
    //     List<MatriculaResponseDTO> matriculas = matriculaService.buscarMatriculasPorAluno(alunoId);
    //     return ResponseEntity.ok(matriculas);
    // }

    // @GetMapping("/disciplina/{disciplinaId}")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    // public ResponseEntity<List<MatriculaResponseDTO>> buscarMatriculasPorDisciplina(@PathVariable Long disciplinaId) {
    //     List<MatriculaResponseDTO> matriculas = matriculaService.buscarMatriculasPorDisciplina(disciplinaId);
    //     return ResponseEntity.ok(matriculas);
    // }
}