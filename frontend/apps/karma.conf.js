// Karma configuration file, see link for more information
// https://karma-runner.github.io/0.13/config/configuration-file.html

module.exports = function (config) {
  let testEntry;
  switch (config.angularCli.app) {
    case '0':
    case 'viewer':
      testEntry = './src/viewer/test.ts';
      break;
    case '1':
    case 'webstore':
      testEntry = './src/webstore/test.ts';
      break;
    default:
      testEntry = './src/viewer/test.ts';
      break;
  }

  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular/cli'],
    plugins: [
      require('karma-jasmine'),
      require('karma-nightmare'),
      require('karma-spec-reporter'),
      require('karma-coverage-istanbul-reporter'),
      require('@angular/cli/plugins/karma')
    ],
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    files: [
      'node_modules/zone.js/dist/long-stack-trace-zone.js',
      'node_modules/zone.js/dist/proxy.js',
      'node_modules/zone.js/dist/sync-test.js',
      'node_modules/zone.js/dist/jasmine-patch.js',
      'node_modules/zone.js/dist/async-test.js',
      'node_modules/zone.js/dist/fake-async-test.js',
      {
        pattern: testEntry,
        watched: false
      },
    ],
    preprocessors: {
      [testEntry]: ['@angular/cli'],
    },
    mime: {
      'text/x-typescript': ['ts', 'tsx']
    },
    coverageIstanbulReporter: {
      reports: ['text', 'lcovonly'],
      fixWebpackSourcePaths: true
    },
    angularCli: {
      environment: 'dev'
    },
    reporters: config.angularCli && config.angularCli.codeCoverage ? ['spec', 'coverage-istanbul'] : ['spec'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Nightmare'],
    singleRun: false,
    nightmareOptions: {
      show: true
    }
  });
};
