package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

/**
 * For each position find a tag which has the maximum score(dd + fst) for that
 * position. Does not include any bigram scoring.
 * 
 * @author sswayamd
 * 
 * @param <T>
 *            type of tag sequence required - Integer for parsing, String for
 *            tagging.
 */
public class AdditiveUniHamDistFst<T> implements Fst<T, List<T>> {

    private static final double NIN = Double.NEGATIVE_INFINITY;

    private List<T> seq; // predicted sequence under the FST
    private double seqScore; // score of the predicted sequence (includes dd
                             // scores)

    private final double hammingWeight;

    public AdditiveUniHamDistFst(double hammingWeight) {
        this.hammingWeight = hammingWeight;
    }

    /** Runs in O(ntk). TODO: add a test to check!!! */
    private void run(List<SequenceResult<T>> kBest, List<Map<T, Double>> dd, TagSet<T> tagSet) {
        seq = new ArrayList<T>();
        seqScore = 0.0;

        int sentSize = kBest.get(0).getSequence().size();
        for (int i = 0; i < sentSize; i++) {
            double maxScore = NIN;
            T bestTag = null;
            for (T tag : tagSet.getTags()) {
                double score = getLocalScore(kBest, tag, i) + dd.get(i).get(tag);
                if (score > maxScore) {
                    maxScore = score;
                    bestTag = tag;
                }
            }
            seq.add(bestTag);
            seqScore += maxScore;
        }
    }

    private double getLocalScore(List<SequenceResult<T>> kBest, T tag, int pos) {
        double score = 0.0;
        for (SequenceResult<T> best : kBest) {
            if (tag.equals(best.getSequence().get(pos))) {
                score -= hammingWeight;
            }
        }
        return score;
    }

    public SequenceResult<T> getResult(
            List<SequenceResult<T>> kBest, List<Map<T, Double>> dd, TagSet<T> tagSet) {
        run(kBest, dd, tagSet);
        return new SequenceResult<T>(seq, seqScore);
    }

    public double getFstOnlyScore(SequenceResult<T> sequence, List<SequenceResult<T>> kBest) {
        double fstScore = 0.0;
        for (int pos = 0; pos < sequence.getSequence().size(); pos++) {
            fstScore += getLocalScore(kBest, sequence.getSequence().get(pos), pos);
        }
        return fstScore;
    }
}
