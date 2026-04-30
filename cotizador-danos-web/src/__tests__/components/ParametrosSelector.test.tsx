import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import ParametrosSelector from '@/components/Cotizador/ParametrosSelector';
import {
  datosGeneralesSchema,
  type DatosGeneralesFormValues,
} from '@/lib/schemas/datosGenerales.schema';
import type { CatalogoItem } from '@/lib/schemas/cotizacion.schema';

// ---------------------------------------------------------------------------
// Fábricas de props para el componente
// ---------------------------------------------------------------------------
const TIPOS_SEGUROS: CatalogoItem[] = [
  { id: 'tipo-001', nombre: 'Automóvil', activo: true },
];
const MONEDAS: CatalogoItem[] = [{ id: 'mon-001', nombre: 'MXN', activo: true }];
const CANALES: CatalogoItem[] = [{ id: 'can-001', nombre: 'Agente', activo: true }];

interface WrapperProps {
  loading: boolean;
  error: string | null;
  tiposSeguros?: CatalogoItem[];
  monedas?: CatalogoItem[];
  canalesVenta?: CatalogoItem[];
}

function ParametrosSelectorWrapper({
  loading,
  error,
  tiposSeguros = [],
  monedas = [],
  canalesVenta = [],
}: WrapperProps) {
  const {
    control,
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
    <ParametrosSelector
      control={control}
      errors={errors}
      catalogs={{ tiposSeguros, monedas, canalesVenta, loading, error }}
    />
  );
}

describe('ParametrosSelector', () => {
  // ---------------------------------------------------------------------------
  // Test 8: muestra spinner mientras carga catálogos
  // ---------------------------------------------------------------------------
  it('muestra spinner mientras carga catálogos', () => {
    // GIVEN — catálogos en estado de carga (loading=true)
    render(<ParametrosSelectorWrapper loading={true} error={null} />);

    // WHEN — el componente se renderiza en estado loading

    // THEN — los selectores muestran "Cargando..." y el spinner SVG está presente
    const options = screen.getAllByText(/cargando/i);
    expect(options.length).toBeGreaterThan(0);

    // El spinner es un SVG con aria-hidden; verificamos que los selects están deshabilitados
    const selects = screen.getAllByRole('combobox');
    selects.forEach((select) => expect(select).toBeDisabled());
  });

  // ---------------------------------------------------------------------------
  // Test 9: muestra opciones del catálogo al cargar correctamente
  // ---------------------------------------------------------------------------
  it('muestra opciones del catálogo al cargar correctamente', () => {
    // GIVEN — catálogos cargados exitosamente (loading=false, error=null)
    render(
      <ParametrosSelectorWrapper
        loading={false}
        error={null}
        tiposSeguros={TIPOS_SEGUROS}
        monedas={MONEDAS}
        canalesVenta={CANALES}
      />
    );

    // WHEN — el componente se renderiza con datos

    // THEN — las opciones de catálogo aparecen en los selectores correspondientes
    expect(screen.getByRole('option', { name: 'Automóvil' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'MXN' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'Agente' })).toBeInTheDocument();
  });

  // ---------------------------------------------------------------------------
  // Test 10: muestra error cuando el servicio de catálogos falla
  // ---------------------------------------------------------------------------
  it('muestra error cuando el servicio de catálogos falla', () => {
    // GIVEN — catálogos con error (el servicio no estuvo disponible)
    render(
      <ParametrosSelectorWrapper
        loading={false}
        error="No se pudieron cargar las opciones. Intente de nuevo más tarde."
      />
    );

    // WHEN — el componente se renderiza con error de catálogo

    // THEN — se muestran los mensajes de error y los selects están deshabilitados
    const alerts = screen.getAllByRole('alert');
    expect(alerts.length).toBeGreaterThan(0);
    alerts.forEach((alert) =>
      expect(alert).toHaveTextContent(/no se pudieron cargar las opciones/i)
    );

    const selects = screen.getAllByRole('combobox');
    selects.forEach((select) => expect(select).toBeDisabled());
  });
});
