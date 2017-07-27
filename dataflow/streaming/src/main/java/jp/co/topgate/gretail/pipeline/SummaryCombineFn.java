package jp.co.topgate.gretail.pipeline;

import jp.co.topgate.gretail.model.input.Receipt;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.Combine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nagai on 2017/05/07.
 */
@SuppressWarnings("serial")
public class SummaryCombineFn extends Combine.CombineFn<Receipt, SummaryCombineFn.Accum, SummaryCombineFn.Summary> {

    @DefaultCoder(AvroCoder.class)
    public static class Accum {
        private Map<String,Integer> priceSumByStore = new HashMap<>();
        private Map<String,Integer> quantitySumByStore = new HashMap<>();
        private Map<String,Integer> priceSumByProduct = new HashMap<>();
        private Map<String,Integer> quantitySumByProduct = new HashMap<>();
    }

    @Override
    public Accum createAccumulator() { return new Accum(); }

    @Override
    public Accum addInput(Accum accum, Receipt input) {

        accum.priceSumByStore.merge(input.getStoreCode(), input.getPrice(), (oldValue, newValue) -> oldValue + newValue);
        accum.quantitySumByStore.merge(input.getStoreCode(), input.getQuantity(), (oldValue, newValue) -> oldValue + newValue);
        accum.priceSumByProduct.merge(input.getProductCode(), input.getPrice(), (oldValue, newValue) -> oldValue + newValue);
        accum.quantitySumByProduct.merge(input.getProductCode(), input.getQuantity(), (oldValue, newValue) -> oldValue + newValue);

        return accum;
    }

    @Override
    public Accum mergeAccumulators(Iterable<Accum> accums) {
        Accum merged = createAccumulator();
        for (Accum accum : accums) {
            for (Map.Entry<String,Integer> entry : accum.priceSumByStore.entrySet()) {
                merged.priceSumByStore.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
            }
            for (Map.Entry<String,Integer> entry : accum.quantitySumByStore.entrySet()) {
                merged.quantitySumByStore.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
            }
            for (Map.Entry<String,Integer> entry : accum.priceSumByProduct.entrySet()) {
                merged.priceSumByProduct.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
            }
            for (Map.Entry<String,Integer> entry : accum.quantitySumByProduct.entrySet()) {
                merged.quantitySumByProduct.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
            }
        }
        return merged;
    }

    @Override
    public SummaryCombineFn.Summary extractOutput(Accum accum) {
        Summary ar = new Summary();
        ar.setProductPriceSum(accum.priceSumByProduct);
        ar.setProductQuantitySum(accum.quantitySumByProduct);
        ar.setStorePriceSum(accum.priceSumByStore);
        ar.setStoreQuantitySum(accum.quantitySumByStore);
        return ar;
    }

    @DefaultCoder(AvroCoder.class)
    public class Summary implements Serializable {

        private Map<String,Integer> storePriceSum;
        private Map<String,Integer> storeQuantitySum;
        private Map<String,Integer> productPriceSum;
        private Map<String,Integer> productQuantitySum;


        public Map<String, Integer> getStorePriceSum() {
            return storePriceSum;
        }

        public void setStorePriceSum(Map<String, Integer> storePriceSum) {
            this.storePriceSum = storePriceSum;
        }

        public Map<String, Integer> getStoreQuantitySum() {
            return storeQuantitySum;
        }

        public void setStoreQuantitySum(Map<String, Integer> storeQuantitySum) {
            this.storeQuantitySum = storeQuantitySum;
        }

        public Map<String, Integer> getProductPriceSum() {
            return productPriceSum;
        }

        public void setProductPriceSum(Map<String, Integer> productPriceSum) {
            this.productPriceSum = productPriceSum;
        }

        public Map<String, Integer> getProductQuantitySum() {
            return productQuantitySum;
        }

        public void setProductQuantitySum(Map<String, Integer> productQuantitySum) {
            this.productQuantitySum = productQuantitySum;
        }

    }
}
