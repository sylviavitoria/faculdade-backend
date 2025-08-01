package com.sylviavitoria.apifaculdade.interfaces;

import com.sylviavitoria.apifaculdade.dto.UsuarioRequestDTO;
import com.sylviavitoria.apifaculdade.dto.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO criarAdmin(UsuarioRequestDTO usuarioRequestDTO);
}