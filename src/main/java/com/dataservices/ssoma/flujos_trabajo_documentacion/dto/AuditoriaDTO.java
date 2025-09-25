package com.dataservices.ssoma.flujos_trabajo_documentacion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaDTO {

    private UUID auditoriaId;
    private UUID usuarioId;
    private String accion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHora;

    private String detallesJson;

    // Información adicional (solo lectura)
    private String nombreUsuario;
    private String nombrePersona;
}
