package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class KBest<T> {

    public List<SequenceResult<T>> kBest;
    public int[] iterations;

    public KBest(List<SequenceResult<T>> kBest, int[] iterations) {
        this.kBest = kBest;
        this.iterations = iterations;
    }

}
