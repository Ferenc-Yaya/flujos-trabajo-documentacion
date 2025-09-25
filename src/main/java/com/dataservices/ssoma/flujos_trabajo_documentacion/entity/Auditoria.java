package com.dataservices.ssoma.flujos_trabajo_documentacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AUDITORIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "usuario")
@ToString(exclude = "usuario")
public class Auditoria {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "auditoria_id")
    private UUID auditoriaId;

    @Column(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private UUID usuarioId;

    @Column(name = "accion", nullable = false)
    @NotBlank(message = "La acción es obligatoria")
    private String accion;

    @Column(name = "fecha_hora", nullable = false)
    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    // Configuración correcta para JSONB en PostgreSQL
    @Column(name = "detalles_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String detallesJson;

    // Relación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}
