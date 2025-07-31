package com.sylviavitoria.apifaculdade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>  {

}
