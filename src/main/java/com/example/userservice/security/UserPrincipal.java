package com.example.userservice.security;

import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    // El objeto User original queda encapsulado y protegido
    private final User user; 

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapeo profesional: Spring Security espera "ROLE_ADMIN", no solo "ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Usamos email como identificador único (username)
    }

    @Override
    public boolean isAccountNonLocked() {
        // Lógica de negocio aplicada directamente a la seguridad
        return user.getState() != State.BANNED;
    }

    @Override
    public boolean isEnabled() {
        return user.getState() != State.DELETED;
    }

    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }
    
    // Métodos extra útiles para obtener datos del usuario sin exponer toda la entidad si no quieres
    public Long getId() {
        return user.getId();
    }

    public User getUser() {
        return this.user;
    }
}