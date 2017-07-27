package jp.co.topgate.gretail.util;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.Combine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nagai on 2017/05/10.
 */
public class CSVToMapLineCombineFn extends Combine.CombineFn<String, CSVToMapLineCombineFn.Accum, Map<String,String>> {

    @DefaultCoder(AvroCoder.class)
    public static class Accum {
        private Map<String,String> master = new HashMap<>();
    }

    @Override
    public Accum createAccumulator() { return new Accum(); }

    @Override
    public Accum addInput(Accum accum, String input) {
        String[] strs = input.split(",", -1);
        if(strs.length > 0) {
            accum.master.put(strs[0], input);
        }
        return accum;
    }

    @Override
    public Accum mergeAccumulators(Iterable<Accum> accums) {
        Accum merged = createAccumulator();
        for (Accum accum : accums) {
            merged.master.putAll(accum.master);
        }
        return merged;
    }

    @Override
    public Map<String,String> extractOutput(Accum accum) {
        return accum.master;
    }
}
