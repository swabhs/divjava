package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.ark.cle.Weighted;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class CleCaller {

    static final int ROOT = 0;

    /** Gets CLE tree (Sam's implementation) */
    static SequenceResult<Integer> getBestTree(double[][] weights) {
        Weighted<Map<Integer, Integer>> result = ChuLiuEdmonds.getMaxSpanningTree(weights, ROOT);
        Map<Integer, Integer> value = result.val;
        List<Integer> tree = new ArrayList<Integer>();

        for (int child = 1; child <= value.size(); ++child) {
            tree.add(value.get(child));
        }
        // double cleScore = result.weight; // contains DD weights
        return new SequenceResult<Integer>(tree, result.weight);
    }

    /**
     * Given a tree, and the edge weights of the graph, return the score of the
     * tree under the model. (No DD scores included)
     */
    static double getTreeModelScore(double[][] weights, List<Integer> tree) {
        double score = 0.0;

        // tree contains the parents of each node.
        for (int child = 1; child <= tree.size(); child++) { // root = 0 can
                                                             // never be a child
            int parent = tree.get(child - 1);
            score += weights[parent][child];
        }

        return score;
    }

    /** Maps Camerini output into KBest<Integer> */
    // TODO: integrate with getTree above
    static KBest<Integer> getKBestTrees(double[][] weights, int k) {
        List<SequenceResult<Integer>> kbest = Lists.newArrayList();
        int iterations[] = new int[k];
        List<Weighted<Map<Integer, Integer>>> weightedSpanningTrees =
                ChuLiuEdmonds.getKBestSpanningTrees(weights, 0, k);
        if (weightedSpanningTrees.size() < k) {
            System.err.println("not enough solutions");
        }
        for (Weighted<Map<Integer, Integer>> weightedSpanningTree : weightedSpanningTrees) {
            Map<Integer, Integer> value = weightedSpanningTree.val;
            List<Integer> tree = new ArrayList<Integer>();

            for (int child = 1; child <= value.size(); ++child) {
                tree.add(value.get(child));
            }
            kbest.add(new SequenceResult<Integer>(tree, weightedSpanningTree.weight));
            iterations[kbest.size() - 1] = 1;
        }
        return new KBest<Integer>(kbest, iterations);
    }
}
