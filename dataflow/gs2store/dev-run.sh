mvn compile exec:java -Dexec.mainClass=jp.co.topgate.StorageToDatastore -Dexec.args="--project=retail-dataflow-demo --stagingLocation=gs://staging.retail-dataflow-demo.appspot.com/gs2store/staging/ --runner=BlockingDataflowPipelineRunner"