package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.cs.lti.ark.diversity.fst.SiblingFst;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

/**
 * Operations on a data structure u[childPos][parentTag][sibTag]. If there are
 * no siblings, the sibTag is set to -1.
 * 
 * @author sswayamd
 * 
 */

public class DdSibHelper {

    public static List<Map<Integer, Map<Integer, Double>>> init(int n) {
        List<Map<Integer, Map<Integer, Double>>> dd = Lists.newArrayList();
        for (int childPos = 0; childPos < n; childPos++) {
            Map<Integer, Map<Integer, Double>> parentMap = Maps.newHashMap();
            for (int parent = 0; parent <= n; parent++) {
                Map<Integer, Double> sibMap = Maps.newHashMap();
                for (int sib = -1; sib <= n; sib++) {
                    sibMap.put(sib, 0.0);
                }
                parentMap.put(parent, sibMap);
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
            final int cleSibTag = SiblingFst.findSibling(i, cleResult);
            final int fstParentTag = fstTree.get(i);
            final int fstSibTag = SiblingFst.findSibling(i, fstResult);

            // either parents are not equal or
            // parents are equal, but siblings are not equal
            if (cleParentTag != fstParentTag || cleSibTag != fstSibTag) {
                // add step-size to cle dd params
                double delta = dd.get(i).get(cleParentTag).get(cleSibTag);
                dd.get(i).get(cleParentTag).put(cleSibTag, delta + stepSize);
                // subtract step-size from fst dd params
                delta = dd.get(i).get(fstParentTag).get(fstSibTag);
                dd.get(i).get(fstParentTag).put(fstSibTag, delta - stepSize);
            }
        }
        return dd;
    }

    public static boolean agree(
            SequenceResult<Integer> cleResult, SequenceResult<Integer> fstResult) {
        return cleResult.getSequence().equals(fstResult.getSequence());
    }

}
