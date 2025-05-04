package org.example.job_portal.security.service;

import org.example.job_portal.dto.request.LoginRequest;
import org.example.job_portal.dto.request.RegisterRequest;
import org.example.job_portal.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}