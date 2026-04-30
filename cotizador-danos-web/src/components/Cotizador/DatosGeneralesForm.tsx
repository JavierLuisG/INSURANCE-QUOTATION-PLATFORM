'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { datosGeneralesSchema, type DatosGeneralesFormValues } from '@/lib/schemas/datosGenerales.schema';
import { useCotizacion } from '@/hooks/useCotizacion';
import { useCatalogos } from '@/hooks/useCatalogos';
import AseguradoFields from './AseguradoFields';
import ParametrosSelector from './ParametrosSelector';
import VigenciaFields from './VigenciaFields';
import type { CotizacionResponse } from '@/lib/schemas/cotizacion.schema';

interface DatosGeneralesFormProps {
  folio: string;
  cotizacion: CotizacionResponse | null;
}

/**
 * Formulario unificado de datos generales del cotizador.
 *
 * Integra React Hook Form con resolver Zod (datosGeneralesSchema).
 * Al montar pre-carga los valores de la cotización existente si los hay.
 * El submit llama a `actualizar()` del hook useCotizacion.
 * Muestra feedback de guardado exitoso o el mensaje de error del store.
 */
export default function DatosGeneralesForm({ folio, cotizacion }: DatosGeneralesFormProps) {
  const { actualizar, loading, error } = useCotizacion();
  const catalogs = useCatalogos();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isSubmitSuccessful },
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

  // Pre-cargar valores cuando la cotización cambie (nueva carga o carga por folio)
  useEffect(() => {
    if (cotizacion) {
      reset({
        nombreAsegurado: cotizacion.nombreAsegurado ?? '',
        rfcAsegurado: cotizacion.rfcAsegurado ?? '',
        tipoSeguroId: cotizacion.tipoSeguroId ?? '',
        monedaId: cotizacion.monedaId ?? '',
        canalVentaId: cotizacion.canalVentaId ?? '',
        fechaInicioVigencia: cotizacion.fechaInicioVigencia ?? '',
        fechaFinVigencia: cotizacion.fechaFinVigencia ?? '',
        version: cotizacion.version,
      });
    }
  }, [cotizacion, reset]);

  async function onSubmit(values: DatosGeneralesFormValues) {
    await actualizar(folio, {
      nombreAsegurado: values.nombreAsegurado || undefined,
      rfcAsegurado: values.rfcAsegurado || undefined,
      tipoSeguroId: values.tipoSeguroId || undefined,
      monedaId: values.monedaId || undefined,
      canalVentaId: values.canalVentaId || undefined,
      fechaInicioVigencia: values.fechaInicioVigencia || undefined,
      fechaFinVigencia: values.fechaFinVigencia || undefined,
      version: values.version,
    });
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      noValidate
      className="flex flex-col gap-6 rounded-lg border border-gray-200 bg-white p-6 shadow-sm"
    >
      <h2 className="text-base font-semibold text-gray-900">Datos Generales</h2>

      <AseguradoFields control={control} errors={errors} />
      <ParametrosSelector control={control} errors={errors} catalogs={catalogs} />
      <VigenciaFields control={control} errors={errors} />

      {/* Mensajes de feedback */}
      {error && (
        <div
          className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
          role="alert"
        >
          {error}
        </div>
      )}

      {isSubmitSuccessful && !error && (
        <div
          className="rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700"
          role="status"
        >
          Cambios guardados con éxito
        </div>
      )}

      <div className="flex justify-end">
        <button
          type="submit"
          disabled={loading}
          className="inline-flex items-center gap-2 rounded-md bg-blue-600 px-5 py-2.5
                     text-sm font-medium text-white hover:bg-blue-700 focus:outline-none
                     focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
                     disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading && (
            <svg
              className="h-4 w-4 animate-spin"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
              />
            </svg>
          )}
          {loading ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
}
