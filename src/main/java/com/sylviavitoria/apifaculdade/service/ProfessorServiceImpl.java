package com.sylviavitoria.apifaculdade.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.enums.TipoUsuario;
import com.sylviavitoria.apifaculdade.exception.BusinessException;
import com.sylviavitoria.apifaculdade.exception.EntityNotFoundException;
import com.sylviavitoria.apifaculdade.interfaces.ProfessorService;
import com.sylviavitoria.apifaculdade.mapper.ProfessorMapper;
import com.sylviavitoria.apifaculdade.model.Professor;
import com.sylviavitoria.apifaculdade.model.Usuario;
import com.sylviavitoria.apifaculdade.repository.ProfessorRepository;
import com.sylviavitoria.apifaculdade.repository.UsuarioRepository;
import com.sylviavitoria.apifaculdade.security.UsuarioUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfessorMapper professorMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO professorRequestDTO) {
        if (usuarioRepository.existsByEmail(professorRequestDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        Professor professor = professorMapper.toEntity(professorRequestDTO);
        professor = professorRepository.save(professor);

        Usuario usuario = new Usuario();
        usuario.setEmail(professorRequestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(professorRequestDTO.getSenha()));
        usuario.setTipo(TipoUsuario.PROFESSOR);
        usuario.setProfessor(professor);
        usuarioRepository.save(usuario);

        return professorMapper.toDTO(professor);
    }

    @Override
    @Transactional
    public ProfessorResponseDTO atualizarProfessor(Long id, ProfessorRequestDTO professorRequestDTO) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        Professor professorAtualizado = professorMapper.toEntity(professorRequestDTO);
        professorAtualizado.setId(professor.getId());
        professorAtualizado = professorRepository.save(professorAtualizado);

        Usuario usuario = usuarioRepository.findByProfessor(professor)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        usuario.setEmail(professorRequestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(professorRequestDTO.getSenha()));
        usuarioRepository.save(usuario);

        return professorMapper.toDTO(professorAtualizado);
    }

    @Override
    @Transactional
    public void deletarProfessor(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
        
        usuarioRepository.deleteByProfessor(professor);
        professorRepository.delete(professor);
    }

    @Override
    public ProfessorResponseDTO buscarProfessorPorId(Long id) {
        return professorRepository.findById(id)
                .map(professorMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
    }

    @Override
    public Page<ProfessorResponseDTO> listarProfessores(int page, int size, List<String> sort) {
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(sort.toArray(new String[0])));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("nome"));
        }

        return professorRepository.findAll(pageable)
                .map(professorMapper::toDTO);
    }

    @Override
    public ProfessorResponseDTO buscarProfessorLogado() {
        UsuarioUserDetails userDetails = (UsuarioUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        if (usuario.getProfessor() == null) {
            throw new BusinessException("Usuário não é um professor");
        }
        
        return professorMapper.toDTO(usuario.getProfessor());
    }
}