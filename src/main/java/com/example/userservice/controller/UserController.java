package com.example.userservice.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.userservice.model.dto.*;
import com.example.userservice.model.enums.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios")
public class UserController {
	
	private final UserService service;
	
	public UserController(UserService service) {
		this.service = service;
	}

	@Operation(summary = "Crea un nuevo usuario", description = "Registra un nuevo usuario en la base de datos")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
	@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
	@ApiResponse(responseCode = "409", description = "El email ya se encuentra registrado")
	@PostMapping
	public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO dto){
			UserResponseDTO userCreado = service.createUser(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(userCreado);
	} 
	
	@Operation(summary = "Obtiene un usuario por ID", description = "Retorna los datos del usuario si existe")
	@ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	@GetMapping("/{idUser}")
	public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long idUser){
		UserResponseDTO user = service.getUser(idUser);
		return ResponseEntity.ok(user);
	}
	
	@Operation(summary = "Obtiene todos los usuarios", description = "Retorna los datos de todos los usuarios si existen")
	@ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponseDTO>> getUsers(){
		List<UserResponseDTO> users = service.getUsers();
		return ResponseEntity.ok(users);
	}
	
	@Operation(summary = "Borra un usuario por id", description = "Borra un usuario en la base de datos por id")
    @ApiResponse(responseCode = "204", description = "Usuario borrado exitosamente")
	@ApiResponse(responseCode = "404", description = "Usuario no existe")
	@ApiResponse(responseCode = "410", description = "El usuario ya fue eliminado anteriormente")
	@DeleteMapping("/{idUser}")
	@PreAuthorize("hasRole('ADMIN') or #idUser == authentication.principal.id")
	public ResponseEntity<Void> deleteUser(@PathVariable Long idUser) {
		service.deleteUser(idUser);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(summary = "Edita un usuario por ID", description = "Edita al usuario si existe")
	@ApiResponse(responseCode = "200", description = "Usuario editado exitosamente")
	@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	@ApiResponse(responseCode = "409", description = "El email ya está siendo usado por otro usuario")
	@PutMapping("/{idUser}")
	@PreAuthorize("hasRole('ADMIN') or #idUser == authentication.principal.id")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long idUser, @Valid @RequestBody UserCreateDTO dto) {
		UserResponseDTO user = service.editUser(idUser, dto);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Obtiene un usuario por su email", description = "Retorna el usuario que coincida con el email")
	@ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	@GetMapping("/email/{email}")
	@PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
	public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email){
		UserResponseDTO user = service.getUserByEmail(email);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Obtiene usuarios por su firstName", description = "Retorna a los usuarios que coincida con el firstName")
	@ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
	@GetMapping("/firstname/{firstname}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponseDTO>> getUsersByFirstName(@PathVariable String firstname){
		List<UserResponseDTO> users = service.getUsersByFirstName(firstname);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "Obtiene usuarios por su lastName", description = "Retorna a los usuarios que coincida con el lastName")
	@ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
	@GetMapping("/lastName/{lastName}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponseDTO>> getUserByLastName(@PathVariable String lastName){
		List<UserResponseDTO> users = service.getUsersByLastName(lastName);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "cambia el estado", description = "Notifica el estado de un usuario")
	@ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente")
	@ApiResponse(responseCode = "404", description = "El usuario no existe")
    @ApiResponse(responseCode = "400", description = "Estado no válido o error en la solicitud")
	@PatchMapping("/{idUser}/state/{newState}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> changeState(@PathVariable Long idUser, @PathVariable State newState){
		service.changeState(idUser, newState);
		return ResponseEntity.ok().build();	
	}
	
}


