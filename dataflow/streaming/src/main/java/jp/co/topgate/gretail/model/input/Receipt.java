package jp.co.topgate.gretail.model.input;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.util.Date;

/**
 * Created by nagai on 2017/05/07.
 */
@DefaultCoder(AvroCoder.class)
public class Receipt {

    private Date salesDate;
    private String storeCode;
    private String registerID;
    private Integer receiptNumber;
    private Integer receiptBranchNumber;
    private String productCode;
    private Integer quantity;
    private Integer price;

    public Date getSalesDate() { return salesDate; }

    public void setSalesDate(Date salesDate) { this.salesDate = salesDate; }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
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
