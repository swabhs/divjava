package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class CostAugmentedParser {

    private final double HAMMING_WT;

    public CostAugmentedParser(double hammingWt) {
        HAMMING_WT = hammingWt;
    }

    double fstLikeScore = 0.0;

    /** TEST!!! */
    private double[][] updateWeights(List<SequenceResult<Integer>> sequences, double[][] weights) {
        fstLikeScore = 0.0;
        for (int child = 1; child <= sequences.get(0).getSequence().size(); child++) {
            for (SequenceResult<Integer> sequence : sequences) {
                int parent = sequence.getSequence().get(child - 1);
                weights[parent][child] -= HAMMING_WT;
                fstLikeScore -= HAMMING_WT;
            }
        }
        return weights;
    }

    KBest<Integer> run(double[][] weights, int k) {
        List<SequenceResult<Integer>> sequences = new ArrayList<SequenceResult<Integer>>();

        double ithWeights[][] = new double[weights.length][weights[0].length];
        for (int u = 0; u < weights.length; u++) {
            for (int v = 0; v < weights[0].length; v++) {
                ithWeights[u][v] = weights[u][v];
            }
        }

        while (sequences.size() < k) {
            SequenceResult<Integer> nextResult = CleCaller.getBestTree(ithWeights);
            double cleScore = CleCaller.getTreeModelScore(weights, nextResult.getSequence());
            sequences.add(nextResult);
            ithWeights = new double[weights.length][weights[0].length];
            for (int u = 0; u < weights.length; u++) {
                for (int v = 0; v < weights[0].length; v++) {
                    ithWeights[u][v] = weights[u][v];
                }
            }
            ithWeights = updateWeights(sequences, ithWeights);
        }
        return new KBest<Integer>(sequences, new int[k]);
    }
}