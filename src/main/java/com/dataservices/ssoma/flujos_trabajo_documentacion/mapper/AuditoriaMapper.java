package com.dataservices.ssoma.flujos_trabajo_documentacion.mapper;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.AuditoriaDTO;
import com.dataservices.ssoma.flujos_trabajo_documentacion.entity.Auditoria;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditoriaMapper {

    @Mapping(target = "nombreUsuario", ignore = true)
    @Mapping(target = "nombrePersona", ignore = true)
    AuditoriaDTO toDTO(Auditoria auditoria);

    @Mapping(target = "usuario", ignore = true)
    Auditoria toEntity(AuditoriaDTO auditoriaDTO);

    List<AuditoriaDTO> toDTOList(List<Auditoria> auditorias);

    List<Auditoria> toEntityList(List<AuditoriaDTO> auditoriasDTO);
}
