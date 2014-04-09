package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.ResultAnalyzer;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;

public class ParserMain {

    private static String weightsFileName = "data/dev_edges.weights";
    private static String depsFileName = "data/dev.deps";
    // private static String depsFileName = "data/turbo_basic_dev.pred";

    private static final int K = 100;
    // private static final double HAMMING_WT = 1.0;

    private static List<Integer> visitedExamples;

    /** Runs DD-based Diverse Parsing */
    private static List<KBest<Integer>> ddMain(List<double[][]> edgeWeightsList, double HAMMING_WT) {
        List<KBest<Integer>> predictions = new ArrayList<KBest<Integer>>();

        ParserDD parserdd = new ParserDD(HAMMING_WT, K);

        visitedExamples = new ArrayList<Integer>();
        for (int example = 0; example < edgeWeightsList.size(); example++) {
            if (example % 25 == 0) {
                System.out.println();
            }
            System.err.print(example + "...");
            double[][] graph = edgeWeightsList.get(example);

            KBest<Integer> result = parserdd.run(graph);
            predictions.add(result);
            visitedExamples.add(example);
            // break;
        }
        return predictions;
    }

    /** Runs diverse parsing using only CLE */
    private static List<KBest<Integer>> ddLessMain(List<double[][]> edgeWeightsList,
            double HAMMING_WT) {
        List<KBest<Integer>> predictions = new ArrayList<KBest<Integer>>();
        NoDD noDD = new NoDD(HAMMING_WT);

        visitedExamples = new ArrayList<Integer>();
        for (int example = 0; example < edgeWeightsList.size(); example++) {
            double[][] weights = edgeWeightsList.get(example);

            KBest<Integer> kDivBest = noDD.run(weights, K);
            predictions.add(kDivBest);
            visitedExamples.add(example);
            // break;
        }
        return predictions;
    }

    public static void main(String[] args) {
        List<double[][]> weights = DataReader.readEdgeWeights(weightsFileName);
        double HAMMING_WT[] = new double[] { 0.5, 0.75, 1.0 };

        for (double hm : HAMMING_WT) {
            System.out.println("\n\nalpha = " + hm);
            List<KBest<Integer>> predictions = ddMain(weights, hm);
            // List<KBest<Integer>> predictions = ddLessMain(weights, hm);

            List<List<Integer>> allGold = DataReader.readDepParse(depsFileName);
            List<List<Integer>> visitedGold = new ArrayList<List<Integer>>();

            for (int example : visitedExamples) {
                visitedGold.add(allGold.get(example));
            }

            ResultAnalyzer<Integer> analyzer = new ResultAnalyzer<Integer>(predictions,
                    visitedGold, K);
            analyzer.analyze(hm);
        }
    }
}
