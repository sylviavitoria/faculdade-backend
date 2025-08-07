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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplina", description = "Endpoints para gerenciamento de disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Criar disciplina", description = "Cria uma nova disciplina")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Disciplina criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<DisciplinaResponseDTO> criarDisciplina(
            @Valid @RequestBody DisciplinaRequestDTO disciplinaRequestDTO) {
        DisciplinaResponseDTO disciplinaCriada = disciplinaService.criarDisciplina(disciplinaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaCriada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Atualizar disciplina", description = "Atualiza os dados de uma disciplina existente (Apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplina atualizada com sucesso",
                content = @Content(schema = @Schema(implementation = DisciplinaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Disciplina não encontrada", content = @Content)
    })
    public ResponseEntity<DisciplinaResponseDTO> atualizarDisciplina(
            @PathVariable Long id,
            @Valid @RequestBody DisciplinaRequestDTO disciplinaRequestDTO) {
        DisciplinaResponseDTO disciplinaAtualizada = disciplinaService.atualizarDisciplina(id, disciplinaRequestDTO);
        return ResponseEntity.ok(disciplinaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deletar disciplina", description = "Remove uma disciplina por ID (Apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Disciplina deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Disciplina não encontrada", content = @Content)
    })
    public ResponseEntity<Void> deletarDisciplina(@PathVariable Long id) {
        disciplinaService.deletarDisciplina(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @Operation(summary = "Buscar disciplina por ID", description = "Retorna os dados de uma disciplina")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disciplina encontrada",
                content = @Content(schema = @Schema(implementation = DisciplinaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Disciplina não encontrada", content = @Content)
    })
    public ResponseEntity<DisciplinaResponseDTO> buscarDisciplina(@PathVariable Long id) {
        DisciplinaResponseDTO disciplina = disciplinaService.buscarDisciplinaPorId(id);
        return ResponseEntity.ok(disciplina);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @Operation(summary = "Listar disciplinas", description = "Retorna uma lista paginada de disciplinas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<DisciplinaResponseDTO>> listarDisciplinas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort) {
        Page<DisciplinaResponseDTO> disciplinas = disciplinaService.listarDisciplinas(page, size, sort);
        return ResponseEntity.ok(disciplinas);
    }

}
