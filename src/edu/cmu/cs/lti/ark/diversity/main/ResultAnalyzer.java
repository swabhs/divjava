package edu.cmu.cs.lti.ark.diversity.main;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultAnalyzer<T> {

    private List<KBest<T>> predictions;
    private List<List<T>> goldParses;

    private int k;

    private Performance<T> performance = new Performance<T>();
    private DecimalFormat df = new DecimalFormat("#.##");

    public ResultAnalyzer(List<KBest<T>> predictions, List<List<T>> goldParses, int k) {
        this.predictions = predictions;
        this.goldParses = goldParses;
        this.k = k;
    }

    public void analyze(double hammingWt) {
        int trueK[] = calculateTrueK();
        double effK[] = calculateEffectiveK(trueK);
        double avgAcc[] = calculateAverageAccuracy(trueK);
        double oracleAcc[] = calculateOracleAccuracy();
        double genOracleAcc[] = calculateGenerousOracleAccuracy();
        double avgIterations[] = calculateAverageIterations(trueK);
        double duplicates = calculatePercentDuplicates();

        System.out
                .println("convrate" + (hammingWt * 1000) + "=c(" + Arrays.toString(trueK) + ")\n");
        System.out.println("EffK" + (hammingWt * 1000) + "=c(" + Arrays.toString(effK) + ")\n");

        System.out.println("avgacc" + (hammingWt * 1000) + "=c(" + Arrays.toString(avgAcc) + ")\n");
        System.out.println("oracle" + (hammingWt * 1000) + "=c(" + Arrays.toString(oracleAcc)
                + ")\n");
        System.out.println("genoracle" + (hammingWt * 1000) + "=c(" + Arrays.toString(genOracleAcc)
                + ")\n");

        System.out.println("it" + (hammingWt * 1000) + "=c(" + Arrays.toString(avgIterations)
                + ")\n");
        // System.out.println("% examples containing duplicates:\n" + duplicates
        // + "\n");
    }

    /** Finds the average of the actual size of the k-best list returned */
    private int[] calculateTrueK() {
        int trueK[] = new int[k];
        for (int example = 0; example < goldParses.size(); example++) {
            KBest<T> result = predictions.get(example);

            for (int i = 0; i < result.kBest.size(); i++) {
                trueK[i] += 1;
            }
        }
        return trueK;
    }

    private double[] calculateAverageAccuracy(int trueK[]) {
        double accuracies[] = new double[k];

        for (int example = 0; example < goldParses.size(); example++) {
            KBest<T> result = predictions.get(example);
            List<T> goldParse = goldParses.get(example);

            for (int i = 0; i < result.kBest.size(); i++) {
                accuracies[i] += performance.evaluateAccuracy(goldParse, result.kBest.get(i));
            }
        }
        for (int i = 0; i < k; i++) {
            accuracies[i] = Double.valueOf(df.format(accuracies[i] / trueK[i]));
        }
        return accuracies;
    }

    private double[] calculateOracleAccuracy() {
        Oracle<T> oracle = new Oracle<T>();
        double oracleAcc[] = new double[k];
        for (int i = 0; i < k; i++) {
            oracleAcc[i] = Double.valueOf(df.format(oracle.evaluate(goldParses, predictions, i)));
        }
        return oracleAcc;
    }

    private double[] calculateGenerousOracleAccuracy() {
        GenerousOracle<T> genOracle = new GenerousOracle<T>();
        double genAcc[] = new double[k];
        for (int i = 0; i < k; i++) {
            genAcc[i] = Double
                    .valueOf(df.format(genOracle.evaluate(goldParses, predictions, i + 1)));
        }
        return genAcc;
    }

    private double[] calculateAverageIterations(int trueK[]) {
        double avgIterations[] = new double[k];
        for (int example = 0; example < predictions.size(); example++) {
            KBest<T> result = predictions.get(example);
            for (int i = 0; i < result.kBest.size(); i++) {
                avgIterations[i] += result.iterations[i];
            }
        }
        for (int i = 0; i < k; i++) {
            avgIterations[i] = Double.valueOf(df.format(avgIterations[i] / trueK[i]));
        }
        return avgIterations;
    }

    public double[] calculateEffectiveK(int trueK[]) {
        double effK[] = new double[k];

        for (int exampleNum = 0; exampleNum < predictions.size(); exampleNum++) {
            KBest<T> prediction = predictions.get(exampleNum);

            for (int i = 0; i < prediction.kBest.size(); i++) {
                Set<List<T>> uniqueSeqs = new HashSet<List<T>>(prediction.kBest.subList(0, i + 1));
                // System.out.print(uniqueSeqs.size() + "...");
                // if (uniqueSeqs.size() == 2) {
                // System.out.println(exampleNum + "size = " +
                // prediction.kBest.get(0).size());
                //
                // }
                effK[i] += uniqueSeqs.size();
            }
            // System.out.println();
        }

        for (int i = 0; i < k; i++) {
            effK[i] = Double.valueOf(df.format(effK[i] / trueK[i]));
            // Assuming each solution is of size k
        }
        return effK;
    }

    private int calculatePercentDuplicates() {
        int exWithDuplicates = 0;
        for (int example = 0; example < predictions.size(); example++) {
            Set<List<T>> uniqueSeqs = new HashSet<List<T>>(predictions.get(example).kBest);
            if (uniqueSeqs.size() != k) {
                exWithDuplicates += 1;
            }
        }
        return (100 * exWithDuplicates / predictions.size());
    }
}
