package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.ResultAnalyzer;
import edu.cmu.cs.lti.ark.diversity.utils.Conll;
import edu.cmu.cs.lti.ark.diversity.utils.Conll.ConllElement;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;
import edu.cmu.cs.lti.ark.diversity.utils.FileUtils;

public class ParserMain {
    private static String conllFileName = "data/parsing/ptb.dev.conll";
    private static String weightsFileName = "data/parsing/ptb.dev.wts";
    private static String labelsFileName = "data/parsing/ptb.dev.labels";

    // runtime options
    private static final int K = 500;
    private static final double hammingWt[] = new double[]{
            0.00075, 0.005, 0.0075, 0.010, 0.02};
    // 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1.0, 2.0};
    private static final double initialDdStepSize = 0.05; // value <= hammingWt
    private static final int maxDdIterations = 200;
    private static boolean useParseAnyway = true;
    private static boolean useDD = false;
    private static boolean useCamerini = true;

    // TODO: this is stupid, better way?
    private static List<Integer> visitedExamples;

    public static void main(String[] args) {
        List<double[][]> allWeights = DataReader.readEdgeWeights(weightsFileName);
        List<String[][]> labels = DataReader.readEdgeLabels(labelsFileName);
        List<Conll> conlls = FileUtils.readConllFile(conllFileName);
        assert conlls.size() == labels.size();

        for (double hm : hammingWt) {
            List<KBest<Integer>> predictions;
            if (useCamerini) {
                predictions = exactKBest(allWeights);
            } else {
                if (useDD) {
                    predictions = runDdParser(allWeights, hm);
                } else {
                    predictions = runCostAugParser(allWeights, hm);
                }
            }
            // assert predictions.size() == conlls.size();

            List<List<Integer>> allGold = getGoldTrees(conlls);
            List<List<Integer>> visitedGold = new ArrayList<List<Integer>>();
            for (int example : visitedExamples) {
                visitedGold.add(allGold.get(example));
            }

            ResultAnalyzer<Integer> analyzer =
                    new ResultAnalyzer<Integer>(predictions, visitedGold, K);
            analyzer.analyze(hm);
            if (useCamerini)
                break; // no need to loop over all hammingWt
        }
        // DataWriter.prepareConll(predictions, conlls, labels, K);
    }

    /** Runs DD-based Diverse Parsing */
    static List<KBest<Integer>> runDdParser(List<double[][]> allWeights, double hammingWt) {
        List<KBest<Integer>> predictions = new ArrayList<KBest<Integer>>();
        DdParser parser = new DdParser(
                hammingWt, K, initialDdStepSize, useParseAnyway, maxDdIterations);

        visitedExamples = new ArrayList<Integer>();
        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 1 == 0)
                System.err.print(example + "...");

            KBest<Integer> result = parser.runDualDecomposition(allWeights.get(example));
            predictions.add(result);
            visitedExamples.add(example);
            // break;
        }
        System.err.println();
        return predictions;
    }

    /** Runs diverse parsing using a cost-augmented CLE */
    static List<KBest<Integer>> runCostAugParser(List<double[][]> allWeights, double hammingWt) {
        List<KBest<Integer>> predictions = Lists.newArrayList();
        CostAugmentedParser parser = new CostAugmentedParser(hammingWt);
        System.err.println("Cost-augmented: alpha = " + hammingWt);
        visitedExamples = new ArrayList<Integer>();

        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 100 == 0)
                System.err.print(example + "...");

            KBest<Integer> kDivBest = parser.run(allWeights.get(example), K);
            predictions.add(kDivBest);
            visitedExamples.add(example);
        }
        return predictions;
    }

    /** Get exact k-best parses using (Camerini et.al. 1980) */
    static List<KBest<Integer>> exactKBest(List<double[][]> allWeights) {
        System.err.println("Running Camerini Baseline");
        List<KBest<Integer>> predictions = Lists.newArrayList();

        visitedExamples = Lists.newArrayList();
        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 100 == 0) {
                System.err.print(example + "...");
            }
            predictions.add(CleCaller.getKBestTrees(allWeights.get(example), K));
            visitedExamples.add(example);
        }
        System.err.println("\nDone!");
        return predictions;
    }

    /** Given a list of Conll objects, returns the gold standard trees */
    static List<List<Integer>> getGoldTrees(List<Conll> inputs) {
        List<List<Integer>> goldTrees = Lists.newArrayList();
        for (Conll input : inputs) {
            List<Integer> goldTree = Lists.newArrayList();
            for (ConllElement element : input.getElements()) {
                goldTree.add(element.getGoldParent());
            }
            goldTrees.add(goldTree);
        }
        return goldTrees;
    }
}
