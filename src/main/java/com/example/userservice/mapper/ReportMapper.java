package com.example.userservice.mapper;

import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    
    Report toEntity(ReportCreateDTO dto);

    ReportCreateDTO toDTO(Report entity);
}