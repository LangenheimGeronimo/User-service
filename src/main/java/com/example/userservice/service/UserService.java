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
	
	@Transactional
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		if (dto == null) {
            throw new IllegalArgumentException("El DTO de creación de usuario no puede ser nulo");
        }
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
	
	public UserResponseDTO getUser(Long idUser) {
		if (idUser == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
		log.info("Intento de obtener por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
	    return userMapper.toResponseDto(user);
	}
	
	@Transactional
	public UserResponseDTO editUser(Long idUser, UserUpdateDTO dto) {
		if (dto == null) {
            throw new IllegalArgumentException("El DTO de actualización no puede ser nulo");
        }
		if (idUser == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
		
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

	@Transactional
	public void deleteUser(Long idUser) {
		if (idUser == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
		log.info("Intento de borrar un usuario por id: {}", idUser);
	    User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

	    if(user.getState() == (State.DELETED)) {
	    	throw new UserIsAlreadyDeletedException();
	    }

	    user.setState(State.DELETED);
		log.info("Usuario eliminado correctamente: {}", idUser);
    	userRepository.save(user);
	}
	
	public State getState(Long idUser) {	
		if (idUser == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
		log.info("Intento de obtener un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		State userState = user.getState();
		return userState;
	}
	
	public UserResponseDTO getUserByEmail(String email) {
		if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email de búsqueda no puede ser nulo o vacío");
        }
		log.info("Intento de obtener un usuario por email: {}", email);
	    User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
		log.info("Usuario encontrado con email: {}", email);
	    return userMapper.toResponseDto(user);
	}

	@Transactional
	public void changeState(Long idUser, State newState) {
		if (idUser == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
		log.info("Intento de cambiar de estado de un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);
		
		user.setState(newState);
		userRepository.save(user);
		log.info("Estado actualizado correctamente a {} para el usuario {}", newState, idUser);
	}

	public Page<UserResponseDTO> getUsers(String firstName, String lastName, String email, State state, Pageable pageable) {
		if (pageable == null) {
            throw new IllegalArgumentException("El parámetro Pageable no puede ser nulo");
        }
		log.info("Búsqueda avanzada de usuarios con filtros dinámicos");

		Specification<User> spec = Specification.where(UserSpecifications.hasFirstName(firstName))
												.and(UserSpecifications.hasLastName(lastName))
												.and(UserSpecifications.hasEmail(email))
												.and(UserSpecifications.hasState(state));

		return userRepository.findAll(spec, pageable).map(userMapper::toResponseDto);
	}

}
