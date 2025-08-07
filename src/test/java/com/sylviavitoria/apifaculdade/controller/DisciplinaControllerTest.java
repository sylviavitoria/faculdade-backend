package com.sylviavitoria.apifaculdade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.DisciplinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DisciplinaController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = {
            com.sylviavitoria.apifaculdade.security.JwtAuthFilter.class,
            com.sylviavitoria.apifaculdade.security.JwtUtil.class,
            com.sylviavitoria.apifaculdade.security.UserDetailsServiceImpl.class,
            com.sylviavitoria.apifaculdade.config.SecurityConfig.class
        }))
@TestPropertySource(properties = {
    "spring.security.enabled=false",
    "jwt.secret=test-secret",
    "jwt.expiration=86400000"
})
@DisplayName("DisciplinaController Tests")
class DisciplinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DisciplinaService disciplinaService;

    private DisciplinaRequestDTO disciplinaRequestDTO;
    private DisciplinaResponseDTO disciplinaResponseDTO;

    @BeforeEach
    void setUp() {
        disciplinaRequestDTO = new DisciplinaRequestDTO();
        disciplinaRequestDTO.setNome("Algoritmos");
        disciplinaRequestDTO.setCodigo("ALG101");
        disciplinaRequestDTO.setProfessorId(1L);

        ProfessorResponseDTO professorResponseDTO = ProfessorResponseDTO.builder()
                .nome("Maria Silva")
                .email("maria.silva@universidade.com")
                .build();

        disciplinaResponseDTO = new DisciplinaResponseDTO();
        disciplinaResponseDTO.setId(1L);
        disciplinaResponseDTO.setNome("Algoritmos");
        disciplinaResponseDTO.setCodigo("ALG101");
        disciplinaResponseDTO.setProfessor(professorResponseDTO);
    }

    @Test
    @DisplayName("Deve criar disciplina com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveCriarDisciplinaComSucessoQuandoUsuarioEhAdmin() throws Exception {

        when(disciplinaService.criarDisciplina(argThat(dto -> 
            "Algoritmos".equals(dto.getNome()) &&
            "ALG101".equals(dto.getCodigo()) &&
            Long.valueOf(1L).equals(dto.getProfessorId())
        ))).thenReturn(disciplinaResponseDTO);

        mockMvc.perform(post("/api/v1/disciplinas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disciplinaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Algoritmos"))
                .andExpect(jsonPath("$.codigo").value("ALG101"))
                .andExpect(jsonPath("$.professor.nome").value("Maria Silva"));

        verify(disciplinaService, times(1)).criarDisciplina(argThat(dto -> 
            "Algoritmos".equals(dto.getNome()) &&
            "ALG101".equals(dto.getCodigo()) &&
            Long.valueOf(1L).equals(dto.getProfessorId())
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar criar disciplina sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveRetornar403AoTentarCriarDisciplinaSemSerAdmin() throws Exception {

        mockMvc.perform(post("/api/v1/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disciplinaRequestDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(disciplinaService);
    }

    @Test
    @DisplayName("Deve retornar 400 quando dados são inválidos ao criar disciplina")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveRetornar400QuandoDadosSaoInvalidosAoCriarDisciplina() throws Exception {

        DisciplinaRequestDTO invalidRequest = new DisciplinaRequestDTO();
        invalidRequest.setNome(""); 
        invalidRequest.setCodigo(""); 

        mockMvc.perform(post("/api/v1/disciplinas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(disciplinaService);
    }

    @Test
    @DisplayName("Deve atualizar disciplina com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveAtualizarDisciplinaComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long disciplinaId = 1L;
        when(disciplinaService.atualizarDisciplina(eq(disciplinaId), argThat(dto -> 
            "Algoritmos".equals(dto.getNome()) &&
            "ALG101".equals(dto.getCodigo()) &&
            Long.valueOf(1L).equals(dto.getProfessorId())
        ))).thenReturn(disciplinaResponseDTO);

        mockMvc.perform(put("/api/v1/disciplinas/{id}", disciplinaId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disciplinaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Algoritmos"));

        verify(disciplinaService, times(1)).atualizarDisciplina(eq(disciplinaId), argThat(dto -> 
            "Algoritmos".equals(dto.getNome()) &&
            "ALG101".equals(dto.getCodigo()) &&
            Long.valueOf(1L).equals(dto.getProfessorId())
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar deletar disciplina sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveRetornar403AoTentarDeletarDisciplinaSemSerAdmin() throws Exception {

        Long disciplinaId = 1L;

        mockMvc.perform(delete("/api/v1/disciplinas/{id}", disciplinaId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(disciplinaService);
    }

    @Test
    @DisplayName("Deve buscar disciplina por ID com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveBuscarDisciplinaPorIdComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long disciplinaId = 1L;
        when(disciplinaService.buscarDisciplinaPorId(disciplinaId)).thenReturn(disciplinaResponseDTO);

        mockMvc.perform(get("/api/v1/disciplinas/{id}", disciplinaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Algoritmos"));

        verify(disciplinaService, times(1)).buscarDisciplinaPorId(disciplinaId);
    }

    @Test
    @DisplayName("Deve buscar disciplina por ID com sucesso quando usuário é PROFESSOR")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveBuscarDisciplinaPorIdComSucessoQuandoUsuarioEhProfessor() throws Exception {

        Long disciplinaId = 1L;
        when(disciplinaService.buscarDisciplinaPorId(disciplinaId)).thenReturn(disciplinaResponseDTO);

        mockMvc.perform(get("/api/v1/disciplinas/{id}", disciplinaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(disciplinaService, times(1)).buscarDisciplinaPorId(disciplinaId);
    }

    @Test
    @DisplayName("Deve buscar disciplina por ID com sucesso quando usuário é ALUNO")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveBuscarDisciplinaPorIdComSucessoQuandoUsuarioEhAluno() throws Exception {

        Long disciplinaId = 1L;
        when(disciplinaService.buscarDisciplinaPorId(disciplinaId)).thenReturn(disciplinaResponseDTO);

        mockMvc.perform(get("/api/v1/disciplinas/{id}", disciplinaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(disciplinaService, times(1)).buscarDisciplinaPorId(disciplinaId);
    }

    @Test
    @DisplayName("Deve listar disciplinas com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveListarDisciplinasComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Page<DisciplinaResponseDTO> page = new PageImpl<>(List.of(disciplinaResponseDTO));
        when(disciplinaService.listarDisciplinas(eq(0), eq(10), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/v1/disciplinas")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("Algoritmos"));

        verify(disciplinaService, times(1)).listarDisciplinas(eq(0), eq(10), isNull());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/v1/disciplinas"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(disciplinaService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar disciplina inexistente")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveRetornar404AoBuscarDisciplinaInexistente() throws Exception {

        Long disciplinaId = 999L;
        when(disciplinaService.buscarDisciplinaPorId(disciplinaId))
                .thenThrow(new EntityNotFoundException("Disciplina não encontrada"));

        mockMvc.perform(get("/api/v1/disciplinas/{id}", disciplinaId))
                .andExpect(status().isNotFound());

        verify(disciplinaService, times(1)).buscarDisciplinaPorId(disciplinaId);
    }

}
