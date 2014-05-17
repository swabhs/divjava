package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public interface Fst<T, U> {

    public SequenceResult<T> getResult(List<U> given, List<Map<T, Double>> dd, TagSet<T> tagSet);

    public double getFstOnlyScore(List<T> sequence, List<List<T>> kBest);

}
