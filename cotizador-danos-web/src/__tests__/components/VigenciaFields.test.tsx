import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import VigenciaFields from '@/components/Cotizador/VigenciaFields';
import {
  datosGeneralesSchema,
  type DatosGeneralesFormValues,
} from '@/lib/schemas/datosGenerales.schema';

// ---------------------------------------------------------------------------
// Wrapper de prueba
// ---------------------------------------------------------------------------
interface WrapperProps {
  onSubmit?: (values: DatosGeneralesFormValues) => void;
  defaultValues?: Partial<DatosGeneralesFormValues>;
}

function VigenciaFieldsWrapper({ onSubmit = vi.fn(), defaultValues = {} }: WrapperProps) {
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
      ...defaultValues,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <VigenciaFields control={control} errors={errors} />
      <button type="submit">Guardar</button>
    </form>
  );
}

describe('VigenciaFields', () => {
  // ---------------------------------------------------------------------------
  // Test 11: muestra error cuando fechaFin es anterior a fechaInicio
  // ---------------------------------------------------------------------------
  it('muestra error cuando fechaFin es anterior a fechaInicio', async () => {
    // GIVEN — formulario con fecha inicio posterior a fecha fin
    const user = userEvent.setup();
    render(
      <VigenciaFieldsWrapper
        defaultValues={{
          fechaInicioVigencia: '2026-05-31',
          fechaFinVigencia: '2026-05-01',
        }}
      />
    );

    // WHEN — el usuario envía el formulario con el rango inválido
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — aparece el mensaje de error de rango de vigencia
    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument();
      expect(screen.getByRole('alert')).toHaveTextContent(
        /fecha de fin de vigencia no puede ser anterior/i
      );
    });
  });

  // ---------------------------------------------------------------------------
  // Test 12: acepta fechas iguales (vigencia de un día)
  // ---------------------------------------------------------------------------
  it('acepta fechas iguales (vigencia de un día)', async () => {
    // GIVEN — formulario con fechaInicio === fechaFin (vigencia de un día, CRITERIO-5.4)
    const onSubmit = vi.fn();
    const user = userEvent.setup();
    render(
      <VigenciaFieldsWrapper
        onSubmit={onSubmit}
        defaultValues={{
          fechaInicioVigencia: '2026-05-01',
          fechaFinVigencia: '2026-05-01',
        }}
      />
    );

    // WHEN — el usuario envía el formulario
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — el formulario es válido, no se muestra error y onSubmit fue llamado
    await waitFor(() => {
      expect(screen.queryByRole('alert')).toBeNull();
      expect(onSubmit).toHaveBeenCalledOnce();
    });
  });
});
