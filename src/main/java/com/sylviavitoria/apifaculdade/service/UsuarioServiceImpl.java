package com.sylviavitoria.apifaculdade.service;

import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.interfaces.UsuarioService;
import com.sylviavitoria.apifaculdade.mapper.AlunoMapper;
import com.sylviavitoria.apifaculdade.mapper.UsuarioMapper;
import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.AlunoRepository;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
import com.sylviavitoria.apifaculdade.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlunoMapper alunoMapper;
    private final UsuarioMapper usuarioMapper;

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

        usuario.setSenha(alunoRequestDTO.getSenha());

        usuario.setTipo(TipoUsuario.ALUNO);
        usuario.setAluno(alunoSalvo);

        usuarioRepository.save(usuario);

        return alunoMapper.toDTO(alunoSalvo);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO criarAdmin(UsuarioRequestDTO usuarioRequestDTO) {
        log.info("Iniciando criação de admin: {}", usuarioRequestDTO.getEmail());

        if (usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new BusinessException("Já existe um usuário com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setSenha(usuarioRequestDTO.getSenha()); 
        usuario.setTipo(TipoUsuario.ADMIN);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioSalvo);
    }

}
