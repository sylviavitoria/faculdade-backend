package com.sylviavitoria.apifaculdade.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sylviavitoria.apifaculdade.model.ApplicationLog;
import com.sylviavitoria.apifaculdade.repository.ApplicationLogRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "Endpoints para consulta de logs da aplicação")
public class LogController {

    private final ApplicationLogRepository logRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Listar todos os logs", description = "Lista todos os logs da aplicação (Apenas ADMIN)")
    public ResponseEntity<List<ApplicationLog>> listarTodosLogs() {
        List<ApplicationLog> logs = logRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/por-nivel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Buscar logs por nível", description = "Busca logs filtrados por nível (INFO, ERROR, etc.)")
    public ResponseEntity<List<ApplicationLog>> buscarLogsPorNivel(
            @Parameter(description = "Nível do log (INFO, ERROR, WARN, DEBUG)", example = "INFO")
            @RequestParam String level) {
        List<ApplicationLog> logs = logRepository.findByLevel(level);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/por-usuario")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Buscar logs por usuário", description = "Busca logs filtrados por ID do usuário")
    public ResponseEntity<List<ApplicationLog>> buscarLogsPorUsuario(
            @Parameter(description = "ID do usuário", example = "admin@example.com")
            @RequestParam String userId) {
        List<ApplicationLog> logs = logRepository.findByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/recentes")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Buscar logs recentes", description = "Busca logs das últimas 24 horas")
    public ResponseEntity<List<ApplicationLog>> buscarLogsRecentes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime umDiaAtras = agora.minusDays(1);
        List<ApplicationLog> logs = logRepository.findByTimestampBetween(umDiaAtras, agora);
        return ResponseEntity.ok(logs);
    }
}
