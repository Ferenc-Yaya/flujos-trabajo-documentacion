package com.dataservices.ssoma.flujos_trabajo_documentacion.service;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RolService {

    RolDTO crearRol(RolDTO rolDTO);

    RolDTO obtenerRolPorId(UUID rolId);

    RolDTO obtenerRolPorNombre(String nombreRol);

    List<RolDTO> obtenerTodosLosRoles();

    Page<RolDTO> obtenerTodosLosRoles(Pageable pageable);

    List<RolDTO> buscarRolesPorNombre(String nombre);

    List<RolDTO> obtenerRolesConCantidadUsuarios();

    RolDTO actualizarRol(UUID rolId, RolDTO rolDTO);

    void eliminarRol(UUID rolId);

    boolean existeRolPorNombre(String nombreRol);

    boolean puedeEliminarRol(UUID rolId);
}
