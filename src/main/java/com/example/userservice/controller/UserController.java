package com.example.userservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	
	//Metodo de busqueda por diversos filtros
	@Operation(
        summary = "Obtener lista paginada de usuarios con filtros",
        description = "Permite al administrador buscar usuarios aplicando filtros opcionales por nombre, apellido, email o estado. Soporta paginación y ordenamiento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Operación exitosa. Devuelve una página de usuarios."
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado. El token JWT es inválido o expiró."
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Prohibido. Se requiere rol de ADMIN para acceder."
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
        @Parameter(description = "Filtrar por nombre (búsqueda parcial)") 
        @RequestParam(required = false) String firstName,
        
        @Parameter(description = "Filtrar por apellido (búsqueda parcial)") 
        @RequestParam(required = false) String lastName,
        
        @Parameter(description = "Filtrar por email exacto") 
        @RequestParam(required = false) String email,
        
        @Parameter(description = "Filtrar por estado del usuario (ACTIVE, BANNED, DELETED)") 
        @RequestParam(required = false) State state,
        
        @Parameter(hidden = true) 
        @PageableDefault(size = 10, sort = "id") Pageable pageable 
    ) {
        return ResponseEntity.ok(service.getUsers(firstName, lastName, email, state, pageable));
    }
}


