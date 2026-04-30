'use client';

import { Controller } from 'react-hook-form';
import type { Control, FieldErrors } from 'react-hook-form';
import type { DatosGeneralesFormValues } from '@/lib/schemas/datosGenerales.schema';

interface AseguradoFieldsProps {
  control: Control<DatosGeneralesFormValues>;
  errors: FieldErrors<DatosGeneralesFormValues>;
}

/**
 * Subcampos de Nombre del Asegurado y RFC.
 *
 * Usa Controller de React Hook Form para validación inline en tiempo real.
 * El campo nombre tiene maxLength 255 aplicado también a nivel de input (BR-004).
 */
export default function AseguradoFields({ control, errors }: AseguradoFieldsProps) {
  return (
    <fieldset className="flex flex-col gap-4">
      <legend className="text-sm font-semibold text-gray-700">Datos del Asegurado</legend>

      {/* Nombre del Asegurado */}
      <div className="flex flex-col gap-1">
        <label htmlFor="nombreAsegurado" className="text-sm font-medium text-gray-700">
          Nombre del Asegurado
        </label>
        <Controller
          name="nombreAsegurado"
          control={control}
          render={({ field }) => (
            <input
              {...field}
              id="nombreAsegurado"
              type="text"
              maxLength={255}
              placeholder="Juan Pérez García"
              aria-describedby={errors.nombreAsegurado ? 'nombreAsegurado-error' : undefined}
              className={`rounded-md border px-3 py-2 text-sm shadow-sm
                          placeholder:text-gray-400 focus:outline-none focus:ring-1
                          ${
                            errors.nombreAsegurado
                              ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                              : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500'
                          }`}
            />
          )}
        />
        {errors.nombreAsegurado && (
          <p id="nombreAsegurado-error" className="text-sm text-red-600" role="alert">
            {errors.nombreAsegurado.message}
          </p>
        )}
      </div>

      {/* RFC del Asegurado */}
      <div className="flex flex-col gap-1">
        <label htmlFor="rfcAsegurado" className="text-sm font-medium text-gray-700">
          RFC
        </label>
        <Controller
          name="rfcAsegurado"
          control={control}
          render={({ field }) => (
            <input
              {...field}
              id="rfcAsegurado"
              type="text"
              maxLength={13}
              placeholder="PEPJ8003261G0"
              aria-describedby={errors.rfcAsegurado ? 'rfcAsegurado-error' : undefined}
              className={`rounded-md border px-3 py-2 text-sm font-mono shadow-sm
                          placeholder:text-gray-400 focus:outline-none focus:ring-1
                          ${
                            errors.rfcAsegurado
                              ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                              : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500'
                          }`}
            />
          )}
        />
        {errors.rfcAsegurado && (
          <p id="rfcAsegurado-error" className="text-sm text-red-600" role="alert">
            {errors.rfcAsegurado.message}
          </p>
        )}
      </div>
    </fieldset>
  );
}
