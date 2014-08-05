package edu.cmu.cs.lti.diversity.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for dual decomposition
 * 
 * @author sswayamd TODO: some sort of package access to tagging and parsing??
 */

public class DdHelper<T> {

    public List<Map<T, Double>> init(int n, TagSet<T> tagSet) {
        List<Map<T, Double>> dd = new ArrayList<Map<T, Double>>();
        for (int i = 0; i < n; i++) {
            Map<T, Double> map = new HashMap<T, Double>();
            List<T> allTags = tagSet.getTags();
            for (T tag : allTags) {
                map.put(tag, 0.0);
            }
            dd.add(map);
        }
        return dd;
    }

    /** Is this required? **/
    private List<Map<T, Integer>> computeIndicators(List<T> tagSequence, TagSet<T> tagSet) {
        List<Map<T, Integer>> ind = new ArrayList<Map<T, Integer>>();
        for (int i = 0; i < tagSequence.size(); i++) {
            Map<T, Integer> map = new HashMap<T, Integer>();
            List<T> allTags = tagSet.getTags();
            for (T tag : allTags) {
                if (tagSequence.get(i) == null) {
                    map.put(tag, 0);
                } else if (tagSequence.get(i).equals(tag)) {
                    map.put(tag, 1);
                } else {
                    map.put(tag, 0);
                }
            }
            ind.add(map);
        }
        return ind;
    }

    public List<Map<T, Double>> update(List<Map<T, Double>> dd, List<T> tags1, List<T> tags2,
            double stepSize, TagSet<T> tagSet) {

        List<Map<T, Integer>> indi1 = computeIndicators(tags1, tagSet);
        List<Map<T, Integer>> indi2 = computeIndicators(tags2, tagSet);

        List<T> allTags = tagSet.getTags();
        // System.out.println("DD");
        // for (T tag : allTags)
        // System.out.print(tag + "\t");
        // System.out.println();
        for (int i = 0; i < indi1.size(); i++) {
            for (T tag : allTags) {
                double val = dd.get(i).get(tag);
                // TODO: can be made more efficient
                val -= stepSize * (indi2.get(i).get(tag) - indi1.get(i).get(tag));
                dd.get(i).put(tag, val);
                // System.out.print(dd.get(i).get(tag) + "\t");
            }
            // System.out.println();
        }
        return dd;
    }

    // TODO : make more efficient ??
    public boolean agree(List<T> tags1, List<T> tags2) {
        assert (tags1.size() == tags2.size()) : "Error: Tag sequences from slaves not of the same size!";
        for (int i = 0; i < tags1.size(); i++) {
            if (tags1.get(i) != tags2.get(i)) {
                return false;
            }
        }
        return true;
    }
}
