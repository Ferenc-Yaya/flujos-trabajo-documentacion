package com.dataservices.ssoma.flujos_trabajo_documentacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolDTO {

    private UUID rolId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre del rol no puede exceder 100 caracteres")
    private String nombreRol;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    // Información adicional (solo lectura)
    private Integer cantidadUsuarios;
}
