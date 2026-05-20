package com.example.userservice.mapper;

import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.dto.ReportResponseDTO;
import com.example.userservice.model.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "reporterUserId", ignore = true)
    Report toEntity(ReportCreateDTO dto);

    ReportResponseDTO toResponseDto(Report entity);
}