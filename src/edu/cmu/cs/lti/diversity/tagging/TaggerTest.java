package edu.cmu.cs.lti.diversity.tagging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.diversity.general.KBest;
import edu.cmu.cs.lti.diversity.general.SequenceResult;
import edu.cmu.cs.lti.diversity.general.TagSet;
import edu.cmu.cs.lti.diversity.utils.DataReader;

public class TaggerTest {

    private static final double DELTA = 0.0001;
    private static final TagSet<String> tagSet = new TagSet<String>(
            Lists.newArrayList("A", "B", "C", "*"));
    private static final Map<String, Double> hmm = DataReader.readHmmParams("data/test_hmm");

    @Test
    public void testGetTagSeq() {
        Viterbi viterbi = new Viterbi(tagSet, hmm);
        SequenceResult<String> result = viterbi.getTagSeq(
                new TestInstance(Lists.newArrayList("I", "love", "rain"), hmm, tagSet),
                null);
        double expectedScore = (hmm.get("tr:*~>A"))
                + (hmm.get("em:A~>I"))
                + (hmm.get("tr:A~>B"))
                + (hmm.get("em:B~>love"))
                + (hmm.get("tr:B~>C"))
                + (hmm.get("em:C~>rain"))
                + (hmm.get("tr:C~>STOP"));
        assertEquals(expectedScore, result.getScore(), DELTA);

        List<String> expectedSeq = Lists.newArrayList("A", "B", "C");
        assertTrue(expectedSeq.equals(result.getSequence()));
    }

    @Test
    public void testHmm() {
        String hmmFile = "data/ptb_hmm.txt";
        Map<String, Double> hmm = DataReader.readHmmParams(hmmFile);
        Map<String, Double> emissions = new HashMap<String, Double>();
        Map<String, Double> transitions = new HashMap<String, Double>();
        for (String key : hmm.keySet()) {
            if (key.startsWith("em")) {
                String token = key.substring(3).split("~>")[0];
                if (emissions.containsKey(token)) {
                    double val = emissions.get(token);
                    emissions.put(token, val + hmm.get(key));
                } else {
                    emissions.put(token, hmm.get(key));
                }
            } else if (key.startsWith("tr")) {
                String tag = key.substring(3).split("~>")[0];
                if (transitions.containsKey(tag)) {
                    double val = transitions.get(tag);
                    transitions.put(tag, val + hmm.get(key));
                } else {
                    transitions.put(tag, hmm.get(key));
                }
            }
        }
        for (Double prob : emissions.values()) {
            assertEquals(1.0, prob, DELTA);
        }
        for (Double prob : transitions.values()) {
            assertEquals(1.0, prob, DELTA);
        }
    }

    @Test
    public void testTaggerDD() {
        List<String> sentence = Lists.newArrayList("I", "love", "rain");
        int k = 10;
        TaggerDD taggerDD = new TaggerDD(tagSet, hmm);
        KBest<String> result = taggerDD.run(sentence, k);
        for (SequenceResult<String> kthBest : result.kBest) {
            System.out.println(kthBest);
        }
    }

    @Test
    public void testCostAugmentedTagger() {
        CostAugmentedTagger tagger = new CostAugmentedTagger(0.1, tagSet, hmm);
        KBest<String> kbest = tagger.run(Lists.newArrayList("I", "love", "rain"), 5);
    }
}
