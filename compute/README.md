# Demo Streaming Creator

## SetUp

```
mvn package
gsutil cp target/batchfile/storemock-package.zip gs://{your-project-id}/compute/storemock-package.zip
gsutil cp scripts/startup.sh gs://retail-dataflow-demo/compute/
gcloud compute instance-templates create demo-streaming-creator-template --image-family ubuntu-1704 --image-project ubuntu-os-cloud --machine-type f1-micro --no-restart-on-failure --maintenance-policy "TERMINATE" --preemptible --metadata startup-script-url=gs://{your-project-id}/compute/startup.sh
gcloud compute instance-groups managed create demo-streaming-creator-group --size 1 --template demo-streaming-creator-template --base-instance-name demo-streaming-creator --zone asia-northeast1-b
```

## Deploy

```
mvn package
gsutil cp target/batchfile/storemock-package.zip gs://{your-project-id}/compute/storemock-package.zip
```