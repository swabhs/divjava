package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.fst.GrandFst;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class DdGrandParser implements Dd {

    // dual decomposition parameters
    private final double hammingWt;
    private final int K;
    private final double initialStepSize;
    private final int maxIt;
    private final boolean useParseAnyway;

    public DdGrandParser(
            double hammingWt, int k, double initialStepSize,
            boolean useParseAnyway, int maxIt) {
        this.hammingWt = hammingWt;
        this.K = k;
        this.initialStepSize = initialStepSize;
        this.maxIt = maxIt;
        this.useParseAnyway = useParseAnyway;
        System.err.println("\nGrandParent Diversity");
    }

    /**
     * Modify the weights of the graph by subtracting dual decomposition
     * parameters - consider the grandparent hierarchy. Runs in O(n^3)
     */
    private double[][] updateGrandTreeWeights(List<Map<Integer, Map<Integer, Double>>> dd,
            final double[][] weights) {
        double ithWeights[][] = new double[weights.length][weights[0].length];
        for (int child = 1; child < weights[0].length; child++) {
            int parent = 0; // special case
            int grand = -1;
            ithWeights[parent][child] = weights[parent][child]
                    - dd.get(child - 1).get(parent).get(grand);
            for (parent = 1; parent < weights.length; parent++) {
                for (grand = 0; grand < weights.length; grand++) {
                    ithWeights[parent][child] = weights[parent][child]
                            - dd.get(child - 1).get(parent).get(grand);
                }
            }
        }
        return ithWeights;
    }

    public KBest<Integer> runDualDecomposition(final double[][] weights) {
        int n = weights[0].length - 1;
        GrandFst fst = new GrandFst(hammingWt);
        List<SequenceResult<Integer>> kBestTrees = Lists.newArrayList();
        int iterations[] = new int[K];

        // add the first tree
        SequenceResult<Integer> bestTree = CleCaller.getBestTree(weights);
        kBestTrees.add(bestTree);
        iterations[0] = 1;

        for (int i = 1; i < K; i++) {
            if (i % 10 == 0)
                System.err.print(i + "...");
            List<Map<Integer, Map<Integer, Double>>> dd = DdGrandHelper.init(n);
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
                if (DdGrandHelper.agree(cleResult, fstResult)) {
                    kBestTrees.add(cleResult);
                    // if (isDdReparmetrizationCorrect(
                    // kBestTrees, cleResult, fstResult, weights, fst)) {
                    // kBestTrees.add(cleResult);
                    // }
                    // else {
                    // System.err.println("\nWarning: DD Reparametrization Skewed");
                    // // System.exit(1);
                    // }
                    break;
                }
                // TODO: combine the two updates!
                double stepSize = initialStepSize / Math.sqrt(iterations[i]);
                dd = DdGrandHelper.update(dd, cleResult, fstResult, stepSize);
                double ithWeights[][] = updateGrandTreeWeights(dd, weights);

                fstResult = fst.getResult(kBestTrees, dd);
                cleResult = CleCaller.getBestTree(ithWeights);
                iterations[i] += 1;
            }
            if (iterations[i] == flexIters + 1) { // did not converge
                // System.err.println((i + 1) + " th best does NOT converge");
                iterations[i] = -1;

                // Add the cle tree anyway!!
                if (useParseAnyway) {
                    kBestTrees.add(cleResult);
                }
            }
        }
        System.err.println();
        return new KBest<Integer>(kBestTrees, iterations);
    }

    private boolean isDdReparmetrizationCorrect(
            List<SequenceResult<Integer>> kBestTrees,
            SequenceResult<Integer> cleResult,
            SequenceResult<Integer> fstResult,
            double[][] weights,
            GrandFst fst) {
        double cleDdScore = cleResult.getScore();
        double cleOnlyScore = CleCaller.getTreeModelScore(
                weights, cleResult.getSequence());
        double fstDdScore = fstResult.getScore();
        double fstOnlyScore = fst.getFstOnlyScore(fstResult, kBestTrees);
        return Math.abs(cleDdScore + fstDdScore - cleOnlyScore - fstOnlyScore) < 0.01;
    }

    private double sum(double[][] a) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (Double.isInfinite(a[i][j]) == false)
                    s += a[i][j];
            }
        }
        return s;
    }
}
