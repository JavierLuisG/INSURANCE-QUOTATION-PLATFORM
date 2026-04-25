'use strict';

require('dotenv').config();

const config = {
  mongodb: {
    url: process.env.MONGODB_URI || 'mongodb://localhost:27017/plataforma-core-ohs-mock',
    options: {},
  },
  migrationsDir: require('path').join(__dirname, 'src', 'migrations'),
  changelogCollectionName: 'changelog',
  migrationFileExtension: '.js',
  useFileHash: false,
  moduleSystem: 'commonjs',
};

module.exports = config;
