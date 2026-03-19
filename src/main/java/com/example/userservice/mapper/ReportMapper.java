package com.example.userservice.mapper;

import com.example.userservice.dto.ReportCreateDTO;
import com.example.userservice.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportMapper {

    // MapStruct mapeará reason(), reportedUserId() y reporterUserId()
    // automáticamente a los campos de la entidad Report.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Report toEntity(ReportCreateDTO dto);

    ReportCreateDTO toDTO(Report entity);
}