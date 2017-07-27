mvn compile exec:java -Dexec.mainClass=jp.co.topgate.gretail.pipeline.MainPipeline -Dexec.args="--project=retail-dataflow-demo \
--stagingLocation=gs://retail-dataflow-demo/dataflow/staging/preprocess \
--inputGCSStoreMaster=gs://retail-dataflow-demo/dataflow/resources/store_master.csv \
--inputGCSProductMaster=gs://retail-dataflow-demo/dataflow/resources/product_master.csv \
--inputGCSCategoryMaster=gs://retail-dataflow-demo/dataflow/resources/category_master.csv \
--outputBigQueryTable=record.receipt \
--runner=DataflowRunner"
