package com.example.userservice.mapper;

import com.example.userservice.model.User;

import org.springframework.stereotype.Component;

import com.example.userservice.dto.*;

@Component
public class UserMapper {
	
	public User toEntity(UserCreateDTO dto) {
		User user = new User();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        //user.setActive(true);
		return user;
	}
	
	 //CONTINUAR
	public UserResponseDTO toDto(User user) {
		UserResponseDTO dto = new UserResponseDTO();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setOrderIds(user.getOrderIds());
		dto.setEmail(user.getEmail());
		return dto;
	}

}
