package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

/**
 * Can make point-wise decisions, is more informed because of half graph weights
 * 
 * @author sswayamd
 * 
 */

public class AdditiveUniHamDistHalfNHalfFst {

    private static final double NIN = Double.NEGATIVE_INFINITY;

    private List<Integer> bestSequence;
    private double maxScore = NIN;

    private final double hammingWeight;

    public AdditiveUniHamDistHalfNHalfFst(double hammingWeight) {
        this.hammingWeight = hammingWeight;
    }

    private void init() {
        bestSequence = new ArrayList<Integer>();
    }

    /** Picks up the best tag for a position. Runs in O(ntk) */
    public void run(List<List<Integer>> kBest, List<Map<Integer, Double>> dd,
            TagSet<Integer> tagSet, double weights[][]) {
        init();

        int n = kBest.get(0).size();

        for (int pos = 0; pos < n; pos++) {
            maxScore = NIN;
            Integer bestTag = null;
            for (Integer parent : tagSet.getTags()) {

                double score = getLocalScore(kBest, parent, pos, weights) + dd.get(pos).get(parent);
                if (score > maxScore) {
                    maxScore = score;
                    bestTag = parent;
                }
            }
            bestSequence.add(bestTag);
        }
    }

    private double getLocalScore(List<List<Integer>> kBest, Integer parent, int pos,
            double weights[][]) {
        double score = weights[parent][pos + 1]; // only this is different from
        // AdditiveUniHamDistFst
        for (List<Integer> best : kBest) {
            if (parent.equals(best.get(pos))) {
                score -= hammingWeight;
            }
        }
        return score;
    }

    public SequenceResult<Integer> getSequence(List<List<Integer>> kBest,
            List<Map<Integer, Double>> dd, TagSet<Integer> tagSet, double weights[][]) {
        run(kBest, dd, tagSet, weights);
        return new SequenceResult<Integer>(bestSequence, maxScore);
    }

}
