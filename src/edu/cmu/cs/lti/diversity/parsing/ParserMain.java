package edu.cmu.cs.lti.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.diversity.general.KBest;
import edu.cmu.cs.lti.diversity.utils.Conll;
import edu.cmu.cs.lti.diversity.utils.Conll.ConllElement;
import edu.cmu.cs.lti.diversity.utils.DataReader;
import edu.cmu.cs.lti.diversity.utils.DataWriter;
import edu.cmu.cs.lti.diversity.utils.MyFileUtils;

public class ParserMain {
    private static String dataType; // = "data/parsing/amr.test";
    // private static final String conllFileName = dataType + ".conll";
    // private static final String weightsFileName = dataType + ".wts";
    // private static final String labelsFileName = dataType + ".labels";
    // private static String outDirectory = dataType + "/out_pp/";

    private static String conllFileName;
    private static String weightsFileName;
    private static String labelsFileName;
    private static String outDirectory;

    // runtime options
    private static final int K = 100;

    // diversity metrics
    private static final boolean kbest = false;
    private static final boolean arc = false;
    private static final boolean pp = false;
    private static final boolean sibling = true;
    private static final boolean grand = false;

    // DD options
    private static final double initialDdStepSize = 0.1; // value <= hammingWt
    private static final int maxDdIterations = 100;
    private static final boolean useParseAnyway = true;

    // hyperparameters
    private static final double bestHammingWt = 0.1;
    private static final double bestPpWt = 0.005;

    // TODO: this is stupid, better way?
    private static List<Integer> visitedExamples;

    public static void main(String[] args) {
        dataType = args[0];
        conllFileName = dataType + ".conll";
        weightsFileName = dataType + ".wts";
        labelsFileName = dataType + ".labels";
        writingMain();
    }

    static void writingMain() {
        List<double[][]> allWeights = DataReader.readEdgeWeights(weightsFileName);
        List<Conll> conlls = MyFileUtils.readConllFile(conllFileName);

        List<KBest<Integer>> predictions = null;
        if (kbest) {
            predictions = exactKBest(allWeights);
            outDirectory = dataType + "/out_kbest/";
        } else if (arc) {
            predictions = runCostAugParser(conlls, allWeights, bestHammingWt, bestPpWt);
            outDirectory = dataType + "/out_arc/";
        } else if (pp) {
            predictions = runCostAugParser(conlls, allWeights, bestHammingWt, bestPpWt);
            outDirectory = dataType + "/out_pp/";
        } else if (sibling) {
            predictions = runDdParser(allWeights, bestHammingWt, true);
            outDirectory = dataType + "/out_sib/";
        } else if (grand) {
            predictions = runDdParser(allWeights, bestHammingWt, false);
            outDirectory = dataType + "/out_grand/";
        }

        // List<List<Integer>> allGold = getGoldTrees(conlls);
        // List<List<Integer>> visitedGold = new ArrayList<List<Integer>>();
        // for (int example : visitedExamples) {
        // visitedGold.add(allGold.get(example));
        // }
        //
        // ResultAnalyzer<Integer> analyzer =
        // new ResultAnalyzer<Integer>(predictions, visitedGold, K);
        // analyzer.analyze(bestHammingWt);

        List<String[][]> labels = DataReader.readEdgeLabels(labelsFileName);
        DataWriter.prepareConll(predictions, conlls, labels, K, outDirectory);
    }

    static void tuningMain() {
        // List<double[][]> allWeights =
        // DataReader.readEdgeWeights(weightsFileName);
        // List<Conll> conlls = FileUtils.readConllFile(conllFileName);
        //
        // double bestOracle = Double.NEGATIVE_INFINITY;
        // double besthm = 0.0;
        // double bestpp = 0.0;
        // double count = 0.0;
        // for (double hm = 0.00; hm < 1.00025; hm += 0.025) {
        // for (double pp = 0.00; pp < 1.00025; pp += 0.025) {
        // count += 1.0;
        // System.err.println((count / 16) + " % done...");
        // System.err.println(
        // "\n#Cost-augmented: alpha = " + hm + " ppWt = " + pp);
        //
        // List<KBest<Integer>> predictions;
        // if (useDD) {
        // predictions = runDdParser(allWeights, hm, useSib);
        // } else {
        // predictions = runCostAugParser(conlls, allWeights, hm, pp);
        // }
        //
        // List<List<Integer>> allGold = getGoldTrees(conlls);
        // List<List<Integer>> visitedGold = new
        // ArrayList<List<Integer>>();
        // for (int example : visitedExamples) {
        // visitedGold.add(allGold.get(example));
        // }
        //
        // ResultAnalyzer<Integer> analyzer =
        // new ResultAnalyzer<Integer>(predictions, visitedGold, K);
        // double oracle = analyzer.analyze(hm);
        // if (oracle > bestOracle) {
        // bestOracle = oracle;
        // besthm = hm;
        // bestpp = pp;
        // System.err.println("\ncurrent best oracle = " + oracle);
        // System.out.println("best hamming: " + besthm + "\nbest pp: " +
        // bestpp);
        // }
        // System.err.println("%error PP-attach = "
        // + ResultAnalyzer.calcPpErrors(conlls, visitedGold,
        // predictions, K));
        // }
        // }
        // System.out.println("best hamming: " + besthm + "\nbest pp: " +
        // bestpp);
    }

    /** Runs DD-based Diverse Parsing */
    static List<KBest<Integer>> runDdParser(
            List<double[][]> allWeights, double hammingWt, boolean useSib) {
        List<KBest<Integer>> predictions = new ArrayList<KBest<Integer>>();
        // DdParser parser = new DdParser(
        // hammingWt, K, initialDdStepSize, useParseAnyway, maxDdIterations);
        Dd parser;
        if (useSib) {
            parser = new DdSibParser(
                    hammingWt, K, initialDdStepSize, useParseAnyway, maxDdIterations);
        } else {
            parser = new DdGrandParser(
                    hammingWt, K, initialDdStepSize, useParseAnyway, maxDdIterations);
        }

        visitedExamples = new ArrayList<Integer>();
        System.err.print("#");
        System.err.println(allWeights.size());
        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 1 == 0)
                System.err.println("#" + example + "... len = " + allWeights.get(example).length);

            KBest<Integer> result = parser.runDualDecomposition(allWeights.get(example));
            predictions.add(result);
            visitedExamples.add(example);
        }
        System.err.println();
        return predictions;
    }
    /** Runs diverse parsing using a cost-augmented CLE */

    static List<KBest<Integer>> runCostAugParser(List<Conll> conlls, List<double[][]> allWeights,
            double hammingWt, double ppWt) {
        List<KBest<Integer>> predictions = Lists.newArrayList();
        CostAugmentedParser parser = new CostAugmentedParser(hammingWt, arc, ppWt, pp);

        visitedExamples = new ArrayList<Integer>();
        System.err.print("#");
        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 100 == 0)
                System.err.print(example + "...");

            KBest<Integer> kDivBest = parser.run(conlls.get(example), allWeights.get(example), K);
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
        System.err.print("#");
        for (int example = 0; example < allWeights.size(); example++) {
            if (example % 100 == 0) {
                System.err.print(example + "...");
            }
            predictions.add(CleCaller.getKBestTrees(allWeights.get(example), K));
            visitedExamples.add(example);
        }
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
