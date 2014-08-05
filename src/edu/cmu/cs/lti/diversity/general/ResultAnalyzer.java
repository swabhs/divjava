package edu.cmu.cs.lti.diversity.general;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.cmu.cs.lti.diversity.utils.Conll;
import edu.cmu.cs.lti.diversity.utils.Conll.ConllElement;

public class ResultAnalyzer<T> {
    private List<KBest<T>> predictions;
    private List<List<T>> gold;
    private int k;

    private Performance<T> performance = new Performance<T>();
    private DecimalFormat df = new DecimalFormat("#.####");

    public ResultAnalyzer(List<KBest<T>> predictions, List<List<T>> gold, int k) {
        this.predictions = predictions;
        this.gold = gold;
        this.k = k;
    }

    public double analyze(double hammingWt) {
        int trueK[] = calculateTrueK();
        double effK[] = calculateEffectiveK();
        double avgAcc[] = calculateAverageAccuracy(trueK);
        double oracleAcc[] = calculateOracleAccuracy();
        double genOrAcc[] = calculateGenerousOracleAccuracy();
        double avgIterations[] = calculateAverageIterations(trueK);

        int round = (int) (hammingWt * 1000);
        System.out.println("\nconv" + round + "=c(" + Arrays.toString(trueK)
                + ")\n");
        System.out.println("effK" + round + arrayAsStr(effK));
        System.out.println("avgacc" + round + arrayAsStr(avgAcc));
        System.out.println("oracle" + round + arrayAsStr(oracleAcc));
        System.out.println("genoracle" + round + arrayAsStr(genOrAcc));
        System.out.println("it" + round + arrayAsStr(avgIterations));

        // double duplicates = calculatePercentDuplicates();
        // System.out.println("% examples containing duplicates:\n" + duplicates
        // + "\n");
        return oracleAcc[oracleAcc.length - 1];
    }

    /** Finds the average of the actual size of the k-best list returned */
    private int[] calculateTrueK() {
        int trueK[] = new int[k];
        for (int example = 0; example < gold.size(); example++) {
            KBest<T> result = predictions.get(example);

            for (int i = 0; i < result.kBest.size(); i++) {
                if (result.iterations[i] != -1)
                    trueK[i] += 1;
            }
        }
        return trueK;
    }

    /** Calculates corpus average of the number of unique results seen at each k */
    public double[] calculateEffectiveK() {
        double effK[] = new double[k];

        for (int exampleNum = 0; exampleNum < predictions.size(); exampleNum++) {
            KBest<T> prediction = predictions.get(exampleNum);

            for (int i = 0; i < k; i++) {
                Set<SequenceResult<T>> uniqueSeqs = Sets.newHashSet();
                if (predictions.get(exampleNum).kBest.size() < i + 1) {
                    uniqueSeqs.addAll(predictions.get(exampleNum).kBest);
                } else {
                    uniqueSeqs.addAll(prediction.kBest.subList(0, i + 1));
                }
                effK[i] += uniqueSeqs.size();
            }
        }

        for (int i = 0; i < k; i++) {
            effK[i] = Double.valueOf(df.format(effK[i] / gold.size()));
            // Assuming each solution is of size k
        }
        return effK;
    }

    private double[] calculateAverageAccuracy(int trueK[]) {
        double accuracies[] = new double[k];

        for (int example = 0; example < gold.size(); example++) {
            KBest<T> result = predictions.get(example);
            List<T> goldParse = gold.get(example);

            for (int i = 0; i < result.kBest.size(); i++) {
                accuracies[i] += performance.evaluateAccuracy(
                        goldParse, result.kBest.get(i).getSequence());
            }
        }
        for (int i = 0; i < k; i++) {
            accuracies[i] = Double.valueOf(df.format(accuracies[i] / trueK[0]));
        }
        return accuracies;
    }

    private double[] calculateOracleAccuracy() {
        Oracle<T> oracle = new Oracle<T>();
        double oracleAcc[] = new double[k];
        for (int i = 0; i < k; i++) {
            oracleAcc[i] = Double.valueOf(df.format(oracle.evaluate(gold, predictions, i)));
        }
        return oracleAcc;
    }

    private double[] calculateGenerousOracleAccuracy() {
        GenerousOracle<T> genOracle = new GenerousOracle<T>();
        double genAcc[] = new double[k];
        for (int i = 0; i < k; i++) {
            genAcc[i] = Double
                    .valueOf(df.format(genOracle.evaluate(gold, predictions, i + 1)));
        }
        return genAcc;
    }

    private double[] calculateAverageIterations(int trueK[]) {
        double avgIterations[] = new double[k];
        for (int example = 0; example < predictions.size(); example++) {
            KBest<T> result = predictions.get(example);
            for (int i = 0; i < result.kBest.size(); i++) {
                if (result.iterations[i] != -1)
                    avgIterations[i] += result.iterations[i];
            }
        }
        for (int i = 0; i < k; i++) {
            avgIterations[i] = Double.valueOf(df.format(avgIterations[i] / trueK[0]));
        }
        return avgIterations;
    }

    private int calculatePercentDuplicates() {
        int exWithDuplicates = 0;
        for (int example = 0; example < predictions.size(); example++) {
            Set<SequenceResult<T>> uniqueSeqs = new HashSet<SequenceResult<T>>(
                    predictions.get(example).kBest);
            if (uniqueSeqs.size() != k) {
                exWithDuplicates += 1;
            }
        }
        return (100 * exWithDuplicates / predictions.size());
    }

    private String arrayAsStr(double arr[]) {
        String str = "=c(";
        for (int i = 0; i < arr.length - 1; i++) {
            str += arr[i] + ", ";
        }
        str += arr[arr.length - 1] + ")\n";
        return str;
    }

    /** Check for PP-attachment errors */
    public static double calcPpErrors(
            List<Conll> conlls, List<List<Integer>> golds, List<KBest<Integer>> results, int k) {
        Oracle<Integer> oracle = new Oracle<Integer>();
        oracle.evaluate(golds, results, k);
        List<SequenceResult<Integer>> bestResults = oracle.getBestResults();

        double errorPercentage = 0.0;
        int n = conlls.size();
        int totalPps = 0;
        for (int i = 0; i < n; i++) {
            Conll conll = conlls.get(i);
            int pos = 0;
            for (ConllElement ele : conll.getElements()) {
                if (ele.getCoarsePosTag().equals("IN")) {
                    totalPps += 1;
                    String goldTag;
                    String predTag;
                    if (golds.get(i).get(pos) == 0) {
                        goldTag = "ROOT";
                    } else {
                        goldTag = conll.getElements().get(
                                golds.get(i).get(pos) - 1).getCoarsePosTag();
                    }
                    if (bestResults.get(i).getSequence().get(pos) == 0) {
                        predTag = "ROOT";
                    } else {
                        predTag = conll.getElements().get(
                                bestResults.get(i).getSequence().get(pos) - 1)
                                .getCoarsePosTag();
                    }
                    if (goldTag.equals(predTag) == false) {
                        // // System.out.println(goldTag + "( " +
                        // golds.get(i).get(pos) + ") " + predTag +
                        // " (" + bestResults.get(i).getSequence().get(pos) +
                        // ") sentence :"
                        // + i);
                        errorPercentage += 1;
                    }
                }
                pos += 1;
            }
        }
        return errorPercentage / totalPps;
    }
}
