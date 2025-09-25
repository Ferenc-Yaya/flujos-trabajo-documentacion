package com.dataservices.ssoma.flujos_trabajo_documentacion.repository;

import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {

    Optional<Rol> findByNombreRol(String nombreRol);

    @Query("SELECT r FROM Rol r WHERE r.nombreRol LIKE %:nombre%")
    List<Rol> findByNombreRolContaining(@Param("nombre") String nombre);

    boolean existsByNombreRol(String nombreRol);

    @Query("SELECT r, COUNT(u) as cantidadUsuarios FROM Rol r LEFT JOIN r.usuarios u GROUP BY r")
    List<Object[]> findRolesConCantidadUsuarios();
}
