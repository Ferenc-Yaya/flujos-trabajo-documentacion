package com.dataservices.ssoma.flujos_trabajo_documentacion.config;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.RolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolService rolService;

    @Override
    public void run(String... args) throws Exception {
        inicializarRoles();
        mostrarInstruccionesUsuarioAdmin();
    }

    private void inicializarRoles() {
        try {
            crearRolSiNoExiste("ADMIN", "Administrador del sistema con acceso completo");
            crearRolSiNoExiste("SUPERVISOR", "Supervisor con permisos de gestión y consulta");
            crearRolSiNoExiste("OPERADOR", "Operador con permisos básicos de consulta y registro");
            crearRolSiNoExiste("CONSULTA", "Usuario con permisos solo de consulta");
            log.info("Inicialización de roles completada");
        } catch (Exception e) {
            log.error("Error inicializando roles: ", e);
        }
    }

    private void mostrarInstruccionesUsuarioAdmin() {
        log.info("=====================================");
        log.info("CONFIGURACIÓN INICIAL REQUERIDA:");
        log.info("=====================================");
        log.info("Para crear el usuario administrador:");
        log.info("1. Ejecuta primero el módulo de gestión de personas");
        log.info("2. Crea una empresa y una persona en ese módulo");
        log.info("3. Usa la API REST para crear el usuario admin:");
        log.info("");
        log.info("POST /api/v1/usuarios");
        log.info("{{");
        log.info("  \"personaId\": \"[UUID-de-persona-creada]\",");
        log.info("  \"nombreUsuario\": \"admin\",");
        log.info("  \"password\": \"admin123\",");
        log.info("  \"rolId\": \"[UUID-del-rol-ADMIN]\"");
        log.info("}}");
        log.info("");
        log.info("O visita: http://localhost:8081/swagger-ui.html");
        log.info("=====================================");
    }

    private void crearRolSiNoExiste(String nombreRol, String descripcion) {
        try {
            if (!rolService.existeRolPorNombre(nombreRol)) {
                RolDTO rolDTO = RolDTO.builder()
                        .nombreRol(nombreRol)
                        .descripcion(descripcion)
                        .build();

                rolService.crearRol(rolDTO);
                log.info("Rol creado: {}", nombreRol);
            } else {
                log.debug("Rol ya existe: {}", nombreRol);
            }
        } catch (Exception e) {
            log.error("Error creando rol {}: ", nombreRol, e);
        }
    }
}
