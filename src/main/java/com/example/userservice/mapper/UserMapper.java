package com.example.userservice.mapper;

import com.example.userservice.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.userservice.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // 1. Este es el que necesita el Service para el return (el error de la foto)
    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderIds", ignore = true)
    @Mapping(target = "state", ignore = true)
    User toEntity(UserCreateDTO userDto);
}
