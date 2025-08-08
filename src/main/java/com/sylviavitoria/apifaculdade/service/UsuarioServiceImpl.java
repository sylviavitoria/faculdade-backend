package com.sylviavitoria.apifaculdade.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.interfaces.UsuarioService;
import com.sylviavitoria.apifaculdade.mapper.UsuarioMapper;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
import com.sylviavitoria.apifaculdade.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO criarAdmin(UsuarioRequestDTO usuarioRequestDTO) {
        log.info("Iniciando criação de admin: {}", usuarioRequestDTO.getEmail());

        if (usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new BusinessException("Já existe um usuário com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioRequestDTO.getEmail());  
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha())); 
        usuario.setTipo(TipoUsuario.ADMIN);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioSalvo);
    }

}
