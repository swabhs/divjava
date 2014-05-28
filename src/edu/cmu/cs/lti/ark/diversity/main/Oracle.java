package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

import com.google.common.collect.Lists;

public class Oracle<T> {

    private Performance<T> performance = new Performance<T>();
    private List<SequenceResult<T>> bestResults;

    public List<SequenceResult<T>> getBestResults() {
        return bestResults;
    }

    public double evaluate(List<List<T>> gold, List<KBest<T>> results, int K) {
        bestResults = Lists.newArrayList();
        double totAcc = 0.0;

        int validExamples = 0; // #examples with a k-best list of size >= K
        // for every example, return the best accuracy till length k
        for (int example = 0; example < results.size(); example++) {
            KBest<T> result = results.get(example);
            validExamples++;

            double maxAcc = performance.evaluateAccuracy(
                    gold.get(example), result.kBest.get(0).getSequence());
            SequenceResult<T> bestResult = result.kBest.get(0);
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
                    bestResult = result.kBest.get(k);
                }
            }
            bestResults.add(bestResult);
            totAcc += maxAcc;
        }
        return totAcc / validExamples;
    }
}
