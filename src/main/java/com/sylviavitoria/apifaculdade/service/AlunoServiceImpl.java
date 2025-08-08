package com.sylviavitoria.apifaculdade.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.interfaces.AlunoService;
import com.sylviavitoria.apifaculdade.mapper.AlunoMapper;
import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.AlunoRepository;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlunoMapper alunoMapper;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Override
    @Transactional
    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoRequestDTO) {
        log.info("Iniciando criação de aluno: {}", alunoRequestDTO.getNome());

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            if (usuarioRepository.existsByEmail(alunoRequestDTO.getEmail())) {
                throw new BusinessException("Já existe um usuário com esse email");
            }
            if (alunoRepository.existsByMatricula(alunoRequestDTO.getMatricula())) {
                throw new BusinessException("Já existe um aluno com essa matrícula");
            }

            Aluno aluno = alunoMapper.toEntity(alunoRequestDTO);
            Aluno alunoSalvo = alunoRepository.save(aluno);

            Usuario usuario = new Usuario();
            usuario.setEmail(alunoSalvo.getEmail());
            usuario.setSenha(passwordEncoder.encode(alunoRequestDTO.getSenha()));
            usuario.setTipo(TipoUsuario.ALUNO);
            usuario.setAluno(alunoSalvo);

            usuarioRepository.save(usuario);

            logInfo("Aluno criado com sucesso: " + alunoSalvo.getNome() + " (ID: " + alunoSalvo.getId() + ")",
                    "criarAluno", emailUsuarioLogado, "CREATE_ALUNO");

            return alunoMapper.toDTO(alunoSalvo);
        } catch (Exception e) {
            logError("Erro ao criar aluno: " + e.getMessage(),
                    "criarAluno", emailUsuarioLogado, "CREATE_ALUNO_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoRequestDTO) {
        log.info("Atualizando aluno com ID: {}", id);

        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Aluno alunoExistente = alunoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

            if (!alunoExistente.getEmail().equals(alunoRequestDTO.getEmail()) &&
                    usuarioRepository.existsByEmail(alunoRequestDTO.getEmail())) {
                throw new BusinessException("Email já está em uso");
            }
            

            if (!alunoExistente.getMatricula().equals(alunoRequestDTO.getMatricula()) &&
                    alunoRepository.existsByMatricula(alunoRequestDTO.getMatricula())) {
                throw new BusinessException("Matrícula já está em uso");
            }

            alunoExistente.setNome(alunoRequestDTO.getNome());
            alunoExistente.setEmail(alunoRequestDTO.getEmail());
            alunoExistente.setMatricula(alunoRequestDTO.getMatricula());

            Usuario usuario = usuarioRepository.findByAluno(alunoExistente)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            usuario.setEmail(alunoRequestDTO.getEmail());
            if (alunoRequestDTO.getSenha() != null && !alunoRequestDTO.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(alunoRequestDTO.getSenha()));
            }

            alunoRepository.save(alunoExistente);
            usuarioRepository.save(usuario);

            logService.saveLog("INFO",
                    "Aluno atualizado com sucesso: " + alunoExistente.getNome() + " (ID: " + id + ")",
                    this.getClass().getSimpleName(),
                    "atualizarAluno",
                    emailUsuarioLogado,
                    "UPDATE_ALUNO");

            return alunoMapper.toDTO(alunoExistente);
        } catch (Exception e) {
            logService.saveLog("ERROR",
                    "Erro ao atualizar aluno ID " + id + ": " + e.getMessage(),
                    this.getClass().getSimpleName(),
                    "atualizarAluno",
                    emailUsuarioLogado,
                    "UPDATE_ALUNO_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public void deletarAluno(Long id) {
        log.info("Deletando aluno com ID: {}", id);

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
            Aluno aluno = alunoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

            String nomeAluno = aluno.getNome();

            usuarioRepository.deleteByAluno(aluno);
            alunoRepository.delete(aluno);

            logInfo("Aluno deletado com sucesso: " + nomeAluno + " (ID: " + id + ")",
                    "deletarAluno", emailUsuarioLogado, "DELETE_ALUNO");
        } catch (Exception e) {
            logError("Erro ao deletar aluno ID " + id + ": " + e.getMessage(),
                    "deletarAluno", emailUsuarioLogado, "DELETE_ALUNO_ERROR");
            throw e;
        }
    }

    @Override
    public AlunoResponseDTO buscarAlunoPorId(Long id) {
        log.info("Buscando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        return alunoMapper.toDTO(aluno);
    }

    @Override
    public Page<AlunoResponseDTO> listarAlunos(int page, int size, List<String> sort) {
        log.info("Listando alunos com paginação - página: {}, tamanho: {}, ordenação: {}", page, size, sort);

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("nome"));
        }

        return alunoRepository.findAll(pageable)
                .map(alunoMapper::toDTO);
    }

    @Override
    public AlunoResponseDTO buscarAlunoLogado() {
        log.info("Buscando dados do aluno logado");
        String emailUsuarioLogado = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (usuario.getTipo() != TipoUsuario.ALUNO || usuario.getAluno() == null) {
            throw new BusinessException("Usuário não é um aluno");
        }

        return alunoMapper.toDTO(usuario.getAluno());
    }

    private void logInfo(String message, String method, String user, String action) {
        logService.saveLog("INFO", message, this.getClass().getSimpleName(), method, user, action);
    }

    private void logError(String message, String method, String user, String action) {
        logService.saveLog("ERROR", message, this.getClass().getSimpleName(), method, user, action);
    }

    private String getEmailUsuarioLogado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}