package jp.co.topgate.gretail.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.co.topgate.gretail.model.input.Receipt;
import jp.co.topgate.gretail.model.output.ReceiptResult;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.PCollectionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by nagai on 2017/05/11.
 */
public class ConvertReceiptToJsonDoFn extends DoFn<Receipt, String> {

    private static final String defaultStoreName = "三河屋";
    private static final String defaultProductName = "醤油";

    private static final Logger logger = LoggerFactory.getLogger(ConvertReceiptToJsonDoFn.class);
    private static final Gson gson = new GsonBuilder().create();

    private final PCollectionView<Map<String, String>> storeMasterView;
    private final PCollectionView<Map<String, String>> productMasterView;

    public ConvertReceiptToJsonDoFn(PCollectionView<Map<String, String>> storeMasterView, PCollectionView<Map<String, String>> productMasterView) {
        this.storeMasterView = storeMasterView;
        this.productMasterView = productMasterView;
    }

    @ProcessElement
    public void processElement(ProcessContext c) {
        Map<String,String> storeMaster = c.sideInput(this.storeMasterView);
        Map<String,String> productMaster = c.sideInput(this.productMasterView);
        ReceiptResult result = ReceiptResult.from(c.element());
        String storeName = getMasterValue(storeMaster, result.getStoreCode(), 1, defaultStoreName);
        String productName = getMasterValue(productMaster, result.getProductCode(), 2, defaultProductName);
        result.setStoreName(storeName);
        result.setProductName(productName);
        String json = gson.toJson(result);
        c.output(json);
    }

    private static String getMasterValue(Map<String,String> master, String key, int index, String defaultValue) {
        if(master.containsKey(key)) {
            String line = master.get(key);
            String[] strs = line.split(",");
            if(strs.length <= index) {
                logger.error("master dict size was illegal 0 for key: " + key);
                return defaultValue;
            }
            return strs[index];
        } else {
            logger.error("master has no key : " + key + " so return default value: " + defaultValue);
            return defaultValue;
        }
    }
}
