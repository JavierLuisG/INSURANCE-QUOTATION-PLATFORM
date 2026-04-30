import '@testing-library/jest-dom';
import { afterEach, beforeAll, afterAll } from 'vitest';
import { cleanup } from '@testing-library/react';
import { server } from './mocks/server';

// Levanta el servidor MSW antes de todos los tests
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));

// Limpia handlers específicos de cada test para no contaminar el siguiente
afterEach(() => {
  server.resetHandlers();
  cleanup();
});

// Cierra el servidor al terminar toda la suite
afterAll(() => server.close());
