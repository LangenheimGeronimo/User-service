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
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private Mapper mapper;
	
	public UserService(UserRepository repository, Mapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
	
	//POST
	public UserResponseDTO createUser(UserCreateDTO dto) {
		
		if(repository.existsByEmail(dto.getEmail())) {
			 throw new IllegalArgumentException("Email already in use");
		}
		
		User userEntity = mapper.toEntity(dto);
		User savedUser = repository.save(userEntity);	
		
		
		return mapper.toDto(savedUser);
		
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    return mapper.toDto(user);
	}
	
	public List<UserResponseDTO> getUsers (){
		return  repository.findAll().stream().map(mapper::toDto).toList(); 
	}
	
	// PUT
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {

	    User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    user.setFirstName(dto.getFirstName());
	    user.setLastName(dto.getLastName());
	    user.setEmail(dto.getEmail());
	    user.setPassword(dto.getPassword());

	    User updatedUser = repository.save(user);

	    return mapper.toDto(updatedUser);
	}
	
	//DELETE
	public void deleteUser(Long idUser) {

	    if (!repository.existsById(idUser)) {
	        throw new IllegalArgumentException("User not exists");
	    }

	    repository.deleteById(idUser);
	}

	
	


	
}
