package com.example.userservice.service;

import java.util.List;
import com.example.userservice.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.userservice.dto.*;
import com.example.userservice.exceptions.EmailAlreadyExistsException;
import com.example.userservice.exceptions.UserIsAlreadyDeletedException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.model.State;
import com.example.userservice.model.User;
import com.example.userservice.mapper.*;

@Service
public class UserService {
	
	private final UserRepository repository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(UserRepository repository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
    }
	
	//POST
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		logger.info("Intento de registro para el usuario con email: {}", dto.email());
		if(repository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException("Email already in use");
		}
		User userEntity = userMapper.toEntity(dto);
		userEntity.setState(State.ACTIVE);
		userEntity.setPassword(passwordEncoder.encode(dto.password()));
		User savedUser = repository.save(userEntity);	
		logger.info("Usuario guardado exitosamente: {}", savedUser.getId());
		return userMapper.toResponseDto(savedUser);
	}
	

	//GET
	public UserResponseDTO getUser(Long idUser) {
		logger.info("Intento de obtener por id: {}", idUser);
		User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    return userMapper.toResponseDto(user);
	}
	
	//GET 
	public List<UserResponseDTO> getUsers (){
		logger.info("Intento de obtener todos los usuarios");
		return  repository.findAllByState(State.ACTIVE).stream().map(userMapper::toResponseDto).toList(); 
	}
	
	// PUT
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {
		logger.info("Intento de editar un usuario por id: {}", idUser);
	    User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    
	    if(user.getState() == State.DELETED) {
	    	throw new UserIsAlreadyDeletedException("User already deleted"); 
	    }

	    if (!user.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
	    	   throw new EmailAlreadyExistsException("Email already in use");
	    }
	    user.setFirstName(dto.firstName());
	    user.setLastName(dto.lastName());
	    user.setEmail(dto.email());
		user.setPassword(passwordEncoder.encode(dto.password()));
	    User updatedUser = repository.save(user);
		logger.info("Usuario editado y guardado correctamente: {}", updatedUser.getId());
	    return userMapper.toResponseDto(updatedUser);
	}
	
	//DELETE
	public void deleteUser(Long idUser) {
		logger.info("Intento de borrar un usuario por id: {}", idUser);
	    User user = repository.findById(idUser)
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));

	    if(user.getState() == (State.DELETED)) {
	    	throw new UserIsAlreadyDeletedException("User already deleted");
	    }

	    user.setState(State.DELETED);
		logger.info("Usuario eliminado correctamente: {}", idUser);
    	repository.save(user);
	}
	
	public State getState(Long idUser) {	
		logger.info("Intento de obtener un usuario por id: {}", idUser);
		User user = repository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
		State userState = user.getState();
		return userState;
	}

	//BUSQUEDAS:
	
	public UserResponseDTO getUserByEmail(String email) {
		logger.info("Intento de obtener un usuario por email: {}", email);
	    User user = repository.findByEmail(email)
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));
		logger.info("Usuario encontrado con email: {}", email);
	    return userMapper.toResponseDto(user);
	}


	public List<UserResponseDTO> getUsersByFirstName(String firstName) {
		logger.info("Intento de obtener usuarios por firstName: {}", firstName);
	    List<User> users = repository.findByFirstNameAndState(firstName, State.ACTIVE);

	    return users.stream().map(userMapper::toResponseDto).toList();
	}
	
	
	public List<UserResponseDTO> getUsersByLastName(String lastName) {
		logger.info("Intento de obtener usuarios por lastName: {}", lastName);
	    List<User> users = repository.findByLastNameAndState(lastName, State.ACTIVE);
	    return users.stream().map(userMapper::toResponseDto).toList();
	}
	
	
	//EXTRAS
	public void changeState(Long idUser, State newState) {
		logger.info("Intento de cambiar de estado de un usuario por id: {}", idUser);
		User user = repository.findById(idUser)
		        .orElseThrow(() -> new UserNotFoundException("User not exists"));
		
		user.setState(newState);
		repository.save(user);
		logger.info("Estado actualizado correctamente a {} para el usuario {}", newState, idUser);
	}

	public UserResponseDTO login(LoginDTO dto) {
		logger.info("Intento de login para el correo: {}", dto.email());
		
		User user = repository.findByEmail(dto.email())
				.orElseThrow(() -> new UserNotFoundException("Usuario o contraseña incorrectos"));

		if (user.getState() == State.DELETED) {
			throw new UserIsAlreadyDeletedException("La cuenta se encuentra inhabilitada");
		}

		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new BadCredentialsException("Usuario o contraseña incorrectos");
		}

		logger.info("Login exitoso para el usuario ID: {}", user.getId());
		return userMapper.toResponseDto(user);
	}
}
