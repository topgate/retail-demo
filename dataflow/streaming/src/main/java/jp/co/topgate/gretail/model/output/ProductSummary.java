package jp.co.topgate.gretail.model.output;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by nagai on 2017/05/10.
 */
@DefaultCoder(AvroCoder.class)
public class ProductSummary {

    private static final Logger logger = LoggerFactory.getLogger(ProductSummary.class);

    private String productCode;
    private String productName = "DummyName";
    private Integer quantitySum;
    private Integer priceSum;
    private String categoryId;
    private String categoryName;

    public static List<ProductSummary> from(Map<String,Integer> prices, Map<String,Integer> quantities) {
        List<ProductSummary> results = new ArrayList<>();
        for(Map.Entry<String,Integer> entity : prices.entrySet()) {
            ProductSummary result = new ProductSummary();
            result.setProductCode(entity.getKey());
            result.setPriceSum(entity.getValue());
            result.setQuantitySum(quantities.get(entity.getKey()));
            results.add(result);
        }
        return results;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantitySum() {
        return quantitySum;
    }

    public void setQuantitySum(Integer quantitySum) {
        this.quantitySum = quantitySum;
    }

    public Integer getPriceSum() { return priceSum; }

    public void setPriceSum(Integer priceSum) {
        this.priceSum = priceSum;
    }

    public String getCategoryId() { return categoryId; }

    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }


    public static void sortByPriceDesc(List<ProductSummary> products) {
        try {
            Collections.sort(products, new ProductSummaryComparatorSortByPriceDesc());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage() + ", size: " + products.size());
        }
    }

    public static class ProductSummaryComparatorSortByPriceDesc implements Comparator<ProductSummary> {

        public int compare(ProductSummary a, ProductSummary b) {

            if(a.getPriceSum() == null) {
                a.setPriceSum(0);
            }
            if(b.getPriceSum() == null) {
                b.setPriceSum(0);
            }
            if(a.getPriceSum() < b.getPriceSum()) {
                return 1;
            } else if(a.getPriceSum() == b.getPriceSum()) {
                return 0;
            } else {
                return -1;
            }
        }

    }

}
