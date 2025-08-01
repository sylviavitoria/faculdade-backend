package com.sylviavitoria.apifaculdade.controller;

import com.sylviavitoria.apifaculdade.dto.LoginRequestDTO;
import com.sylviavitoria.apifaculdade.dto.TokenResponseDTO;
import com.sylviavitoria.apifaculdade.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
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
