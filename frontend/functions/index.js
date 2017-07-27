const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// <PROJECT-ID>.firebaseapp.com からProject IDを抽出
const PROJECT_ID = functions.config().firebase.authDomain.split('.')[0];

const streamDatabaseVersion = '0a';
const trendDatabaseVersion = '0';
const heatmapDatabaseVersion = '0';

exports.streamPubSub = functions.pubsub.topic('console').onPublish((event) => {
    console.log(`event published`);
    // schema: https://firebase.google.com/docs/reference/functions/functions.pubsub.Message
    const {
        data,
        json,
        attributes
    } = event.data;
    const messageBody = Buffer.from(data, 'base64').toString();
    console.log('body');
    console.log(messageBody);
    console.log('attributes');
    console.log(attributes);

    const stream = admin.database().ref(`stream/v${streamDatabaseVersion}`);
    stream.push(json);
    console.log(`database updated`);
});

/**
 * Topic: projects/retail-dataflow-demo/topics/trend
 * Payload
 * ```
 * [{
 *     "productCode": "{商品コード値}", // string
 *     "productName": "{商品名値}", // string
 *     "quantity": {過去1分間の商品の販売数合計値}, // integer
 *     "price": {過去1分間の商品の売り上げ合計値} // integer
 * },]
 * ```
 */
exports.trendPubSub = functions.pubsub.topic('trend').onPublish((event) => {
    console.log(`event published`);
    // schema: https://firebase.google.com/docs/reference/functions/functions.pubsub.Message
    const {
        data,
        json,
        attributes
    } = event.data;
    const messageBody = Buffer.from(data, 'base64').toString();
    console.log('body');
    console.log(messageBody);
    console.log('attributes');
    console.log(attributes);

    const stream = admin.database().ref(`trend/v${trendDatabaseVersion}`);
    stream.set(json);
    console.log(`database updated`);
});

/**
 * Topic: projects/retail-dataflow-demo/topics/heatmap
 * Payload
 * ```
 * [{
 *      "storeCode": "{店舗コード値}", // string
 *      "lat": {店舗緯度値}, // float
 *      "lng": {店舗経度値}, // float
 *      "quantity": {過去1分間の店舗の販売数合計値}, // integer
 *      "price": {過去1分間の店舗の売り上げ合計値} // integer
 * },...]
 * ```
 */
exports.heatmapPubSub = functions.pubsub.topic('heatmap').onPublish((event) => {
    console.log(`event published`);
    // schema: https://firebase.google.com/docs/reference/functions/functions.pubsub.Message
    const {
        data,
        json,
        attributes
    } = event.data;
    const messageBody = Buffer.from(data, 'base64').toString();
    console.log('body');
    console.log(messageBody);
    console.log('attributes');
    console.log(attributes);

    const stream = admin.database().ref(`heatmap/v${heatmapDatabaseVersion}`);
    stream.set(json);
    console.log(`database updated`);
});