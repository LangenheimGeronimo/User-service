package com.example.userservice.service;

import com.example.userservice.repository.UserRepository;
import com.example.userservice.specification.UserSpecifications;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.userservice.model.dto.*;
import com.example.userservice.model.enums.*; 
import com.example.userservice.model.entity.User;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserIsAlreadyDeletedException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.*;

@Service
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;


	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
    }
	
	//POST
	@Transactional
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		logger.info("Intento de registro para el usuario con email: {}", dto.email());
		if(userRepository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException();
		}
		User userEntity = userMapper.toEntity(dto);
		userEntity.setState(State.ACTIVE);
		userEntity.setPassword(passwordEncoder.encode(dto.password()));
		User savedUser = userRepository.save(userEntity);	
		logger.info("Usuario guardado exitosamente: {}", savedUser.getId());
		return userMapper.toResponseDto(savedUser);
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		logger.info("Intento de obtener por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
	    return userMapper.toResponseDto(user);
	}
	
	// PUT
	@Transactional
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {
		logger.info("Intento de editar un usuario por id: {}", idUser);
	    User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
	    
	    if(user.getState() == State.DELETED) {
	    	throw new UserIsAlreadyDeletedException(); 
	    }

	    if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
	    	throw new EmailAlreadyExistsException();
	    }
	
		userMapper.updateEntityFromDto(dto, user); 
		user.setPassword(passwordEncoder.encode(dto.password()));

	    User updatedUser = userRepository.save(user);
		logger.info("Usuario editado y guardado correctamente: {}", updatedUser.getId());
	    return userMapper.toResponseDto(updatedUser);
	}

	//DELETE
	@Transactional
	public void deleteUser(Long idUser) {
		logger.info("Intento de borrar un usuario por id: {}", idUser);
	    User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

	    if(user.getState() == (State.DELETED)) {
	    	throw new UserIsAlreadyDeletedException();
	    }

	    user.setState(State.DELETED);
		logger.info("Usuario eliminado correctamente: {}", idUser);
    	userRepository.save(user);
	}

	
	//GET
	public State getState(Long idUser) {	
		logger.info("Intento de obtener un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		State userState = user.getState();
		return userState;
	}

	//BUSQUEDAS:
	
	public UserResponseDTO getUserByEmail(String email) {
		logger.info("Intento de obtener un usuario por email: {}", email);
	    User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
		logger.info("Usuario encontrado con email: {}", email);
	    return userMapper.toResponseDto(user);
	}

	
	
	//EXTRAS
	@Transactional
	public void changeState(Long idUser, State newState) {
		logger.info("Intento de cambiar de estado de un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		
		user.setState(newState);
		userRepository.save(user);
		logger.info("Estado actualizado correctamente a {} para el usuario {}", newState, idUser);
	}

	public Page<UserResponseDTO> getUsers(String firstName, String lastName, String email, State state, Pageable pageable) {
		logger.info("Búsqueda avanzada de usuarios con filtros dinámicos");

		Specification<User> spec = Specification.where(UserSpecifications.hasFirstName(firstName))
				.and(UserSpecifications.hasLastName(lastName))
				.and(UserSpecifications.hasEmail(email))
				.and(UserSpecifications.hasState(state));

		return userRepository.findAll(spec, pageable).map(userMapper::toResponseDto);
	}

}
