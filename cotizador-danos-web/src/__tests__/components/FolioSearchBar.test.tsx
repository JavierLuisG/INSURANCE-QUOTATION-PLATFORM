import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FolioSearchBar from '@/components/Cotizador/FolioSearchBar';

describe('FolioSearchBar', () => {
  // ---------------------------------------------------------------------------
  // Test 1: llama onLoad con folio válido al presionar Cargar
  // ---------------------------------------------------------------------------
  it('llama onLoad con folio válido al presionar Cargar', async () => {
    // GIVEN — componente renderizado con un spy en onLoad
    const onLoad = vi.fn();
    const user = userEvent.setup();
    render(<FolioSearchBar onLoad={onLoad} />);

    const input = screen.getByRole('textbox', { name: /buscar cotización por folio/i });
    const boton = screen.getByRole('button', { name: /cargar/i });

    // WHEN — el usuario escribe un folio válido y presiona el botón
    await user.type(input, 'COT-2026-000001');
    await user.click(boton);

    // THEN — onLoad recibe el folio exacto y no se muestra ningún error
    expect(onLoad).toHaveBeenCalledOnce();
    expect(onLoad).toHaveBeenCalledWith('COT-2026-000001');
    expect(screen.queryByRole('alert')).toBeNull();
  });

  // ---------------------------------------------------------------------------
  // Test 2: muestra error de formato para folio mal formado
  // ---------------------------------------------------------------------------
  it('muestra error de formato para folio mal formado', async () => {
    // GIVEN — componente renderizado con un spy en onLoad
    const onLoad = vi.fn();
    const user = userEvent.setup();
    render(<FolioSearchBar onLoad={onLoad} />);

    const input = screen.getByRole('textbox', { name: /buscar cotización por folio/i });
    const boton = screen.getByRole('button', { name: /cargar/i });

    // WHEN — el usuario escribe un folio con formato inválido y presiona Cargar
    await user.type(input, 'ABC-123');
    await user.click(boton);

    // THEN — se muestra el mensaje de error y onLoad no es invocado
    expect(screen.getByRole('alert')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveTextContent(/formato de folio inválido/i);
    expect(onLoad).not.toHaveBeenCalled();
  });
});
