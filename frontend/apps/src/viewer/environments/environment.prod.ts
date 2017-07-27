export const environment = {
  production: true,
  // https://console.firebase.google.com/project/retail-dataflow-demo/overview
  firebase: {
    apiKey: 'AIzaSyAfr6pk-82unxFOArs6hlv0dIXfEZ4PiH8',
    authDomain: 'retail-dataflow-demo.firebaseapp.com',
    databaseURL: 'https://retail-dataflow-demo.firebaseio.com',
    projectId: 'retail-dataflow-demo',
    storageBucket: 'retail-dataflow-demo.appspot.com',
    messagingSenderId: '306439303332'
  },
  database: {
    stream: '/stream/v0a',
    trend: '/trend/v0',
  }
};
