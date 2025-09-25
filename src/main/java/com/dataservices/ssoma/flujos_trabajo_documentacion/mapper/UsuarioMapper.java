package com.dataservices.ssoma.flujos_trabajo_documentacion.mapper;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.UsuarioDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "nuevaPassword", ignore = true)
    @Mapping(target = "confirmarPassword", ignore = true)
    @Mapping(target = "nombreRol", ignore = true)
    @Mapping(target = "nombrePersona", ignore = true)
    @Mapping(target = "dniPersona", ignore = true)
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "passwordHash", ignore = true) // Se maneja en el servicio
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "auditorias", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);

    List<UsuarioDTO> toDTOList(List<Usuario> usuarios);

    List<Usuario> toEntityList(List<UsuarioDTO> usuariosDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Se maneja en el servicio
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "auditorias", ignore = true)
    void updateEntityFromDTO(UsuarioDTO usuarioDTO, @MappingTarget Usuario usuario);
}
