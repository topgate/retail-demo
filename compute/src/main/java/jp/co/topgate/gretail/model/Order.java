package jp.co.topgate.gretail.model;

/**
 * Created by nagai on 2017/05/15.
 */
public class Order {

    private String storeCode;
    private String productCode;
    private Integer quantity;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
