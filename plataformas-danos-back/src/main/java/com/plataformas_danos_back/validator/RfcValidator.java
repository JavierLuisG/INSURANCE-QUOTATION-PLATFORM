package com.plataformas_danos_back.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validador de RFC del SAT mexicano.
 * Soporta personas físicas (13 chars) y personas morales (12 chars).
 * Regex basado en el estándar oficial SAT:
 *   - 3-4 letras iniciales (nombre/razón social)
 *   - 6 dígitos de fecha (YYMMDD)
 *   - 3 caracteres homoclave alfanumérica (opcional para personas morales que usan 12 chars, obligatorio para personas físicas)
 */
@Slf4j
@Component
public class RfcValidator {

    /**
     * Personas morales: 3 letras + 6 dígitos + 3 homoclave = 12 chars
     * Personas físicas: 4 letras + 6 dígitos + 3 homoclave = 13 chars
     * La Ñ y & están permitidos en las letras iniciales.
     */
    private static final Pattern RFC_PATTERN = Pattern.compile(
            "^([A-ZÑ&]{4}\\d{6}[A-Z\\d]{3}|[A-ZÑ&]{3}\\d{6}[A-Z\\d]{3})$"
    );

    public boolean isValid(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            log.debug("RFC validation failed: value is null or blank");
            return false;
        }
        String rfcUpper = rfc.toUpperCase().trim();
        boolean valid = RFC_PATTERN.matcher(rfcUpper).matches();
        if (!valid) {
            log.debug("RFC validation failed: '{}' does not match SAT pattern", rfcUpper);
        }
        return valid;
    }
}
