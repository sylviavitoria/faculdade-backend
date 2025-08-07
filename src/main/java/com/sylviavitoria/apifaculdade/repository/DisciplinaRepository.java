package com.sylviavitoria.apifaculdade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Disciplina;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    boolean existsByCodigo(String codigo);
    List<Disciplina> findByProfessorId(Long professorId);
}

