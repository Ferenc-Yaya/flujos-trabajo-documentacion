package com.dataservices.ssoma.flujos_trabajo_documentacion.controller;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.AuditoriaDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.ApiResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.PageResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auditorias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping("/{auditoriaId}")
    public ResponseEntity<ApiResponse<AuditoriaDTO>> obtenerAuditoriaPorId(@PathVariable UUID auditoriaId) {
        log.info("Solicitud para obtener auditoría con ID: {}", auditoriaId);

        AuditoriaDTO auditoria = auditoriaService.obtenerAuditoriaPorId(auditoriaId);
        ApiResponse<AuditoriaDTO> response = ApiResponse.success(auditoria);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditoriaDTO>>> obtenerTodasLasAuditorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaHora") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Solicitud para obtener todas las auditorías - Página: {}, Tamaño: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuditoriaDTO> auditorias = auditoriaService.obtenerTodasLasAuditorias(pageable);

        PageResponse<AuditoriaDTO> pageResponse = PageResponse.from(auditorias);
        ApiResponse<PageResponse<AuditoriaDTO>> response = ApiResponse.success(pageResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<AuditoriaDTO>>> obtenerAuditoriasPorUsuario(@PathVariable UUID usuarioId) {
        log.info("Solicitud para obtener auditorías de usuario ID: {}", usuarioId);

        List<AuditoriaDTO> auditorias = auditoriaService.obtenerAuditoriasPorUsuario(usuarioId);
        ApiResponse<List<AuditoriaDTO>> response = ApiResponse.success(auditorias);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/paginado")
    public ResponseEntity<ApiResponse<PageResponse<AuditoriaDTO>>> obtenerAuditoriasPorUsuarioPaginado(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Solicitud para obtener auditorías paginadas de usuario ID: {} - Página: {}, Tamaño: {}",
                usuarioId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());
        Page<AuditoriaDTO> auditorias = auditoriaService.obtenerAuditoriasPorUsuario(usuarioId, pageable);

        PageResponse<AuditoriaDTO> pageResponse = PageResponse.from(auditorias);
        ApiResponse<PageResponse<AuditoriaDTO>> response = ApiResponse.success(pageResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<ApiResponse<List<AuditoriaDTO>>> obtenerAuditoriasPorAccion(@PathVariable String accion) {
        log.info("Solicitud para obtener auditorías por acción: {}", accion);

        List<AuditoriaDTO> auditorias = auditoriaService.obtenerAuditoriasPorAccion(accion);
        ApiResponse<List<AuditoriaDTO>> response = ApiResponse.success(auditorias);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/fecha")
    public ResponseEntity<ApiResponse<List<AuditoriaDTO>>> obtenerAuditoriasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        log.info("Solicitud para obtener auditorías entre {} y {}", fechaInicio, fechaFin);

        List<AuditoriaDTO> auditorias = auditoriaService.obtenerAuditoriasPorFecha(fechaInicio, fechaFin);
        ApiResponse<List<AuditoriaDTO>> response = ApiResponse.success(auditorias);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/fecha")
    public ResponseEntity<ApiResponse<List<AuditoriaDTO>>> obtenerAuditoriasPorUsuarioYFecha(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        log.info("Solicitud para obtener auditorías de usuario {} entre {} y {}", usuarioId, fechaInicio, fechaFin);

        List<AuditoriaDTO> auditorias = auditoriaService.obtenerAuditoriasPorUsuarioYFecha(usuarioId, fechaInicio, fechaFin);
        ApiResponse<List<AuditoriaDTO>> response = ApiResponse.success(auditorias);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/anteriores/{fecha}")
    public ResponseEntity<ApiResponse<Void>> eliminarAuditoriasAnteriores(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {

        log.info("Solicitud para eliminar auditorías anteriores a: {}", fecha);

        auditoriaService.eliminarAuditoriasAnteriores(fecha);
        ApiResponse<Void> response = ApiResponse.success(null, "Auditorías eliminadas exitosamente");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<Void>> registrarAuditoriaManual(
            @RequestParam UUID usuarioId,
            @RequestParam String accion,
            @RequestParam(required = false) String detalles) {

        log.info("Solicitud para registrar auditoría manual - Usuario: {}, Acción: {}", usuarioId, accion);

        auditoriaService.registrarAuditoria(usuarioId, accion, detalles);
        ApiResponse<Void> response = ApiResponse.success(null, "Auditoría registrada exitosamente");

        return ResponseEntity.ok(response);
    }
}
