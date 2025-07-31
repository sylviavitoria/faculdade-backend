package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;
import com.sylviavitoria.apifaculdade.interfaces.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/alunos")
    public ResponseEntity<AlunoResponseDTO> criarAluno(@RequestBody @Valid AlunoRequestDTO alunoRequestDTO) {
        AlunoResponseDTO alunoCriado = usuarioService.criarAluno(alunoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoCriado);
    }

    @PostMapping("/admins")
    public ResponseEntity<UsuarioResponseDTO> criarAdmin(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO adminCriado = usuarioService.criarAdmin(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCriado);
    }
}

