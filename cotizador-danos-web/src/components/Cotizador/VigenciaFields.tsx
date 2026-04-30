'use client';

import { Controller } from 'react-hook-form';
import type { Control, FieldErrors } from 'react-hook-form';
import type { DatosGeneralesFormValues } from '@/lib/schemas/datosGenerales.schema';

interface VigenciaFieldsProps {
  control: Control<DatosGeneralesFormValues>;
  errors: FieldErrors<DatosGeneralesFormValues>;
}

/**
 * Campos de fecha inicio y fin de vigencia.
 *
 * Usa input type="date" (produce valores ISO 8601 YYYY-MM-DD nativamente).
 * La validación de rango cruzado (fechaFin >= fechaInicio) está definida
 * como refinement en datosGeneralesSchema y se muestra debajo de fechaFinVigencia.
 */
export default function VigenciaFields({ control, errors }: VigenciaFieldsProps) {
  return (
    <fieldset className="flex flex-col gap-4">
      <legend className="text-sm font-semibold text-gray-700">Vigencia</legend>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {/* Fecha Inicio Vigencia */}
        <div className="flex flex-col gap-1">
          <label htmlFor="fechaInicioVigencia" className="text-sm font-medium text-gray-700">
            Fecha de Inicio
          </label>
          <Controller
            name="fechaInicioVigencia"
            control={control}
            render={({ field }) => (
              <input
                {...field}
                id="fechaInicioVigencia"
                type="date"
                aria-describedby={
                  errors.fechaInicioVigencia ? 'fechaInicio-error' : undefined
                }
                className={`rounded-md border px-3 py-2 text-sm shadow-sm focus:outline-none
                            focus:ring-1
                            ${
                              errors.fechaInicioVigencia
                                ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                                : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500'
                            }`}
              />
            )}
          />
          {errors.fechaInicioVigencia && (
            <p id="fechaInicio-error" className="text-sm text-red-600" role="alert">
              {errors.fechaInicioVigencia.message}
            </p>
          )}
        </div>

        {/* Fecha Fin Vigencia */}
        <div className="flex flex-col gap-1">
          <label htmlFor="fechaFinVigencia" className="text-sm font-medium text-gray-700">
            Fecha de Fin
          </label>
          <Controller
            name="fechaFinVigencia"
            control={control}
            render={({ field }) => (
              <input
                {...field}
                id="fechaFinVigencia"
                type="date"
                aria-describedby={
                  errors.fechaFinVigencia ? 'fechaFin-error' : undefined
                }
                className={`rounded-md border px-3 py-2 text-sm shadow-sm focus:outline-none
                            focus:ring-1
                            ${
                              errors.fechaFinVigencia
                                ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                                : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500'
                            }`}
              />
            )}
          />
          {errors.fechaFinVigencia && (
            <p id="fechaFin-error" className="text-sm text-red-600" role="alert">
              {errors.fechaFinVigencia.message}
            </p>
          )}
        </div>
      </div>
    </fieldset>
  );
}
