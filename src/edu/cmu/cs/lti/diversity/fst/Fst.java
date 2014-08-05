package edu.cmu.cs.lti.diversity.fst;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.diversity.general.SequenceResult;
import edu.cmu.cs.lti.diversity.general.TagSet;

public interface Fst<T, U> {

    public SequenceResult<T> getResult(
            List<SequenceResult<T>> given, List<Map<T, Double>> dd, TagSet<T> tagSet);

    public double getFstOnlyScore(SequenceResult<T> sequence, List<SequenceResult<T>> kBest);

}
