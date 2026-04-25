'use strict';

const mongoose = require('mongoose');

async function connectDatabase() {
  const uri = process.env.MONGODB_URI;

  if (!uri) {
    throw new Error('MONGODB_URI no está configurada en las variables de entorno');
  }

  await mongoose.connect(uri);
  console.log(`MongoDB conectado: ${mongoose.connection.host}`);
}

module.exports = { connectDatabase };
