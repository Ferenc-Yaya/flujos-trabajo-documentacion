package com.dataservices.ssoma.flujos_trabajo_documentacion.service;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO obtenerUsuarioPorId(UUID usuarioId);

    UsuarioDTO obtenerUsuarioPorNombreUsuario(String nombreUsuario);

    UsuarioDTO obtenerUsuarioPorPersonaId(UUID personaId);

    List<UsuarioDTO> obtenerTodosLosUsuarios();

    Page<UsuarioDTO> obtenerTodosLosUsuarios(Pageable pageable);

    List<UsuarioDTO> obtenerUsuariosPorRol(UUID rolId);

    List<UsuarioDTO> buscarUsuariosPorNombre(String nombre);

    UsuarioDTO actualizarUsuario(UUID usuarioId, UsuarioDTO usuarioDTO);

    void cambiarPassword(UUID usuarioId, String passwordActual, String nuevaPassword);

    void resetearPassword(UUID usuarioId, String nuevaPassword);

    void eliminarUsuario(UUID usuarioId);

    boolean existeUsuarioPorNombreUsuario(String nombreUsuario);

    boolean existeUsuarioPorPersonaId(UUID personaId);

    boolean validarCredenciales(String nombreUsuario, String password);

    UsuarioDTO autenticarUsuario(String nombreUsuario, String password);
}
