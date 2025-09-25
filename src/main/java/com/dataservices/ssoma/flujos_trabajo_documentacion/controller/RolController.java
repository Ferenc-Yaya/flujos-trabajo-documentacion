package com.dataservices.ssoma.flujos_trabajo_documentacion.controller;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.ApiResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.PageResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RolController {

    private final RolService rolService;

    @PostMapping
    public ResponseEntity<ApiResponse<RolDTO>> crearRol(@Valid @RequestBody RolDTO rolDTO) {
        log.info("Solicitud para crear rol: {}", rolDTO.getNombreRol());

        RolDTO rolCreado = rolService.crearRol(rolDTO);
        ApiResponse<RolDTO> response = ApiResponse.success(rolCreado, "Rol creado exitosamente");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{rolId}")
    public ResponseEntity<ApiResponse<RolDTO>> obtenerRolPorId(@PathVariable UUID rolId) {
        log.info("Solicitud para obtener rol con ID: {}", rolId);

        RolDTO rol = rolService.obtenerRolPorId(rolId);
        ApiResponse<RolDTO> response = ApiResponse.success(rol);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<ApiResponse<RolDTO>> obtenerRolPorNombre(@PathVariable String nombreRol) {
        log.info("Solicitud para obtener rol con nombre: {}", nombreRol);

        RolDTO rol = rolService.obtenerRolPorNombre(nombreRol);
        ApiResponse<RolDTO> response = ApiResponse.success(rol);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RolDTO>>> obtenerTodosLosRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreRol") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Solicitud para obtener todos los roles - Página: {}, Tamaño: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RolDTO> roles = rolService.obtenerTodosLosRoles(pageable);

        PageResponse<RolDTO> pageResponse = PageResponse.from(roles);
        ApiResponse<PageResponse<RolDTO>> response = ApiResponse.success(pageResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/lista")
    public ResponseEntity<ApiResponse<List<RolDTO>>> obtenerListaRoles() {
        log.info("Solicitud para obtener lista completa de roles");

        List<RolDTO> roles = rolService.obtenerTodosLosRoles();
        ApiResponse<List<RolDTO>> response = ApiResponse.success(roles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<RolDTO>>> buscarRolesPorNombre(@RequestParam String nombre) {
        log.info("Solicitud para buscar roles con nombre: {}", nombre);

        List<RolDTO> roles = rolService.buscarRolesPorNombre(nombre);
        ApiResponse<List<RolDTO>> response = ApiResponse.success(roles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/con-cantidad-usuarios")
    public ResponseEntity<ApiResponse<List<RolDTO>>> obtenerRolesConCantidadUsuarios() {
        log.info("Solicitud para obtener roles con cantidad de usuarios");

        List<RolDTO> roles = rolService.obtenerRolesConCantidadUsuarios();
        ApiResponse<List<RolDTO>> response = ApiResponse.success(roles);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{rolId}")
    public ResponseEntity<ApiResponse<RolDTO>> actualizarRol(
            @PathVariable UUID rolId,
            @Valid @RequestBody RolDTO rolDTO) {

        log.info("Solicitud para actualizar rol con ID: {}", rolId);

        RolDTO rolActualizado = rolService.actualizarRol(rolId, rolDTO);
        ApiResponse<RolDTO> response = ApiResponse.success(rolActualizado, "Rol actualizado exitosamente");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{rolId}")
    public ResponseEntity<ApiResponse<Void>> eliminarRol(@PathVariable UUID rolId) {
        log.info("Solicitud para eliminar rol con ID: {}", rolId);

        rolService.eliminarRol(rolId);
        ApiResponse<Void> response = ApiResponse.success(null, "Rol eliminado exitosamente");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/existe/nombre/{nombreRol}")
    public ResponseEntity<ApiResponse<Boolean>> existeRolPorNombre(@PathVariable String nombreRol) {
        boolean existe = rolService.existeRolPorNombre(nombreRol);
        ApiResponse<Boolean> response = ApiResponse.success(existe);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rolId}/puede-eliminar")
    public ResponseEntity<ApiResponse<Boolean>> puedeEliminarRol(@PathVariable UUID rolId) {
        boolean puedeEliminar = rolService.puedeEliminarRol(rolId);
        ApiResponse<Boolean> response = ApiResponse.success(puedeEliminar);

        return ResponseEntity.ok(response);
    }
}
