package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema (apenas administradores)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Criar administrador", description = "Cria um novo usuário com perfil de administrador. Apenas administradores autenticados podem acessar.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso",
                     content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    public ResponseEntity<UsuarioResponseDTO> criarAdmin(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO adminCriado = usuarioService.criarAdmin(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCriado);
    }
}

