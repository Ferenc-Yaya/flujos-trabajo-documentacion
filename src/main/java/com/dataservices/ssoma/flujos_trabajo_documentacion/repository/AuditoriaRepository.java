package com.dataservices.ssoma.flujos_trabajo_documentacion.repository;

import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {

    List<Auditoria> findByUsuarioIdOrderByFechaHoraDesc(UUID usuarioId);

    Page<Auditoria> findByUsuarioIdOrderByFechaHoraDesc(UUID usuarioId, Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion ORDER BY a.fechaHora DESC")
    List<Auditoria> findByAccion(@Param("accion") String accion);

    @Query("SELECT a FROM Auditoria a WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    List<Auditoria> findByFechaHoraBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT a FROM Auditoria a WHERE a.usuarioId = :usuarioId AND a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    List<Auditoria> findByUsuarioIdAndFechaHoraBetween(@Param("usuarioId") UUID usuarioId,
                                                       @Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    Page<Auditoria> findAllByOrderByFechaHoraDesc(Pageable pageable);
}
