package com.plataformas_danos_back.service;

// SPEC-012 — Tests unitarios de AuthServiceImpl (HU-AUTH-01 a HU-AUTH-04)
// Criterios cubiertos: CRITERIO-1.1 a 1.4 y CRITERIO-2.1 a 2.3

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    // ─── Dependencias mockeadas ───────────────────────────────────────────────

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl service;

    // ─── register — happy path (CRITERIO-1.1) ────────────────────────────────

    @Test
    void register_usuarioNuevoConRolValido_retornaMensajeExitoso() {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("agente@example.com")
                .password("Segura123")
                .role("agente_ventas")
                .build();

        when(userRepository.findByEmail("agente@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Segura123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        MessageResponse response = service.register(request);

        // THEN
        assertThat(response.getMessage()).isEqualTo("Usuario registrado exitosamente");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("agente@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("$2a$10$hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo("agente_ventas");
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void register_todosLosRolesValidos_registranExitosamente() {
        // GIVEN — verificar todos los roles del dominio
        String[] rolesValidos = {
                "creador_cotizacion", "agente_ventas", "vendedor",
                "administrador_ventas", "editor_cotizaciones"
        };

        for (String rol : rolesValidos) {
            RegisterRequest request = RegisterRequest.builder()
                    .email(rol + "@example.com")
                    .password("Segura123")
                    .role(rol)
                    .build();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("hashed");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            MessageResponse response = service.register(request);

            // THEN
            assertThat(response.getMessage())
                    .as("Rol '%s' debe registrarse sin error", rol)
                    .isEqualTo("Usuario registrado exitosamente");
        }
    }

    // ─── register — email duplicado (CRITERIO-1.2) ───────────────────────────

    @Test
    void register_emailYaRegistrado_lanzaUserAlreadyExistsException() {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicado@example.com")
                .password("Segura123")
                .role("vendedor")
                .build();

        User usuarioExistente = User.builder()
                .id("existing-id")
                .email("duplicado@example.com")
                .password("$2a$10$cualquierHash")
                .role("vendedor")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findByEmail("duplicado@example.com"))
                .thenReturn(Optional.of(usuarioExistente));

        // WHEN / THEN
        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("El email ya está registrado");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ─── register — rol inválido (CRITERIO-1.3) ──────────────────────────────

    @Test
    void register_rolInvalido_lanzaInvalidRoleException() {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("usuario@example.com")
                .password("Segura123")
                .role("rol_inexistente")
                .build();

        // WHEN / THEN
        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(InvalidRoleException.class)
                .hasMessage("Rol no válido");

        // La validación del rol ocurre antes de consultar la DB
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_rolVacio_lanzaInvalidRoleException() {
        // GIVEN — cadena vacía no pertenece al set de roles válidos
        RegisterRequest request = RegisterRequest.builder()
                .email("usuario@example.com")
                .password("Segura123")
                .role("")
                .build();

        // WHEN / THEN
        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(InvalidRoleException.class)
                .hasMessage("Rol no válido");

        verify(userRepository, never()).findByEmail(anyString());
    }

    // ─── login — happy path (CRITERIO-2.1) ───────────────────────────────────

    @Test
    void login_credencialesValidas_retornaTokenYExpiracion() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("agente@example.com")
                .password("Segura123")
                .build();

        User usuario = User.builder()
                .id("user-id-001")
                .email("agente@example.com")
                .password("$2a$10$hashedPassword")
                .role("agente_ventas")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findByEmail("agente@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Segura123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("eyJhbGciOiJIUzI1NiJ9.test.token");

        // WHEN
        LoginResponse response = service.login(request);

        // THEN
        assertThat(response.getToken()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.test.token");
        assertThat(response.getExpiresIn()).isEqualTo(86400L);
        verify(jwtService).generateToken(usuario);
    }

    // ─── login — contraseña incorrecta (CRITERIO-2.2) ────────────────────────

    @Test
    void login_contrasenaIncorrecta_lanzaInvalidCredentialsException() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("agente@example.com")
                .password("ContrasenaErrada1")
                .build();

        User usuario = User.builder()
                .id("user-id-001")
                .email("agente@example.com")
                .password("$2a$10$hashedPasswordCorrecto")
                .role("agente_ventas")
                .active(true)
                .build();

        when(userRepository.findByEmail("agente@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("ContrasenaErrada1", "$2a$10$hashedPasswordCorrecto"))
                .thenReturn(false);

        // WHEN / THEN
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        verify(jwtService, never()).generateToken(any());
    }

    // ─── login — email no existe (CRITERIO-2.3) ──────────────────────────────

    @Test
    void login_emailNoExiste_lanzaInvalidCredentialsException() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("fantasma@example.com")
                .password("Segura123")
                .build();

        when(userRepository.findByEmail("fantasma@example.com")).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        // No debe intentar comparar la contraseña ni generar token
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }
}
