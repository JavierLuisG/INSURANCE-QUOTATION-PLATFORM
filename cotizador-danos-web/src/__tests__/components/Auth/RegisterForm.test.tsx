import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import RegisterForm from '@/components/Auth/RegisterForm';

// ─── Mock del hook useAuth ───────────────────────────────────────────────────
vi.mock('@/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '@/hooks/useAuth';

// ─── Labels de roles (espejo de ROLE_LABELS en RegisterForm.tsx) ──────────────
const ROLE_LABELS: Record<string, string> = {
  creador_cotizacion: 'Creador de cotización',
  agente_ventas: 'Agente de ventas',
  vendedor: 'Vendedor',
  administrador_ventas: 'Administrador de ventas',
  editor_cotizaciones: 'Editor de cotizaciones',
};

// ─── Helpers ─────────────────────────────────────────────────────────────────
const mockUseAuth = useAuth as ReturnType<typeof vi.fn>;

function buildUseAuthMock(overrides?: Partial<ReturnType<typeof useAuth>>) {
  return {
    token: null,
    role: null,
    email: null,
    isAuthenticated: false,
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    ...overrides,
  };
}

// Helpers de acceso al DOM — centralizados para reflejar el HTML real del componente.
// El label del email dice "Correo electrónico", la contraseña "Contraseña", el rol "Rol".
const getEmailInput = () => screen.getByLabelText(/correo electrónico/i);
const getPasswordInput = () => screen.getByLabelText(/^contraseña$/i);
const getRoleSelect = () => screen.getByRole('combobox', { name: /^rol$/i });
const getSubmitButton = () => screen.getByRole('button', { name: /crear cuenta/i });

// ─── Setup ───────────────────────────────────────────────────────────────────
beforeEach(() => {
  vi.clearAllMocks();
});

// ─── Suite ───────────────────────────────────────────────────────────────────
describe('RegisterForm', () => {
  // ── Render ─────────────────────────────────────────────────────────────────
  it('renderiza campos de email, password y selector de rol', () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());

    // WHEN
    render(<RegisterForm onSuccess={vi.fn()} />);

    // THEN
    expect(getEmailInput()).toBeInTheDocument();
    expect(getPasswordInput()).toBeInTheDocument();
    expect(getRoleSelect()).toBeInTheDocument();
    expect(getSubmitButton()).toBeInTheDocument();
  });

  // ── Opciones del selector de rol ──────────────────────────────────────────
  it('muestra todos los roles válidos en el selector', () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());

    // WHEN
    render(<RegisterForm onSuccess={vi.fn()} />);

    // THEN — cada rol debe aparecer como option con el label legible del componente
    const select = getRoleSelect();
    Object.values(ROLE_LABELS).forEach((label) => {
      expect(within(select).getByText(label)).toBeInTheDocument();
    });
  });

  // ── Validación — complejidad de contraseña ────────────────────────────────
  it('muestra error de complejidad de contraseña', async () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());
    render(<RegisterForm onSuccess={vi.fn()} />);

    // WHEN — contraseña sin mayúscula ni dígito (no cumple el regex de registerSchema)
    await userEvent.type(getPasswordInput(), 'simpleclave');
    await userEvent.click(getSubmitButton());

    // THEN — error message from registerSchema: "La contraseña debe tener al menos 8 caracteres, una mayúscula..."
    await waitFor(() => {
      expect(
        screen.getByText(/al menos 8 caracteres, una mayúscula/i)
      ).toBeInTheDocument();
    });
  });

  // ── Validación — email inválido ───────────────────────────────────────────
  it('muestra error de validación para email inválido', async () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());
    render(<RegisterForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'no-valido');
    await userEvent.click(getSubmitButton());

    // THEN — error message from registerSchema: "Ingresa un correo electrónico válido"
    await waitFor(() => {
      expect(
        screen.getByText(/ingresa un correo electrónico válido/i)
      ).toBeInTheDocument();
    });
  });

  // ── Submit correcto ───────────────────────────────────────────────────────
  it('llama a register con los datos correctos al enviar', async () => {
    // GIVEN
    const registerMock = vi.fn().mockResolvedValue('Usuario registrado exitosamente');
    mockUseAuth.mockReturnValue(buildUseAuthMock({ register: registerMock }));
    render(<RegisterForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'nuevo@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.selectOptions(getRoleSelect(), 'agente_ventas');
    await userEvent.click(getSubmitButton());

    // THEN
    await waitFor(() => {
      expect(registerMock).toHaveBeenCalledWith({
        email: 'nuevo@example.com',
        password: 'Password1',
        role: 'agente_ventas',
      });
    });
  });

  // ── Mensaje de éxito ──────────────────────────────────────────────────────
  it('muestra mensaje de éxito tras registro exitoso', async () => {
    // GIVEN
    const successMessage = 'Usuario registrado exitosamente';
    const registerMock = vi.fn().mockResolvedValue(successMessage);
    mockUseAuth.mockReturnValue(buildUseAuthMock({ register: registerMock }));
    render(<RegisterForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'nuevo@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.selectOptions(getRoleSelect(), 'agente_ventas');
    await userEvent.click(getSubmitButton());

    // THEN
    await waitFor(() => {
      expect(screen.getByText(successMessage)).toBeInTheDocument();
    });
  });

  // ── Error del servidor ────────────────────────────────────────────────────
  it('muestra error del servidor cuando registro falla', async () => {
    // GIVEN — error con estructura de respuesta Axios (error.response.data.message)
    const axiosError = Object.assign(new Error('Request failed'), {
      response: { data: { message: 'El correo ya está registrado' } },
    });
    const registerMock = vi.fn().mockRejectedValue(axiosError);
    mockUseAuth.mockReturnValue(buildUseAuthMock({ register: registerMock }));
    render(<RegisterForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'existente@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.selectOptions(getRoleSelect(), 'agente_ventas');
    await userEvent.click(getSubmitButton());

    // THEN — debe mostrar el mensaje proveniente de error.response.data.message
    await waitFor(() => {
      expect(screen.getByText('El correo ya está registrado')).toBeInTheDocument();
    });
  });
});
