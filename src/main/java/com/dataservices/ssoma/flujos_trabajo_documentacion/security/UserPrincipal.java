package com.dataservices.ssoma.flujos_trabajo_documentacion.security;

import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private UUID usuarioId;
    private UUID personaId;
    private String nombreUsuario;
    private String password;
    private String nombreRol;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(Usuario usuario) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (usuario.getRol() != null ?
                        usuario.getRol().getNombreRol() : "USER"))
        );

        return new UserPrincipal(
                usuario.getUsuarioId(),
                usuario.getPersonaId(),
                usuario.getNombreUsuario(),
                usuario.getPasswordHash(),
                usuario.getRol() != null ? usuario.getRol().getNombreRol() : "USER",
                authorities
        );
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
