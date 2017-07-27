package jp.co.topgate.gretail.pipeline;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.coders.StringUtf8Coder;
import com.google.cloud.dataflow.sdk.testing.TestPipeline;
import com.google.cloud.dataflow.sdk.transforms.Combine;
import com.google.cloud.dataflow.sdk.transforms.Create;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.DoFnTester;
import com.google.cloud.dataflow.sdk.util.PCollectionViews;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PCollectionView;
import jp.co.topgate.gretail.model.input.Receipt;
import jp.co.topgate.gretail.util.CSVToMapLineCombineFn;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by nagai on 2017/05/11.
 */
@RunWith(JUnit4.class)
public class MainPipelineTest {

    @Test
    public void testConvertJsonToReceiptDoFn() {
        ConvertJsonToReceiptDoFn dofn = new ConvertJsonToReceiptDoFn();
        DoFnTester<String, Receipt> fnTester = DoFnTester.of(dofn);
        List<String> input = new ArrayList<>();
        input.add("{\"salesDate\": \"2013-02-10T13:45:30+0900\"," +
                "\"storeCode\": \"010\"," +
                "\"registerID\": \"123\"," +
                "\"receiptNumber\": 1," +
                "\"receiptBranchNumber\": 2," +
                "\"productCode\": \"301\"," +
                "\"quantity\": 3," +
                "\"price\": 108 }");
        List<Receipt> receipts = fnTester.processBatch(input);
        Receipt receipt1 = receipts.get(0);
        Assert.assertThat("", receipt1.getPrice(), is(108));
        Assert.assertThat("", receipt1.getQuantity(), is(3));
        Assert.assertThat("", receipt1.getStoreCode(), is("010"));
        Assert.assertThat("", receipt1.getProductCode(), is("301"));
        Assert.assertThat("", receipt1.getRegisterID(), is("123"));
        Assert.assertThat("", receipt1.getReceiptNumber(), is(1));
        Assert.assertThat("", receipt1.getReceiptBranchNumber(), is(2));
    }

    @Test
    public void testCSVToMapLineCombineFn() {

        final String[] csv = new String[] {
                "000,Tokyo,120",
                "001,Osaka,100",
                "002,Kyoto,140"
        };
        final List<String> csvlist = Arrays.asList(csv);

        Pipeline p = TestPipeline.create();
        PCollection<String> maplines = p.apply(Create.of(csvlist)).setCoder(StringUtf8Coder.of());
        PCollectionView<Map<String,String>> mapview = maplines.apply(Combine.globally(new CSVToMapLineCombineFn()).asSingletonView());

        final String[] dummy = new String[] {
                "000",
                "001",
                "002"
        };
        List<String> dummylist = Arrays.asList(dummy);
        DoFnTester<String,String> fnTester = DoFnTester.of(new AAA(mapview));
        fnTester.setSideInputInGlobalWindow(mapview, csvlist);

        //dummylines.apply(ParDo.of(fnTester));
        List<String> results = fnTester.processBatch(dummylist);
        System.out.println(results);

        //p.apply()
    }

    public static class AAA extends DoFn<String, String> {

        private PCollectionView<Map<String,String>> mapvie = PCollectionView

        public AAA(PCollectionView<Map<String,String>> mapview) {
            this.mapview = mapview;
        }

        @Override
        public void processElement(ProcessContext c) throws Exception {
            Map<String,String> map = c.sideInput(mapview);
            c.output(map.get(c.element()));
            //c.output(c.element());
        }
    };

}
