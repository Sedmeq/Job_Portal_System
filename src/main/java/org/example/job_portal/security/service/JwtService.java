package org.example.job_portal.security.service;


import io.jsonwebtoken.Claims;
import org.example.job_portal.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims, T> resolver);
    List<String> getRolesFromToken(String token);
    boolean isValid(String token, UserDetails user);
    String generateToken(User user);
}