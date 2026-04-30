import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import AseguradoFields from '@/components/Cotizador/AseguradoFields';
import {
  datosGeneralesSchema,
  type DatosGeneralesFormValues,
} from '@/lib/schemas/datosGenerales.schema';

// ---------------------------------------------------------------------------
// Wrapper de prueba: expone control y errors al componente sin un form real
// ---------------------------------------------------------------------------
interface WrapperProps {
  onSubmit?: (values: DatosGeneralesFormValues) => void;
}

function AseguradoFieldsWrapper({ onSubmit = vi.fn() }: WrapperProps) {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<DatosGeneralesFormValues>({
    resolver: zodResolver(datosGeneralesSchema),
    defaultValues: {
      nombreAsegurado: '',
      rfcAsegurado: '',
      tipoSeguroId: '',
      monedaId: '',
      canalVentaId: '',
      fechaInicioVigencia: '',
      fechaFinVigencia: '',
      version: 0,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <AseguradoFields control={control} errors={errors} />
      <button type="submit">Guardar</button>
    </form>
  );
}

describe('AseguradoFields', () => {
  // ---------------------------------------------------------------------------
  // Test 5: acepta RFC válido sin mostrar error
  // ---------------------------------------------------------------------------
  it('acepta RFC válido sin mostrar error', async () => {
    // GIVEN — formulario renderizado con valores en blanco
    const onSubmit = vi.fn();
    const user = userEvent.setup();
    render(<AseguradoFieldsWrapper onSubmit={onSubmit} />);

    const rfcInput = screen.getByLabelText(/rfc/i);

    // WHEN — el usuario escribe un RFC válido (persona física, 13 chars) y envía
    await user.type(rfcInput, 'PEPJ800326IG0');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — no aparece ningún mensaje de error para el campo RFC
    await waitFor(() => {
      expect(screen.queryByText(/formato del rfc/i)).toBeNull();
    });
  });

  // ---------------------------------------------------------------------------
  // Test 6: muestra error inline para RFC inválido
  // ---------------------------------------------------------------------------
  it('muestra error inline para RFC inválido', async () => {
    // GIVEN — formulario renderizado
    const user = userEvent.setup();
    render(<AseguradoFieldsWrapper />);

    const rfcInput = screen.getByLabelText(/rfc/i);

    // WHEN — el usuario escribe un RFC con formato incorrecto y envía
    await user.type(rfcInput, 'RFCINVALIDO123');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — se muestra el mensaje de error inline bajo el campo RFC
    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument();
      expect(screen.getByRole('alert')).toHaveTextContent(
        /formato del rfc introducido no es válido/i
      );
    });
  });

  // ---------------------------------------------------------------------------
  // Test 7: limita nombre a 255 caracteres
  // ---------------------------------------------------------------------------
  it('limita nombre a 255 caracteres', () => {
    // GIVEN — formulario renderizado
    render(<AseguradoFieldsWrapper />);

    // WHEN — inspeccionamos el input de nombre del asegurado

    // THEN — el atributo maxLength del input es 255 (limitación en frontend, BR-004)
    const nombreInput = screen.getByLabelText(/nombre del asegurado/i);
    expect(nombreInput).toHaveAttribute('maxLength', '255');
  });
});
