'use client';

import { Controller } from 'react-hook-form';
import type { Control, FieldErrors } from 'react-hook-form';
import type { DatosGeneralesFormValues } from '@/lib/schemas/datosGenerales.schema';
import type { CatalogoItem } from '@/lib/schemas/cotizacion.schema';

interface CatalogosProps {
  tiposSeguros: CatalogoItem[];
  monedas: CatalogoItem[];
  canalesVenta: CatalogoItem[];
  loading: boolean;
  error: string | null;
}

interface ParametrosSelectorProps {
  control: Control<DatosGeneralesFormValues>;
  errors: FieldErrors<DatosGeneralesFormValues>;
  catalogs: CatalogosProps;
}

interface SelectFieldProps {
  id: string;
  label: string;
  name: keyof Pick<DatosGeneralesFormValues, 'tipoSeguroId' | 'monedaId' | 'canalVentaId'>;
  items: CatalogoItem[];
  loading: boolean;
  hasError: boolean;
  control: Control<DatosGeneralesFormValues>;
  fieldError?: string;
  catalogError: string | null;
}

function SelectField({
  id,
  label,
  name,
  items,
  loading,
  hasError,
  control,
  fieldError,
  catalogError,
}: SelectFieldProps) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-sm font-medium text-gray-700">
        {label}
      </label>

      <div className="relative">
        <Controller
          name={name}
          control={control}
          render={({ field }) => (
            <select
              {...field}
              id={id}
              disabled={loading || !!catalogError}
              aria-describedby={hasError ? `${id}-error` : undefined}
              aria-busy={loading}
              className={`w-full appearance-none rounded-md border px-3 py-2 text-sm shadow-sm
                          focus:outline-none focus:ring-1 disabled:cursor-not-allowed
                          disabled:bg-gray-100 disabled:text-gray-400
                          ${
                            hasError
                              ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                              : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500'
                          }`}
            >
              {loading ? (
                <option value="">Cargando...</option>
              ) : catalogError ? (
                <option value="">No disponible</option>
              ) : items.length === 0 ? (
                <option value="">No hay opciones disponibles</option>
              ) : (
                <>
                  <option value="">Seleccione una opción</option>
                  {items.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.nombre}
                    </option>
                  ))}
                </>
              )}
            </select>
          )}
        />

        {loading && (
          <div
            className="pointer-events-none absolute inset-y-0 right-8 flex items-center"
            aria-hidden="true"
          >
            <svg
              className="h-4 w-4 animate-spin text-gray-400"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
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
          </div>
        )}
      </div>

      {catalogError && (
        <p className="text-sm text-orange-600" role="alert">
          No se pudieron cargar las opciones. Intente de nuevo más tarde.
        </p>
      )}

      {fieldError && !catalogError && (
        <p id={`${id}-error`} className="text-sm text-red-600" role="alert">
          {fieldError}
        </p>
      )}
    </div>
  );
}

/**
 * Tres dropdowns: Tipo de Seguro, Moneda y Canal de Venta.
 *
 * Cada uno muestra un spinner de carga mientras se obtiene su catálogo
 * y un mensaje de error individual si el servicio de catálogos falla.
 * Se deshabilitan mientras cargan o ante error del servicio (BR-005, CRITERIO-4.3).
 */
export default function ParametrosSelector({ control, errors, catalogs }: ParametrosSelectorProps) {
  const { tiposSeguros, monedas, canalesVenta, loading, error } = catalogs;

  return (
    <fieldset className="flex flex-col gap-4">
      <legend className="text-sm font-semibold text-gray-700">Parámetros de Cotización</legend>

      <SelectField
        id="tipoSeguroId"
        label="Tipo de Seguro"
        name="tipoSeguroId"
        items={tiposSeguros}
        loading={loading}
        hasError={!!errors.tipoSeguroId}
        control={control}
        fieldError={errors.tipoSeguroId?.message}
        catalogError={error}
      />

      <SelectField
        id="monedaId"
        label="Moneda"
        name="monedaId"
        items={monedas}
        loading={loading}
        hasError={!!errors.monedaId}
        control={control}
        fieldError={errors.monedaId?.message}
        catalogError={error}
      />

      <SelectField
        id="canalVentaId"
        label="Canal de Venta"
        name="canalVentaId"
        items={canalesVenta}
        loading={loading}
        hasError={!!errors.canalVentaId}
        control={control}
        fieldError={errors.canalVentaId?.message}
        catalogError={error}
      />
    </fieldset>
  );
}
