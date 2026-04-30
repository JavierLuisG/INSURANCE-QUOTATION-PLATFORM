package com.plataformas_danos_back.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

// SPEC-011 — Tests unitarios del validador de RFC SAT (BR-003)
@ExtendWith(MockitoExtension.class)
class RfcValidatorTest {

    private final RfcValidator rfcValidator = new RfcValidator();

    // ─── Persona física (13 chars: 4 letras + 6 dígitos + 3 homoclave) ────────

    @Test
    void validar_rfcPersonaFisicaValido_retornaTrue() {
        // GIVEN
        // RFC de persona física: 4 letras iniciales + 6 dígitos de fecha + 3 homoclave = 13 chars
        String rfcPersonaFisica = "PEPJ800326IG0";

        // WHEN
        boolean resultado = rfcValidator.isValid(rfcPersonaFisica);

        // THEN
        assertThat(resultado).isTrue();
    }

    // ─── Persona moral (12 chars: 3 letras + 6 dígitos + 3 homoclave) ─────────

    @Test
    void validar_rfcPersonaMoralValido_retornaTrue() {
        // GIVEN
        // RFC de persona moral: 3 letras iniciales + 6 dígitos de fecha + 3 homoclave = 12 chars
        String rfcPersonaMoral = "SAT930101OI1";

        // WHEN
        boolean resultado = rfcValidator.isValid(rfcPersonaMoral);

        // THEN
        assertThat(resultado).isTrue();
    }

    // ─── RFC mal formado ──────────────────────────────────────────────────────

    @Test
    void validar_rfcMalFormado_retornaFalse() {
        // GIVEN
        // RFC que no cumple el patrón SAT: sin fecha válida, longitud incorrecta
        String rfcMalFormado = "RFCINVALIDO123";

        // WHEN
        boolean resultado = rfcValidator.isValid(rfcMalFormado);

        // THEN
        assertThat(resultado).isFalse();
    }
}
