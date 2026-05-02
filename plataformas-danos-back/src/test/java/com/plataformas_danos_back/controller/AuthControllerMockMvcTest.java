package com.plataformas_danos_back.controller;

// SPEC-012 — Tests MockMvc del controlador de autenticación (HU-AUTH-01 a HU-AUTH-04)
// Cubre: POST /api/v1/auth/register y POST /api/v1/auth/login con todos los casos de error

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plataformas_danos_back.exception.GlobalExceptionHandler;
import com.plataformas_danos_back.exception.InvalidCredentialsException;
import com.plataformas_danos_back.exception.InvalidRoleException;
import com.plataformas_danos_back.exception.UserAlreadyExistsException;
import com.plataformas_danos_back.model.dto.LoginRequest;
import com.plataformas_danos_back.model.dto.LoginResponse;
import com.plataformas_danos_back.model.dto.MessageResponse;
import com.plataformas_danos_back.model.dto.RegisterRequest;
import com.plataformas_danos_back.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerMockMvcTest {

    // ─── Configuración del contexto standalone ────────────────────────────────

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /api/v1/auth/register — 201 happy path ─────────────────────────

    @Test
    void POST_register_retorna201_conMensajeExitoso() throws Exception {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("agente@example.com")
                .password("Segura123")
                .role("agente_ventas")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new MessageResponse("Usuario registrado exitosamente"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"));
    }

    // ─── POST /api/v1/auth/register — 409 email duplicado ────────────────────

    @Test
    void POST_register_retorna409_cuandoEmailDuplicado() throws Exception {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicado@example.com")
                .password("Segura123")
                .role("vendedor")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("El email ya está registrado"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("El email ya está registrado"));
    }

    // ─── POST /api/v1/auth/register — 400 rol inválido ───────────────────────

    @Test
    void POST_register_retorna400_cuandoRolInvalido() throws Exception {
        // GIVEN
        RegisterRequest request = RegisterRequest.builder()
                .email("usuario@example.com")
                .password("Segura123")
                .role("rol_inexistente")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new InvalidRoleException("Rol no válido"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ROLE"))
                .andExpect(jsonPath("$.message").value("Rol no válido"));
    }

    // ─── POST /api/v1/auth/register — 400 Bean Validation: email inválido ────

    @Test
    void POST_register_retorna400_cuandoEmailInvalido() throws Exception {
        // GIVEN — email sin formato válido; Bean Validation rechaza antes de llegar al service
        RegisterRequest request = RegisterRequest.builder()
                .email("no-es-un-email")
                .password("Segura123")
                .role("vendedor")
                .build();

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(authService, never()).register(any());
    }

    // ─── POST /api/v1/auth/register — 400 Bean Validation: contraseña débil ──

    @Test
    void POST_register_retorna400_cuandoPasswordDebil() throws Exception {
        // GIVEN — contraseña sin mayúscula ni dígito, menos de 8 caracteres
        RegisterRequest request = RegisterRequest.builder()
                .email("usuario@example.com")
                .password("debil")
                .role("vendedor")
                .build();

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(authService, never()).register(any());
    }

    // ─── POST /api/v1/auth/register — 400 Bean Validation: role en blanco ────

    @Test
    void POST_register_retorna400_cuandoRoleEnBlanco() throws Exception {
        // GIVEN — @NotBlank en el campo role no se cumple
        RegisterRequest request = RegisterRequest.builder()
                .email("usuario@example.com")
                .password("Segura123")
                .role("")
                .build();

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(authService, never()).register(any());
    }

    // ─── POST /api/v1/auth/login — 200 happy path ────────────────────────────

    @Test
    void POST_login_retorna200_conTokenYExpiracion() throws Exception {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("agente@example.com")
                .password("Segura123")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.test.token")
                .expiresIn(86400L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.test.token"))
                .andExpect(jsonPath("$.expiresIn").value(86400));
    }

    // ─── POST /api/v1/auth/login — 401 credenciales inválidas ────────────────

    @Test
    void POST_login_retorna401_cuandoCredencialesInvalidas() throws Exception {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("agente@example.com")
                .password("ContrasenaErrada1")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    // ─── POST /api/v1/auth/login — 400 Bean Validation: email vacío ──────────

    @Test
    void POST_login_retorna400_cuandoEmailVacio() throws Exception {
        // GIVEN — email en blanco; @NotBlank rechaza antes de llegar al service
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("Segura123")
                .build();

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(authService, never()).login(any());
    }

    // ─── POST /api/v1/auth/login — 400 Bean Validation: password vacío ───────

    @Test
    void POST_login_retorna400_cuandoPasswordVacio() throws Exception {
        // GIVEN — @NotBlank en password rechaza la petición
        LoginRequest request = LoginRequest.builder()
                .email("agente@example.com")
                .password("")
                .build();

        // WHEN / THEN
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(authService, never()).login(any());
    }
}
