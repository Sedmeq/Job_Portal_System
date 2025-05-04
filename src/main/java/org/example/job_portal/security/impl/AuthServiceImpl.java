package org.example.job_portal.security.impl;

import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.LoginRequest;
import org.example.job_portal.dto.request.RegisterRequest;
import org.example.job_portal.dto.response.AuthResponse;
import org.example.job_portal.enums.Role;
import org.example.job_portal.exception.NotFoundException;
import org.example.job_portal.model.Employer;
import org.example.job_portal.model.User;
import org.example.job_portal.repository.EmployerRepository;
import org.example.job_portal.repository.UserRepository;
import org.example.job_portal.security.service.AuthService;
import org.example.job_portal.security.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // If the role is EMPLOYER, create an employer profile
//        if (request.getRole() == Role.EMPLOYER) {
//            Employer employer = Employer.builder()
//                    .name(request.getUsername())
//                    .description("New employer")
//                    .user(user)
//                    .build();
//            employerRepository.save(employer);
//        }

        return new AuthResponse("User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password. Please check your credentials and try again.");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        return new AuthResponse(accessToken);
    }
}