package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class NoDD {

    private final double HAMMING_WT;

    public NoDD(double hammingWt) {
        HAMMING_WT = hammingWt;
    }

    double fstLikeScore = 0.0;

    /** TEST!!! */
    private double[][] updateWeights(List<List<Integer>> sequences, double[][] weights) {
        fstLikeScore = 0.0;
        for (int child = 1; child <= sequences.get(0).size(); child++) {
            for (List<Integer> sequence : sequences) {
                int parent = sequence.get(child - 1);
                weights[parent][child] -= HAMMING_WT;
                fstLikeScore -= HAMMING_WT;
            }
        }
        return weights;
    }

    KBest<Integer> run(double[][] weights, int k) {
        List<List<Integer>> sequences = new ArrayList<List<Integer>>();

        double ithWeights[][] = new double[weights.length][weights[0].length];
        for (int u = 0; u < weights.length; u++) {
            for (int v = 0; v < weights[0].length; v++) {
                ithWeights[u][v] = weights[u][v];
            }
        }

        while (sequences.size() < k) {
            SequenceResult<Integer> nextResult = CleCaller.getTree(ithWeights);
            List<Integer> nextTree = nextResult.getSequence();
            double cleScore = CleCaller.getTreeModelScore(weights, nextTree);
            sequences.add(nextTree);
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