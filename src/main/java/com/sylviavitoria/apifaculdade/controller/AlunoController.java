package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.AlunoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/alunos")
@RequiredArgsConstructor
@Tag(name = "Aluno", description = "Endpoints para gerenciamento de alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     @Operation(summary = "Criar aluno", description = "Cria um novo aluno (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<AlunoResponseDTO> criarAluno(@Valid @RequestBody AlunoRequestDTO alunoRequestDTO) {
        AlunoResponseDTO alunoCriado = alunoService.criarAluno(alunoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Atualizar aluno", description = "Atualiza os dados de um aluno (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<AlunoResponseDTO> atualizarAluno(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequestDTO alunoRequestDTO) {
        AlunoResponseDTO alunoAtualizado = alunoService.atualizarAluno(id, alunoRequestDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deletar aluno", description = "Deleta um aluno por ID (Apenas ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aluno deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletarAluno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @Operation(summary = "Buscar aluno por ID", description = "Retorna os dados de um aluno pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<AlunoResponseDTO> buscarAluno(@PathVariable Long id) {
        AlunoResponseDTO aluno = alunoService.buscarAlunoPorId(id);
        return ResponseEntity.ok(aluno);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @Operation(summary = "Listar alunos", description = "Retorna uma lista paginada de alunos")
    public ResponseEntity<Page<AlunoResponseDTO>> listarAlunos(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Parâmetros de ordenação") @RequestParam(required = false) List<String> sort) {
        Page<AlunoResponseDTO> alunos = alunoService.listarAlunos(page, size, sort);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_ALUNO')")
    @Operation(summary = "Buscar aluno logado", description = "Retorna os dados do aluno autenticado")
    @ApiResponse(responseCode = "200", description = "Dados do aluno logado retornados com sucesso")
    public ResponseEntity<AlunoResponseDTO> buscarDadosDoAlunoLogado() {
        AlunoResponseDTO aluno = alunoService.buscarAlunoLogado();
        return ResponseEntity.ok(aluno);
    }
}