import { z } from 'zod';

export const VALID_ROLES = [
  'creador_cotizacion',
  'agente_ventas',
  'vendedor',
  'administrador_ventas',
  'editor_cotizaciones',
] as const;

export const roleEnum = z.enum(VALID_ROLES);

export type Role = z.infer<typeof roleEnum>;

export const loginSchema = z.object({
  email: z.string().email('Ingresa un correo electrónico válido'),
  password: z.string().min(8, 'La contraseña debe tener al menos 8 caracteres'),
});

export type LoginFormData = z.infer<typeof loginSchema>;

export const registerSchema = z.object({
  email: z.string().email('Ingresa un correo electrónico válido'),
  password: z
    .string()
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
      'La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número'
    ),
  role: roleEnum,
});

export type RegisterFormData = z.infer<typeof registerSchema>;

export const loginResponseSchema = z.object({
  token: z.string(),
  expiresIn: z.number(),
});

export type LoginResponse = z.infer<typeof loginResponseSchema>;

export const registerResponseSchema = z.object({
  message: z.string(),
});

export type RegisterResponse = z.infer<typeof registerResponseSchema>;
