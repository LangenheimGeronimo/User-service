package com.example.userservice.service;

import java.util.List;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.userservice.dto.*;
import com.example.userservice.model.State;
import com.example.userservice.model.User;
import com.example.userservice.mapper.*;

@Service
public class UserService {
	
	//@Autowired
	private final UserRepository repository;
	
	//@Autowired
	private final UserMapper userMapper;
	
	public UserService(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }
	
	//POST
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		
		if(repository.existsByEmail(dto.getEmail())) {
			 throw new IllegalArgumentException("Email already in use");
		}
		
		User userEntity = userMapper.toEntity(dto);
		userEntity.setState(State.ACTIVE);
		User savedUser = repository.save(userEntity);	
		
		
		return userMapper.toDto(savedUser);
		
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		
		User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    return userMapper.toDto(user);
	}
	
	public List<UserResponseDTO> getUsers (){
		return  repository.findAll().stream().map(userMapper::toDto).toList(); 
	}
	
	// PUT
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {

	    User user = repository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("User not exists"));
	    
	    if(user.getState() == State.DELETED) {
	    	throw new IllegalStateException("User already deleted");
	    }

	    if (!user.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
	    	   throw new IllegalArgumentException("Email already exists");
	    }
	  
	    
	    user.setFirstName(dto.getFirstName());
	    user.setLastName(dto.getLastName());
	    user.setEmail(dto.getEmail());
	    user.setPassword(dto.getPassword());

	    User updatedUser = repository.save(user);

	    return userMapper.toDto(updatedUser);
	}
	
	//DELETE
	public void deleteUser(Long idUser) {
	    User user = repository.findById(idUser)
	        .orElseThrow(() -> new IllegalArgumentException("User not exists"));

	    if(user.getState() == (State.DELETED)) {
	    	throw new IllegalStateException("User already deleted");
	    }

	    user.setState(State.DELETED);
    	repository.save(user);
	}


	
	/*
	 * 
	 * 
	 * */


	
}
