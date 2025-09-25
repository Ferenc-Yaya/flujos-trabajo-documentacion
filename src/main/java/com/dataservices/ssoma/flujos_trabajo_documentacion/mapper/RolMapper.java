package com.dataservices.ssoma.flujos_trabajo_documentacion.mapper;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.RolDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Rol;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolMapper {

    @Mapping(target = "cantidadUsuarios", ignore = true)
    RolDTO toDTO(Rol rol);

    @Mapping(target = "usuarios", ignore = true)
    Rol toEntity(RolDTO rolDTO);

    List<RolDTO> toDTOList(List<Rol> roles);

    List<Rol> toEntityList(List<RolDTO> rolesDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolId", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    void updateEntityFromDTO(RolDTO rolDTO, @MappingTarget Rol rol);
}
