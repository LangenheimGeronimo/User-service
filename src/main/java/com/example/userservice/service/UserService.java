package com.example.userservice.service;

import com.example.userservice.repository.UserRepository;
import com.example.userservice.specification.UserSpecifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;
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
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	//POST
	@Transactional
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		log.info("Intento de registro para el usuario con email: {}", dto.email());
		if(userRepository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException();
		}
		User userEntity = userMapper.toEntity(dto);
		userEntity.setState(State.ACTIVE);
		userEntity.setPassword(passwordEncoder.encode(dto.password()));

		User savedUser = userRepository.save(userEntity);	
		log.info("Usuario guardado exitosamente: {}", savedUser.getId());
		return userMapper.toResponseDto(savedUser);
	}
	
	//GET
	public UserResponseDTO getUser(Long idUser) {
		log.info("Intento de obtener por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
	    return userMapper.toResponseDto(user);
	}
	
	// PUT
	@Transactional
	public UserResponseDTO editUser(Long idUser, UserUpdateDTO dto) {
		log.info("Intento de editar un usuario por id: {}", idUser);
		
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		
		if (user.getState() == State.DELETED) {
			throw new UserIsAlreadyDeletedException(); 
		}

		userMapper.updateEntityFromDto(dto, user); 

		User updatedUser = userRepository.save(user);
		
		log.info("Usuario editado y guardado correctamente: {}", updatedUser.getId());
		return userMapper.toResponseDto(updatedUser);
	}

	//DELETE
	@Transactional
	public void deleteUser(Long idUser) {
		log.info("Intento de borrar un usuario por id: {}", idUser);
	    User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

	    if(user.getState() == (State.DELETED)) {
	    	throw new UserIsAlreadyDeletedException();
	    }

	    user.setState(State.DELETED);
		log.info("Usuario eliminado correctamente: {}", idUser);
    	userRepository.save(user);
	}
	
	//GET
	public State getState(Long idUser) {	
		log.info("Intento de obtener un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		State userState = user.getState();
		return userState;
	}
	
	//GET/email
	public UserResponseDTO getUserByEmail(String email) {
		log.info("Intento de obtener un usuario por email: {}", email);
	    User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
		log.info("Usuario encontrado con email: {}", email);
	    return userMapper.toResponseDto(user);
	}

	//EXTRA
	@Transactional
	public void changeState(Long idUser, State newState) {
		log.info("Intento de cambiar de estado de un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		
		user.setState(newState);
		userRepository.save(user);
		log.info("Estado actualizado correctamente a {} para el usuario {}", newState, idUser);
	}

	//GET
	public Page<UserResponseDTO> getUsers(String firstName, String lastName, String email, State state, Pageable pageable) {
		log.info("Búsqueda avanzada de usuarios con filtros dinámicos");

		Specification<User> spec = Specification.where(UserSpecifications.hasFirstName(firstName))
												.and(UserSpecifications.hasLastName(lastName))
												.and(UserSpecifications.hasEmail(email))
												.and(UserSpecifications.hasState(state));

		return userRepository.findAll(spec, pageable).map(userMapper::toResponseDto);
	}

}
