package com.sylviavitoria.apifaculdade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.enums.StatusMatricula;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.MatriculaService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MatriculaController.class,
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
@DisplayName("MatriculaController Tests")
class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatriculaService matriculaService;

    private MatriculaRequestDTO matriculaRequestDTO;
    private MatriculaResponseDTO matriculaResponseDTO;
    private NotaRequestDTO notaRequestDTO;
    private AlunoResponseDTO alunoResponseDTO;
    private DisciplinaResponseDTO disciplinaResponseDTO;
    private ProfessorResponseDTO professorResponseDTO;

    @BeforeEach
    void setUp() {

        alunoResponseDTO = AlunoResponseDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao.silva@email.com")
                .matricula("2023001234")
                .build();

        professorResponseDTO = ProfessorResponseDTO.builder()
                .nome("Maria Silva")
                .email("maria.silva@universidade.com")
                .build();

        disciplinaResponseDTO = new DisciplinaResponseDTO();
        disciplinaResponseDTO.setId(1L);
        disciplinaResponseDTO.setNome("Algoritmos");
        disciplinaResponseDTO.setCodigo("ALG101");
        disciplinaResponseDTO.setProfessor(professorResponseDTO);

        matriculaRequestDTO = new MatriculaRequestDTO();
        matriculaRequestDTO.setAlunoId(1L);
        matriculaRequestDTO.setDisciplinaId(1L);

        matriculaResponseDTO = MatriculaResponseDTO.builder()
                .id(1L)
                .aluno(alunoResponseDTO)
                .disciplina(disciplinaResponseDTO)
                .nota1(null)
                .nota2(null)
                .status(StatusMatricula.CURSANDO)
                .dataMatricula(LocalDateTime.now())
                .build();

        notaRequestDTO = new NotaRequestDTO();
        notaRequestDTO.setNota1(BigDecimal.valueOf(8.5));
        notaRequestDTO.setNota2(BigDecimal.valueOf(7.0));
    }

    @Test
    @DisplayName("Deve criar matrícula com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveCriarMatriculaComSucessoQuandoUsuarioEhAdmin() throws Exception {

        when(matriculaService.criarMatricula(argThat(dto -> 
            dto.getAlunoId().equals(1L) &&
            dto.getDisciplinaId().equals(1L)
        ))).thenReturn(matriculaResponseDTO);

        mockMvc.perform(post("/api/v1/matriculas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matriculaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.aluno.id").value(1L))
                .andExpect(jsonPath("$.aluno.nome").value("João da Silva"))
                .andExpect(jsonPath("$.disciplina.id").value(1L))
                .andExpect(jsonPath("$.disciplina.nome").value("Algoritmos"))
                .andExpect(jsonPath("$.status").value("CURSANDO"));

        verify(matriculaService, times(1)).criarMatricula(argThat(dto -> 
            dto.getAlunoId().equals(1L) &&
            dto.getDisciplinaId().equals(1L)
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar criar matrícula sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveRetornar403AoTentarCriarMatriculaSemSerAdmin() throws Exception {

        mockMvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matriculaRequestDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(matriculaService);
    }

    @Test
    @DisplayName("Deve atualizar notas com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveAtualizarNotasComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long matriculaId = 1L;
        MatriculaResponseDTO matriculaAtualizada = MatriculaResponseDTO.builder()
                .id(matriculaId)
                .aluno(alunoResponseDTO)
                .disciplina(disciplinaResponseDTO)
                .nota1(BigDecimal.valueOf(8.5))
                .nota2(BigDecimal.valueOf(7.0))
                .status(StatusMatricula.APROVADO)
                .dataMatricula(LocalDateTime.now())
                .build();

        when(matriculaService.atualizarNotas(eq(matriculaId), argThat(dto -> 
            dto.getNota1().compareTo(BigDecimal.valueOf(8.5)) == 0 &&
            dto.getNota2().compareTo(BigDecimal.valueOf(7.0)) == 0
        )))
                .thenReturn(matriculaAtualizada);

        mockMvc.perform(put("/api/v1/matriculas/{id}/notas", matriculaId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matriculaId))
                .andExpect(jsonPath("$.nota1").value(8.5))
                .andExpect(jsonPath("$.nota2").value(7.0))
                .andExpect(jsonPath("$.status").value("APROVADO"));

        verify(matriculaService, times(1)).atualizarNotas(eq(matriculaId), argThat(dto -> 
            dto.getNota1().compareTo(BigDecimal.valueOf(8.5)) == 0 &&
            dto.getNota2().compareTo(BigDecimal.valueOf(7.0)) == 0
        ));
    }

    @Test
    @DisplayName("Deve atualizar notas com sucesso quando usuário é PROFESSOR")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveAtualizarNotasComSucessoQuandoUsuarioEhProfessor() throws Exception {

        Long matriculaId = 1L;
        MatriculaResponseDTO matriculaAtualizada = MatriculaResponseDTO.builder()
                .id(matriculaId)
                .aluno(alunoResponseDTO)
                .disciplina(disciplinaResponseDTO)
                .nota1(BigDecimal.valueOf(8.5))
                .nota2(BigDecimal.valueOf(7.0))
                .status(StatusMatricula.APROVADO)
                .dataMatricula(LocalDateTime.now())
                .build();

        when(matriculaService.atualizarNotas(eq(matriculaId), argThat(dto -> 
            dto.getNota1().compareTo(BigDecimal.valueOf(8.5)) == 0 &&
            dto.getNota2().compareTo(BigDecimal.valueOf(7.0)) == 0
        )))
                .thenReturn(matriculaAtualizada);

        mockMvc.perform(put("/api/v1/matriculas/{id}/notas", matriculaId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota1").value(8.5))
                .andExpect(jsonPath("$.nota2").value(7.0));

        verify(matriculaService, times(1)).atualizarNotas(eq(matriculaId), argThat(dto -> 
            dto.getNota1().compareTo(BigDecimal.valueOf(8.5)) == 0 &&
            dto.getNota2().compareTo(BigDecimal.valueOf(7.0)) == 0
        ));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar atualizar notas quando usuário é ALUNO")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveRetornar403AoTentarAtualizarNotasQuandoUsuarioEhAluno() throws Exception {

        Long matriculaId = 1L;

        mockMvc.perform(put("/api/v1/matriculas/{id}/notas", matriculaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notaRequestDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(matriculaService);
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar deletar matrícula sem ser ADMIN")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveRetornar403AoTentarDeletarMatriculaSemSerAdmin() throws Exception {

        Long matriculaId = 1L;

        mockMvc.perform(delete("/api/v1/matriculas/{id}", matriculaId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(matriculaService);
    }

    @Test
    @DisplayName("Deve buscar matrícula por ID com sucesso quando usuário é ADMIN")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveBuscarMatriculaPorIdComSucessoQuandoUsuarioEhAdmin() throws Exception {

        Long matriculaId = 1L;
        when(matriculaService.buscarMatriculaPorId(matriculaId)).thenReturn(matriculaResponseDTO);

        mockMvc.perform(get("/api/v1/matriculas/{id}", matriculaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.aluno.nome").value("João da Silva"))
                .andExpect(jsonPath("$.disciplina.nome").value("Algoritmos"));

        verify(matriculaService, times(1)).buscarMatriculaPorId(matriculaId);
    }

    @Test
    @DisplayName("Deve buscar matrícula por ID com sucesso quando usuário é PROFESSOR")
    @WithMockUser(authorities = "ROLE_PROFESSOR")
    void deveBuscarMatriculaPorIdComSucessoQuandoUsuarioEhProfessor() throws Exception {

        Long matriculaId = 1L;
        when(matriculaService.buscarMatriculaPorId(matriculaId)).thenReturn(matriculaResponseDTO);

        mockMvc.perform(get("/api/v1/matriculas/{id}", matriculaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(matriculaService, times(1)).buscarMatriculaPorId(matriculaId);
    }

    @Test
    @DisplayName("Deve buscar matrícula por ID com sucesso quando usuário é ALUNO")
    @WithMockUser(authorities = "ROLE_ALUNO")
    void deveBuscarMatriculaPorIdComSucessoQuandoUsuarioEhAluno() throws Exception {

        Long matriculaId = 1L;
        when(matriculaService.buscarMatriculaPorId(matriculaId)).thenReturn(matriculaResponseDTO);

        mockMvc.perform(get("/api/v1/matriculas/{id}", matriculaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(matriculaService, times(1)).buscarMatriculaPorId(matriculaId);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar matrícula inexistente")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveRetornar404AoBuscarMatriculaInexistente() throws Exception {

        Long matriculaId = 999L;
        when(matriculaService.buscarMatriculaPorId(matriculaId))
                .thenThrow(new EntityNotFoundException("Matrícula não encontrada"));

        mockMvc.perform(get("/api/v1/matriculas/{id}", matriculaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Matrícula não encontrada"));

        verify(matriculaService, times(1)).buscarMatriculaPorId(matriculaId);
    }


    @Test
    @DisplayName("Deve listar matrículas com parâmetros de ordenação")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deveListarMatriculasComParametrosDeOrdenacao() throws Exception {

        Page<MatriculaResponseDTO> page = new PageImpl<>(List.of(matriculaResponseDTO));
        List<String> sortParams = List.of("dataMatricula,desc", "id,asc");
        when(matriculaService.listarMatriculas(eq(0), eq(5), eq(sortParams))).thenReturn(page);

        mockMvc.perform(get("/api/v1/matriculas")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "dataMatricula,desc")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk());

        verify(matriculaService, times(1)).listarMatriculas(eq(0), eq(5), eq(sortParams));
    }

    @Test
    @DisplayName("Deve retornar 401 quando não autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {

        mockMvc.perform(get("/api/v1/matriculas"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(matriculaService);
    }

}
