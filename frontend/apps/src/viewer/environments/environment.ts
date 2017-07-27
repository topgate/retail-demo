// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  firebase: {
    apiKey: 'AIzaSyDqhSOkqiQkiTrTRmJzteb5FxNZDC87HBU',
    authDomain: 'retail-dataflow-demo.firebaseapp.com',
    databaseURL: 'https://retail-dataflow-demo.firebaseio.com',
    projectId: 'retail-dataflow-demo',
    storageBucket: 'retail-dataflow-demo.appspot.com',
    messagingSenderId: '882318759698'
  },
  database: {
    stream: '/stream/v0a',
    trend: '/trend/v0',
  }
};
