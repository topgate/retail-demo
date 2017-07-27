# Retail Streaming Dataflow Demo

## SetUp

### Cloud Storage

```
gsutil mb -l asia-northeast1 gs://{your-project-id}
gsutil cp "./src/main/resources/*.csv" gs://{your-project-id}/dataflow/resources/
```

### Cloud Pub/Sub

```
gcloud beta pubsub topics create receipt
gcloud beta pubsub topics create heatmap
gcloud beta pubsub topics create trend
gcloud beta pubsub topics create console
```

### BigQuery

```
bq mk --project {your-project-id} record
bq mk --project {your-project-id} --schema scripts/bqschema.json -t record.receipt
```

### Dataflow

```
gcloud auth application-default login
```

## Deploy

### Dataflow Streaming Mode Deploy

```
./scripts/deploy.sh
```