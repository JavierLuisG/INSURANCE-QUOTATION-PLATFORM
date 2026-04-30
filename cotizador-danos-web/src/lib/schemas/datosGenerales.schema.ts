import { z } from 'zod';
import { RFC_REGEX } from './cotizacion.schema';

const FECHA_ISO_REGEX = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;

/**
 * Subschema para validación del formulario de datos generales.
 *
 * Todos los campos son opcionales al crear (cotización puede quedar INCOMPLETA),
 * pero tienen validaciones cuando se proporcionan.
 *
 * Incluye refinement de rango cruzado de fechas: fechaFin >= fechaInicio.
 */
export const datosGeneralesSchema = z
  .object({
    nombreAsegurado: z
      .string()
      .max(255, 'El nombre no puede superar 255 caracteres')
      .regex(
        /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s'\-.]+$/,
        'El nombre solo puede contener letras, espacios, guiones y apóstrofes'
      )
      .optional()
      .or(z.literal('')),
    rfcAsegurado: z
      .string()
      .regex(
        RFC_REGEX,
        'El formato del RFC introducido no es válido. Por favor, verifique.'
      )
      .optional()
      .or(z.literal('')),
    tipoSeguroId: z
      .string()
      .min(1, 'Por favor, seleccione un valor para Tipo de Seguro')
      .optional()
      .or(z.literal('')),
    monedaId: z
      .string()
      .min(1, 'Por favor, seleccione un valor para Moneda')
      .optional()
      .or(z.literal('')),
    canalVentaId: z
      .string()
      .min(1, 'Por favor, seleccione un valor para Canal de Venta')
      .optional()
      .or(z.literal('')),
    fechaInicioVigencia: z
      .string()
      .regex(
        FECHA_ISO_REGEX,
        'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.'
      )
      .optional()
      .or(z.literal('')),
    fechaFinVigencia: z
      .string()
      .regex(
        FECHA_ISO_REGEX,
        'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.'
      )
      .optional()
      .or(z.literal('')),
    version: z.number(),
  })
  .refine(
    (data) => {
      if (data.fechaInicioVigencia && data.fechaFinVigencia) {
        return data.fechaFinVigencia >= data.fechaInicioVigencia;
      }
      return true;
    },
    {
      message: 'La fecha de fin de vigencia no puede ser anterior a la fecha de inicio',
      path: ['fechaFinVigencia'],
    }
  );

export type DatosGeneralesFormValues = z.infer<typeof datosGeneralesSchema>;
