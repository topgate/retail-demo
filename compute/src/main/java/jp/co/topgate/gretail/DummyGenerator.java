package jp.co.topgate.gretail;

import jp.co.topgate.gretail.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by nagai on 2017/05/15.
 */
public class DummyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DummyGenerator.class);

    private List<String> productCodes;
    private List<String> storeCodes;
    private final double lambda;
    private final double stddev;

    private static final int N = 12;

    private int lastShuffleDay = 0;

    public DummyGenerator(double lambda, double stddev, boolean useTurnAroundCategory) {
        if(useTurnAroundCategory) {
            this.productCodes = readProductMasterSortedByTurnAroundCategory(false);
        } else {
            this.productCodes = readProductMaster();
        }
        this.storeCodes = readStoreMaster(false);
        this.lambda = lambda;
        this.stddev = stddev;
    }

    public Order generate() {
        shuffleMaster();
        Order receipt = new Order();
        String productCode = selectProductCode();
        String storeCode = selectStoreCode();
        receipt.setProductCode(productCode);
        receipt.setStoreCode(storeCode);
        receipt.setQuantity(2);
        System.out.println(productCode + " : " + storeCode);
        return receipt;
    }

    private void shuffleMaster() {
        int currentDay = (int)(System.currentTimeMillis()/(1000 * 60 * 60 * 24));
        if(currentDay != this.lastShuffleDay) {
            if(currentDay >= 17331) { // (over 6/15, use fixed order) for next tokyo demo
                this.productCodes = readProductMasterSortedByTurnAroundCategory(false);
                this.storeCodes = readStoreMaster(false);
            } else {
                this.productCodes = readProductMasterSortedByTurnAroundCategory(true);
                this.storeCodes = readStoreMaster(true);
            }
            this.lastShuffleDay = currentDay;
        }
    }

    private String selectProductCode() {
        int retry = 0;
        int index = generateNumByExponentialDistribution();
        while(index >= this.productCodes.size()) {
            index = generateNumByExponentialDistribution();
            if(retry > 5) {
                return this.productCodes.get(0);
            }
            retry++;
        }
        return this.productCodes.get(index);
    }

    private String selectStoreCode() {
        int retry = 0;
        int index = generateNumByNormalDistribution();
        index = index < 0 ? -index : index;
        while(index >= this.storeCodes.size() || index < 0) {
            index = generateNumByNormalDistribution();
            if(retry > 5) {
                return this.storeCodes.get(0);
            }
            retry++;
        }
        return this.storeCodes.get(index);
    }

    private int generateNumByNormalDistribution() {
        double xw = 0.0;
        for (int n = 1; n <= N*2; n ++) {
            xw += Math.random();
        }
        return (int)(this.stddev * (xw - N));
    }

    private int generateNumByExponentialDistribution() {
        return (int)(-1.0 / this.lambda * Math.log(1.0 - Math.random()));
    }

    private List<String> readProductMaster() {
        List<String> lines = readCSV("master/product_master.csv");
        List<String> productCodes = new ArrayList<>();
        for(String line : lines) {
            String[] strs = line.split(",");
            productCodes.add(strs[0]);
        }

        return productCodes;
    }

    private List<String> readProductMasterSortedByTurnAroundCategory(boolean shuffle) {
        List<String> lines = readCSV("master/product_master.csv");
        Map<String,List<String>> productCodesByCategory = new TreeMap<>();
        for(String line : lines) {
            String[] strs = line.split(",");
            if(productCodesByCategory.containsKey(strs[1])) {
                productCodesByCategory.get(strs[1]).add(strs[0]);
            } else {
                List<String> productCodes = new ArrayList<>();
                productCodes.add(strs[0]);
                productCodesByCategory.put(strs[1], productCodes);
            }
        }

        if(shuffle) {
            for (Map.Entry<String, List<String>> entry : productCodesByCategory.entrySet()) {
                Collections.shuffle(entry.getValue());
            }
        }

        List<String> lastProductCodes = new ArrayList<>();
        int index = 0;
        while(lines.size() > lastProductCodes.size()) {
            for(List<String> productCodes : productCodesByCategory.values()) {
                if(productCodes.size() > index) {
                    lastProductCodes.add(productCodes.get(index));
                }
            }
            index++;
        }
        return lastProductCodes;
    }

    private List<String> readStoreMaster(boolean shuffle) {
        List<String> lines = readCSV("master/store_master.csv");
        List<String> storeCodes = new ArrayList<>();
        for(String line : lines) {
            String[] strs = line.split(",");
            storeCodes.add(strs[0]);
        }
        if(shuffle) {
            Collections.shuffle(storeCodes);
        }
        return storeCodes;
    }

    private List<String> readCSV(String resourceFilePath) {

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceFilePath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            List<String> list = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine();
            }
            br.close();
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

}
