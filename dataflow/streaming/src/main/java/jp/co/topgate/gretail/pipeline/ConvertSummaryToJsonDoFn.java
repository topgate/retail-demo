package jp.co.topgate.gretail.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.co.topgate.gretail.model.output.ProductSummary;
import jp.co.topgate.gretail.model.output.StoreSummary;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by nagai on 2017/05/11.
 */
public class ConvertSummaryToJsonDoFn extends DoFn<SummaryCombineFn.Summary, String> {

    private static final Logger logger = LoggerFactory.getLogger(ConvertSummaryToJsonDoFn.class);
    private static final Gson gson = new GsonBuilder().create();

    private final PCollectionView<Map<String, String>> productMasterView;
    private final PCollectionView<Map<String, String>> storeMasterView;
    private final PCollectionView<Map<String, String>> categoryMasterView;
    private final TupleTag<String> tagOutputTrend;
    private final TupleTag<String> tagOutputHeatmap;
    private final int limitStoreNum;


    public ConvertSummaryToJsonDoFn(PCollectionView<Map<String, String>> productMasterView,
                                    PCollectionView<Map<String, String>> storeMasterView,
                                    PCollectionView<Map<String, String>> categoryMasterView,
                                    TupleTag<String> tagOutputTrend,
                                    TupleTag<String> tagOutputHeatmap,
                                    int limitStoreNum) {
        this.productMasterView = productMasterView;
        this.categoryMasterView = categoryMasterView;
        this.storeMasterView = storeMasterView;
        this.tagOutputTrend = tagOutputTrend;
        this.tagOutputHeatmap = tagOutputHeatmap;
        this.limitStoreNum = limitStoreNum;
    }

    @ProcessElement
    public void processElement(ProcessContext c) {

        String productsJson = processProducts(c.element().getProductPriceSum(), c.element().getProductQuantitySum(), c.sideInput(productMasterView), c.sideInput(categoryMasterView));
        String storesJson = processStores(c.element().getStorePriceSum(), c.element().getStoreQuantitySum(), c.sideInput(storeMasterView));
        logger.debug("products json" + productsJson);
        logger.debug("stores json" + storesJson);
        c.output(productsJson);
        c.output(tagOutputHeatmap, storesJson);
    }

    public String processStores(Map<String,Integer> storePriceSum, Map<String,Integer> storeQuantitySum, Map<String, String> storeMaster) {

        List<StoreSummary> storeResults = StoreSummary.from(storePriceSum, storeQuantitySum);
        for (StoreSummary storeResult : storeResults) {
            if (storeMaster.containsKey(storeResult.getStoreCode())) {
                String line = storeMaster.get(storeResult.getStoreCode());
                String[] strs = line.split(",");
                try {
                    String name = strs[1];
                    float lat = Float.valueOf(strs[2]);
                    float lng = Float.valueOf(strs[3]);
                    storeResult.setStoreName(name);
                    storeResult.setLat(lat);
                    storeResult.setLng(lng);
                } catch (NumberFormatException e) {
                    logger.error("parse latlng error: " + e.getMessage());
                } catch (NullPointerException e) {
                    logger.error("latlng are null: " + e.getMessage());
                }
            } else {
                logger.info("product master do not have: " + storeResult.getStoreCode() + " with dic size: " + storeMaster.size());
            }
        }
        return gson.toJson(storeResults);
    }

    public String processProducts(Map<String,Integer> productPriceSum, Map<String,Integer> productQuantitySum,
                                  Map<String, String> productMaster, Map<String, String> categoryMaster) {

        List<ProductSummary> productResults = ProductSummary.from(productPriceSum, productQuantitySum);

        for (ProductSummary productResult : productResults) {
            if (productMaster.containsKey(productResult.getProductCode())) {
                String line = productMaster.get(productResult.getProductCode());
                String[] strs = line.split(",");
                try {
                    String categoryId = strs[1];
                    String categoryName = categoryMaster.get(categoryId);
                    String name = strs[2];
                    productResult.setProductName(name);
                    productResult.setCategoryId(categoryId);
                    productResult.setCategoryName(categoryName);
                } catch (NullPointerException e) {
                    logger.error("product line are null: " + e.getMessage());
                }
            } else {
                logger.info("product master do not have: " + productResult.getProductCode() + " with dic size: " + productMaster.size());
            }
        }

        ProductSummary.sortByPriceDesc(productResults);
        if(this.limitStoreNum > 0 && this.limitStoreNum < productResults.size()) {
            return gson.toJson(productResults.subList(0, limitStoreNum));
        } else {
            return gson.toJson(productResults);
        }
    }
}
