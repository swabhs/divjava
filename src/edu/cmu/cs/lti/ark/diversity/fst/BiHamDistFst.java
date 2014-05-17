package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class BiHamDistFst<T> implements Fst<T, T> {

    private final double nin = Double.NEGATIVE_INFINITY;

    private List<Map<T, Double>> pi;
    private List<Map<T, T>> bp;

    private final double hammingWeight;

    public BiHamDistFst(double hammingWeight) {
        this.hammingWeight = hammingWeight;
    }

    private void init(List<T> given, List<Map<T, Double>> dd, TagSet<T> tagSet) {
        Map<T, Double> pi1 = new HashMap<T, Double>();
        for (T tag : tagSet.getTags()) {
            double score = tag.equals(given.get(0)) ? -hammingWeight : 0.0;
            pi1.put(tag, score + dd.get(0).get(tag));
        }

        pi = new ArrayList<Map<T, Double>>();
        pi.add(pi1);

        bp = new ArrayList<Map<T, T>>();
    }

    private void run(List<T> given, List<Map<T, Double>> dd, TagSet<T> tagSet) {
        init(given, dd, tagSet);

        double maxScore = nin;
        int n = given.size();

        for (int i = 0; i < n; i++) {
            Map<T, Double> piI = new HashMap<T, Double>();
            Map<T, T> bpI = new HashMap<T, T>();

            for (T tag : tagSet.getTags()) {
                maxScore = nin;
                T best = null;
                for (T prevTag : tagSet.getTags()) {
                    double score = pi.get(i - 1).get(prevTag)
                            + getLocalScore(tag, prevTag, given, i) + dd.get(i).get(tag);
                    if (score > maxScore) {
                        maxScore = score;
                        best = prevTag;
                    }
                }
                piI.put(tag, maxScore);
                bpI.put(tag, best);
            }
            pi.add(piI);
            bp.add(bpI);
        }
    }

    private double getLocalScore(T tag, T prevTag, List<T> given, int pos) {
        return tag.equals(given.get(pos)) && prevTag.equals(given.get(pos - 1))
                ? -hammingWeight : 0.0;
    }

    @Override
    public SequenceResult<T> getResult(List<T> given, List<Map<T, Double>> dd, TagSet<T> tagSet) {
        run(given, dd, tagSet);
        List<T> tagSeq = new ArrayList<T>();

        double maxScore = nin;
        T lastTag = null;

        int n = given.size();
        for (T tag : tagSet.getTags()) {
            if (pi.get(n - 1).get(tag) > maxScore) {
                maxScore = pi.get(n - 1).get(tag);
                lastTag = tag;
            }
        }
        tagSeq.add(lastTag);

        for (int i = n - 1; i > 0; i--) {
            tagSeq.add(bp.get(i).get(tagSeq.get(tagSeq.size() - 1)));
        }

        Collections.reverse(tagSeq);
        return new SequenceResult<T>(tagSeq, maxScore);
    }

    @Override
    public double getFstOnlyScore(List<T> sequence, List<List<T>> kBest) {
        // TODO Auto-generated method stub
        return 0;
    }

}
