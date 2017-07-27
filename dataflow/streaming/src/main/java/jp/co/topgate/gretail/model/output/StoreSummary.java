package jp.co.topgate.gretail.model.output;

import java.util.*;

/**
 * Created by nagai on 2017/05/10.
 */
public class StoreSummary {

    private String storeCode;
    private String storeName;
    private Float lat;
    private Float lng;
    private Integer quantitySum;
    private Integer priceSum;

    public static List<StoreSummary> from(Map<String,Integer> prices, Map<String,Integer> quantities) {
        List<StoreSummary> results = new ArrayList<>();
        for(Map.Entry<String,Integer> entity : prices.entrySet()) {
            StoreSummary result = new StoreSummary();
            result.setStoreCode(entity.getKey());
            result.setPriceSum(entity.getValue());
            result.setQuantitySum(quantities.get(entity.getKey()));
            results.add(result);
        }
        return results;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() { return storeName; }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Integer getQuantitySum() {
        return quantitySum;
    }

    public void setQuantitySum(Integer quantitySum) {
        this.quantitySum = quantitySum;
    }

    public Integer getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(Integer priceSum) {
        this.priceSum = priceSum;
    }

    public static void sortByPriceDesc(List<StoreSummary> products) {
        Collections.sort(products, new StoreSummaryComparatorSortByPriceDesc());
    }

    public static class StoreSummaryComparatorSortByPriceDesc implements Comparator<StoreSummary> {

        public int compare(StoreSummary a, StoreSummary b) {
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
