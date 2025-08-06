package com.sylviavitoria.apifaculdade.service;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.mapper.ProfessorMapper;
import com.sylviavitoria.apifaculdade.model.Professor;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.ProfessorRepository;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
import com.sylviavitoria.apifaculdade.security.UsuarioUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
@DisplayName("ProfessorServiceImpl Tests")
class ProfessorServiceImplTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfessorMapper professorMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UsuarioUserDetails usuarioUserDetails;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    private ProfessorRequestDTO professorRequestDTO;
    private Professor professor;
    private ProfessorResponseDTO professorResponseDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        professorRequestDTO = new ProfessorRequestDTO();
        professorRequestDTO.setNome("Maria Silva");
        professorRequestDTO.setEmail("maria.silva@universidade.com");
        professorRequestDTO.setSenha("senha123");

        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Maria Silva");
        professor.setEmail("maria.silva@universidade.com");
        professor.setSenha("senha123");

        professorResponseDTO = ProfessorResponseDTO.builder()
                .nome("Maria Silva")
                .email("maria.silva@universidade.com")
                .build();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("maria.silva@universidade.com");
        usuario.setSenha("senhaEncriptada");
        usuario.setTipo(TipoUsuario.PROFESSOR);
        usuario.setProfessor(professor);
    }

    @Test
    @DisplayName("Deve criar professor com sucesso")
    void deveCriarProfessorComSucesso() {

        when(usuarioRepository.existsByEmail("maria.silva@universidade.com")).thenReturn(false);
        when(professorMapper.toEntity(professorRequestDTO)).thenReturn(professor);
        when(professorRepository.save(professor)).thenReturn(professor);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

        ProfessorResponseDTO resultado = professorService.criarProfessor(professorRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.getEmail()).isEqualTo("maria.silva@universidade.com");

        verify(usuarioRepository).existsByEmail("maria.silva@universidade.com");
        verify(professorMapper).toEntity(professorRequestDTO);
        verify(professorRepository).save(professor);
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(professorMapper).toDTO(professor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {

        when(usuarioRepository.existsByEmail("maria.silva@universidade.com")).thenReturn(true);

        assertThatThrownBy(() -> professorService.criarProfessor(professorRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository).existsByEmail("maria.silva@universidade.com");
        verifyNoInteractions(professorMapper);
        verifyNoInteractions(professorRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve buscar professor por ID com sucesso")
    void deveBuscarProfessorPorIdComSucesso() {

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

        ProfessorResponseDTO resultado = professorService.buscarProfessorPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.getEmail()).isEqualTo("maria.silva@universidade.com");

        verify(professorRepository).findById(1L);
        verify(professorMapper).toDTO(professor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando professor não encontrado por ID")
    void deveLancarExcecaoQuandoProfessorNaoEncontradoPorId() {

        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.buscarProfessorPorId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Professor não encontrado");

        verify(professorRepository).findById(1L);
        verifyNoInteractions(professorMapper);
    }

    @Test
    @DisplayName("Deve listar professores com paginação padrão")
    void deveListarProfessoresComPaginacaoPadrao() {

        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Professor> pageProfessores = new PageImpl<>(List.of(professor), pageableEsperado, 1);
        
        when(professorRepository.findAll(pageableEsperado)).thenReturn(pageProfessores);
        when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

        Page<ProfessorResponseDTO> resultado = professorService.listarProfessores(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Maria Silva");

        verify(professorRepository).findAll(pageableEsperado);
        verify(professorMapper).toDTO(professor);
    }

    @Test
    @DisplayName("Deve listar professores com ordenação customizada")
    void deveListarProfessoresComOrdenacaoCustomizada() {

        List<String> sort = List.of("email");
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("email"));
        Page<Professor> pageProfessores = new PageImpl<>(List.of(professor), pageableEsperado, 1);
        
        when(professorRepository.findAll(pageableEsperado)).thenReturn(pageProfessores);
        when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

        Page<ProfessorResponseDTO> resultado = professorService.listarProfessores(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(professorRepository).findAll(pageableEsperado);
        verify(professorMapper).toDTO(professor);
    }

    @Test
    @DisplayName("Deve listar professores com lista de ordenação vazia")
    void deveListarProfessoresComListaOrdenacaoVazia() {

        List<String> sort = Collections.emptyList();
        Pageable pageableEsperado = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Professor> pageProfessores = new PageImpl<>(List.of(professor), pageableEsperado, 1);
        
        when(professorRepository.findAll(pageableEsperado)).thenReturn(pageProfessores);
        when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

        Page<ProfessorResponseDTO> resultado = professorService.listarProfessores(0, 10, sort);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);

        verify(professorRepository).findAll(pageableEsperado);
        verify(professorMapper).toDTO(professor);
    }

    @Test
    @DisplayName("Deve deletar professor com sucesso")
    void deveDeletarProfessorComSucesso() {

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

        professorService.deletarProfessor(1L);

        verify(professorRepository).findById(1L);
        verify(usuarioRepository).deleteByProfessor(professor);
        verify(professorRepository).delete(professor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar professor não encontrado")
    void deveLancarExcecaoAoDeletarProfessorNaoEncontrado() {

        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.deletarProfessor(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Professor não encontrado");

        verify(professorRepository).findById(1L);
        verifyNoMoreInteractions(professorRepository);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve atualizar professor com sucesso")
    void deveAtualizarProfessorComSucesso() {

        ProfessorRequestDTO requestAtualizado = new ProfessorRequestDTO();
        requestAtualizado.setNome("Maria Silva Atualizada");
        requestAtualizado.setEmail("maria.nova@universidade.com");
        requestAtualizado.setSenha("novaSenha123");

        Professor professorAtualizado = new Professor();
        professorAtualizado.setId(1L);
        professorAtualizado.setNome("Maria Silva Atualizada");
        professorAtualizado.setEmail("maria.nova@universidade.com");
        professorAtualizado.setSenha("novaSenha123");

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(professorMapper.toEntity(requestAtualizado)).thenReturn(professorAtualizado);
        when(professorRepository.save(professorAtualizado)).thenReturn(professorAtualizado);
        when(usuarioRepository.findByProfessor(professor)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(professorMapper.toDTO(professorAtualizado)).thenReturn(professorResponseDTO);

        ProfessorResponseDTO resultado = professorService.atualizarProfessor(1L, requestAtualizado);

        assertThat(resultado).isNotNull();

        verify(professorRepository).findById(1L);
        verify(professorMapper).toEntity(requestAtualizado);
        verify(professorRepository).save(professorAtualizado);
        verify(usuarioRepository).findByProfessor(professor);
        verify(passwordEncoder).encode("novaSenha123");
        verify(usuarioRepository).save(usuario);
        verify(professorMapper).toDTO(professorAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar professor não encontrado")
    void deveLancarExcecaoAoAtualizarProfessorNaoEncontrado() {

        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.atualizarProfessor(1L, professorRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Professor não encontrado");

        verify(professorRepository).findById(1L);
        verifyNoMoreInteractions(professorRepository);
        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(professorMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quando usuário do professor não encontrado")
    void deveLancarExcecaoAoAtualizarQuandoUsuarioDoProfessorNaoEncontrado() {

        Professor professorAtualizado = new Professor();
        professorAtualizado.setId(1L);

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(professorMapper.toEntity(professorRequestDTO)).thenReturn(professorAtualizado);
        when(professorRepository.save(professorAtualizado)).thenReturn(professorAtualizado);
        when(usuarioRepository.findByProfessor(professor)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.atualizarProfessor(1L, professorRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuário não encontrado");

        verify(professorRepository).findById(1L);
        verify(professorMapper).toEntity(professorRequestDTO);
        verify(professorRepository).save(professorAtualizado);
        verify(usuarioRepository).findByProfessor(professor);
        verifyNoMoreInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve buscar professor logado com sucesso")
    void deveBuscarProfessorLogadoComSucesso() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioUserDetails);
            when(usuarioUserDetails.getUsername()).thenReturn("maria.silva@universidade.com");
            when(usuarioRepository.findByEmail("maria.silva@universidade.com")).thenReturn(Optional.of(usuario));
            when(professorMapper.toDTO(professor)).thenReturn(professorResponseDTO);

            ProfessorResponseDTO resultado = professorService.buscarProfessorLogado();

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo("Maria Silva");

            verify(usuarioRepository).findByEmail("maria.silva@universidade.com");
            verify(professorMapper).toDTO(professor);
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não encontrado")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoEncontrado() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioUserDetails);
            when(usuarioUserDetails.getUsername()).thenReturn("maria.silva@universidade.com");
            when(usuarioRepository.findByEmail("maria.silva@universidade.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> professorService.buscarProfessorLogado())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Usuário não encontrado");

            verify(usuarioRepository).findByEmail("maria.silva@universidade.com");
            verifyNoInteractions(professorMapper);
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não é professor")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoEhProfessor() {

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setTipo(TipoUsuario.ADMIN);
        usuarioAdmin.setProfessor(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioUserDetails);
            when(usuarioUserDetails.getUsername()).thenReturn("admin@universidade.com");
            when(usuarioRepository.findByEmail("admin@universidade.com")).thenReturn(Optional.of(usuarioAdmin));

            assertThatThrownBy(() -> professorService.buscarProfessorLogado())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Usuário não é um professor");

            verify(usuarioRepository).findByEmail("admin@universidade.com");
            verifyNoInteractions(professorMapper);
        }
    }
}
