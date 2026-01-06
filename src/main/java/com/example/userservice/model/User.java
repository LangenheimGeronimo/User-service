package com.example.userservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class User {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long idUsuario;
	private String nombre;
	private String apellido;
	private LocalDate fechaNacimiento;
	@Transient //este campo no se guarda en la base de datos
	private List<Long> pedidos;
	@Column(nullable = false, unique = true) 
	private String email;
	private boolean activo;
	@Enumerated(EnumType.STRING)
	private Role rol;
	private String password;
	
	public User() {
		super();
		this.pedidos = new ArrayList<>();
	}

	public User(Long idUsuario, String nombre, String apellido, LocalDate fechaNacimiento, String email,
			boolean activo, Role rol, String password) {
		super();
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaNacimiento = fechaNacimiento;
		this.email = email;
		this.activo = activo;
		this.rol = rol;
		this.password = password;
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
	
	
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
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
	
	
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
	public Role getRol() {
		return rol;
	}
	public void setRol(Role rol) {
		this.rol = rol;
	}
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Long getIdUsuario() {
		return idUsuario;
	}

	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", nombre=" + nombre + ", apellido=" + apellido
				+ ", fechaNacimiento=" + fechaNacimiento + ", pedidos=" + pedidos + ", email=" + email + ", activo="
				+ activo + ", rol=" + rol + "]";
	}
	
	
	
	
}
