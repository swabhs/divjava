package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.fst.SiblingFst;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class DdSibParser implements Dd {

    // dual decomposition parameters
    private final double hammingWt;
    private final int K;
    private final double initialStepSize;
    private final int maxIt;
    private final boolean useParseAnyway;

    public DdSibParser(
            double hammingWt, int k, double initialStepSize, boolean useParseAnyway, int maxIt) {
        this.hammingWt = hammingWt;
        this.K = k;
        this.initialStepSize = initialStepSize;
        this.maxIt = maxIt;
        this.useParseAnyway = useParseAnyway;
        System.err.println("\nSibling Diversity");
    }

    /**
     * Modify the weights of the graph by subtracting dual decomposition
     * parameters - consider the grandparent hierarchy
     */
    private void updateSibTreeWeights(List<Map<Integer, Map<Integer, Double>>> dd,
            double[][] weights) {
        for (int child = 1; child < weights[0].length; child++) {
            for (int parent = 0; parent < weights.length; parent++) {
                for (int sib = -1; sib < weights.length; sib++) {
                    weights[parent][child] -= dd.get(child - 1).get(parent).get(sib);
                }
            }
        }
    }

    public KBest<Integer> runDualDecomposition(final double[][] weights) {
        int n = weights[0].length - 1;
        SiblingFst fst = new SiblingFst(hammingWt);
        List<SequenceResult<Integer>> kBestTrees = Lists.newArrayList();
        int iterations[] = new int[K];

        // add the first tree
        SequenceResult<Integer> bestTree = CleCaller.getBestTree(weights);
        kBestTrees.add(bestTree);
        iterations[0] = 1;

        for (int i = 1; i < K; i++) {
            if (i % 10 == 0)
                System.err.print(i + "...");
            List<Map<Integer, Map<Integer, Double>>> dd = DdSibHelper.init(n);
            SequenceResult<Integer> fstResult = fst.getResult(kBestTrees, dd);
            SequenceResult<Integer> cleResult = CleCaller.getBestTree(weights);
            iterations[i] = 1;
            int flexIters = maxIt;
            if (i > 10) {
                if (iterations[i - 3] == -1) {
                    flexIters = 10;
                }
            }
            while (iterations[i] <= flexIters) {
                if (DdSibHelper.agree(cleResult, fstResult)) {
                    // System.err.println((i + 1) + " th best converges in " +
                    // iterations[i]);
                    kBestTrees.add(cleResult);
                    break;
                }
                double stepSize = initialStepSize / Math.sqrt(iterations[i]);
                dd = DdSibHelper.update(dd, cleResult, fstResult, stepSize);
                double ithWeights[][] = new double[weights.length][weights[0].length];
                for (int v = 0; v < weights[0].length; v++) {
                    for (int u = 0; u < weights.length; u++) {
                        ithWeights[u][v] = weights[u][v];
                    }
                }

                updateSibTreeWeights(dd, ithWeights);
                fstResult = fst.getResult(kBestTrees, dd);
                cleResult = CleCaller.getBestTree(ithWeights);
                iterations[i] += 1;
            }
            if (iterations[i] == flexIters + 1) { // did not converge
                // System.err.println(iterations[i]);
                // System.err.println((i + 1) + " th best does NOT converge");
                iterations[i] = -1;

                // Add the cle tree anyway!!
                if (useParseAnyway) {
                    kBestTrees.add(cleResult);
                }
            }
        }
        return new KBest<Integer>(kBestTrees, iterations);
    }

}
