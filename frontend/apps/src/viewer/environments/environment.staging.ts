export const environment = {
  production: true,
  // https://console.firebase.google.com/project/retail-dataflow-demo
  firebase: {
    apiKey: 'AIzaSyBh9EXfXXRDTUhsyAw_LEVJpQ_crvDXTaU',
    authDomain: 'retail-dataflow-demo.firebaseapp.com',
    databaseURL: 'https://retail-dataflow-demo.firebaseio.com',
    projectId: 'retail-dataflow-demo',
    storageBucket: 'retail-dataflow-demo.appspot.com',
    messagingSenderId: '1043929951204'
  },
  database: {
    stream: '/stream/v0a',
    trend: '/trend/v0',
  }
};
