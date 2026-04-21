package com.example.userservice.mapper;

import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.entity.Report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // Agregamos mapeos explícitos porque los nombres en el Record no tienen "get"
    @Mapping(source = "reason", target = "reason")
    @Mapping(source = "reporterUserId", target = "reporterUserId")
    @Mapping(source = "reportedUserId", target = "reportedUserId")
    Report toEntity(ReportCreateDTO dto);

    ReportCreateDTO toDTO(Report entity);
}