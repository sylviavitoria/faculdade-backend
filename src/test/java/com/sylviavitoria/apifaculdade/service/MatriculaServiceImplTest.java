package com.sylviavitoria.apifaculdade.service;

import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.dto.NotaRequestDTO;
import com.sylviavitoria.apifaculdade.enums.StatusMatricula;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.mapper.MatriculaMapper;
import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Disciplina;
import com.sylviavitoria.apifaculdade.model.Matricula;
import com.sylviavitoria.apifaculdade.repository.AlunoRepository;
import com.sylviavitoria.apifaculdade.repository.DisciplinaRepository;
import com.sylviavitoria.apifaculdade.repository.MatriculaRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatriculaServiceImpl Tests")
class MatriculaServiceImplTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private MatriculaMapper matriculaMapper;

    @InjectMocks
    private MatriculaServiceImpl matriculaService;

    private MatriculaRequestDTO matriculaRequestDTO;
    private NotaRequestDTO notaRequestDTO;
    private Matricula matricula;
    private MatriculaResponseDTO matriculaResponseDTO;
    private Aluno aluno;
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        matriculaRequestDTO = new MatriculaRequestDTO();
        matriculaRequestDTO.setAlunoId(1L);
        matriculaRequestDTO.setDisciplinaId(1L);

        notaRequestDTO = new NotaRequestDTO();
        notaRequestDTO.setNota1(BigDecimal.valueOf(8.5));
        notaRequestDTO.setNota2(BigDecimal.valueOf(7.0));

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setEmail("joao@email.com");
        aluno.setMatricula("2023001");

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setNome("Algoritmos");
        disciplina.setCodigo("ALG101");

        matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setDisciplina(disciplina);
        matricula.setStatus(StatusMatricula.CURSANDO);
        matricula.setDataMatricula(LocalDateTime.now());

        matriculaResponseDTO = MatriculaResponseDTO.builder()
                .id(1L)
                .status(StatusMatricula.CURSANDO)
                .dataMatricula(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar matrícula com sucesso")
    void deveCriarMatriculaComSucesso() {

        when(matriculaRepository.existsByAlunoIdAndDisciplinaId(1L, 1L)).thenReturn(false);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.of(disciplina));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        MatriculaResponseDTO resultado = matriculaService.criarMatricula(matriculaRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo(StatusMatricula.CURSANDO);

        verify(matriculaRepository).existsByAlunoIdAndDisciplinaId(1L, 1L);
        verify(alunoRepository).findById(1L);
        verify(disciplinaRepository).findById(1L);
        verify(matriculaRepository).save(any(Matricula.class));
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve lançar exceção quando aluno já está matriculado na disciplina")
    void deveLancarExcecaoQuandoAlunoJaMatriculado() {

        when(matriculaRepository.existsByAlunoIdAndDisciplinaId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.criarMatricula(matriculaRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Aluno já está matriculado nesta disciplina");

        verify(matriculaRepository).existsByAlunoIdAndDisciplinaId(1L, 1L);
        verifyNoInteractions(alunoRepository);
        verifyNoInteractions(disciplinaRepository);
        verifyNoInteractions(matriculaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando aluno não encontrado")
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {

        when(matriculaRepository.existsByAlunoIdAndDisciplinaId(1L, 1L)).thenReturn(false);
        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.criarMatricula(matriculaRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Aluno não encontrado");

        verify(matriculaRepository).existsByAlunoIdAndDisciplinaId(1L, 1L);
        verify(alunoRepository).findById(1L);
        verifyNoInteractions(disciplinaRepository);
        verifyNoInteractions(matriculaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando disciplina não encontrada")
    void deveLancarExcecaoQuandoDisciplinaNaoEncontrada() {

        when(matriculaRepository.existsByAlunoIdAndDisciplinaId(1L, 1L)).thenReturn(false);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.criarMatricula(matriculaRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Disciplina não encontrada");

        verify(matriculaRepository).existsByAlunoIdAndDisciplinaId(1L, 1L);
        verify(alunoRepository).findById(1L);
        verify(disciplinaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
        verifyNoInteractions(matriculaMapper);
    }

    @Test
    @DisplayName("Deve buscar matrícula por ID com sucesso")
    void deveBuscarMatriculaPorIdComSucesso() {

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        MatriculaResponseDTO resultado = matriculaService.buscarMatriculaPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);

        verify(matriculaRepository).findById(1L);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada por ID")
    void deveLancarExcecaoQuandoMatriculaNaoEncontradaPorId() {

        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.buscarMatriculaPorId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Matrícula não encontrada");

        verify(matriculaRepository).findById(1L);
        verifyNoInteractions(matriculaMapper);
    }

    @Test
    @DisplayName("Deve listar matrículas com paginação padrão")
    void deveListarMatriculasComPaginacaoPadrao() {

        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("dataMatricula").descending());
        Page<Matricula> pageMatriculas = new PageImpl<>(List.of(matricula), pageableEsperado, 1);
        
        when(matriculaRepository.findAll(pageableEsperado)).thenReturn(pageMatriculas);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        Page<MatriculaResponseDTO> resultado = matriculaService.listarMatriculas(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(1L);

        verify(matriculaRepository).findAll(pageableEsperado);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve listar matrículas com ordenação customizada")
    void deveListarMatriculasComOrdenacaoCustomizada() {

        List<String> sort = List.of("id");
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("id"));
        Page<Matricula> pageMatriculas = new PageImpl<>(List.of(matricula), pageableEsperado, 1);
        
        when(matriculaRepository.findAll(pageableEsperado)).thenReturn(pageMatriculas);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        Page<MatriculaResponseDTO> resultado = matriculaService.listarMatriculas(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(matriculaRepository).findAll(pageableEsperado);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve listar matrículas com lista de ordenação vazia")
    void deveListarMatriculasComListaOrdenacaoVazia() {

        List<String> sort = Collections.emptyList();
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("dataMatricula").descending());
        Page<Matricula> pageMatriculas = new PageImpl<>(List.of(matricula), pageableEsperado, 1);
        
        when(matriculaRepository.findAll(pageableEsperado)).thenReturn(pageMatriculas);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        Page<MatriculaResponseDTO> resultado = matriculaService.listarMatriculas(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(matriculaRepository).findAll(pageableEsperado);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve deletar matrícula com sucesso")
    void deveDeletarMatriculaComSucesso() {

        when(matriculaRepository.existsById(1L)).thenReturn(true);

        matriculaService.deletarMatricula(1L);

        verify(matriculaRepository).existsById(1L);
        verify(matriculaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar matrícula não encontrada")
    void deveLancarExcecaoAoDeletarMatriculaNaoEncontrada() {

        when(matriculaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> matriculaService.deletarMatricula(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Matrícula não encontrada");

        verify(matriculaRepository).existsById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    @DisplayName("Deve atualizar notas com sucesso")
    void deveAtualizarNotasComSucesso() {

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(matricula)).thenReturn(matricula);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        MatriculaResponseDTO resultado = matriculaService.atualizarNotas(1L, notaRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(matricula.getNota1()).isEqualTo(BigDecimal.valueOf(8.5));
        assertThat(matricula.getNota2()).isEqualTo(BigDecimal.valueOf(7.0));

        verify(matriculaRepository).findById(1L);
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve atualizar apenas nota1 quando nota2 é null")
    void deveAtualizarApenasNota1QuandoNota2EhNull() {

        NotaRequestDTO notaComApenasnota1 = new NotaRequestDTO();
        notaComApenasnota1.setNota1(BigDecimal.valueOf(9.0));
        notaComApenasnota1.setNota2(null);

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(matricula)).thenReturn(matricula);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        MatriculaResponseDTO resultado = matriculaService.atualizarNotas(1L, notaComApenasnota1);

        assertThat(resultado).isNotNull();
        assertThat(matricula.getNota1()).isEqualTo(BigDecimal.valueOf(9.0));
        assertThat(matricula.getNota2()).isNull();

        verify(matriculaRepository).findById(1L);
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve atualizar apenas nota2 quando nota1 é null")
    void deveAtualizarApenasNota2QuandoNota1EhNull() {

        NotaRequestDTO notaComApenasNota2 = new NotaRequestDTO();
        notaComApenasNota2.setNota1(null);
        notaComApenasNota2.setNota2(BigDecimal.valueOf(6.5));

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(matricula)).thenReturn(matricula);
        when(matriculaMapper.toDTO(matricula)).thenReturn(matriculaResponseDTO);

        MatriculaResponseDTO resultado = matriculaService.atualizarNotas(1L, notaComApenasNota2);

        assertThat(resultado).isNotNull();
        assertThat(matricula.getNota1()).isNull();
        assertThat(matricula.getNota2()).isEqualTo(BigDecimal.valueOf(6.5));

        verify(matriculaRepository).findById(1L);
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toDTO(matricula);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar notas de matrícula não encontrada")
    void deveLancarExcecaoAoAtualizarNotasDeMatriculaNaoEncontrada() {

        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.atualizarNotas(1L, notaRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Matrícula não encontrada");

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
        verifyNoInteractions(matriculaMapper);
    }
}