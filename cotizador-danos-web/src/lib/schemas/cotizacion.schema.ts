import { z } from 'zod';

/**
 * Regex RFC SAT — personas físicas (13 chars) y morales (12 chars).
 * Formato: 3-4 letras + 6 dígitos fecha + 2-3 chars homoclave.
 */
export const RFC_REGEX =
  /^([A-ZÑ&]{3,4})(\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01]))([A-Z\d]{2}[A\d])$/;

/**
 * Regex formato de folio de negocio: COT-YYYY-NNNNNN
 */
export const FOLIO_REGEX = /^COT-\d{4}-\d{6}$/;

/**
 * Regex fecha ISO 8601 YYYY-MM-DD
 */
const FECHA_ISO_REGEX = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;

export const estadoValidacionEnum = z.enum(['COMPLETA', 'INCOMPLETA', 'INACTIVA']);

export type EstadoValidacion = z.infer<typeof estadoValidacionEnum>;

/**
 * Schema de la respuesta completa de la API para una cotización.
 */
export const cotizacionSchema = z.object({
  folio: z
    .string()
    .regex(FOLIO_REGEX, 'Formato de folio inválido'),
  estadoValidacion: estadoValidacionEnum,
  nombreAsegurado: z.string().max(255).nullable().optional(),
  rfcAsegurado: z
    .string()
    .regex(RFC_REGEX, 'El formato del RFC introducido no es válido. Por favor, verifique.')
    .nullable()
    .optional(),
  tipoSeguroId: z.string().nullable().optional(),
  monedaId: z.string().nullable().optional(),
  canalVentaId: z.string().nullable().optional(),
  fechaInicioVigencia: z
    .string()
    .regex(FECHA_ISO_REGEX, 'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.')
    .nullable()
    .optional(),
  fechaFinVigencia: z
    .string()
    .regex(FECHA_ISO_REGEX, 'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.')
    .nullable()
    .optional(),
  createdAt: z.string(),
  updatedAt: z.string(),
  version: z.number(),
});

export type CotizacionResponse = z.infer<typeof cotizacionSchema>;

/**
 * Schema del request para actualizar una cotización (PUT).
 * Todos los campos son opcionales en la llamada; version es obligatorio para
 * el versionado optimista.
 */
export const cotizacionRequestSchema = z.object({
  nombreAsegurado: z.string().max(255).optional(),
  rfcAsegurado: z
    .string()
    .regex(RFC_REGEX, 'El formato del RFC introducido no es válido. Por favor, verifique.')
    .optional(),
  tipoSeguroId: z.string().optional(),
  monedaId: z.string().optional(),
  canalVentaId: z.string().optional(),
  fechaInicioVigencia: z
    .string()
    .regex(FECHA_ISO_REGEX, 'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.')
    .optional(),
  fechaFinVigencia: z
    .string()
    .regex(FECHA_ISO_REGEX, 'Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD.')
    .optional(),
  version: z.number(),
});

export type CotizacionRequest = z.infer<typeof cotizacionRequestSchema>;

/**
 * Schema para un ítem de catálogo devuelto por la API de catálogos.
 */
export const catalogoItemSchema = z.object({
  id: z.string(),
  nombre: z.string(),
  activo: z.boolean().optional(),
});

export type CatalogoItem = z.infer<typeof catalogoItemSchema>;
