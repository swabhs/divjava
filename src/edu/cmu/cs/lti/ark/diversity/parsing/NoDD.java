package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.KBest;

public class NoDD {

    private final double HAMMING_WT;

    public NoDD(double hammingWt) {
        HAMMING_WT = hammingWt;
    }

    /** TEST!!! */
    private double[][] updateWeights(List<Integer> sequence, double[][] weights) {

        for (int child = 1; child <= sequence.size(); child++) {
            int parent = sequence.get(child - 1);
            weights[parent][child] -= HAMMING_WT;
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
            // for (int i = 0; i < weights.length; i++) {
            // for (int j = 0; j < weights[0].length; j++) {
            // System.out.print(ithWeights[i][j] + " \t");
            // }
            // System.out.println();
            // }
            List<Integer> nextBestSeq = ParserDD.getTree(ithWeights);
            // System.out.println(nextBestSeq + " " + sequences.size());
            sequences.add(nextBestSeq);
            ithWeights = updateWeights(nextBestSeq, ithWeights);
        }
        return new KBest<Integer>(sequences, new int[k]);
    }
}