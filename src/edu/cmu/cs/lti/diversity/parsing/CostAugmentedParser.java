package edu.cmu.cs.lti.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.diversity.general.KBest;
import edu.cmu.cs.lti.diversity.general.SequenceResult;
import edu.cmu.cs.lti.diversity.utils.Conll;
import edu.cmu.cs.lti.diversity.utils.Conll.ConllElement;

public class CostAugmentedParser {

    private final double hammingWt;
    private final boolean ppAttachFlag;
    private final boolean uniHamFlag;
    private double diversityScore; // score added wrt uni-ham and pp-attach

    private final double ppWt;

    public CostAugmentedParser(
            double hammingWt, boolean uniHamFlag, double ppWt, boolean ppAttachFlag) {
        this.hammingWt = hammingWt;
        this.uniHamFlag = uniHamFlag;
        this.ppWt = ppWt;
        this.ppAttachFlag = ppAttachFlag;
    }

    /** TODO: TEST!!! */
    private double[][] updateWeightsWithUnigramHamming(
            List<SequenceResult<Integer>> sequences, double[][] weights) {

        for (int child = 1; child <= sequences.get(0).getSequence().size(); child++) {
            for (SequenceResult<Integer> sequence : sequences) {
                int parent = sequence.getSequence().get(child - 1);
                weights[parent][child] -= hammingWt;
                diversityScore -= hammingWt;
            }
        }
        return weights;
    }

    /** extra penalty for same pp attachment TODO: TEST!!! */
    private double[][] updateWeightsWithPpAttachment(
            Conll sentence, List<SequenceResult<Integer>> sequences, double[][] weights) {
        for (ConllElement element : sentence.getElements()) {
            if (element.getCoarsePosTag().equals("IN")) {
                int child = element.getPosition();
                for (SequenceResult<Integer> sequence : sequences) {
                    int parent = sequence.getSequence().get(child - 1);
                    weights[parent][child] -= ppWt;
                    diversityScore -= ppWt;
                }
            }
        }
        return weights;
    }

    public KBest<Integer> run(Conll sentence, double[][] weights, int k) {
        List<SequenceResult<Integer>> sequences = new ArrayList<SequenceResult<Integer>>();
        int iterations[] = new int[k];

        double ithWeights[][] = new double[weights.length][weights[0].length];
        for (int u = 0; u < weights.length; u++) {
            for (int v = 0; v < weights[0].length; v++) {
                ithWeights[u][v] = weights[u][v];
            }
        }

        while (sequences.size() < k) {
            diversityScore = 0.0;
            SequenceResult<Integer> nextResult = CleCaller.getBestTree(ithWeights);
            // double cleScore = CleCaller.getTreeModelScore(weights,
            // nextResult.getSequence());
            sequences.add(nextResult);
            // System.out.println(sequences.size());
            // printTreeAndScore(nextResult, sentence);
            // System.out.println(diversityScore);
            iterations[sequences.size() - 1] = 1;
            ithWeights = new double[weights.length][weights[0].length];
            for (int u = 0; u < weights.length; u++) {
                for (int v = 0; v < weights[0].length; v++) {
                    ithWeights[u][v] = weights[u][v];
                }
            }
            if (uniHamFlag) {
                ithWeights = updateWeightsWithUnigramHamming(sequences, ithWeights);
            }
            if (ppAttachFlag) {
                ithWeights = updateWeightsWithPpAttachment(sentence, sequences, ithWeights);
            }

        }
        return new KBest<Integer>(sequences, iterations);
    }

    // TODO: when used as public, diversityScore might return incorrect value...
    public void printTreeAndScore(SequenceResult<Integer> result, Conll
            sentence) {
        int pos = 0;
        for (int parent : result.getSequence()) {
            String parentPos;
            if (parent == 0) {
                parentPos = "ROOT";
            } else {
                sentence.getElements();
                sentence.getElements().get(parent - 1);
                parentPos = sentence.getElements().get(parent - 1).getCoarsePosTag();
            }
            String childPos = sentence.getElements().get(pos).getCoarsePosTag();
            System.out.println(parentPos + "(" + parent + ") -> " + childPos
                    + "(" + (pos + 1) + ")");
            pos += 1;
        }
        System.out.println();
    }
    // public void printTreeAndScore(KBest<Integer> result, Conll sentence) {
    // int pos = 0;
    // for (int parent : result.getSequence()) {
    // String parentPos;
    // if (parent == 0) {
    // parentPos = "ROOT";
    // } else {
    // sentence.getElements();
    // sentence.getElements().get(parent - 1);
    // parentPos = sentence.getElements().get(parent - 1).getCoarsePosTag();
    // }
    // String childPos = sentence.getElements().get(pos).getCoarsePosTag();
    // System.out.println(parentPos + " -> " + childPos);
    // pos += 1;
    // }
    // System.out.println();
    // }
}