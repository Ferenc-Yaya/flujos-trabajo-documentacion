package com.dataservices.ssoma.flujos_trabajo_documentacion.service.impl;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.UsuarioDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Usuario;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.AuthenticationException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.BusinessException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.DuplicatedResourceException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.ResourceNotFoundException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.mapper.UsuarioMapper;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.RolRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.UsuarioRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.AuditoriaService;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.UsuarioService;
import com.dataservices.ssoma.flujos_trabajo_documentacion.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordUtil passwordUtil;
    private final AuditoriaService auditoriaService;

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        log.info("Creando nuevo usuario: {}", usuarioDTO.getNombreUsuario());

        // Validaciones
        validarDatosUsuario(usuarioDTO);

        // Verificar que el rol existe
        if (!rolRepository.existsById(usuarioDTO.getRolId())) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDTO.getRolId());
        }

        // Verificar duplicados
        if (usuarioRepository.existsByNombreUsuario(usuarioDTO.getNombreUsuario())) {
            throw new DuplicatedResourceException("Ya existe un usuario con el nombre: " + usuarioDTO.getNombreUsuario());
        }

        if (usuarioRepository.existsByPersonaId(usuarioDTO.getPersonaId())) {
            throw new DuplicatedResourceException("Ya existe un usuario para la persona ID: " + usuarioDTO.getPersonaId());
        }

        // Crear usuario
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario.setPasswordHash(passwordUtil.hashPassword(usuarioDTO.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Registrar auditoría SOLO después de que el usuario esté guardado y confirmado
        try {
            if (auditoriaService != null) {
                // Usar una nueva transacción para la auditoría
                auditoriaService.registrarAuditoria(
                        usuarioGuardado.getUsuarioId(),
                        "USUARIO_CREADO",
                        "Usuario creado: " + usuarioGuardado.getNombreUsuario()
                );
            }
        } catch (Exception e) {
            // No fallar la creación del usuario si falla la auditoría
            log.warn("No se pudo registrar auditoría para usuario creado {}: {}", usuarioGuardado.getNombreUsuario(), e.getMessage());
        }

        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getUsuarioId());
        return usuarioMapper.toDTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(UUID usuarioId) {
        log.info("Buscando usuario con ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return enriquecerUsuarioDTO(usuarioMapper.toDTO(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
        log.info("Buscando usuario con nombre: {}", nombreUsuario);

        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con nombre: " + nombreUsuario));

        return enriquecerUsuarioDTO(usuarioMapper.toDTO(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorPersonaId(UUID personaId) {
        log.info("Buscando usuario para persona ID: {}", personaId);

        Usuario usuario = usuarioRepository.findByPersonaId(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para persona ID: " + personaId));

        return enriquecerUsuarioDTO(usuarioMapper.toDTO(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        log.info("Obteniendo todos los usuarios");

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> usuariosDTO = usuarioMapper.toDTOList(usuarios);

        return usuariosDTO.stream()
                .map(this::enriquecerUsuarioDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> obtenerTodosLosUsuarios(Pageable pageable) {
        log.info("Obteniendo usuarios paginados - Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        return usuarios.map(usuario -> enriquecerUsuarioDTO(usuarioMapper.toDTO(usuario)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosPorRol(UUID rolId) {
        log.info("Obteniendo usuarios por rol ID: {}", rolId);

        List<Usuario> usuarios = usuarioRepository.findByRolId(rolId);
        List<UsuarioDTO> usuariosDTO = usuarioMapper.toDTOList(usuarios);

        return usuariosDTO.stream()
                .map(this::enriquecerUsuarioDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarUsuariosPorNombre(String nombre) {
        log.info("Buscando usuarios con nombre que contenga: {}", nombre);

        List<Usuario> usuarios = usuarioRepository.findByNombreUsuarioContaining(nombre);
        List<UsuarioDTO> usuariosDTO = usuarioMapper.toDTOList(usuarios);

        return usuariosDTO.stream()
                .map(this::enriquecerUsuarioDTO)
                .toList();
    }

    @Override
    public UsuarioDTO actualizarUsuario(UUID usuarioId, UsuarioDTO usuarioDTO) {
        log.info("Actualizando usuario con ID: {}", usuarioId);

        Usuario usuarioExistente = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar cambios únicos
        if (!usuarioExistente.getNombreUsuario().equals(usuarioDTO.getNombreUsuario()) &&
                usuarioRepository.existsByNombreUsuario(usuarioDTO.getNombreUsuario())) {
            throw new DuplicatedResourceException("Ya existe un usuario con el nombre: " + usuarioDTO.getNombreUsuario());
        }

        if (!usuarioExistente.getPersonaId().equals(usuarioDTO.getPersonaId()) &&
                usuarioRepository.existsByPersonaId(usuarioDTO.getPersonaId())) {
            throw new DuplicatedResourceException("Ya existe un usuario para la persona ID: " + usuarioDTO.getPersonaId());
        }

        // Verificar que el rol existe
        if (!rolRepository.existsById(usuarioDTO.getRolId())) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDTO.getRolId());
        }

        // Actualizar
        usuarioMapper.updateEntityFromDTO(usuarioDTO, usuarioExistente);
        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);

        // Registrar auditoría
        auditoriaService.registrarAuditoria(
                usuarioId,
                "USUARIO_ACTUALIZADO",
                "Usuario actualizado: " + usuarioActualizado.getNombreUsuario()
        );

        log.info("Usuario actualizado exitosamente con ID: {}", usuarioId);
        return enriquecerUsuarioDTO(usuarioMapper.toDTO(usuarioActualizado));
    }

    @Override
    public void cambiarPassword(UUID usuarioId, String passwordActual, String nuevaPassword) {
        log.info("Cambiando contraseña para usuario ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Verificar contraseña actual
        if (!passwordUtil.verifyPassword(passwordActual, usuario.getPasswordHash())) {
            throw new AuthenticationException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (nuevaPassword.length() < 6) {
            throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordUtil.hashPassword(nuevaPassword));
        usuarioRepository.save(usuario);

        // Registrar auditoría
        auditoriaService.registrarAuditoria(usuarioId, "PASSWORD_CAMBIADA", "Contraseña cambiada por el usuario");

        log.info("Contraseña cambiada exitosamente para usuario ID: {}", usuarioId);
    }

    @Override
    public void resetearPassword(UUID usuarioId, String nuevaPassword) {
        log.info("Reseteando contraseña para usuario ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar nueva contraseña
        if (nuevaPassword.length() < 6) {
            throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordUtil.hashPassword(nuevaPassword));
        usuarioRepository.save(usuario);

        // Registrar auditoría
        auditoriaService.registrarAuditoria(usuarioId, "PASSWORD_RESETEADA", "Contraseña reseteada por administrador");

        log.info("Contraseña reseteada exitosamente para usuario ID: {}", usuarioId);
    }

    @Override
    public void eliminarUsuario(UUID usuarioId) {
        log.info("Eliminando usuario con ID: {}", usuarioId);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }

        // Registrar auditoría antes de eliminar
        auditoriaService.registrarAuditoria(usuarioId, "USUARIO_ELIMINADO", "Usuario eliminado");

        usuarioRepository.deleteById(usuarioId);
        log.info("Usuario eliminado exitosamente con ID: {}", usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuarioPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuarioPorPersonaId(UUID personaId) {
        return usuarioRepository.existsByPersonaId(personaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarCredenciales(String nombreUsuario, String password) {
        try {
            Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                    .orElse(null);

            if (usuario == null) {
                return false;
            }

            return passwordUtil.verifyPassword(password, usuario.getPasswordHash());
        } catch (Exception e) {
            log.error("Error validando credenciales para usuario: {}", nombreUsuario, e);
            return false;
        }
    }

    @Override
    public UsuarioDTO autenticarUsuario(String nombreUsuario, String password) {
        log.info("Autenticando usuario: {}", nombreUsuario);

        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));

        // Debug temporal
        log.info("Password enviada: {}", password);
        log.info("Hash en BD: {}", usuario.getPasswordHash());
        boolean isValid = passwordUtil.verifyPassword(password, usuario.getPasswordHash());
        log.info("Validación resultado: {}", isValid);

        if (!isValid) {
            throw new AuthenticationException("Credenciales inválidas");
        }

        // Registrar auditoría
        auditoriaService.registrarAuditoria(usuario.getUsuarioId(), "LOGIN_EXITOSO", "Inicio de sesión exitoso");

        log.info("Usuario autenticado exitosamente: {}", nombreUsuario);
        return enriquecerUsuarioDTO(usuarioMapper.toDTO(usuario));
    }

    // Métodos auxiliares
    private void validarDatosUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private UsuarioDTO enriquecerUsuarioDTO(UsuarioDTO usuarioDTO) {
        // Aquí se puede enriquecer con información del rol y persona
        // Por ahora dejamos la implementación básica

        // Obtener nombre del rol
        rolRepository.findById(usuarioDTO.getRolId())
                .ifPresent(rol -> usuarioDTO.setNombreRol(rol.getNombreRol()));

        // TODO: Aquí se podría llamar al servicio de personas para obtener más información
        // personaService.obtenerPersonaPorId(usuarioDTO.getPersonaId())
        //     .ifPresent(persona -> {
        //         usuarioDTO.setNombrePersona(persona.getNombreCompleto());
        //         usuarioDTO.setDniPersona(persona.getDni());
        //     });

        return usuarioDTO;
    }
}
