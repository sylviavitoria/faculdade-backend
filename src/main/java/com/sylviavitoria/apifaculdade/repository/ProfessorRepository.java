package com.sylviavitoria.apifaculdade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long>  {

}
