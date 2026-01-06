package com.example.userservice.dto;

import java.util.ArrayList;
import java.util.List;

public class UserResponseDTO {

	private Long idUsuario;
	private String nombre;
	private String apellido;
	private List<Long> pedidos;
	private String email;
	
	public UserResponseDTO() {
		super();
		this.pedidos = new ArrayList<>();
	}

	public UserResponseDTO(Long idUsuario, String nombre, String apellido, String email) {
		super();
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.pedidos = new ArrayList<>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public List<Long> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Long> pedidos) {
		this.pedidos = pedidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}
	
	
	
	
}
