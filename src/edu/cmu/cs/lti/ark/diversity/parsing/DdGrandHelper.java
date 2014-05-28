package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

/**
 * Operations on a data structure u[childPos][parentTag][grandTag]. If parentTag
 * is 0, grandTag is only -1.
 * 
 * @author sswayamd
 * 
 */

public class DdGrandHelper {

    public static List<Map<Integer, Map<Integer, Double>>> init(int n) {
        List<Map<Integer, Map<Integer, Double>>> dd = Lists.newArrayList();
        for (int childPos = 0; childPos < n; childPos++) {
            Map<Integer, Map<Integer, Double>> parentMap = Maps.newHashMap();
            int parent = 0; // special case
            int grand = -1;
            Map<Integer, Double> grandMap = Maps.newHashMap();
            grandMap.put(-1, 0.0);
            parentMap.put(parent, grandMap);
            for (parent = 1; parent <= n; parent++) {
                grandMap = Maps.newHashMap();
                for (grand = 0; grand <= n; grand++) {
                    grandMap.put(grand, 0.0);
                }
                parentMap.put(parent, grandMap);
            }
            dd.add(parentMap);
        }
        return dd;
    }

    public static List<Map<Integer, Map<Integer, Double>>> update(
            List<Map<Integer, Map<Integer, Double>>> dd,
            SequenceResult<Integer> cleResult,
            SequenceResult<Integer> fstResult,
            double stepSize) {
        List<Integer> cleTree = cleResult.getSequence();
        List<Integer> fstTree = fstResult.getSequence();
        for (int i = 0; i < cleTree.size(); i++) {
            final int cleParentTag = cleTree.get(i);
            final int cleGranTag = cleParentTag > 0 ? cleTree.get(cleParentTag - 1) : -1;
            final int fstParentTag = fstTree.get(i);
            final int fstGranTag = fstParentTag > 0 ? fstTree.get(fstParentTag - 1) : -1;

            // either parents are not equal or
            // parents are equal, but grands are not equal
            if (cleParentTag != fstParentTag || cleGranTag != fstGranTag) {
                // add step-size to cle dd params
                double delta = dd.get(i).get(cleParentTag).get(cleGranTag);
                dd.get(i).get(cleParentTag).put(cleGranTag, delta + stepSize);
                // subtract step-size from fst dd params
                delta = dd.get(i).get(fstParentTag).get(fstGranTag);
                dd.get(i).get(fstParentTag).put(fstGranTag, delta - stepSize);
            }
        }
        return dd;
    }

    public static boolean agree(
            SequenceResult<Integer> cleResult, SequenceResult<Integer> fstResult) {
        return cleResult.getSequence().equals(fstResult.getSequence());
    }
}
