package com.dataservices.ssoma.flujos_trabajo_documentacion.controller;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.UsuarioDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.ApiResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.PageResponse;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.UsuarioService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("Solicitud para crear usuario: {}", usuarioDTO.getNombreUsuario());

        UsuarioDTO usuarioCreado = usuarioService.crearUsuario(usuarioDTO);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuarioCreado, "Usuario creado exitosamente");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerUsuarioPorId(@PathVariable UUID usuarioId) {
        log.info("Solicitud para obtener usuario con ID: {}", usuarioId);

        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nombre/{nombreUsuario}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerUsuarioPorNombreUsuario(@PathVariable String nombreUsuario) {
        log.info("Solicitud para obtener usuario con nombre: {}", nombreUsuario);

        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorNombreUsuario(nombreUsuario);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerUsuarioPorPersonaId(@PathVariable UUID personaId) {
        log.info("Solicitud para obtener usuario para persona ID: {}", personaId);

        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorPersonaId(personaId);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UsuarioDTO>>> obtenerTodosLosUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreUsuario") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Solicitud para obtener todos los usuarios - Página: {}, Tamaño: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios(pageable);

        PageResponse<UsuarioDTO> pageResponse = PageResponse.from(usuarios);
        ApiResponse<PageResponse<UsuarioDTO>> response = ApiResponse.success(pageResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/lista")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> obtenerListaUsuarios() {
        log.info("Solicitud para obtener lista completa de usuarios");

        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        ApiResponse<List<UsuarioDTO>> response = ApiResponse.success(usuarios);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> obtenerUsuariosPorRol(@PathVariable UUID rolId) {
        log.info("Solicitud para obtener usuarios por rol ID: {}", rolId);

        List<UsuarioDTO> usuarios = usuarioService.obtenerUsuariosPorRol(rolId);
        ApiResponse<List<UsuarioDTO>> response = ApiResponse.success(usuarios);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        log.info("Solicitud para buscar usuarios con nombre: {}", nombre);

        List<UsuarioDTO> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
        ApiResponse<List<UsuarioDTO>> response = ApiResponse.success(usuarios);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarUsuario(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        log.info("Solicitud para actualizar usuario con ID: {}", usuarioId);

        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(usuarioId, usuarioDTO);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuarioActualizado, "Usuario actualizado exitosamente");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{usuarioId}/cambiar-password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @PathVariable UUID usuarioId,
            @RequestBody Map<String, String> passwords) {

        log.info("Solicitud para cambiar contraseña de usuario ID: {}", usuarioId);

        String passwordActual = passwords.get("passwordActual");
        String nuevaPassword = passwords.get("nuevaPassword");

        usuarioService.cambiarPassword(usuarioId, passwordActual, nuevaPassword);
        ApiResponse<Void> response = ApiResponse.success(null, "Contraseña cambiada exitosamente");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{usuarioId}/resetear-password")
    public ResponseEntity<ApiResponse<Void>> resetearPassword(
            @PathVariable UUID usuarioId,
            @RequestBody Map<String, String> request) {

        log.info("Solicitud para resetear contraseña de usuario ID: {}", usuarioId);

        String nuevaPassword = request.get("nuevaPassword");

        usuarioService.resetearPassword(usuarioId, nuevaPassword);
        ApiResponse<Void> response = ApiResponse.success(null, "Contraseña reseteada exitosamente");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<ApiResponse<UsuarioDTO>> autenticarUsuario(
            @RequestBody Map<String, String> credenciales) {

        String nombreUsuario = credenciales.get("nombreUsuario");
        String password = credenciales.get("password");

        log.info("Solicitud de autenticación para usuario: {}", nombreUsuario);

        UsuarioDTO usuario = usuarioService.autenticarUsuario(nombreUsuario, password);
        ApiResponse<UsuarioDTO> response = ApiResponse.success(usuario, "Autenticación exitosa");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validar-credenciales")
    public ResponseEntity<ApiResponse<Boolean>> validarCredenciales(
            @RequestBody Map<String, String> credenciales) {

        String nombreUsuario = credenciales.get("nombreUsuario");
        String password = credenciales.get("password");

        boolean validas = usuarioService.validarCredenciales(nombreUsuario, password);
        ApiResponse<Boolean> response = ApiResponse.success(validas);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable UUID usuarioId) {
        log.info("Solicitud para eliminar usuario con ID: {}", usuarioId);

        usuarioService.eliminarUsuario(usuarioId);
        ApiResponse<Void> response = ApiResponse.success(null, "Usuario eliminado exitosamente");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/existe/nombre/{nombreUsuario}")
    public ResponseEntity<ApiResponse<Boolean>> existeUsuarioPorNombreUsuario(@PathVariable String nombreUsuario) {
        boolean existe = usuarioService.existeUsuarioPorNombreUsuario(nombreUsuario);
        ApiResponse<Boolean> response = ApiResponse.success(existe);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/existe/persona/{personaId}")
    public ResponseEntity<ApiResponse<Boolean>> existeUsuarioPorPersonaId(@PathVariable UUID personaId) {
        boolean existe = usuarioService.existeUsuarioPorPersonaId(personaId);
        ApiResponse<Boolean> response = ApiResponse.success(existe);

        return ResponseEntity.ok(response);
    }
}
