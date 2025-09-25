package com.dataservices.ssoma.flujos_trabajo_documentacion.repository;

import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByPersonaId(UUID personaId);

    List<Usuario> findByRolId(UUID rolId);

    @Query("SELECT u FROM Usuario u WHERE u.nombreUsuario LIKE %:nombre%")
    List<Usuario> findByNombreUsuarioContaining(@Param("nombre") String nombre);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByPersonaId(UUID personaId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rolId = :rolId")
    Long countByRolId(@Param("rolId") UUID rolId);
}
