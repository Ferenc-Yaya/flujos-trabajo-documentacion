package com.dataservices.ssoma.flujos_trabajo_documentacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ROLES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "rol_id")
    private UUID rolId;

    @Column(name = "nombre_rol", nullable = false, length = 100)
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre del rol no puede exceder 100 caracteres")
    private String nombreRol;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Relaciones
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}