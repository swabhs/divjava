package edu.cmu.cs.lti.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.diversity.fst.Fst;
import edu.cmu.cs.lti.diversity.general.DdHelperSimple;
import edu.cmu.cs.lti.diversity.general.KBest;
import edu.cmu.cs.lti.diversity.general.SequenceResult;
import edu.cmu.cs.lti.diversity.general.TagSet;

public class DdParserSimple {

    // dual decomposition parameters
    private final double HAMMING_WT;
    private final int K;
    private final double INITIAL_STEP;
    private final int MAX_ITERATIONS;
    private final boolean USE_PARSE_ANYWYAY;

    private DdHelperSimple<Integer> helper = new DdHelperSimple<Integer>();

    public DdParserSimple(
            double hammingWt, int k, double initialStepSize, boolean useParseAnyway, int maxIt) {
        HAMMING_WT = hammingWt;
        K = k;
        INITIAL_STEP = initialStepSize;
        MAX_ITERATIONS = maxIt;
        USE_PARSE_ANYWYAY = useParseAnyway;
    }

    /**
     * Modify the weights of the graph by subtracting dual decomposition
     * parameters
     */
    private void updateTreeWeights(List<Map<Integer, Double>> dd, double[][] weights) {
        for (int parent = 0; parent < weights.length; parent++) {
            for (int child = 1; child < weights[0].length; child++) {
                weights[parent][child] -= dd.get(child - 1).get(parent);
            }
        }
    }

    /** Tags are parents for a node -- can be any node including the root */
    private TagSet<Integer> createTagset(int n) {
        List<Integer> tags = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            tags.add(i);
        }
        return new TagSet<Integer>(tags);
    }

    /**
     * Run dual decomposition to get the k-best dependency parse trees for a
     * sentence, given the edge weights of the graph.
     */
    public KBest<Integer> runDualDecomposition(final double[][] weights) {
        int sentenceSize = weights[0].length - 1;
        TagSet<Integer> tagSet = createTagset(sentenceSize + 1); // tagset
                                                                 // includes
                                                                 // root too!

        Fst<Integer, List<Integer>> fst = new AdditiveUniHamDistFst<Integer>(HAMMING_WT);
        List<SequenceResult<Integer>> kBestTrees = Lists.newArrayList();

        int iterations[] = new int[K];

        for (int i = 0; i < K; i++) {
            // System.out.println("\n" + i + "th best");

            SequenceResult<Integer> fstResult = null;
            SequenceResult<Integer> cleResult = null;
            List<Integer> fstTree = null;
            List<Integer> cleTree = null;

            List<Map<Integer, Double>> dd = helper.init(sentenceSize, tagSet);

            // copy the weights for the ith rank
            double ithWeights[][] = new double[weights.length][weights[0].length];
            for (int v = 0; v < weights[0].length; v++) {
                for (int u = 0; u < weights.length; u++) {
                    ithWeights[u][v] = weights[u][v];
                }
            }

            // get the best tree - TODO - make the actual dd process generic
            // enough to handle this
            if (i == 0) {
                cleResult = CleCaller.getBestTree(ithWeights);
                kBestTrees.add(cleResult);
                iterations[0] = 1;
                continue;
            }

            iterations[i] = 1;
            while (iterations[i] <= MAX_ITERATIONS) {
                // System.out.println("Iteration #" + iterations[i]);
                double stepSize = INITIAL_STEP / Math.sqrt(iterations[i]);

                fstResult = fst.getResult(kBestTrees, dd, tagSet);
                fstTree = fstResult.getSequence();

                cleResult = CleCaller.getBestTree(ithWeights);
                cleTree = cleResult.getSequence();

                // System.out.println(cleTree + " cle");
                // System.out.println(fstTree + " fst");

                if (helper.agree(cleTree, fstTree)) {
                    double ddscore = fstResult.getScore() + cleResult.getScore();
                    double fstOnlyScore = fst.getFstOnlyScore(fstResult, kBestTrees);
                    double cleOnlyScore = CleCaller.getTreeModelScore(weights, cleTree);
                    double modelonlyscore = fstOnlyScore + cleOnlyScore;
                    System.err.println(ddscore + " " + modelonlyscore);
                    if (Math.abs(ddscore - modelonlyscore) > 0.01) {
                        System.out.println(ddscore + " " + modelonlyscore);
                        System.err.println("BUG in dual decomposition - reparametrization wrong");
                        System.exit(0);
                    }
                    kBestTrees.add(new SequenceResult<Integer>(cleTree, modelonlyscore));
                    break;
                } else {
                    dd = helper.update(dd, cleTree, fstTree, stepSize, tagSet);
                    for (int v = 0; v < weights[0].length; v++) {
                        for (int u = 0; u < weights.length; u++) {
                            ithWeights[u][v] = weights[u][v];
                        }
                    }
                    updateTreeWeights(dd, ithWeights);
                }
                iterations[i] += 1;
            }

            if (iterations[i] == MAX_ITERATIONS + 1) { // did not converge
                iterations[i] = -1;

                // Add the cle tree anyway!!
                if (USE_PARSE_ANYWYAY) {
                    kBestTrees.add(cleResult);
                }
            }
            // System.out.println("Converges in " + iterations[i]);
        }
        return new KBest<Integer>(kBestTrees, iterations);
    }
}
