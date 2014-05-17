package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.lti.ark.cle.Weighted;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class CleCaller {

    static final int ROOT = 0;

    /** Gets CLE tree (Sam's implementation) */
    static SequenceResult<Integer> getTree(double[][] weights) {
        Weighted<Map<Integer, Integer>> result = ChuLiuEdmonds.getMaxSpanningTree(weights, ROOT);
        Map<Integer, Integer> value = result.val;
        List<Integer> tree = new ArrayList<Integer>();

        for (int child = 1; child <= value.size(); ++child) {
            tree.add(value.get(child));
        }
        double cleScore = result.weight; // may or may not contain the dd
                                         // weights
        return new SequenceResult<Integer>(tree, cleScore);
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

}
