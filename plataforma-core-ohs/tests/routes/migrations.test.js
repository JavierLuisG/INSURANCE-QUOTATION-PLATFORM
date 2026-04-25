'use strict';

const path = require('path');
const fs = require('fs');

const MIGRATIONS_DIR = path.resolve(__dirname, '../../src/migrations');

describe('migrations structure', () => {
  const migrationFiles = fs
    .readdirSync(MIGRATIONS_DIR)
    .filter((f) => f.endsWith('.js'))
    .sort();

  test('at least one migration file exists', () => {
    // GIVEN / WHEN — reading migrations directory
    // THEN
    expect(migrationFiles.length).toBeGreaterThan(0);
  });

  migrationFiles.forEach((file) => {
    it(`test_migration_structure — ${file} exports up and down`, () => {
      // GIVEN
      const filePath = path.join(MIGRATIONS_DIR, file);

      // WHEN
      const migration = require(filePath);

      // THEN
      expect(typeof migration.up).toBe('function');
      expect(typeof migration.down).toBe('function');
    });
  });
});
