package com.plataformas_danos_back.service;

import com.plataformas_danos_back.exception.InvalidCredentialsException;
import com.plataformas_danos_back.exception.InvalidRoleException;
import com.plataformas_danos_back.exception.UserAlreadyExistsException;
import com.plataformas_danos_back.model.dto.LoginRequest;
import com.plataformas_danos_back.model.dto.LoginResponse;
import com.plataformas_danos_back.model.dto.MessageResponse;
import com.plataformas_danos_back.model.dto.RegisterRequest;
import com.plataformas_danos_back.model.entity.User;
import com.plataformas_danos_back.repository.UserRepository;
import com.plataformas_danos_back.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final Set<String> VALID_ROLES = Set.of(
            "creador_cotizacion",
            "agente_ventas",
            "vendedor",
            "administrador_ventas",
            "editor_cotizaciones"
    );

    private static final long EXPIRES_IN_SECONDS = 86400L;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public MessageResponse register(RegisterRequest request) {
        if (!VALID_ROLES.contains(request.getRole())) {
            throw new InvalidRoleException("Rol no válido");
        }

        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new UserAlreadyExistsException("El email ya está registrado");
        });

        Instant now = Instant.now();
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userRepository.save(user);
        log.debug("User registered successfully: email={}, role={}", user.getEmail(), user.getRole());

        return new MessageResponse("Usuario registrado exitosamente");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        log.debug("User logged in successfully: email={}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .expiresIn(EXPIRES_IN_SECONDS)
                .build();
    }
}
