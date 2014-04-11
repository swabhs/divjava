package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.lti.ark.cle.Weighted;
import edu.cmu.cs.lti.ark.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.fst.Fst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class ParserDD {

    private static final int ROOT = 0;
    private final double MAX_ITERATIONS = 200;

    private final double HAMMING_WT;
    private final int K;

    private DdHelper<Integer> helper = new DdHelper<Integer>();

    public ParserDD(double hammingWt, int k) {
        HAMMING_WT = hammingWt;
        K = k;
    }

    /** CLE runner */
    static List<Integer> getTree(double[][] weights) {
        Weighted<Map<Integer, Integer>> result = ChuLiuEdmonds.getMaxSpanningTree(weights, ROOT);
        Map<Integer, Integer> value = result.val;
        List<Integer> tree = new ArrayList<Integer>();

        for (int child = 1; child <= value.size(); ++child) {
            tree.add(value.get(child));
        }
        // System.out.println(result.weight);
        return tree;
    }

    private double[][] updateWeights(List<Map<Integer, Double>> dd, double[][] weights) {
        for (Integer parent : dd.get(0).keySet()) {
            for (int child = 0; child < dd.size(); child++) {
                weights[parent][child] -= dd.get(child).get(parent);
                // System.out.print(weights[parent][child] + " \t");
            }
            // System.out.println();
        }
        return weights;
    }

    private TagSet<Integer> createTagset(int n) {
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            tags.add(i);
        }
        return new TagSet<Integer>(tags);
    }

    public KBest<Integer> run(double[][] weights) {
        // n = length of sentence, n+1 = length of tagset
        int n = weights[0].length - 1;
        TagSet<Integer> tagSet = createTagset(n + 1);

        Fst<Integer, List<Integer>> fst = new AdditiveUniHamDistFst<Integer>(HAMMING_WT);
        List<List<Integer>> kBestTrees = new ArrayList<List<Integer>>();

        int iterations[] = new int[K];

        for (int i = 0; i < K; i++) {
            // System.out.println("\n" + (i + 1) + "th best");
            List<Integer> cleTree = null;
            List<Integer> fstTree = null;
            List<Map<Integer, Double>> dd = helper.init(n, tagSet);

            double ithWeights[][] = new double[weights.length][weights[0].length];
            for (int u = 0; u < weights.length; u++) {
                for (int v = 0; v < weights[0].length; v++) {
                    ithWeights[u][v] = weights[u][v];
                    // System.out.print(weights[u][v] + " \t");
                }
                // System.out.println();
            }

            // get the best tree
            if (i == 0) {
                List<Integer> bestTree = getTree(ithWeights);
                kBestTrees.add(bestTree);
                // System.out.println(bestTree);
                iterations[0] = 1;
                continue;
            }
            iterations[i] = 1;
            while (iterations[i] <= MAX_ITERATIONS) {
                double stepSize = 1.0 / Math.sqrt(iterations[i]);

                fstTree = fst.getSequence(kBestTrees, dd, tagSet).getSequence();
                cleTree = getTree(ithWeights);

                // System.out.println(cleTree + " cle");
                // System.out.println(fstTree + " fst");
                if (helper.agree(cleTree, fstTree)) {
                    kBestTrees.add(cleTree);
                    // System.out.println(i + " " + cleTree);
                    break;
                } else {
                    dd = helper.update(dd, cleTree, fstTree, stepSize, tagSet);
                    ithWeights = updateWeights(dd, ithWeights);
                }
                iterations[i] += 1;
            }
            if (iterations[i] == MAX_ITERATIONS + 1) { // did not converge
                iterations[i] = -1;
                // Add the cle tree anyway!!
                kBestTrees.add(cleTree);
            }
        }
        return new KBest<Integer>(kBestTrees, iterations);
    }
}
