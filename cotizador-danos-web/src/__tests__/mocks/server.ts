import { setupServer } from 'msw/node';
import { handlers } from './handlers';

/**
 * Servidor MSW para interceptar llamadas HTTP en los tests unitarios (Node env).
 * Se inicia/resetea/cierra desde src/__tests__/setup.ts.
 */
export const server = setupServer(...handlers);
