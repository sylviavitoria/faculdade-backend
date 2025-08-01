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

    @Override
    @Transactional
    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoRequestDTO) {
        log.info("Iniciando criação de aluno: {}", alunoRequestDTO.getNome());

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

        return alunoMapper.toDTO(alunoSalvo);
    }

    @Override
    @Transactional
    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoRequestDTO) {
        log.info("Atualizando aluno com ID: {}", id);

        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado"));

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
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        
        usuario.setEmail(alunoRequestDTO.getEmail());
        if (alunoRequestDTO.getSenha() != null && !alunoRequestDTO.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(alunoRequestDTO.getSenha()));
        }

        alunoRepository.save(alunoExistente);
        usuarioRepository.save(usuario);

        return alunoMapper.toDTO(alunoExistente);
    }

    @Override
    @Transactional
    public void deletarAluno(Long id) {
        log.info("Deletando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado"));

        usuarioRepository.deleteByAluno(aluno);
        alunoRepository.delete(aluno);
    }

    @Override
    public AlunoResponseDTO buscarAlunoPorId(Long id) {
        log.info("Buscando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado"));

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
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getTipo() != TipoUsuario.ALUNO || usuario.getAluno() == null) {
            throw new BusinessException("Usuário não é um aluno");
        }

        return alunoMapper.toDTO(usuario.getAluno());
    }
}