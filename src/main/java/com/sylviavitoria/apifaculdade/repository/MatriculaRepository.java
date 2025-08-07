package com.sylviavitoria.apifaculdade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Matricula;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    boolean existsByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);
}