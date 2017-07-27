package jp.co.topgate.gretail.model.output;

import jp.co.topgate.gretail.model.input.Receipt;

/**
 * Created by nagai on 2017/05/10.
 */
public class ReceiptResult {
    private Long salesDate;
    private String storeCode;
    private String storeName;
    private String registerID;
    private Integer receiptNumber;
    private Integer receiptBranchNumber;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Integer price;

    public static ReceiptResult from(Receipt receipt) {
        ReceiptResult result = new ReceiptResult();
        result.salesDate = receipt.getSalesDate().getTime();
        result.storeCode = receipt.getStoreCode();
        result.registerID = receipt.getRegisterID();
        result.receiptNumber = receipt.getReceiptNumber();
        result.receiptBranchNumber = receipt.getReceiptBranchNumber();
        result.productCode = receipt.getProductCode();
        result.quantity = receipt.getQuantity();
        result.price = receipt.getPrice();
        return result;
    }

    public Long getSalesDate() { return salesDate; }

    public void setSalesDate(Long salesDate) { this.salesDate = salesDate; }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getRegisterID() {
        return registerID;
    }

    public void setRegisterID(String registerID) {
        this.registerID = registerID;
    }

    public Integer getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(Integer receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Integer getReceiptBranchNumber() {
        return receiptBranchNumber;
    }

    public void setReceiptBranchNumber(Integer receiptBranchNumber) {
        this.receiptBranchNumber = receiptBranchNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
