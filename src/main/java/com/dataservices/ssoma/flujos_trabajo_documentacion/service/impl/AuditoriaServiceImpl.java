package com.dataservices.ssoma.flujos_trabajo_documentacion.service.impl;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.AuditoriaDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Auditoria;
import com.dataservices.ssoma.flujos_trabajo_documentacion.exception.ResourceNotFoundException;
import com.dataservices.ssoma.flujos_trabajo_documentacion.mapper.AuditoriaMapper;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.AuditoriaRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.repository.UsuarioRepository;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.AuditoriaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaMapper auditoriaMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarAuditoria(UUID usuarioId, String accion, String detallesJson) {
        try {
            log.debug("Registrando auditoría - Usuario: {}, Acción: {}", usuarioId, accion);

            // Verificar que el usuario existe antes de crear la auditoría
            if (!usuarioRepository.existsById(usuarioId)) {
                log.warn("Intento de crear auditoría para usuario inexistente: {}", usuarioId);
                return; // Salir silenciosamente si el usuario no existe
            }

            // Asegurar que tenemos JSON válido
            String jsonValido = prepararJsonValido(detallesJson);

            Auditoria auditoria = Auditoria.builder()
                    .usuarioId(usuarioId)
                    .accion(accion)
                    .fechaHora(LocalDateTime.now())
                    .detallesJson(jsonValido)
                    .build();

            auditoriaRepository.save(auditoria);

        } catch (Exception e) {
            // No fallar la operación principal si falla la auditoría
            log.error("Error registrando auditoría - Usuario: {}, Acción: {}, Error: {}", usuarioId, accion, e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarAuditoria(UUID usuarioId, String accion, Object detalles) {
        try {
            String detallesJson = convertirAJson(detalles);
            registrarAuditoria(usuarioId, accion, detallesJson);

        } catch (Exception e) {
            log.error("Error general en auditoría", e);
            // Como último recurso, crear un JSON simple con el error
            try {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Error en auditoría: " + e.getMessage());
                String errorJson = objectMapper.writeValueAsString(errorMap);
                registrarAuditoria(usuarioId, accion, errorJson);
            } catch (Exception finalE) {
                log.error("Error crítico en auditoría, no se puede registrar: {}", finalE.getMessage());
            }
        }
    }

    private String prepararJsonValido(String input) {
        if (input == null) {
            return "null";
        }

        // Si ya es JSON válido, devolverlo tal como está
        if (esJsonValido(input)) {
            return input;
        }

        // Si no es JSON, crear un objeto JSON simple
        try {
            Map<String, String> wrapper = new HashMap<>();
            wrapper.put("mensaje", input);
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            log.error("Error creando JSON wrapper", e);
            // Como último recurso, crear JSON manualmente
            return "{\"mensaje\": \"" + input.replace("\"", "\\\"") + "\"}";
        }
    }

    private String convertirAJson(Object objeto) throws JsonProcessingException {
        if (objeto == null) {
            return "null";
        }

        if (objeto instanceof String) {
            String str = (String) objeto;
            // Si ya es JSON válido, devolverlo
            if (esJsonValido(str)) {
                return str;
            }
            // Si no, crear un wrapper
            Map<String, String> wrapper = new HashMap<>();
            wrapper.put("mensaje", str);
            return objectMapper.writeValueAsString(wrapper);
        }

        // Para otros objetos, convertir directamente a JSON
        return objectMapper.writeValueAsString(objeto);
    }

    private boolean esJsonValido(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Resto de métodos del servicio...
    @Override
    @Transactional(readOnly = true)
    public AuditoriaDTO obtenerAuditoriaPorId(UUID auditoriaId) {
        log.info("Buscando auditoría con ID: {}", auditoriaId);

        Auditoria auditoria = auditoriaRepository.findById(auditoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoría no encontrada con ID: " + auditoriaId));

        return enriquecerAuditoriaDTO(auditoriaMapper.toDTO(auditoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> obtenerAuditoriasPorUsuario(UUID usuarioId) {
        log.info("Obteniendo auditorías para usuario ID: {}", usuarioId);

        List<Auditoria> auditorias = auditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
        List<AuditoriaDTO> auditoriasDTO = auditoriaMapper.toDTOList(auditorias);

        return auditoriasDTO.stream()
                .map(this::enriquecerAuditoriaDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> obtenerAuditoriasPorUsuario(UUID usuarioId, Pageable pageable) {
        log.info("Obteniendo auditorías paginadas para usuario ID: {} - Página: {}, Tamaño: {}",
                usuarioId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Auditoria> auditorias = auditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId, pageable);
        return auditorias.map(auditoria -> enriquecerAuditoriaDTO(auditoriaMapper.toDTO(auditoria)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> obtenerAuditoriasPorAccion(String accion) {
        log.info("Obteniendo auditorías por acción: {}", accion);

        List<Auditoria> auditorias = auditoriaRepository.findByAccion(accion);
        List<AuditoriaDTO> auditoriasDTO = auditoriaMapper.toDTOList(auditorias);

        return auditoriasDTO.stream()
                .map(this::enriquecerAuditoriaDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> obtenerAuditoriasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo auditorías entre {} y {}", fechaInicio, fechaFin);

        List<Auditoria> auditorias = auditoriaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
        List<AuditoriaDTO> auditoriasDTO = auditoriaMapper.toDTOList(auditorias);

        return auditoriasDTO.stream()
                .map(this::enriquecerAuditoriaDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> obtenerAuditoriasPorUsuarioYFecha(UUID usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo auditorías para usuario {} entre {} y {}", usuarioId, fechaInicio, fechaFin);

        List<Auditoria> auditorias = auditoriaRepository.findByUsuarioIdAndFechaHoraBetween(usuarioId, fechaInicio, fechaFin);
        List<AuditoriaDTO> auditoriasDTO = auditoriaMapper.toDTOList(auditorias);

        return auditoriasDTO.stream()
                .map(this::enriquecerAuditoriaDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> obtenerTodasLasAuditorias(Pageable pageable) {
        log.info("Obteniendo todas las auditorías paginadas - Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Auditoria> auditorias = auditoriaRepository.findAllByOrderByFechaHoraDesc(pageable);
        return auditorias.map(auditoria -> enriquecerAuditoriaDTO(auditoriaMapper.toDTO(auditoria)));
    }

    @Override
    @Transactional
    public void eliminarAuditoriasAnteriores(LocalDateTime fecha) {
        log.info("Eliminando auditorías anteriores a: {}", fecha);

        List<Auditoria> auditoriasAnteriores = auditoriaRepository.findByFechaHoraBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0), fecha);

        auditoriaRepository.deleteAll(auditoriasAnteriores);

        log.info("Eliminadas {} auditorías anteriores a {}", auditoriasAnteriores.size(), fecha);
    }

    // Método auxiliar para enriquecer DTOs
    private AuditoriaDTO enriquecerAuditoriaDTO(AuditoriaDTO auditoriaDTO) {
        // TODO: Aquí se puede enriquecer con información del usuario y persona
        return auditoriaDTO;
    }
}
