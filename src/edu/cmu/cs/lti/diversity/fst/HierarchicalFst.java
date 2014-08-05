package edu.cmu.cs.lti.diversity.fst;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.diversity.general.SequenceResult;

public interface HierarchicalFst {

    public SequenceResult<Integer> getResult(
            List<SequenceResult<Integer>> kBest, List<Map<Integer, Map<Integer, Double>>> dd);

    public double getLocalScore(
            List<SequenceResult<Integer>> kBest, int parent, int sibling, int pos);

    public double getFstOnlyScore(
            SequenceResult<Integer> sequence, List<SequenceResult<Integer>> kBest);
}
