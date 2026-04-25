'use strict';

require('dotenv').config();

const express = require('express');
const cors = require('cors');
const path = require('path');
const { connectDatabase } = require('./config/database');

const subscribersRouter = require('./routes/subscribers');
const agentsRouter = require('./routes/agents');
const businessLinesRouter = require('./routes/businessLines');
const zipCodesRouter = require('./routes/zipCodes');
const catalogsRouter = require('./routes/catalogs');
const tariffsRouter = require('./routes/tariffs');
const foliosRouter = require('./routes/folios');
const mockScenariosRouter = require('./routes/mockScenarios');

async function migrateMongoUp() {
  const { database, up, config } = require('migrate-mongo');

  const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/plataforma-core-ohs-mock';
  const dbName = mongoUri.split('/').pop().split('?')[0];

  config.set({
    mongodb: {
      url: mongoUri,
      databaseName: dbName,
      options: {},
    },
    migrationsDir: path.join(__dirname, 'migrations'),
    changelogCollectionName: 'changelog',
    migrationFileExtension: '.js',
    useFileHash: false,
    moduleSystem: 'commonjs',
  });

  const { db, client } = await database.connect();
  try {
    const migrated = await up(db, client);
    if (migrated.length > 0) {
      console.log(`migrate-mongo: ${migrated.length} migración(es) aplicada(s):`, migrated);
    } else {
      console.log('migrate-mongo: No pending migrations');
    }
  } finally {
    await client.close();
  }
}

async function startServer() {
  try {
    await connectDatabase();
    await migrateMongoUp();

    const app = express();

    app.use(cors({ origin: '*' }));
    app.use(express.json());

    app.get('/health', (req, res) => {
      return res.status(200).json({
        status: 'UP',
        service: 'plataforma-core-ohs-mock',
        timestamp: new Date().toISOString(),
      });
    });

    app.use('/v1', subscribersRouter);
    app.use('/v1', agentsRouter);
    app.use('/v1', businessLinesRouter);
    app.use('/v1', zipCodesRouter);
    app.use('/v1', catalogsRouter);
    app.use('/v1', tariffsRouter);
    app.use('/v1', foliosRouter);
    app.use('/', mockScenariosRouter);

    app.use((req, res) => {
      return res.status(404).json({ message: 'Endpoint no encontrado', code: 'NOT_FOUND' });
    });

    const PORT = parseInt(process.env.PORT || '3001', 10);

    const server = app.listen(PORT, () => {
      console.log(`plataforma-core-ohs-mock escuchando en puerto ${PORT}`);
    });

    server.on('error', (err) => {
      if (err.code === 'EADDRINUSE') {
        console.error(`Puerto ${PORT} ya está en uso`);
        process.exit(1);
      }
      throw err;
    });
  } catch (err) {
    console.error('Error al iniciar el servidor:', err.message);
    process.exit(1);
  }
}

startServer();
