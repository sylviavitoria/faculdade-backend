package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.ProfessorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/professores")
@RequiredArgsConstructor
@Tag(name = "Professor", description = "Endpoints para gerenciamento de professores")
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Criar professor", description = "Cria um novo professor (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Professor criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ProfessorResponseDTO> criarProfessor(@Valid @RequestBody ProfessorRequestDTO professorRequestDTO) {
        ProfessorResponseDTO professorCriado = professorService.criarProfessor(professorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(professorCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Atualizar professor", description = "Atualiza os dados de um professor (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado")
    })
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(
            @Parameter(description = "ID do professor") @PathVariable Long id,
            @Valid @RequestBody ProfessorRequestDTO professorRequestDTO) {
        ProfessorResponseDTO professorAtualizado = professorService.atualizarProfessor(id, professorRequestDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deletar professor", description = "Deleta um professor por ID (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Professor deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado")
    })
    public ResponseEntity<Void> deletarProfessor(@Parameter(description = "ID do professor") @PathVariable Long id) {
        professorService.deletarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Buscar professor por ID", description = "Retorna os dados de um professor pelo ID (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Professor encontrado"),
        @ApiResponse(responseCode = "404", description = "Professor não encontrado")
    })
    public ResponseEntity<ProfessorResponseDTO> buscarProfessor(@Parameter(description = "ID do professor") @PathVariable Long id) {
        ProfessorResponseDTO professor = professorService.buscarProfessorPorId(id);
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    @Operation(summary = "Buscar professor logado", description = "Retorna os dados do professor autenticado")
    @ApiResponse(responseCode = "200", description = "Dados do professor logado retornados com sucesso")
    public ResponseEntity<ProfessorResponseDTO> buscarDadosDoProfessorLogado() {
        ProfessorResponseDTO professor = professorService.buscarProfessorLogado();
        return ResponseEntity.ok(professor);
    }
}