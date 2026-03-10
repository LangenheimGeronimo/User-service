package com.example.userservice.service;

import java.util.List;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.userservice.dto.*;
import com.example.userservice.exeptions.EmailAlreadyExistsException;
import com.example.userservice.exeptions.UserIsAlreadyDeletedException;
import com.example.userservice.exeptions.UserNotFoundException;
import com.example.userservice.model.State;
import com.example.userservice.model.User;
import com.example.userservice.mapper.*;

@Service
public class UserService {
	
	private final UserRepository repository;
	
	private final UserMapper userMapper;
	
	public UserService(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }
	
	//POST
	public UserResponseDTO createUser(userCreateDTO dto) { 
		if(repository.existsByEmail(dto.getEmail())) {
			throw new EmailAlreadyExistsException("Email already in use");
		}
		User userEntity = userMapper.toEntity(dto);
		userEntity.setState(State.ACTIVE);
		User savedUser = repository.save(userEntity);	
		return userMapper.toDto(savedUser);
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    return userMapper.toDto(user);
	}
	
	public List<UserResponseDTO> getUsers (){
		return  repository.findAll().stream().map(userMapper::toDto).toList(); 
	}
	
	// PUT
	public UserResponseDTO editUser(Long idUser, userCreateDTO dto) {

	    User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    
	    if(user.getState() == State.DELETED) {
	    	throw new UserIsAlreadyDeletedException("User already deleted"); 
	    }

	    if (!user.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
	    	   throw new EmailAlreadyExistsException("Email already in use");
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
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));

	    if(user.getState() == (State.DELETED)) {
	    	throw new IllegalStateException("User already deleted");
	    }

	    user.setState(State.DELETED);
    	repository.save(user);
	}
	
	public State getState(Long idUser) {	
		
		User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
		
		State userState = user.getState();
		
		return userState;
	}

	//BUSQUEDAS:
	
	public UserResponseDTO getUserByEmail(String email) {
	    User user = repository.findByEmail(email)
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));

	    return userMapper.toDto(user);
	}

	public List<UserResponseDTO> getUsersByFirstName(String firstName) {
	    List<User> users = repository.findByFirstName(firstName);

	    if (users.isEmpty()) {
	        throw new UserNotFoundException("Users not exists");
	    }

	    return users.stream()
	            .map(userMapper::toDto)
	            .toList();
	}
	
	public List<UserResponseDTO> getUserByLastName(String LastName) {
	    List<User> users = repository.findByLastName(LastName);
	    		
	    if(users.isEmpty()) {
	    	throw new UserNotFoundException("Users not exists");
	    }

	    return users.stream().map(userMapper::toDto).toList();
	}
	
	
	//EXTRAS
	public void changeState(Long idUser, State newState) {

		User user = repository.findById(idUser)
		        .orElseThrow(() -> new UserNotFoundException("User not exists"));
		
		user.setState(newState);
		repository.save(user);
	}

	
}
