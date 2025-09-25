package com.dataservices.ssoma.flujos_trabajo_documentacion.service.impl;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Rol;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.BusinessException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.DuplicatedResourceException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.ResourceNotFoundException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.mapper.RolMapper;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.RolRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.UsuarioRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.RolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolMapper rolMapper;

    @Override
    public RolDTO crearRol(RolDTO rolDTO) {
        log.info("Creando nuevo rol: {}", rolDTO.getNombreRol());

        // Validar que no exista el nombre del rol
        if (rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicatedResourceException("Ya existe un rol con el nombre: " + rolDTO.getNombreRol());
        }

        Rol rol = rolMapper.toEntity(rolDTO);
        Rol rolGuardado = rolRepository.save(rol);

        log.info("Rol creado exitosamente con ID: {}", rolGuardado.getRolId());
        return rolMapper.toDTO(rolGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerRolPorId(UUID rolId) {
        log.info("Buscando rol con ID: {}", rolId);

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        return rolMapper.toDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerRolPorNombre(String nombreRol) {
        log.info("Buscando rol con nombre: {}", nombreRol);

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + nombreRol));

        return rolMapper.toDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> obtenerTodosLosRoles() {
        log.info("Obteniendo todos los roles");

        List<Rol> roles = rolRepository.findAll();
        return rolMapper.toDTOList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolDTO> obtenerTodosLosRoles(Pageable pageable) {
        log.info("Obteniendo roles paginados - Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Rol> roles = rolRepository.findAll(pageable);
        return roles.map(rolMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> buscarRolesPorNombre(String nombre) {
        log.info("Buscando roles con nombre que contenga: {}", nombre);

        List<Rol> roles = rolRepository.findByNombreRolContaining(nombre);
        return rolMapper.toDTOList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> obtenerRolesConCantidadUsuarios() {
        log.info("Obteniendo roles con cantidad de usuarios");

        List<Object[]> resultados = rolRepository.findRolesConCantidadUsuarios();
        List<RolDTO> rolesDTO = new ArrayList<>();

        for (Object[] resultado : resultados) {
            Rol rol = (Rol) resultado[0];
            Long cantidadUsuarios = (Long) resultado[1];

            RolDTO rolDTO = rolMapper.toDTO(rol);
            rolDTO.setCantidadUsuarios(cantidadUsuarios.intValue());
            rolesDTO.add(rolDTO);
        }

        return rolesDTO;
    }

    @Override
    public RolDTO actualizarRol(UUID rolId, RolDTO rolDTO) {
        log.info("Actualizando rol con ID: {}", rolId);

        Rol rolExistente = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        // Validar nombre único si cambió
        if (!rolExistente.getNombreRol().equals(rolDTO.getNombreRol()) &&
                rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicatedResourceException("Ya existe un rol con el nombre: " + rolDTO.getNombreRol());
        }

        rolMapper.updateEntityFromDTO(rolDTO, rolExistente);
        Rol rolActualizado = rolRepository.save(rolExistente);

        log.info("Rol actualizado exitosamente con ID: {}", rolId);
        return rolMapper.toDTO(rolActualizado);
    }

    @Override
    public void eliminarRol(UUID rolId) {
        log.info("Eliminando rol con ID: {}", rolId);

        if (!rolRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }

        // Verificar si se puede eliminar
        if (!puedeEliminarRol(rolId)) {
            throw new BusinessException("No se puede eliminar el rol porque tiene usuarios asociados", "ROLE_HAS_USERS");
        }

        rolRepository.deleteById(rolId);
        log.info("Rol eliminado exitosamente con ID: {}", rolId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeRolPorNombre(String nombreRol) {
        return rolRepository.existsByNombreRol(nombreRol);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeEliminarRol(UUID rolId) {
        Long cantidadUsuarios = usuarioRepository.countByRolId(rolId);
        return cantidadUsuarios == 0;
    }
}
