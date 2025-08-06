package com.sylviavitoria.apifaculdade.service;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.mapper.DisciplinaMapper;
import com.sylviavitoria.apifaculdade.model.Disciplina;
import com.sylviavitoria.apifaculdade.model.Professor;
import com.sylviavitoria.apifaculdade.repository.DisciplinaRepository;
import com.sylviavitoria.apifaculdade.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DisciplinaServiceImpl Tests")
class DisciplinaServiceImplTest {

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private DisciplinaMapper disciplinaMapper;

    @InjectMocks
    private DisciplinaServiceImpl disciplinaService;

    private DisciplinaRequestDTO disciplinaRequestDTO;
    private Disciplina disciplina;
    private DisciplinaResponseDTO disciplinaResponseDTO;
    private Professor professor;
    private ProfessorResponseDTO professorResponseDTO;

    @BeforeEach
    void setUp() {
        disciplinaRequestDTO = new DisciplinaRequestDTO();
        disciplinaRequestDTO.setNome("Algoritmos e Estruturas de Dados");
        disciplinaRequestDTO.setCodigo("AED101");
        disciplinaRequestDTO.setProfessorId(1L);

        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Dr. João Silva");
        professor.setEmail("joao.silva@universidade.com");
        professor.setSenha("senhaEncriptada");

        professorResponseDTO = ProfessorResponseDTO.builder()
                .nome("Dr. João Silva")
                .email("joao.silva@universidade.com")
                .build();

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setNome("Algoritmos e Estruturas de Dados");
        disciplina.setCodigo("AED101");
        disciplina.setProfessor(professor);

        disciplinaResponseDTO = new DisciplinaResponseDTO();
        disciplinaResponseDTO.setId(1L);
        disciplinaResponseDTO.setNome("Algoritmos e Estruturas de Dados");
        disciplinaResponseDTO.setCodigo("AED101");
        disciplinaResponseDTO.setProfessor(professorResponseDTO);
    }

    @Test
    @DisplayName("Deve criar disciplina com sucesso")
    void deveCriarDisciplinaComSucesso() {

        when(disciplinaRepository.existsByCodigo("AED101")).thenReturn(false);
        when(disciplinaMapper.toEntity(disciplinaRequestDTO)).thenReturn(disciplina);
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(disciplinaRepository.save(disciplina)).thenReturn(disciplina);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.criarDisciplina(disciplinaRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Algoritmos e Estruturas de Dados");
        assertThat(resultado.getCodigo()).isEqualTo("AED101");
        assertThat(resultado.getProfessor()).isNotNull();

        verify(disciplinaRepository).existsByCodigo("AED101");
        verify(disciplinaMapper).toEntity(disciplinaRequestDTO);
        verify(professorRepository).findById(1L);
        verify(disciplinaRepository).save(disciplina);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve criar disciplina sem professor com sucesso")
    void deveCriarDisciplinaSemProfessorComSucesso() {

        disciplinaRequestDTO.setProfessorId(null);
        disciplina.setProfessor(null);
        disciplinaResponseDTO.setProfessor(null);

        when(disciplinaRepository.existsByCodigo("AED101")).thenReturn(false);
        when(disciplinaMapper.toEntity(disciplinaRequestDTO)).thenReturn(disciplina);
        when(disciplinaRepository.save(disciplina)).thenReturn(disciplina);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.criarDisciplina(disciplinaRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Algoritmos e Estruturas de Dados");
        assertThat(resultado.getCodigo()).isEqualTo("AED101");
        assertThat(resultado.getProfessor()).isNull();

        verify(disciplinaRepository).existsByCodigo("AED101");
        verify(disciplinaMapper).toEntity(disciplinaRequestDTO);
        verify(disciplinaRepository).save(disciplina);
        verify(disciplinaMapper).toDTO(disciplina);
        verifyNoInteractions(professorRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código já existe")
    void deveLancarExcecaoQuandoCodigoJaExiste() {

        when(disciplinaRepository.existsByCodigo("AED101")).thenReturn(true);

        assertThatThrownBy(() -> disciplinaService.criarDisciplina(disciplinaRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe uma disciplina com esse código");

        verify(disciplinaRepository).existsByCodigo("AED101");
        verifyNoInteractions(disciplinaMapper);
        verifyNoInteractions(professorRepository);
        verifyNoMoreInteractions(disciplinaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando professor não encontrado")
    void deveLancarExcecaoQuandoProfessorNaoEncontrado() {

        when(disciplinaRepository.existsByCodigo("AED101")).thenReturn(false);
        when(disciplinaMapper.toEntity(disciplinaRequestDTO)).thenReturn(disciplina);
        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.criarDisciplina(disciplinaRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Professor não encontrado");

        verify(disciplinaRepository).existsByCodigo("AED101");
        verify(disciplinaMapper).toEntity(disciplinaRequestDTO);
        verify(professorRepository).findById(1L);
        verifyNoMoreInteractions(disciplinaRepository);
    }

    @Test
    @DisplayName("Deve buscar disciplina por ID com sucesso")
    void deveBuscarDisciplinaPorIdComSucesso() {
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.buscarDisciplinaPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Algoritmos e Estruturas de Dados");
        assertThat(resultado.getCodigo()).isEqualTo("AED101");

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve lançar exceção quando disciplina não encontrada por ID")
    void deveLancarExcecaoQuandoDisciplinaNaoEncontradaPorId() {

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.buscarDisciplinaPorId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Disciplina não encontrada");

        verify(disciplinaRepository).findById(1L);
        verifyNoInteractions(disciplinaMapper);
    }

    @Test
    @DisplayName("Deve listar disciplinas com paginação padrão")
    void deveListarDisciplinasComPaginacaoPadrao() {

        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Disciplina> pageDisciplinas = new PageImpl<>(List.of(disciplina), pageableEsperado, 1);
        
        when(disciplinaRepository.findAll(pageableEsperado)).thenReturn(pageDisciplinas);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        Page<DisciplinaResponseDTO> resultado = disciplinaService.listarDisciplinas(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Algoritmos e Estruturas de Dados");

        verify(disciplinaRepository).findAll(pageableEsperado);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve listar disciplinas com ordenação customizada")
    void deveListarDisciplinasComOrdenacaoCustomizada() {

        List<String> sort = List.of("codigo");
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("codigo"));
        Page<Disciplina> pageDisciplinas = new PageImpl<>(List.of(disciplina), pageableEsperado, 1);
        
        when(disciplinaRepository.findAll(pageableEsperado)).thenReturn(pageDisciplinas);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        Page<DisciplinaResponseDTO> resultado = disciplinaService.listarDisciplinas(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getCodigo()).isEqualTo("AED101");

        verify(disciplinaRepository).findAll(pageableEsperado);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve listar disciplinas com lista de ordenação vazia")
    void deveListarDisciplinasComListaOrdenacaoVazia() {

        List<String> sort = Collections.emptyList();
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Disciplina> pageDisciplinas = new PageImpl<>(List.of(disciplina), pageableEsperado, 1);
        
        when(disciplinaRepository.findAll(pageableEsperado)).thenReturn(pageDisciplinas);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        Page<DisciplinaResponseDTO> resultado = disciplinaService.listarDisciplinas(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Algoritmos e Estruturas de Dados");

        verify(disciplinaRepository).findAll(pageableEsperado);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve deletar disciplina com sucesso")
    void deveDeletarDisciplinaComSucesso() {

        when(disciplinaRepository.existsById(1L)).thenReturn(true);

        disciplinaService.deletarDisciplina(1L);


        verify(disciplinaRepository).existsById(1L);
        verify(disciplinaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar disciplina não encontrada")
    void deveLancarExcecaoAoDeletarDisciplinaNaoEncontrada() {

        when(disciplinaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> disciplinaService.deletarDisciplina(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Disciplina não encontrada");

        verify(disciplinaRepository).existsById(1L);
        verifyNoMoreInteractions(disciplinaRepository);
    }

    @Test
    @DisplayName("Deve atualizar disciplina com sucesso")
    void deveAtualizarDisciplinaComSucesso() {

        DisciplinaRequestDTO requestAtualizado = new DisciplinaRequestDTO();
        requestAtualizado.setNome("Algoritmos Avançados");
        requestAtualizado.setCodigo("ALG201");
        requestAtualizado.setProfessorId(2L);

        Professor novoProfessor = new Professor();
        novoProfessor.setId(2L);
        novoProfessor.setNome("Dra. Maria Santos");

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(disciplinaRepository.existsByCodigo("ALG201")).thenReturn(false);
        when(professorRepository.findById(2L)).thenReturn(Optional.of(novoProfessor));
        when(disciplinaRepository.save(disciplina)).thenReturn(disciplina);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.atualizarDisciplina(1L, requestAtualizado);

        assertThat(resultado).isNotNull();

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaRepository).existsByCodigo("ALG201");
        verify(disciplinaMapper).updateEntity(requestAtualizado, disciplina);
        verify(professorRepository).findById(2L);
        verify(disciplinaRepository).save(disciplina);
        verify(disciplinaMapper).toDTO(disciplina);
    }

    @Test
    @DisplayName("Deve atualizar disciplina mantendo o mesmo código")
    void deveAtualizarDisciplinaManendoMesmoCodigo() {

        DisciplinaRequestDTO requestAtualizado = new DisciplinaRequestDTO();
        requestAtualizado.setNome("Algoritmos e Estruturas de Dados - Avançado");
        requestAtualizado.setCodigo("AED101"); 
        requestAtualizado.setProfessorId(1L);

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(disciplinaRepository.save(disciplina)).thenReturn(disciplina);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.atualizarDisciplina(1L, requestAtualizado);

        assertThat(resultado).isNotNull();

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaMapper).updateEntity(requestAtualizado, disciplina);
        verify(professorRepository).findById(1L);
        verify(disciplinaRepository).save(disciplina);
        verify(disciplinaMapper).toDTO(disciplina);
        verify(disciplinaRepository, never()).existsByCodigo("AED101");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar disciplina não encontrada")
    void deveLancarExcecaoAoAtualizarDisciplinaNaoEncontrada() {

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.atualizarDisciplina(1L, disciplinaRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Disciplina não encontrada");

        verify(disciplinaRepository).findById(1L);
        verifyNoMoreInteractions(disciplinaRepository);
        verifyNoInteractions(professorRepository);
        verifyNoInteractions(disciplinaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com código já em uso")
    void deveLancarExcecaoAoAtualizarComCodigoJaEmUso() {

        DisciplinaRequestDTO requestComCodigoExistente = new DisciplinaRequestDTO();
        requestComCodigoExistente.setNome("Nova Disciplina");
        requestComCodigoExistente.setCodigo("MAT101"); 
        requestComCodigoExistente.setProfessorId(1L);

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(disciplinaRepository.existsByCodigo("MAT101")).thenReturn(true);

        assertThatThrownBy(() -> disciplinaService.atualizarDisciplina(1L, requestComCodigoExistente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe uma disciplina com esse código");

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaRepository).existsByCodigo("MAT101");
        verifyNoMoreInteractions(disciplinaRepository);
        verifyNoInteractions(professorRepository);
        verifyNoInteractions(disciplinaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com professor não encontrado")
    void deveLancarExcecaoAoAtualizarComProfessorNaoEncontrado() {

        DisciplinaRequestDTO requestComProfessorInexistente = new DisciplinaRequestDTO();
        requestComProfessorInexistente.setNome("Algoritmos");
        requestComProfessorInexistente.setCodigo("AED101");
        requestComProfessorInexistente.setProfessorId(999L);

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(professorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.atualizarDisciplina(1L, requestComProfessorInexistente))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Professor não encontrado");

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaMapper).updateEntity(requestComProfessorInexistente, disciplina);
        verify(professorRepository).findById(999L);
        verifyNoMoreInteractions(disciplinaRepository);
    }

    @Test
    @DisplayName("Deve atualizar disciplina sem professor")
    void deveAtualizarDisciplinaSemProfessor() {

        DisciplinaRequestDTO requestSemProfessor = new DisciplinaRequestDTO();
        requestSemProfessor.setNome("Algoritmos Independentes");
        requestSemProfessor.setCodigo("ALG301");
        requestSemProfessor.setProfessorId(null);

        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(disciplinaRepository.existsByCodigo("ALG301")).thenReturn(false);
        when(disciplinaRepository.save(disciplina)).thenReturn(disciplina);
        when(disciplinaMapper.toDTO(disciplina)).thenReturn(disciplinaResponseDTO);

        DisciplinaResponseDTO resultado = disciplinaService.atualizarDisciplina(1L, requestSemProfessor);

        assertThat(resultado).isNotNull();

        verify(disciplinaRepository).findById(1L);
        verify(disciplinaRepository).existsByCodigo("ALG301");
        verify(disciplinaMapper).updateEntity(requestSemProfessor, disciplina);
        verify(disciplinaRepository).save(disciplina);
        verify(disciplinaMapper).toDTO(disciplina);
        verifyNoInteractions(professorRepository);
    }
}
