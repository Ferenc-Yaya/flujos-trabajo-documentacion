package com.dataservices.ssoma.flujos_trabajo_documentacion.service;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.AuditoriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditoriaService {

    void registrarAuditoria(UUID usuarioId, String accion, String detallesJson);

    void registrarAuditoria(UUID usuarioId, String accion, Object detalles);

    AuditoriaDTO obtenerAuditoriaPorId(UUID auditoriaId);

    List<AuditoriaDTO> obtenerAuditoriasPorUsuario(UUID usuarioId);

    Page<AuditoriaDTO> obtenerAuditoriasPorUsuario(UUID usuarioId, Pageable pageable);

    List<AuditoriaDTO> obtenerAuditoriasPorAccion(String accion);

    List<AuditoriaDTO> obtenerAuditoriasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<AuditoriaDTO> obtenerAuditoriasPorUsuarioYFecha(UUID usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<AuditoriaDTO> obtenerTodasLasAuditorias(Pageable pageable);

    void eliminarAuditoriasAnteriores(LocalDateTime fecha);
}
