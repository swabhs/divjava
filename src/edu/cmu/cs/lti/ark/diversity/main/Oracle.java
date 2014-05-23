package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class Oracle<T> {

    Performance<T> performance = new Performance<T>();

    public double evaluate(List<List<T>> gold, List<KBest<T>> results, int K) {
        double totAcc = 0.0;

        int validExamples = 0; // #examples with a k-best list of size >= K
        // for every example, return the best accuracy till length k
        for (int example = 0; example < results.size(); example++) {
            KBest<T> result = results.get(example);
            validExamples++;

            double maxAcc = performance.evaluateAccuracy(
                    gold.get(example), result.kBest.get(0).getSequence());
            for (int k = 1; k < K; k++) {
                List<T> prediction;
                if (result.kBest.size() <= k) {
                    prediction = result.kBest.get(result.kBest.size() - 1).getSequence();
                    // continue;
                } else {
                    prediction = result.kBest.get(k).getSequence();
                }
                double acc = performance.evaluateAccuracy(gold.get(example), prediction);
                if (acc > maxAcc) {
                    maxAcc = acc;
                }
            }
            totAcc += maxAcc;
        }
        return totAcc / validExamples;
    }
}
