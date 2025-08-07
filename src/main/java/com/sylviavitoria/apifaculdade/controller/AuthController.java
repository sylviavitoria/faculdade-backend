package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.LoginRequestDTO;
import com.sylviavitoria.apifaculdade.dto.TokenResponseDTO;
import com.sylviavitoria.apifaculdade.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna o token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getSenha());

            authenticationManager.authenticate(authToken);

            String token = jwtUtil.generateToken(loginRequest.getEmail());
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }
}
