package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.fst.Fst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class ParserDD {

    // dual decomposition parameters
    private final double HAMMING_WT;
    private final int K;
    private final double INITIAL_STEP;
    private final double MAX_ITERATIONS;
    private final boolean USE_PARSE_ANYWYAY;

    private DdHelper<Integer> helper = new DdHelper<Integer>();

    public ParserDD(
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
    private void updateWeights(List<Map<Integer, Double>> dd, double[][] weights) {
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
    public KBest<Integer> runDualDecomposition(double[][] weights) {
        int sentenceSize = weights[0].length - 1;
        TagSet<Integer> tagSet = createTagset(sentenceSize + 1); // tagset
                                                                 // includes
                                                                 // root too!

        Fst<Integer, List<Integer>> fst = new AdditiveUniHamDistFst<Integer>(HAMMING_WT);
        List<List<Integer>> kBestTrees = new ArrayList<List<Integer>>();

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
                cleResult = CleCaller.getTree(ithWeights);
                List<Integer> bestTree = cleResult.getSequence();
                kBestTrees.add(bestTree);
                // System.out.println(i + " " + bestTree + "\nmodelscore = " +
                // cleResult.getScore());
                iterations[0] = 1;
                continue;
            }

            iterations[i] = 1;
            while (iterations[i] <= MAX_ITERATIONS) {
                // System.out.println("Iteration #" + iterations[i]);
                double stepSize = INITIAL_STEP / Math.sqrt(iterations[i]);

                fstResult = fst.getResult(kBestTrees, dd, tagSet);
                fstTree = fstResult.getSequence();

                cleResult = CleCaller.getTree(ithWeights);
                cleTree = cleResult.getSequence();

                // System.out.println(cleTree + " cle");
                // System.out.println(fstTree + " fst");

                if (helper.agree(cleTree, fstTree)) {

                    // System.out.println("\n" + i + " " + cleTree);
                    // System.out.println("ddscore    = " +
                    // String.format("%.4g", ddscore) + " cle = "
                    // +
                    // String.format("%.4g", cleResult.getScore())
                    // + " fst = " + String.format("%.4g",
                    // fstResult.getScore()));
                    // System.out.println("modelScore = " +
                    // String.format("%.4g", modelonlyscore) +
                    // " cle = " + String.format("%.4g", cleOnlyScore)
                    // + " fst = " + String.format("%.4g", fstOnlyScore));
                    double ddscore = fstResult.getScore() + cleResult.getScore();
                    double fstOnlyScore = fst.getFstOnlyScore(fstTree, kBestTrees);
                    double cleOnlyScore = CleCaller.getTreeModelScore(weights, cleTree);
                    double modelonlyscore = fstOnlyScore + cleOnlyScore;
                    if (Math.abs(ddscore - modelonlyscore) > 0.1) {
                        System.err.println("BUG in dual decomposition - reparametrization wrong");
                        System.exit(0);
                    }
                    kBestTrees.add(cleTree);
                    break;
                } else {
                    dd = helper.update(dd, cleTree, fstTree, stepSize, tagSet);
                    for (int v = 0; v < weights[0].length; v++) {
                        for (int u = 0; u < weights.length; u++) {
                            ithWeights[u][v] = weights[u][v];
                        }
                    }
                    updateWeights(dd, ithWeights);
                }
                iterations[i] += 1;
            }

            if (iterations[i] == MAX_ITERATIONS + 1) { // did not converge
                iterations[i] = -1;

                // Add the cle tree anyway!!
                if (USE_PARSE_ANYWYAY) {
                    kBestTrees.add(cleTree);
                }
            }
            // System.out.println("Converges in " + iterations[i]);
        }
        return new KBest<Integer>(kBestTrees, iterations);
    }
}
