// Plantilla para tests de hooks React con Vitest + Testing Library
// Copiar a: cotizador-danos-web/__tests__/hooks/use<Feature>.test.ts

import { renderHook, act, waitFor } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';
// import { useFeature } from '@/hooks/useFeature';

// Mock del servicio de API
vi.mock('@/lib/services/featureService', () => ({
  getFeature: vi.fn(),
}));

// Mock del store de Zustand si el hook lo consume
vi.mock('@/store/quotationStore', () => ({
  useQuotationStore: () => ({
    folio: null,
    setFolio: vi.fn(),
  }),
}));

describe('useFeature', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ─── Happy Path ──────────────────────────────────────────────────────────

  it('returns data after successful fetch', async () => {
    // const { getFeature } = await import('@/lib/services/featureService');
    // vi.mocked(getFeature).mockResolvedValue({ folio: 'COT-2026-000001', estado: 'COMPLETA' });
    //
    // const { result } = renderHook(() => useFeature());
    //
    // await waitFor(() => {
    //   expect(result.current.loading).toBe(false);
    // });
    //
    // expect(result.current.data?.folio).toBe('COT-2026-000001');
    // expect(result.current.error).toBeNull();
  });

  // ─── Error Path ─────────────────────────────────────────────────────────

  it('sets error when fetch fails', async () => {
    // const { getFeature } = await import('@/lib/services/featureService');
    // vi.mocked(getFeature).mockRejectedValue(new Error('API Error'));
    //
    // const { result } = renderHook(() => useFeature());
    //
    // await waitFor(() => {
    //   expect(result.current.loading).toBe(false);
    // });
    //
    // expect(result.current.error).toBeInstanceOf(Error);
    // expect(result.current.data).toBeNull();
  });

  // ─── Edge Cases ─────────────────────────────────────────────────────────

  it('does not fetch when required param is missing', async () => {
    // const { result } = renderHook(() => useFeature(null));
    // await act(async () => {});
    // expect(result.current.loading).toBe(false);
    // expect(result.current.data).toBeNull();
  });
});
