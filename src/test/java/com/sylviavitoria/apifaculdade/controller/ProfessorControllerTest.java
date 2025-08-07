package com.sylviavitoria.apifaculdade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.ProfessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfessorController.class,
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
@DisplayName("ProfessorController Tests")
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfessorService professorService;

    private ProfessorRequestDTO professorRequestDTO;
    private ProfessorResponseDTO professorResponseDTO;

    @BeforeEach
    void setUp() {
        professorRequestDTO = new ProfessorRequestDTO();
        professorRequestDTO.setNome("Maria Silva");
        professorRequestDTO.setEmail("maria.silva@universidade.com");
        professorRequestDTO.setSenha("123456");

        professorResponseDTO = ProfessorResponseDTO.builder()
                .nome("Maria Silva")
                .email("maria.silva@universidade.com")
                .build();
    }

    @Test
    @DisplayName("Deve criar professor com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveCriarProfessorComSucessoQuandoUsuarioEhAdmin() throws Exception {

        when(professorService.criarProfessor(argThat(dto -> 
            "Maria Silva".equals(dto.getNome()) &&
            "maria.silva@universidade.com".equals(dto.getEmail()) &&
            "123456".equals(dto.getSenha())
        ))).thenReturn(professorResponseDTO);

        mockMvc.perform(post("/api/v1/professores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@universidade.com"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário não é ADMIN ao criar professor")
    @WithMockUser(authorities = "ROLE_USER")
    void deveRetornar403QuandoUsuarioNaoEhAdminAoCriarProfessor() throws Exception {

        mockMvc.perform(post("/api/v1/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO)))
                .andExpect(status().isForbidden());

        verify(professorService, never()).criarProfessor(argThat(dto -> 
            "Maria Silva".equals(dto.getNome()) &&
            "maria.silva@universidade.com".equals(dto.getEmail()) &&
            "123456".equals(dto.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve atualizar professor com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveAtualizarProfessorComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long professorId = 1L;
        ProfessorResponseDTO professorAtualizado = ProfessorResponseDTO.builder()
                .nome("Maria Silva Santos")
                .email("maria.silva@universidade.com")
                .build();

        when(professorService.atualizarProfessor(eq(professorId), argThat(dto -> 
            "Maria Silva".equals(dto.getNome()) &&
            "maria.silva@universidade.com".equals(dto.getEmail()) &&
            "123456".equals(dto.getSenha())
        ))).thenReturn(professorAtualizado);

        mockMvc.perform(put("/api/v1/professores/{id}", professorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva Santos"))
                .andExpect(jsonPath("$.email").value("maria.silva@universidade.com"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário não é ADMIN ao atualizar professor")
    @WithMockUser(authorities = "ROLE_USER")
    void deveRetornar403QuandoUsuarioNaoEhAdminAoAtualizarProfessor() throws Exception {

        Long professorId = 1L;

        mockMvc.perform(put("/api/v1/professores/{id}", professorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO)))
                .andExpect(status().isForbidden());

        verify(professorService, never()).atualizarProfessor(eq(1L), argThat(dto -> 
            "Maria Silva".equals(dto.getNome()) &&
            "maria.silva@universidade.com".equals(dto.getEmail()) &&
            "123456".equals(dto.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve buscar professor por ID com sucesso")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveBuscarProfessorPorIdComSucesso() throws Exception {

        Long professorId = 1L;
        when(professorService.buscarProfessorPorId(professorId)).thenReturn(professorResponseDTO);

        mockMvc.perform(get("/api/v1/professores/{id}", professorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@universidade.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando professor não existe ao buscar por ID")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveRetornar404QuandoProfessorNaoExisteAoBuscarPorId() throws Exception {

        Long professorId = 999L;
        when(professorService.buscarProfessorPorId(professorId))
                .thenThrow(new EntityNotFoundException("Professor não encontrado"));

        mockMvc.perform(get("/api/v1/professores/{id}", professorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não encontrado"));
    }

    

    @Test
    @DisplayName("Deve buscar professor logado com sucesso")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveBuscarProfessorLogadoComSucesso() throws Exception {

        when(professorService.buscarProfessorLogado()).thenReturn(professorResponseDTO);

        mockMvc.perform(get("/api/v1/professores/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@universidade.com"));
    }
}
