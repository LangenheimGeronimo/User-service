package com.example.userservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.userservice.service.UserService;

import com.example.userservice.dto.*;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService service;
	
	@PostMapping
	public ResponseEntity<UserResponseDTO> createUser( @RequestBody userCreateDTO dto){
			UserResponseDTO userCreado = service.createUser(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(userCreado);
	} 
	
	@GetMapping("/{idUser}")
	public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long idUser){
		UserResponseDTO user = service.getUser(idUser);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getUsers(){
		List<UserResponseDTO> users = service.getUsers();
		return ResponseEntity.ok(users);
	}
	
	@DeleteMapping("/{idUser}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long idUser) {
		service.deleteUser(idUser);
		return ResponseEntity.noContent().build();
	}
	
	
	@PutMapping("/{idUser}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long idUser, @RequestBody userCreateDTO dto) {
		UserResponseDTO user = service.editUser(idUser, dto);
		return ResponseEntity.ok(user);
	}
	
}


