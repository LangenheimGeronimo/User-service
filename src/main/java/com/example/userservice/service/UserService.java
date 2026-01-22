package com.example.userservice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.userservice.dto.*;
import com.example.userservice.model.User;
import com.example.userservice.mapper.*;

@Service
public class UserService {
	
	//@Autowired
	private UserRepository repository;
	
	@Autowired
	private Mapper UserMapper;
	
	public UserService(UserRepository repository, Mapper UserMapper) {
        this.repository = repository;
        this.UserMapper = UserMapper;
    }
	
	//POST
	public UserResponseDTO createUser(UserCreateDTO dto) {
		
		if(repository.existsByEmail(dto.getEmail())) {
			 throw new IllegalArgumentException("Email already in use");
		}
		
		User userEntity = UserMapper.toEntity(dto);
		User savedUser = repository.save(userEntity);	
		
		
		return UserMapper.toDto(savedUser);
		
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    return UserMapper.toDto(user);
	}
	
	public List<UserResponseDTO> getUsers (){
		return  repository.findAll().stream().map(UserMapper::toDto).toList(); 
	}
	
	// PUT
	//Falta verificacion
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {

	    User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    user.setFirstName(dto.getFirstName());
	    user.setLastName(dto.getLastName());
	    user.setEmail(dto.getEmail());
	    user.setPassword(dto.getPassword());

	    User updatedUser = repository.save(user);

	    return UserMapper.toDto(updatedUser);
	}
	
	//DELETE
	public void deleteUser(Long idUser) {
	    User user = repository.findById(idUser)
	        .orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    repository.delete(user);
	}


	
	


	
}
