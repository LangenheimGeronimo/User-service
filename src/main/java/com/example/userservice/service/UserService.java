package com.example.userservice.service;

import java.util.List;
import com.example.userservice.repository.ReportRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.UserPrincipal;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.userservice.model.dto.*;
import com.example.userservice.model.entity.Report;
import com.example.userservice.model.enums.*; 
import com.example.userservice.model.entity.User;
import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserIsAlreadyDeletedException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.*;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService{
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final ReportMapper reportMapper;
	private final ReportRepository reportRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, ReportMapper reportMapper, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.reportMapper = reportMapper;
		this.reportRepository = reportRepository;
    }
	
	//POST
	@Transactional
	public UserResponseDTO createUser(UserCreateDTO dto) { 
		logger.info("Intento de registro para el usuario con email: {}", dto.email());
		if(userRepository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException("Email already in use");
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
		User user = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    return userMapper.toResponseDto(user);
	}
	
	//GET 
	public List<UserResponseDTO> getUsers (){
		logger.info("Intento de obtener todos los usuarios");
		return userRepository.findAllByState(State.ACTIVE).stream().map(userMapper::toResponseDto).toList(); 
	}
	
	// PUT
	@Transactional
	public UserResponseDTO editUser(Long idUser, UserCreateDTO dto) {
		logger.info("Intento de editar un usuario por id: {}", idUser);
	    User user = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
	    
	    if(user.getState() == State.DELETED) {
	    	throw new UserIsAlreadyDeletedException("User already deleted"); 
	    }

	    if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
	    	   throw new EmailAlreadyExistsException("Email already in use");
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
	    User user = userRepository.findById(idUser)
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));

	    if(user.getState() == (State.DELETED)) {
	    	throw new UserIsAlreadyDeletedException("User already deleted");
	    }

	    user.setState(State.DELETED);
		logger.info("Usuario eliminado correctamente: {}", idUser);
    	userRepository.save(user);
	}

	
	//GET
	public State getState(Long idUser) {	
		logger.info("Intento de obtener un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not exists"));
		State userState = user.getState();
		return userState;
	}

	//BUSQUEDAS:
	
	public UserResponseDTO getUserByEmail(String email) {
		logger.info("Intento de obtener un usuario por email: {}", email);
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new UserNotFoundException("User not exists"));
		logger.info("Usuario encontrado con email: {}", email);
	    return userMapper.toResponseDto(user);
	}

	
	public List<UserResponseDTO> getUsersByFirstName(String firstName) {
		logger.info("Intento de obtener usuarios por firstName: {}", firstName);
	    List<User> users = userRepository.findByFirstNameAndState(firstName, State.ACTIVE);

	    return users.stream().map(userMapper::toResponseDto).toList();
	}
	
	
	public List<UserResponseDTO> getUsersByLastName(String lastName) {
		logger.info("Intento de obtener usuarios por lastName: {}", lastName);
	    List<User> users = userRepository.findByLastNameAndState(lastName, State.ACTIVE);
	    return users.stream().map(userMapper::toResponseDto).toList();
	}
	
	//EXTRAS
	@Transactional
	public void changeState(Long idUser, State newState) {
		logger.info("Intento de cambiar de estado de un usuario por id: {}", idUser);
		User user = userRepository.findById(idUser)
		        .orElseThrow(() -> new UserNotFoundException("User not exists"));
		
		user.setState(newState);
		userRepository.save(user);
		logger.info("Estado actualizado correctamente a {} para el usuario {}", newState, idUser);
	}

	//SEGURIDAD

	@Transactional
	public UserResponseDTO login(LoginDTO dto) {
		logger.info("Intento de login para el correo: {}", dto.email());
		
		User user = userRepository.findByEmail(dto.email())
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

	@Transactional
	public void addReport(ReportCreateDTO dto){ 
		logger.info("Intento de realizar un reporte a un Usuario:");
		if (reportRepository.existsByReporterUserIdAndReportedUserId(dto.reporterUserId(), dto.reportedUserId())){
			throw new AlreadyReportedException("El usuario ya ha realizado una denuncia previa contra este perfil.");
		}
		Report report = reportMapper.toEntity(dto);
		reportRepository.save(report);
		logger.info("Reporte guardado exitosamente: {}", report);

		long cont = reportRepository.countByReportedUserId(dto.reportedUserId());

		if(cont >= 3){
			User user = userRepository.findById(dto.reportedUserId())
        	.orElseThrow(() -> new UserNotFoundException("No se encontró el usuario para banear"));
    
			user.setState(State.BANNED);
			userRepository.save(user);
			logger.warn("¡USUARIO BANEADO! El ID {} alcanzó los {} reportes.", dto.reportedUserId(), cont);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		logger.info("Cargando detalles de seguridad para el email: {}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario: " + email));
				
		return new UserPrincipal(user); 
	}

}
