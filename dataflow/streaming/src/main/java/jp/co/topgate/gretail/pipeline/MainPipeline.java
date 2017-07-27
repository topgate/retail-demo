package jp.co.topgate.gretail.pipeline;

import jp.co.topgate.gretail.model.input.Receipt;
import jp.co.topgate.gretail.util.CSVToMapLineCombineFn;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.*;
import org.joda.time.Duration;

import java.util.Map;


/**
 * Created by nagai on 2017/05/07.
 */
public class MainPipeline {

    private static String buildTopicName(String project, String topic) {
        return "projects/" + project + "/topics/" + topic;
    }

    public interface MainPipelineOptions extends DataflowPipelineOptions {
        @Description("Input PubSub topic")
        @Default.String("receipt")
        String getInputTopicReceipt();
        void setInputTopicReceipt(String s);

        @Description("Output PubSub topic trend")
        @Default.String("trend")
        String getOutputTopicTrend();
        void setOutputTopicTrend(String s);

        @Description("Output PubSub topic map")
        @Default.String("heatmap")
        String getOutputTopicHeatmap();
        void setOutputTopicHeatmap(String s);

        @Description("Output PubSub topic console")
        @Default.String("console")
        String getOutputTopicConsole();
        void setOutputTopicConsole(String s);

        @Description("Output BigQuery table")
        @Default.String("test.receipt")
        String getOutputBigQueryTable();
        void setOutputBigQueryTable(String s);

        @Description("Input GCS path store master")
        @Default.String("gs://retail-dataflow-demo/dataflow/resources/store_master.csv")
        String getInputGCSStoreMaster();
        void setInputGCSStoreMaster(String s);

        @Description("Input GCS path product master")
        @Default.String("gs://retail-dataflow-demo/dataflow/resources/product_master.csv")
        String getInputGCSProductMaster();
        void setInputGCSProductMaster(String s);

        @Description("Input GCS path product master")
        @Default.String("gs://retail-dataflow-demo/dataflow/resources/category_master.csv")
        String getInputGCSCategoryMaster();
        void setInputGCSCategoryMaster(String s);

        @Description("Input Limit products summary num")
        @Default.Integer(10)
        Integer getLimitStores();
        void setLimitStores(Integer s);

        @Description("Input Filter rate for console pubsub")
        @Default.Double(0.1)
        Double getFilterRate();
        void setFilterRate(Double s);

        @Description("Window size for aggregation")
        @Default.Long(15L)
        Long getWindowSize();
        void setWindowSize(Long s);

        @Description("Window interval for aggregation")
        @Default.Long(5L)
        Long getWindowInterval();
        void setWindowInterval(Long s);

    }

    public static void main(String[] args) {

        MainPipelineOptions options = PipelineOptionsFactory.fromArgs(args).as(MainPipelineOptions.class);
        options.setStreaming(true);

        // Set cloud resource settings depending on the situation.
        //options.setAutoscalingAlgorithm(AutoscalingAlgorithmType.NONE); // default: AutoscalingAlgorithmType.THROUGHPUT_BASED
        //options.setWorkerMachineType("n1-standard-4");
        //options.setMaxNumWorkers(30);
        //options.setNumWorkers(1);
        //options.setDiskSizeGb(30);

        Pipeline p = Pipeline.create(options);

        String topicReceipt = buildTopicName(options.getProject(), options.getInputTopicReceipt());
        String topicTrend = buildTopicName(options.getProject(), options.getOutputTopicTrend());
        String topicHeatmap = buildTopicName(options.getProject(), options.getOutputTopicHeatmap());
        String topicConsole = buildTopicName(options.getProject(), options.getOutputTopicConsole());


        // Read input from each source
        PCollection<Receipt> receipts = p
                .apply("Read Receipt JSON From PubSub", PubsubIO.readStrings().fromTopic(topicReceipt))
                .apply("Convert JSON to Entity", ParDo.of(new ConvertJsonToReceiptDoFn()));

        PCollectionView<Map<String, String>> storeMasterView = p
                .apply("Read store master file", TextIO.read().from(options.getInputGCSStoreMaster()))
                .apply("Aggregate store master as map View", Combine.globally(new CSVToMapLineCombineFn()).asSingletonView());

        PCollectionView<Map<String, String>> productMasterView = p
                .apply("Read product master file", TextIO.read().from(options.getInputGCSProductMaster()))
                .apply("Aggregate product master as map View", Combine.globally(new CSVToMapLineCombineFn()).asSingletonView());

        PCollectionView<Map<String, String>> categoryMasterView = p
                .apply("Read category master file", TextIO.read().from(options.getInputGCSCategoryMaster()))
                .apply("Aggregate category master as map View", Combine.globally(new CSVToMapLineCombineFn()).asSingletonView());


        // Execute ETL processes
        double filterRate = options.getFilterRate();
        PCollection<Receipt> filteredReceipts = receipts.apply("Filter receipts with demo shop or hit given rate",
                Filter.by((Receipt receipt) -> receipt.getStoreCode().equals("000") || Math.random() < filterRate));

        PCollection<Receipt> windowedReceipts = receipts
                .apply("Set sliding window", Window.into(SlidingWindows
                        .of(Duration.standardSeconds(options.getWindowSize()))
                        .every(Duration.standardSeconds(options.getWindowInterval()))));
        PCollection<SummaryCombineFn.Summary> combinedReceipts = windowedReceipts
                .apply("Combine Receipts by window", Combine.globally(new SummaryCombineFn()).withoutDefaults());

        TupleTag<String> tagOutputTrend  = new TupleTag<String>(){ private static final long serialVersionUID = 1L; };
        TupleTag<String> tagOutputHeatmap = new TupleTag<String>(){ private static final long serialVersionUID = 1L; };

        PCollectionTuple results = combinedReceipts
                .apply("Convert Summary to JSON", ParDo
                        .of(new ConvertSummaryToJsonDoFn(productMasterView, storeMasterView, categoryMasterView,tagOutputTrend, tagOutputHeatmap, options.getLimitStores()))
                        .withSideInputs(productMasterView, storeMasterView, categoryMasterView)
                        .withOutputTags(tagOutputTrend, TupleTagList.of(tagOutputHeatmap)));


        // Write results to each sink
        receipts.apply("Convert Entity to BQ TableRow",ParDo.of(new ConvertReceiptToBQTableDoFn()))
                .apply("Insert to BigQuery Table", BigQueryIO.writeTableRows().to(options.getOutputBigQueryTable())
                        .withSchema(ConvertReceiptToBQTableDoFn.buildBigQuerySchema())
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED));

        filteredReceipts
                .apply("Convert Entity to JSON", ParDo.of(new ConvertReceiptToJsonDoFn(storeMasterView, productMasterView))
                        .withSideInputs(productMasterView, storeMasterView))
                .apply("Publish to PubSub topic console", PubsubIO.writeStrings().to(topicConsole));

        results.get(tagOutputTrend).apply("Publish to PubSub topic trend", PubsubIO.writeStrings().to(topicTrend));
        results.get(tagOutputHeatmap).apply("Publish to PubSub topic heatmap", PubsubIO.writeStrings().to(topicHeatmap));

        p.run();
    }

}
