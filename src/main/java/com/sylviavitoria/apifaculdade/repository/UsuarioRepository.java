package com.sylviavitoria.apifaculdade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Aluno;
import com.sylviavitoria.apifaculdade.model.Professor;
import com.sylviavitoria.apifaculdade.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>  {
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByAluno(Aluno aluno); 
    void deleteByAluno(Aluno aluno);
    Optional<Usuario> findByProfessor(Professor professor);
    void deleteByProfessor(Professor professor);
}
