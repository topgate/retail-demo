package jp.co.topgate.gretail.pipeline;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import jp.co.topgate.gretail.model.input.Receipt;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagai on 2017/05/11.
 */
public class ConvertReceiptToBQTableDoFn extends DoFn<Receipt, TableRow> {

    public static TableSchema buildBigQuerySchema() {
        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("sales_date").setType("TIMESTAMP"));
        fields.add(new TableFieldSchema().setName("store_code").setType("STRING"));
        fields.add(new TableFieldSchema().setName("register_id").setType("STRING"));
        fields.add(new TableFieldSchema().setName("receipt_number").setType("INTEGER"));
        fields.add(new TableFieldSchema().setName("receipt_branch_number").setType("INTEGER"));
        fields.add(new TableFieldSchema().setName("product_code").setType("STRING"));
        fields.add(new TableFieldSchema().setName("quantity").setType("INTEGER"));
        fields.add(new TableFieldSchema().setName("price").setType("INTEGER"));
        TableSchema schema = new TableSchema().setFields(fields);
        return schema;
    }

    @ProcessElement
    public void processElement(ProcessContext c) throws Exception {
        TableRow row = new TableRow();
        row.set("sales_date", (c.element().getSalesDate().getTime()/1000));
        row.set("store_code", c.element().getStoreCode());
        row.set("register_id", c.element().getRegisterID());
        row.set("receipt_number", c.element().getReceiptNumber());
        row.set("receipt_branch_number", c.element().getReceiptBranchNumber());
        row.set("product_code", c.element().getProductCode());
        row.set("quantity", c.element().getQuantity());
        row.set("price", c.element().getPrice());
        c.output(row);
    }
}