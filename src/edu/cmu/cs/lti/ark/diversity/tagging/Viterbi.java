package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

/**
 * Main Viterbi algorithm
 * 
 * @author sswayamd
 * 
 */

public class Viterbi {

    private final double NIN = Double.NEGATIVE_INFINITY;
    private final String DEFAULT_TAG;

    private TagSet<String> fullTagSet;
    private Map<String, Double> hmm;

    private List<Map<String, Double>> pi;
    private List<Map<String, String>> bp;
    private TagSet<String> optimizedTagset;

    public Viterbi(
            TagSet<String> fullTagSet,
            Map<String, Double> hmm) {

        this.fullTagSet = fullTagSet;
        this.hmm = hmm;

        DEFAULT_TAG = "";
    }

    private void init(int n) {
        pi = new ArrayList<Map<String, Double>>(n + 1);
        bp = new ArrayList<Map<String, String>>(n + 1);

        for (int i = 0; i < n + 1; i++) {
            Map<String, Double> map = new HashMap<String, Double>();
            Map<String, String> bpMap = new HashMap<String, String>();
            if (i == 0) {
                List<String> allTags = optimizedTagset.getTags();
                for (String tag : allTags) {
                    if (tag.equals("*")) {
                        map.put(tag, 0.0);
                    }
                    else {
                        map.put(tag, NIN);
                    }
                    bpMap.put(tag, "*");
                }
            }
            pi.add(map);
            bp.add(bpMap);
        }
    }

    /**
     * Runs the main Viterbi algorithm in O(nt^2)
     * 
     * @param tokens
     * @param dd
     */
    private void run(List<String> tokens, List<Map<String, Double>> dd) {
        this.optimizedTagset = optimize(tokens);

        int n = tokens.size();
        init(n);

        for (int k = 1; k < n + 1; k++) {
            for (String u : optimizedTagset.getTags()) {
                double maxScore = NIN;
                String argMax = DEFAULT_TAG;
                for (String w : optimizedTagset.getTags()) {
                    double localScore = getLocalScore(tokens.get(k - 1), w, u);
                    double score = pi.get(k - 1).get(w) + localScore;
                    if (dd != null) {
                        score -= dd.get(k - 1).get(u);
                    }

                    if (score > maxScore) {
                        maxScore = score;
                        argMax = w;
                    }
                }
                pi.get(k).put(u, maxScore);
                bp.get(k).put(u, argMax);
                // System.out.print(pi.get(k).get(u) + "\t");
            }
            // System.out.println();
        }
    }

    private SequenceResult<String> decode(int n) {
        List<String> tagSeq = new ArrayList<String>();

        double maxScore = NIN;
        String bestLabel = DEFAULT_TAG;

        List<String> allTags = optimizedTagset.getTags();
        for (String w : allTags) {
            double localScore = getLocalScore("", w, "STOP");
            double score = pi.get(n).get(w) + localScore;

            if (score > maxScore) {
                maxScore = score;
                bestLabel = w;
            }
        }
        tagSeq.add(bestLabel);

        for (int k = n - 1; k > 0; k--) {
            String last_tag = tagSeq.get(tagSeq.size() - 1);
            String next_tag = bp.get(k + 1).get(last_tag);
            tagSeq.add(next_tag);
        }
        Collections.reverse(tagSeq);
        return new SequenceResult<String>(tagSeq, maxScore);
    }

    private Double getLocalScore(String word, String prevTag, String tag) {
        String trans = "tr:" + prevTag + "~>" + tag;
        double tscore, escore;
        if (hmm.containsKey(trans))
            tscore = hmm.get(trans);
        else
            tscore = NIN;

        if (word.equals("")) // last position
            return tscore;

        String emi = "em:" + tag + "~>" + word;
        if (hmm.containsKey(emi))
            escore = hmm.get(emi);
        else {

            escore = NIN;
        }

        return tscore + escore;
    }

    public SequenceResult<String> getTagSeq(
            TestInstance testInstance, List<Map<String, Double>> dd) {
        List<String> tokens = testInstance.getTestTokens();
        run(tokens, dd);
        return decode(tokens.size());
    }

    /**
     * Removes the tags not required for this sentence. Runs in O(nt).
     * 
     * @param tagSet
     * @param tokens
     * @return
     */
    private TagSet<String> optimize(List<String> tokens) {
        List<String> tags = new ArrayList<String>();

        for (String tag : fullTagSet.getTags()) {
            boolean required = false;
            for (String token : tokens) {
                String emi = "em:" + tag + "~>" + token;
                if (hmm.containsKey(emi)) {
                    required = true;
                    break;
                }
            }
            if (required == true) {
                tags.add(tag);
            }
        }
        if (tags.contains("*") == false) {
            tags.add("*");
        }
        return new TagSet<String>(tags);
    }

}
