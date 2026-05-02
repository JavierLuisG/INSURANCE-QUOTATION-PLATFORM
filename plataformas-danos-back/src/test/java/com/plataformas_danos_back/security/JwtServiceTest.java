package com.plataformas_danos_back.security;

// SPEC-012 — Tests unitarios de JwtService — generación y claims del JWT

import com.plataformas_danos_back.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    // ─── Constantes de prueba ─────────────────────────────────────────────────

    private static final String JWT_SECRET     = "this-is-a-test-jwt-secret-key-for-unit-tests";
    private static final long   EXPIRATION_SEC = 86400L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationSeconds", EXPIRATION_SEC);
    }

    // ─── generateToken — claims esperados ────────────────────────────────────

    @Test
    void generateToken_contieneClaimsEsperados_sub_roles_iat_exp() {
        // GIVEN
        User user = User.builder()
                .id("user-001")
                .email("agente@example.com")
                .password("$2a$10$hash")
                .role("agente_ventas")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // WHEN
        String token = jwtService.generateToken(user);

        // THEN — parsear el token y verificar cada claim
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject())
                .as("El subject debe ser el email del usuario")
                .isEqualTo("agente@example.com");

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        assertThat(roles)
                .as("El claim 'roles' debe contener el rol del usuario")
                .containsExactly("agente_ventas");

        assertThat(claims.getIssuedAt())
                .as("issuedAt debe estar presente y no ser nulo")
                .isNotNull();

        assertThat(claims.getExpiration())
                .as("expiration debe estar presente y no ser nulo")
                .isNotNull();
    }

    // ─── generateToken — ventana de expiración ───────────────────────────────

    @Test
    void generateToken_expiracionEsCorrecta_iatMas86400Segundos() {
        // GIVEN
        User user = User.builder()
                .id("user-002")
                .email("admin@example.com")
                .password("$2a$10$hash")
                .role("administrador_ventas")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        long tiempoAntesDeLlamar = System.currentTimeMillis();

        // WHEN
        String token = jwtService.generateToken(user);

        long tiempoDespuesDeLlamar = System.currentTimeMillis();

        // THEN
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date iat = claims.getIssuedAt();
        Date exp = claims.getExpiration();

        // La diferencia entre exp e iat debe ser exactamente 86400 segundos (en ms)
        long diferenciaMilis = exp.getTime() - iat.getTime();
        assertThat(diferenciaMilis)
                .as("La diferencia exp - iat debe ser 86400 segundos en milisegundos")
                .isEqualTo(EXPIRATION_SEC * 1000);

        // El iat debe estar dentro de la ventana de la llamada (tolerancia 2 segundos)
        assertThat(iat.getTime())
                .as("issuedAt debe estar entre el inicio y el fin de la llamada")
                .isBetween(tiempoAntesDeLlamar - 2000, tiempoDespuesDeLlamar + 2000);

        // La expiración debe ser aproximadamente iat + 86400 s
        long expEsperadoMinimo = tiempoAntesDeLlamar  + EXPIRATION_SEC * 1000 - 2000;
        long expEsperadoMaximo = tiempoDespuesDeLlamar + EXPIRATION_SEC * 1000 + 2000;
        assertThat(exp.getTime())
                .as("expiration debe ser issuedAt + 86400 s (con tolerancia de 2 s)")
                .isBetween(expEsperadoMinimo, expEsperadoMaximo);
    }

    // ─── generateToken — token es válido y no nulo ───────────────────────────

    @Test
    void generateToken_retornaTokenCompactoNoVacio() {
        // GIVEN
        User user = User.builder()
                .id("user-003")
                .email("editor@example.com")
                .password("$2a$10$hash")
                .role("editor_cotizaciones")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // WHEN
        String token = jwtService.generateToken(user);

        // THEN — un JWT compacto tiene exactamente 3 secciones separadas por '.'
        assertThat(token)
                .isNotBlank()
                .contains(".");
        assertThat(token.split("\\."))
                .as("Un JWT compacto tiene header.payload.signature")
                .hasSize(3);
    }
}
