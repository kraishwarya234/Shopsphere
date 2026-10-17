package com.shopsphere.service;

import com.shopsphere.dto.AuthResponse;
import com.shopsphere.dto.LoginRequest;
import com.shopsphere.dto.RegisterRequest;
import com.shopsphere.entity.Role;
import com.shopsphere.entity.User;
import com.shopsphere.exception.BadRequestException;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }
        User user = userRepository.save(User.builder()
                .name(req.name().trim()).email(email)
                .password(passwordEncoder.encode(req.password())).role(Role.USER).build());
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}
