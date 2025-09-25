package com.dataservices.ssoma.flujos_trabajo_documentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private UUID usuarioId;

    @NotNull(message = "La persona es obligatoria")
    private UUID personaId;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String nombreUsuario;

    // Solo para creación/actualización - nunca se devuelve en respuestas
    @JsonIgnore
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private UUID rolId;

    // Información adicional (solo lectura)
    private String nombreRol;
    private String nombrePersona;
    private String dniPersona;

    // Para cambio de contraseña
    @JsonIgnore
    private String nuevaPassword;

    @JsonIgnore
    private String confirmarPassword;
}
