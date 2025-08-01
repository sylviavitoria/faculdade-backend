package com.sylviavitoria.apifaculdade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.ProfessorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfessorResponseDTO> criarProfessor(@Valid @RequestBody ProfessorRequestDTO professorRequestDTO) {
        ProfessorResponseDTO professorCriado = professorService.criarProfessor(professorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(professorCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorRequestDTO professorRequestDTO) {
        ProfessorResponseDTO professorAtualizado = professorService.atualizarProfessor(id, professorRequestDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletarProfessor(@PathVariable Long id) {
        professorService.deletarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProfessorResponseDTO> buscarProfessor(@PathVariable Long id) {
        ProfessorResponseDTO professor = professorService.buscarProfessorPorId(id);
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<ProfessorResponseDTO> buscarDadosDoProfessorLogado() {
        ProfessorResponseDTO professor = professorService.buscarProfessorLogado();
        return ResponseEntity.ok(professor);
    }
}