import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import CotizadorHeader from '@/components/Cotizador/CotizadorHeader';

describe('CotizadorHeader', () => {
  // ---------------------------------------------------------------------------
  // Test 3: muestra el folio y el estado de la cotización correctamente
  // ---------------------------------------------------------------------------
  it('muestra el folio y el estado de la cotización correctamente', () => {
    // GIVEN — props con folio válido y estado INCOMPLETA
    render(
      <CotizadorHeader folio="COT-2026-000001" estadoValidacion="INCOMPLETA" />
    );

    // WHEN — el componente se renderiza (efecto implícito)

    // THEN — el folio y el badge de estado son visibles con los textos correctos
    expect(screen.getByDisplayValue('COT-2026-000001')).toBeInTheDocument();
    expect(screen.getByText('Incompleta')).toBeInTheDocument();
  });

  // ---------------------------------------------------------------------------
  // Test 4: campo folio está deshabilitado
  // ---------------------------------------------------------------------------
  it('campo folio está deshabilitado', () => {
    // GIVEN — componente renderizado con folio y estado
    render(
      <CotizadorHeader folio="COT-2026-000001" estadoValidacion="COMPLETA" />
    );

    // WHEN — inspeccionamos el input del folio

    // THEN — el input tiene el atributo disabled (no es editable por el usuario)
    const folioInput = screen.getByDisplayValue('COT-2026-000001');
    expect(folioInput).toBeDisabled();
  });
});
