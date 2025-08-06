package com.sylviavitoria.apifaculdade.service;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.mapper.AlunoMapper;
import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.AlunoRepository;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlunoServiceImpl Tests")
class AlunoServiceImplTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlunoMapper alunoMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AlunoServiceImpl alunoService;

    private AlunoRequestDTO alunoRequestDTO;
    private Aluno aluno;
    private AlunoResponseDTO alunoResponseDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        alunoRequestDTO = new AlunoRequestDTO();
        alunoRequestDTO.setNome("João da Silva");
        alunoRequestDTO.setEmail("joao@email.com");
        alunoRequestDTO.setMatricula("2023001");
        alunoRequestDTO.setSenha("senha123");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João da Silva");
        aluno.setEmail("joao@email.com");
        aluno.setMatricula("2023001");
        aluno.setSenha("senha123");

        alunoResponseDTO = AlunoResponseDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao@email.com")
                .matricula("2023001")
                .build();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaEncriptada");
        usuario.setTipo(TipoUsuario.ALUNO);
        usuario.setAluno(aluno);
    }

    @Test
    @DisplayName("Deve criar aluno com sucesso")
    void deveCriarAlunoComSucesso() {

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(alunoRepository.existsByMatricula("2023001")).thenReturn(false);
        when(alunoMapper.toEntity(alunoRequestDTO)).thenReturn(aluno);
        when(alunoRepository.save(aluno)).thenReturn(aluno);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        AlunoResponseDTO resultado = alunoService.criarAluno(alunoRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João da Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getMatricula()).isEqualTo("2023001");

        verify(usuarioRepository).existsByEmail("joao@email.com");
        verify(alunoRepository).existsByMatricula("2023001");
        verify(alunoMapper).toEntity(alunoRequestDTO);
        verify(alunoRepository).save(aluno);
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.criarAluno(alunoRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um usuário com esse email");

        verify(usuarioRepository).existsByEmail("joao@email.com");
        verifyNoInteractions(alunoRepository);
        verifyNoInteractions(alunoMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula já existe")
    void deveLancarExcecaoQuandoMatriculaJaExiste() {

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(alunoRepository.existsByMatricula("2023001")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.criarAluno(alunoRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um aluno com essa matrícula");

        verify(usuarioRepository).existsByEmail("joao@email.com");
        verify(alunoRepository).existsByMatricula("2023001");
        verifyNoMoreInteractions(alunoRepository);
        verifyNoInteractions(alunoMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso")
    void deveBuscarAlunoPorIdComSucesso() {

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        AlunoResponseDTO resultado = alunoService.buscarAlunoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João da Silva");

        verify(alunoRepository).findById(1L);
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção quando aluno não encontrado por ID")
    void deveLancarExcecaoQuandoAlunoNaoEncontradoPorId() {

        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarAlunoPorId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Aluno não encontrado");

        verify(alunoRepository).findById(1L);
        verifyNoInteractions(alunoMapper);
    }

    @Test
    @DisplayName("Deve listar alunos com paginação padrão")
    void deveListarAlunosComPaginacaoPadrao() {

        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Aluno> pageAlunos = new PageImpl<>(List.of(aluno), pageableEsperado, 1);
        
        when(alunoRepository.findAll(pageableEsperado)).thenReturn(pageAlunos);
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        Page<AlunoResponseDTO> resultado = alunoService.listarAlunos(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(1L);

        verify(alunoRepository).findAll(pageableEsperado);
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve listar alunos com ordenação customizada")
    void deveListarAlunosComOrdenacaoCustomizada() {

        List<String> sort = List.of("email");
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("email"));
        Page<Aluno> pageAlunos = new PageImpl<>(List.of(aluno), pageableEsperado, 1);
        
        when(alunoRepository.findAll(pageableEsperado)).thenReturn(pageAlunos);
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        Page<AlunoResponseDTO> resultado = alunoService.listarAlunos(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(alunoRepository).findAll(pageableEsperado);
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve listar alunos com lista de ordenação vazia")
    void deveListarAlunosComListaOrdenacaoVazia() {

        List<String> sort = Collections.emptyList();
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Aluno> pageAlunos = new PageImpl<>(List.of(aluno), pageableEsperado, 1);
        
        when(alunoRepository.findAll(pageableEsperado)).thenReturn(pageAlunos);
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        Page<AlunoResponseDTO> resultado = alunoService.listarAlunos(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(alunoRepository).findAll(pageableEsperado);
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve deletar aluno com sucesso")
    void deveDeletarAlunoComSucesso() {

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        alunoService.deletarAluno(1L);

        verify(alunoRepository).findById(1L);
        verify(usuarioRepository).deleteByAluno(aluno);
        verify(alunoRepository).delete(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar aluno não encontrado")
    void deveLancarExcecaoAoDeletarAlunoNaoEncontrado() {

        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.deletarAluno(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Aluno não encontrado");

        verify(alunoRepository).findById(1L);
        verifyNoMoreInteractions(alunoRepository);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso")
    void deveAtualizarAlunoComSucesso() {

        AlunoRequestDTO requestAtualizado = new AlunoRequestDTO();
        requestAtualizado.setNome("João Silva Atualizado");
        requestAtualizado.setEmail("joao.novo@email.com");
        requestAtualizado.setMatricula("2023002");
        requestAtualizado.setSenha("novaSenha123");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(usuarioRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(alunoRepository.existsByMatricula("2023002")).thenReturn(false);
        when(usuarioRepository.findByAluno(aluno)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncriptada");
        when(alunoRepository.save(aluno)).thenReturn(aluno);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        AlunoResponseDTO resultado = alunoService.atualizarAluno(1L, requestAtualizado);

        assertThat(resultado).isNotNull();

        verify(alunoRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("joao.novo@email.com");
        verify(alunoRepository).existsByMatricula("2023002");
        verify(usuarioRepository).findByAluno(aluno);
        verify(passwordEncoder).encode("novaSenha123");
        verify(alunoRepository).save(aluno);
        verify(usuarioRepository).save(usuario);
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar aluno não encontrado")
    void deveLancarExcecaoAoAtualizarAlunoNaoEncontrado() {

        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.atualizarAluno(1L, alunoRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Aluno não encontrado");

        verify(alunoRepository).findById(1L);
        verifyNoMoreInteractions(alunoRepository);
        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(alunoMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já em uso")
    void deveLancarExcecaoAoAtualizarComEmailJaEmUso() {

        AlunoRequestDTO requestComEmailExistente = new AlunoRequestDTO();
        requestComEmailExistente.setNome("João Silva");
        requestComEmailExistente.setEmail("outro@email.com");
        requestComEmailExistente.setMatricula("2023001");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(usuarioRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.atualizarAluno(1L, requestComEmailExistente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já está em uso");

        verify(alunoRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("outro@email.com");
        verifyNoMoreInteractions(usuarioRepository);
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com matrícula já em uso")
    void deveLancarExcecaoAoAtualizarComMatriculaJaEmUso() {

        AlunoRequestDTO requestComMatriculaExistente = new AlunoRequestDTO();
        requestComMatriculaExistente.setNome("João Silva");
        requestComMatriculaExistente.setEmail("joao@email.com");
        requestComMatriculaExistente.setMatricula("2023999");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.existsByMatricula("2023999")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.atualizarAluno(1L, requestComMatriculaExistente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Matrícula já está em uso");

        verify(alunoRepository).findById(1L);
        verify(alunoRepository).existsByMatricula("2023999");
        verifyNoMoreInteractions(alunoRepository);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve buscar aluno logado com sucesso")
    void deveBuscarAlunoLogadoComSucesso() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(alunoMapper.toDTO(aluno)).thenReturn(alunoResponseDTO);

        AlunoResponseDTO resultado = alunoService.buscarAlunoLogado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);

        verify(usuarioRepository).findByEmail("joao@email.com");
        verify(alunoMapper).toDTO(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não encontrado")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoEncontrado() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarAlunoLogado())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuário não encontrado");

        verify(usuarioRepository).findByEmail("joao@email.com");
        verifyNoInteractions(alunoMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não é aluno")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoEhAluno() {

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setTipo(TipoUsuario.ADMIN);
        usuarioAdmin.setAluno(null);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@email.com");
        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(usuarioAdmin));

        assertThatThrownBy(() -> alunoService.buscarAlunoLogado())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não é um aluno");

        verify(usuarioRepository).findByEmail("admin@email.com");
        verifyNoInteractions(alunoMapper);
    }

}
