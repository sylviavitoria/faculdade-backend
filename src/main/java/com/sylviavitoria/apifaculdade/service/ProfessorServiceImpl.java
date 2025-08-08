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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfessorMapper professorMapper;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Override
    @Transactional
    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO professorRequestDTO) {
        log.info("Iniciando criação de professor: {}", professorRequestDTO.getNome());

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
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

            logInfo("Professor criado com sucesso: " + professorRequestDTO.getNome()  + " (ID: " + professor.getId() + ")", "criarProfessor", emailUsuarioLogado, "CREATE_PROFESSOR");

            return professorMapper.toDTO(professor);
        } catch (Exception e) {
            logError("Erro ao criar professor: " + e.getMessage(), "criarProfessor", emailUsuarioLogado, "CREATE_PROFESSOR_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public ProfessorResponseDTO atualizarProfessor(Long id, ProfessorRequestDTO professorRequestDTO) {
        log.info("Atualizando professor com ID: {}", id);

        String emailUsuarioLogado = getEmailUsuarioLogado();

        try {
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

            logInfo("Professor atualizado com sucesso: " + professorAtualizado.getNome() + " (ID: " + id + ")",
                    "atualizarProfessor", emailUsuarioLogado, "UPDATE_PROFESSOR");

            return professorMapper.toDTO(professorAtualizado);
        } catch (Exception e) {
            logError("Erro ao atualizar professor ID " + id + ": " + e.getMessage(),
                    "atualizarProfessor", emailUsuarioLogado, "UPDATE_PROFESSOR_ERROR");
            throw e;
        }
    }

    @Override
    @Transactional
    public void deletarProfessor(Long id) {
        String emailUsuarioLogado = getEmailUsuarioLogado();
        
        try {
            Professor professor = professorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
            
            log.info("Iniciando exclusão do professor: {}", professor.getNome());
            
            usuarioRepository.deleteByProfessor(professor);
            professorRepository.delete(professor);
            
            logInfo("Professor deletado com sucesso: " + professor.getNome(), "deletarProfessor", emailUsuarioLogado, "DELETE_PROFESSOR");
            log.info("Professor deletado com sucesso: {}", professor.getNome());
        } catch (Exception e) {
            logError("Erro ao deletar professor: " + e.getMessage(), "deletarProfessor", emailUsuarioLogado, "DELETE_PROFESSOR");
            log.error("Erro ao deletar professor: {}", e.getMessage(), e);
            throw e;
        }
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