import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '@/components/Auth/LoginForm';

// ─── Mock del hook useAuth ───────────────────────────────────────────────────
vi.mock('@/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '@/hooks/useAuth';

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
// El label dice "Correo electrónico", no "Email".
const getEmailInput = () => screen.getByLabelText(/correo electrónico/i);
const getPasswordInput = () => screen.getByLabelText(/contraseña/i);
const getSubmitButton = () => screen.getByRole('button', { name: /iniciar sesión/i });

// ─── Setup ───────────────────────────────────────────────────────────────────
beforeEach(() => {
  vi.clearAllMocks();
});

// ─── Suite ───────────────────────────────────────────────────────────────────
describe('LoginForm', () => {
  // ── Render ─────────────────────────────────────────────────────────────────
  it('renderiza campos de email y password', () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());

    // WHEN
    render(<LoginForm onSuccess={vi.fn()} />);

    // THEN
    expect(getEmailInput()).toBeInTheDocument();
    expect(getPasswordInput()).toBeInTheDocument();
    expect(getSubmitButton()).toBeInTheDocument();
  });

  // ── Validación — campos vacíos ─────────────────────────────────────────────
  it('muestra error de validación para campos vacíos', async () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());
    render(<LoginForm onSuccess={vi.fn()} />);

    // WHEN — submit sin completar ningún campo
    await userEvent.click(getSubmitButton());

    // THEN — RHF debe reportar al menos un error de campo requerido
    await waitFor(() => {
      const errors = screen.getAllByRole('paragraph').filter((el) =>
        el.textContent && el.textContent.trim().length > 0
      );
      expect(errors.length).toBeGreaterThan(0);
    });
  });

  // ── Validación — email con formato inválido ────────────────────────────────
  it('muestra error de validación para email inválido', async () => {
    // GIVEN
    mockUseAuth.mockReturnValue(buildUseAuthMock());
    render(<LoginForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'no-es-un-email');
    await userEvent.click(getSubmitButton());

    // THEN — error message from loginSchema: "Ingresa un correo electrónico válido"
    await waitFor(() => {
      expect(screen.getByText(/ingresa un correo electrónico válido/i)).toBeInTheDocument();
    });
  });

  // ── Submit correcto ────────────────────────────────────────────────────────
  it('llama a login con los datos correctos al enviar', async () => {
    // GIVEN
    const loginMock = vi.fn().mockResolvedValue(undefined);
    mockUseAuth.mockReturnValue(buildUseAuthMock({ login: loginMock }));
    render(<LoginForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'user@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.click(getSubmitButton());

    // THEN
    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith({
        email: 'user@example.com',
        password: 'Password1',
      });
    });
  });

  // ── Callback onSuccess ────────────────────────────────────────────────────
  it('llama a onSuccess cuando login es exitoso', async () => {
    // GIVEN
    const loginMock = vi.fn().mockResolvedValue(undefined);
    const onSuccess = vi.fn();
    mockUseAuth.mockReturnValue(buildUseAuthMock({ login: loginMock }));
    render(<LoginForm onSuccess={onSuccess} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'user@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.click(getSubmitButton());

    // THEN
    await waitFor(() => {
      expect(onSuccess).toHaveBeenCalledOnce();
    });
  });

  // ── Error del servidor ────────────────────────────────────────────────────
  it('muestra error del servidor cuando login falla', async () => {
    // GIVEN
    const loginMock = vi.fn().mockRejectedValue(new Error('Credenciales inválidas'));
    mockUseAuth.mockReturnValue(buildUseAuthMock({ login: loginMock }));
    render(<LoginForm onSuccess={vi.fn()} />);

    // WHEN
    await userEvent.type(getEmailInput(), 'user@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.click(getSubmitButton());

    // THEN — LoginForm muestra el mensaje hardcodeado cuando el login lanza
    await waitFor(() => {
      expect(
        screen.getByText(/credenciales inválidas. verifica tu correo y contraseña/i)
      ).toBeInTheDocument();
    });
  });

  // ── Estado de carga ───────────────────────────────────────────────────────
  it('deshabilita el botón durante la carga', async () => {
    // GIVEN — login que nunca resuelve inmediatamente para capturar el estado intermedio
    let resolveLogin!: () => void;
    const loginMock = vi.fn().mockReturnValue(
      new Promise<void>((resolve) => { resolveLogin = resolve; })
    );
    mockUseAuth.mockReturnValue(buildUseAuthMock({ login: loginMock }));
    render(<LoginForm onSuccess={vi.fn()} />);

    // WHEN — completar el formulario y enviar
    await userEvent.type(getEmailInput(), 'user@example.com');
    await userEvent.type(getPasswordInput(), 'Password1');
    await userEvent.click(getSubmitButton());

    // THEN — mientras la promesa está pendiente el botón debe estar deshabilitado
    await waitFor(() => {
      const button = screen.getByRole('button', { name: /iniciando sesión/i });
      expect(button).toBeDisabled();
    });

    // Cleanup — resolver la promesa para no dejar handles colgados
    resolveLogin();
  });
});
