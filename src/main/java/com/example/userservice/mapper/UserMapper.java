package com.example.userservice.mapper;

import com.example.userservice.model.User;
import org.springframework.stereotype.Component;
import com.example.userservice.dto.*;

@Component
public class UserMapper {
    
    public User toEntity(UserCreateDTO dto) {
        User user = new User();
        user.setFirstName(dto.firstName()); 
        user.setLastName(dto.lastName());
        user.setBirthDate(dto.birthDate());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }
    
    public UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getOrderIds()
        );
    }
}
