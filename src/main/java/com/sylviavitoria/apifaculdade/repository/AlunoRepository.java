package com.sylviavitoria.apifaculdade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>  {
    boolean existsByMatricula(String matricula);
}
