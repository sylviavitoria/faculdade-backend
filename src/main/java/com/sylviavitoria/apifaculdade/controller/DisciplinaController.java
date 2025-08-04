package com.sylviavitoria.apifaculdade.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.DisciplinaService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DisciplinaResponseDTO> criarDisciplina(
            @Valid @RequestBody DisciplinaRequestDTO disciplinaRequestDTO) {
        DisciplinaResponseDTO disciplinaCriada = disciplinaService.criarDisciplina(disciplinaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaCriada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DisciplinaResponseDTO> atualizarDisciplina(
            @PathVariable Long id,
            @Valid @RequestBody DisciplinaRequestDTO disciplinaRequestDTO) {
        DisciplinaResponseDTO disciplinaAtualizada = disciplinaService.atualizarDisciplina(id, disciplinaRequestDTO);
        return ResponseEntity.ok(disciplinaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletarDisciplina(@PathVariable Long id) {
        disciplinaService.deletarDisciplina(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    public ResponseEntity<DisciplinaResponseDTO> buscarDisciplina(@PathVariable Long id) {
        DisciplinaResponseDTO disciplina = disciplinaService.buscarDisciplinaPorId(id);
        return ResponseEntity.ok(disciplina);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    public ResponseEntity<Page<DisciplinaResponseDTO>> listarDisciplinas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort) {
        Page<DisciplinaResponseDTO> disciplinas = disciplinaService.listarDisciplinas(page, size, sort);
        return ResponseEntity.ok(disciplinas);
    }

}
