package com.dataservices.ssoma.flujos_trabajo_documentacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "USUARIOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"rol", "auditorias"})
@ToString(exclude = {"passwordHash", "rol", "auditorias"})
public class Usuario {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "persona_id", nullable = false)
    @NotNull(message = "La persona es obligatoria")
    private UUID personaId;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 100, message = "El nombre de usuario no puede exceder 100 caracteres")
    private String nombreUsuario;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String passwordHash;

    @Column(name = "rol_id", nullable = false)
    @NotNull(message = "El rol es obligatorio")
    private UUID rolId;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", insertable = false, updatable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Auditoria> auditorias;
}
