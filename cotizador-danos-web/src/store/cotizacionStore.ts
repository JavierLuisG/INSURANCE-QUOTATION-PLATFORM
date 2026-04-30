import { create } from 'zustand';
import type { CotizacionResponse } from '@/lib/schemas/cotizacion.schema';

interface CotizacionStore {
  cotizacion: CotizacionResponse | null;
  loading: boolean;
  error: string | null;

  setCotizacion: (cotizacion: CotizacionResponse) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  reset: () => void;
}

export const useCotizacionStore = create<CotizacionStore>((set) => ({
  cotizacion: null,
  loading: false,
  error: null,

  setCotizacion: (cotizacion) => set({ cotizacion }),
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
  reset: () => set({ cotizacion: null, loading: false, error: null }),
}));
