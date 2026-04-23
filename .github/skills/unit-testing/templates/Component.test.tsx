// Plantilla para tests de componentes React con Vitest + Testing Library
// Copiar a: cotizador-danos-web/__tests__/components/<Component>.test.tsx

import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi, describe, it, expect, beforeEach } from 'vitest';
// import FeatureComponent from '@/components/Feature/FeatureComponent';

// Mock de servicios externos
vi.mock('@/lib/services/featureService', () => ({
  getFeature: vi.fn().mockResolvedValue({ folio: 'COT-2026-000001', estado: 'COMPLETA' }),
}));

// Mock del Zustand store si el componente lo consume
vi.mock('@/store/quotationStore', () => ({
  useQuotationStore: () => ({
    folio: 'COT-2026-000001',
    setFolio: vi.fn(),
  }),
}));

describe('FeatureComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ─── Happy Path ──────────────────────────────────────────────────────────

  it('renders the component title', () => {
    // render(<FeatureComponent title="Mi Feature" />);
    // expect(screen.getByRole('heading', { name: /mi feature/i })).toBeInTheDocument();
  });

  it('displays data after loading', async () => {
    // render(<FeatureComponent />);
    // await waitFor(() => {
    //   expect(screen.getByText('COT-2026-000001')).toBeInTheDocument();
    // });
  });

  // ─── Error Path ─────────────────────────────────────────────────────────

  it('shows error message when fetch fails', async () => {
    // const { getFeature } = await import('@/lib/services/featureService');
    // vi.mocked(getFeature).mockRejectedValueOnce(new Error('Network error'));
    // render(<FeatureComponent />);
    // await waitFor(() => {
    //   expect(screen.getByText(/error/i)).toBeInTheDocument();
    // });
  });

  // ─── User Interactions ───────────────────────────────────────────────────

  it('calls onAction when button is clicked', async () => {
    // const onAction = vi.fn();
    // render(<FeatureComponent onAction={onAction} />);
    // await userEvent.click(screen.getByRole('button', { name: /acción/i }));
    // expect(onAction).toHaveBeenCalledOnce();
  });
});
