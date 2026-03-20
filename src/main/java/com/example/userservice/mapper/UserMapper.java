package com.example.userservice.mapper;

import com.example.userservice.model.dto.*;
import com.example.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderIds", ignore = true)
    @Mapping(target = "state", ignore = true)
    User toEntity(UserCreateDTO userDto);
}
