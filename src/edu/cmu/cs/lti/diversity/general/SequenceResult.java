package edu.cmu.cs.lti.diversity.general;

import java.util.List;

public class SequenceResult<T> {

    private List<T> sequence;
    private double score;
    // private double ddScore; // saves a score only when DD is used

    public SequenceResult(List<T> sequence, double score) {
        this.sequence = sequence;
        this.score = score;
        // this.ddScore = Double.NEGATIVE_INFINITY;
    }

    // public SequenceResult(List<T> sequence, double score, double ddScore) {
    // this.sequence = sequence;
    // this.score = score;
    // this.ddScore = ddScore;
    // }

    public List<T> getSequence() {
        return sequence;
    }

    public double getScore() {
        return score;
    }

    // public double getDdScore() {
    // return ddScore;
    // }
}
