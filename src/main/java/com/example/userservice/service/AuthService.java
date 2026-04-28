package com.example.userservice.service;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;

public interface AuthService {
    AuthResponse login(LoginDTO loginDto);
}