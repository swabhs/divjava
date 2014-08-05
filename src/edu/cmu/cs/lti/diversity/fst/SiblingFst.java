package edu.cmu.cs.lti.diversity.fst;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.diversity.general.SequenceResult;

/**
 * Penalize trees with the same parent+sibling(in the same direction) constructs
 * as the previous (k-1) trees TODO: why is direction important here?
 */
public class SiblingFst {

    private static final double NIN = Double.NEGATIVE_INFINITY;
    private final double hammingWt;

    public SiblingFst(double hammingWt) {
        this.hammingWt = hammingWt;
    }

    // TODO Test!!
    public SequenceResult<Integer> getResult(
            List<SequenceResult<Integer>> kBest, List<Map<Integer, Map<Integer, Double>>> dd) {
        List<Integer> seq = Lists.newArrayList(); // predicted sequence under
        // the FST
        double seqScore = 0.0; // fst + dd score
        int sentSize = kBest.get(0).getSequence().size();
        for (int pos = 0; pos < sentSize; pos++) {
            double maxScore = NIN;
            int bestTag = -1;
            for (int parentTag = 0; parentTag <= sentSize; parentTag++) {
                for (int sibTag = -1; sibTag <= sentSize; sibTag++) {
                    double score = getLocalScore(kBest, parentTag, sibTag, pos)
                            + dd.get(pos).get(parentTag).get(sibTag);
                    if (score > maxScore) {
                        maxScore = score;
                        bestTag = parentTag;
                    }
                }
            }
            seq.add(bestTag);
            seqScore += maxScore;
        }
        return new SequenceResult<Integer>(seq, seqScore);
    }

    /**
     * Penalizes same parent and sibling construct if seen in previous k-1
     * parses. Parents are the same : penalize Parents are not the same, but
     * siblings are the same: penalize
     */
    private double getLocalScore(
            List<SequenceResult<Integer>> kBest, int parent, int sibling, int pos) {
        double score = 0.0;
        for (SequenceResult<Integer> kth : kBest) {
            int parentInKth = kth.getSequence().get(pos);
            if (parent == parentInKth) {
                score -= hammingWt;
            } else {
                int sibInKth = findSibling(pos, kth);
                if (sibling == sibInKth) {
                    score -= hammingWt;
                }
            }
        }
        return score;
    }

    public double getFstOnlyScore(
            SequenceResult<Integer> sequence, List<SequenceResult<Integer>> kBest) {
        double fstScore = 0.0;
        int n = sequence.getSequence().size();
        for (int pos = 0; pos < n; pos++) {
            int parent = sequence.getSequence().get(pos);
            int sibling = findSibling(pos, sequence);
            fstScore += getLocalScore(kBest, parent, sibling, pos);
        }
        return fstScore;
    }

    /** Returns the tag (NOT position) of the sibling. TESTED :D */
    public static int findSibling(int childPos, SequenceResult<Integer> tree) {
        int parent = tree.getSequence().get(childPos);
        if (parent == 0) { // root node should have only a single child?
            return -1;
        }
        // child is the position, parent is the tag
        int parentPos = parent - 1;

        if (parentPos > childPos) { // child is on the left of parent
            for (int i = 0; i < parentPos; i++) {
                if (i != childPos && tree.getSequence().get(i) == parent) {
                    return i;
                }
            }
            return -1; // no siblings on the same side
        } else {
            for (int i = parentPos + 1; i < tree.getSequence().size(); i++) {
                if (i != childPos && tree.getSequence().get(i) == parent) {
                    return i;
                }
            }
            return -1;
        }
    }
}
