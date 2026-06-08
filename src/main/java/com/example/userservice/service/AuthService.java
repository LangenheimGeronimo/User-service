package com.example.userservice.service;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.model.dto.RegisterDTO;
import com.example.userservice.model.dto.UserResponseDTO;

public interface AuthService {
    AuthResponse login(LoginDTO loginDto);

    UserResponseDTO register(RegisterDTO registerDto);
}