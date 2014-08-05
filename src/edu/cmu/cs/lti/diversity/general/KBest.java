package edu.cmu.cs.lti.diversity.general;

import java.util.List;

public class KBest<T> {

    public List<SequenceResult<T>> kBest;
    public int[] iterations;

    public KBest(List<SequenceResult<T>> kBest, int[] iterations) {
        this.kBest = kBest;
        this.iterations = iterations;
    }

}
