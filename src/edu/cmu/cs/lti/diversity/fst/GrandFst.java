package edu.cmu.cs.lti.diversity.fst;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.ark.cle.Pair;
import edu.cmu.cs.lti.diversity.general.SequenceResult;

/**
 * Penalize trees with the same grandparent+parent constructs as the previous
 * (k-1) trees
 */
public class GrandFst {

    private final double hammingWt;

    public GrandFst(double hammingWt) {
        this.hammingWt = hammingWt;
    }

    // TODO: Test!
    /** O(n^3)k */
    public SequenceResult<Integer> getResult(
            List<SequenceResult<Integer>> kBest, List<Map<Integer, Map<Integer, Double>>> dd) {
        List<Integer> seq = Lists.newArrayList(); // predicted sequence under
                                                  // the FST
        List<Pair<Integer, Integer>> sequence = Lists.newArrayList();

        double seqScore = 0.0; // fst + dd score
        int sentSize = kBest.get(0).getSequence().size();

        for (int i = 0; i < sentSize; i++) {
            int parentTag = 0;
            int granTag = -1;
            double maxScore = getLocalScore(kBest, parentTag, granTag, i)
                    + dd.get(i).get(parentTag).get(granTag);
            int bestTag = parentTag;
            int bestGranTag = granTag;
            for (parentTag = 1; parentTag <= sentSize; parentTag++) {
                if (parentTag == i + 1) {
                    continue; // child cannot be its own parent
                }
                for (granTag = 0; granTag <= sentSize; granTag++) {
                    if (granTag == i + 1 || granTag == parentTag) {
                        continue; // child cannot be its own parent
                    }
                    double score = getLocalScore(kBest, parentTag, granTag, i)
                            + dd.get(i).get(parentTag).get(granTag);
                    if (score > maxScore) {
                        maxScore = score;
                        bestTag = parentTag;
                        bestGranTag = granTag;
                    }
                }
            }
            sequence.add(new Pair<Integer, Integer>(bestTag, bestGranTag));
            seq.add(bestTag);
            seqScore += maxScore;
        }
        return new SequenceResult<Integer>(seq, seqScore);
    }

    private boolean checkForAgreement(Pair<Integer, Integer> parGranPair, List<Integer> parents) {
        boolean consistent = false;
        for (int parent : parents) {

        }
        return consistent;
    }

    /**
     * Penalizes same parent and grandparent construct if seen in previous k-1
     * parses. Parents are the same : penalize Parents are not the same, but
     * grandparents are the same: penalize
     * 
     * Runs in O(k)
     */
    double getLocalScore(
            List<SequenceResult<Integer>> kBest, int parent, int gran, int pos) {
        double score = 0.0;
        for (SequenceResult<Integer> kth : kBest) {
            int parentInKth = kth.getSequence().get(pos);
            if (parent == parentInKth) {
                score -= hammingWt;
            } else {
                int granInKth = findGran(pos, kth);
                if (gran == granInKth) {
                    score -= hammingWt;
                }
            }
        }
        return score;
    }

    public double getFstOnlyScore(
            SequenceResult<Integer> result, List<SequenceResult<Integer>> kBest) {
        double fstScore = 0.0;
        int n = result.getSequence().size();
        for (int pos = 0; pos < n; pos++) {
            int parent = result.getSequence().get(pos);
            int grand = -1;
            if (parent != 0) {
                grand = result.getSequence().get(parent - 1);
            }
            fstScore += getLocalScore(kBest, parent, grand, pos);
        }
        return fstScore;
    }

    /** Finds the grandparent of a node in a sequence */
    static int findGran(int childPos, SequenceResult<Integer> tree) {
        int parent = tree.getSequence().get(childPos);
        if (parent == 0) {
            return -1;
        }
        return tree.getSequence().get(parent - 1);
    }

}
