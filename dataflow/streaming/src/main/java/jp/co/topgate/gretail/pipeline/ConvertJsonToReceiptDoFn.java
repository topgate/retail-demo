package jp.co.topgate.gretail.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.co.topgate.gretail.model.input.Receipt;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nagai on 2017/05/11.
 */
public class ConvertJsonToReceiptDoFn extends DoFn<String, Receipt> {

    private static final Logger logger = LoggerFactory.getLogger(ConvertJsonToReceiptDoFn.class);
    private static final Gson gson = new GsonBuilder().create();

    @ProcessElement
    public void processElement(ProcessContext c) {
        logger.debug("input json string: " + c.element());
        Receipt receipt = gson.fromJson(c.element(), Receipt.class);
        c.output(receipt);
    }
}
