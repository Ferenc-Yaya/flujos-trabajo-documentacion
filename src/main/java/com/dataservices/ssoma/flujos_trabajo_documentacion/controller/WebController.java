package com.dataservices.ssoma.flujos_trabajo_documentacion.controller;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.UsuarioDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.security.UserPrincipal;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.RolService;
import com.dataservices.ssoma.flujos_trabajo_documentacion.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("message", "Ha cerrado sesión exitosamente");
        }

        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        model.addAttribute("usuario", userPrincipal.getNombreUsuario());
        model.addAttribute("rol", userPrincipal.getNombreRol());
        return "dashboard";
    }

    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarUsuarios(Model model) {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());

        List<RolDTO> roles = rolService.obtenerTodosLosRoles();
        model.addAttribute("roles", roles);

        return "admin/usuario-form";
    }

    @PostMapping("/admin/usuarios/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String crearUsuario(@Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<RolDTO> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);
            return "admin/usuario-form";
        }

        try {
            usuarioService.crearUsuario(usuarioDTO);
            redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            log.error("Error creando usuario", e);
            model.addAttribute("error", "Error al crear usuario: " + e.getMessage());

            List<RolDTO> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);

            return "admin/usuario-form";
        }
    }

    @GetMapping("/admin/usuarios/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarUsuario(@PathVariable UUID id, Model model) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
            model.addAttribute("usuario", usuario);
            model.addAttribute("editando", true);

            List<RolDTO> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);

            return "admin/usuario-form";

        } catch (Exception e) {
            log.error("Error obteniendo usuario", e);
            return "redirect:/admin/usuarios?error=" + e.getMessage();
        }
    }

    @PostMapping("/admin/usuarios/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizarUsuario(@PathVariable UUID id,
                                    @Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            List<RolDTO> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);
            return "admin/usuario-form";
        }

        try {
            usuarioService.actualizarUsuario(id, usuarioDTO);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            log.error("Error actualizando usuario", e);
            model.addAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            model.addAttribute("editando", true);

            List<RolDTO> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);

            return "admin/usuario-form";
        }
    }

    @PostMapping("/admin/usuarios/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarUsuario(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error eliminando usuario", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarRoles(Model model) {
        List<RolDTO> roles = rolService.obtenerRolesConCantidadUsuarios();
        model.addAttribute("roles", roles);
        return "admin/roles";
    }

    @GetMapping("/profile")
    public String perfil(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(userPrincipal.getUsuarioId());
            model.addAttribute("usuario", usuario);
            return "profile";
        } catch (Exception e) {
            log.error("Error obteniendo perfil", e);
            return "redirect:/dashboard?error=" + e.getMessage();
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
