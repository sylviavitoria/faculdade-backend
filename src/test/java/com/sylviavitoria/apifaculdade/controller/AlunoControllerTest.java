package com.sylviavitoria.apifaculdade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.AlunoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlunoController.class,
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
@DisplayName("AlunoController Tests")
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlunoService alunoService;

    private AlunoRequestDTO alunoRequestDTO;
    private AlunoResponseDTO alunoResponseDTO;

    @BeforeEach
    void setUp() {
        alunoRequestDTO = new AlunoRequestDTO();
        alunoRequestDTO.setNome("João da Silva");
        alunoRequestDTO.setEmail("joao.silva@email.com");
        alunoRequestDTO.setMatricula("2023001234");
        alunoRequestDTO.setSenha("123456");

        alunoResponseDTO = AlunoResponseDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao.silva@email.com")
                .matricula("2023001234")
                .build();
    }

    @Test
    @DisplayName("Deve criar aluno com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveCriarAlunoComSucessoQuandoUsuarioEhAdmin() throws Exception {

        when(alunoService.criarAluno(argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ))).thenReturn(alunoResponseDTO);

        mockMvc.perform(post("/api/v1/alunos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"))
                .andExpect(jsonPath("$.matricula").value("2023001234"));

        verify(alunoService, times(1)).criarAluno(argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar criar aluno sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveRetornar403AoTentarCriarAlunoSemSerAdmin() throws Exception {

        mockMvc.perform(post("/api/v1/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequestDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(alunoService);
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveAtualizarAlunoComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long alunoId = 1L;
        when(alunoService.atualizarAluno(eq(alunoId), argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ))).thenReturn(alunoResponseDTO);

        mockMvc.perform(put("/api/v1/alunos/{id}", alunoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João da Silva"));

        verify(alunoService, times(1)).atualizarAluno(eq(alunoId), argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar aluno inexistente")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveRetornar404AoAtualizarAlunoInexistente() throws Exception {

        Long alunoId = 999L;
        when(alunoService.atualizarAluno(eq(alunoId), argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ))).thenThrow(new EntityNotFoundException("Aluno não encontrado"));

        mockMvc.perform(put("/api/v1/alunos/{id}", alunoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequestDTO)))
                .andExpect(status().isNotFound());

        verify(alunoService, times(1)).atualizarAluno(eq(alunoId), argThat(dto -> 
            "João da Silva".equals(dto.getNome()) &&
            "joao.silva@email.com".equals(dto.getEmail()) &&
            "2023001234".equals(dto.getMatricula()) &&
            "123456".equals(dto.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar deletar aluno sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveRetornar403AoTentarDeletarAlunoSemSerAdmin() throws Exception {

        Long alunoId = 1L;

        mockMvc.perform(delete("/api/v1/alunos/{id}", alunoId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(alunoService);
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveBuscarAlunoPorIdComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long alunoId = 1L;
        when(alunoService.buscarAlunoPorId(alunoId)).thenReturn(alunoResponseDTO);

        mockMvc.perform(get("/api/v1/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João da Silva"));

        verify(alunoService, times(1)).buscarAlunoPorId(alunoId);
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso quando usuário é PROFESSOR")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveBuscarAlunoPorIdComSucessoQuandoUsuarioEhProfessor() throws Exception {

        Long alunoId = 1L;
        when(alunoService.buscarAlunoPorId(alunoId)).thenReturn(alunoResponseDTO);

        mockMvc.perform(get("/api/v1/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(alunoService, times(1)).buscarAlunoPorId(alunoId);
    }

    @Test
    @DisplayName("Deve listar alunos com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveListarAlunosComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Page<AlunoResponseDTO> page = new PageImpl<>(List.of(alunoResponseDTO));
        when(alunoService.listarAlunos(eq(0), eq(10), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/v1/alunos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("João da Silva"));

        verify(alunoService, times(1)).listarAlunos(eq(0), eq(10), isNull());
    }

    @Test
    @DisplayName("Deve listar alunos com parâmetros de ordenação")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveListarAlunosComParametrosDeOrdenacao() throws Exception {

        Page<AlunoResponseDTO> page = new PageImpl<>(List.of(alunoResponseDTO));
        List<String> sortParams = List.of("nome,asc", "id,desc");
        when(alunoService.listarAlunos(eq(0), eq(5), eq(sortParams))).thenReturn(page);

        mockMvc.perform(get("/api/v1/alunos")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "nome,asc")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk());

        verify(alunoService, times(1)).listarAlunos(eq(0), eq(5), eq(sortParams));
    }

    @Test
    @DisplayName("Deve buscar dados do aluno logado com sucesso quando usuário é ALUNO")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveBuscarDadosDoAlunoLogadoComSucessoQuandoUsuarioEhAluno() throws Exception {

        when(alunoService.buscarAlunoLogado()).thenReturn(alunoResponseDTO);

        mockMvc.perform(get("/api/v1/alunos/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"));

        verify(alunoService, times(1)).buscarAlunoLogado();
    }

    @Test
    @DisplayName("Deve retornar 401 quando não autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/v1/alunos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(alunoService);
    }
}