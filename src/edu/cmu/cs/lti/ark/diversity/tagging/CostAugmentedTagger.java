package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

/**
 * Cost augmented Viterbi decoding
 * 
 * @author sswayamd
 * 
 */
// TODO: abstract out to make a single class for parsing and tagging
public class CostAugmentedTagger {

    private final double hammingWt;
    private final TagSet<String> tagSet;
    private final Map<String, Double> hmm;

    private double fstLikeScore = 0.0; // TODO: what exactly does this do?

    public CostAugmentedTagger(double hammingWt, TagSet<String> tagSet, Map<String, Double> hmm) {
        this.hammingWt = hammingWt;
        this.tagSet = tagSet;
        this.hmm = hmm;
    }

    private void updateWeights(
            List<SequenceResult<String>> bests, Map<String, Double> ithHmm, List<String> tokens) {
        fstLikeScore = 0.0;
        for (SequenceResult<String> kthBest : bests) {
            int pos = 0;
            for (String tag : kthBest.getSequence()) {
                String key = "em:" + tag + "~>" + tokens.get(pos);
                double score = ithHmm.get(key);
                ithHmm.put(key, score - hammingWt);
                fstLikeScore -= hammingWt;
                pos += 1;
            }
        }
    }

    /** TEST!!! */
    public KBest<String> run(List<String> tokens, int k) {
        List<SequenceResult<String>> bests = Lists.newArrayList();

        Map<String, Double> ithHmm = Maps.newHashMap();
        ithHmm.putAll(hmm);

        while (bests.size() < k) {
            // TODO: seems redundant to initialize both Viterbi and TestInstance
            // with hmm and tagset
            Viterbi viterbi = new Viterbi(tagSet, ithHmm);
            SequenceResult<String> nextResult = viterbi.getTagSeq(
                    new TestInstance(tokens, ithHmm, tagSet), null);

            bests.add(nextResult);
            ithHmm = Maps.newHashMap();
            ithHmm.putAll(hmm);
            updateWeights(bests, ithHmm, tokens);
        }
        return new KBest<String>(bests, new int[k]);
    }

}
