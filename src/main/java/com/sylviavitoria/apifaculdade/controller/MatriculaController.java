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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/matriculas")
@RequiredArgsConstructor
@Tag(name = "Matrículas", description = "Gerenciamento de matrículas de alunos em disciplinas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Criar matrícula", description = "Permite que o administrador matricule um aluno em uma disciplina.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Matrícula criada com sucesso",
                     content = @Content(schema = @Schema(implementation = MatriculaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<MatriculaResponseDTO> criarMatricula(
            @Valid @RequestBody MatriculaRequestDTO matriculaRequestDTO) {
        MatriculaResponseDTO matricula = matriculaService.criarMatricula(matriculaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }

    @PutMapping("/{id}/notas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @Operation(summary = "Atualizar notas", description = "Permite que o professor ou administrador atualize as notas de uma matrícula.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notas atualizadas com sucesso",
                     content = @Content(schema = @Schema(implementation = MatriculaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Matrícula não encontrada", content = @Content)
    })
    public ResponseEntity<MatriculaResponseDTO> atualizarNotas(
            @Parameter(description = "ID da matrícula") @PathVariable Long id,
            @Valid @RequestBody NotaRequestDTO notasRequestDTO) {
        MatriculaResponseDTO matricula = matriculaService.atualizarNotas(id, notasRequestDTO);
        return ResponseEntity.ok(matricula);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deletar matrícula", description = "Permite que o administrador delete uma matrícula.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Matrícula deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Matrícula não encontrada", content = @Content)
    })
    public ResponseEntity<Void> deletarMatricula(
            @Parameter(description = "ID da matrícula") @PathVariable Long id) {
        matriculaService.deletarMatricula(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @Operation(summary = "Buscar matrícula por ID", description = "Permite que alunos, professores ou administradores consultem uma matrícula específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Matrícula encontrada",
                     content = @Content(schema = @Schema(implementation = MatriculaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Matrícula não encontrada", content = @Content)
    })
    public ResponseEntity<MatriculaResponseDTO> buscarMatricula(
            @Parameter(description = "ID da matrícula") @PathVariable Long id) {
        MatriculaResponseDTO matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(matricula);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Listar matrículas", description = "Lista todas as matrículas com paginação e ordenação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de matrículas retornada com sucesso",
                     content = @Content(schema = @Schema(implementation = MatriculaResponseDTO.class)))
    })
    public ResponseEntity<Page<MatriculaResponseDTO>> listarMatriculas(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação") @RequestParam(required = false) List<String> sort) {
        Page<MatriculaResponseDTO> matriculas = matriculaService.listarMatriculas(page, size, sort);
        return ResponseEntity.ok(matriculas);
    }
}