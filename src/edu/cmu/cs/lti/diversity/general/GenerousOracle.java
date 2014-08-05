package edu.cmu.cs.lti.diversity.general;

import java.util.List;

/**
 * Rewards if correct tag has been assigned to position at least once in the
 * k-best list
 */
public class GenerousOracle<T> {

    private boolean seen[];

    private void checkPosition(List<T> goldParse, List<T> parse) {
        int pos = 0;
        for (T parent : goldParse) {
            if (parent.equals(parse.get(pos))) {
                seen[pos] = true;
            }
            pos += 1;
        }
    }

    public double evaluate(List<List<T>> gold, List<KBest<T>> results, int K) {
        double avgAcc = 0.0;

        int validExamples = 0;
        for (int exampleNum = 0; exampleNum < results.size(); exampleNum++) {
            KBest<T> result = results.get(exampleNum);
            if (result.kBest.size() < K) {
                continue;
            }
            validExamples++;

            int n = result.kBest.get(0).getSequence().size();
            seen = new boolean[n];

            for (int k = 0; k < K; k++) {
                checkPosition(gold.get(exampleNum), results.get(exampleNum).kBest.get(k)
                        .getSequence());
            }
            double acc = 0.0;
            for (int j = 0; j < n; j++) {
                if (seen[j] == true) {
                    acc += 1;
                }
            }
            avgAcc += acc / n;

        }
        return avgAcc / validExamples;
    }

}
