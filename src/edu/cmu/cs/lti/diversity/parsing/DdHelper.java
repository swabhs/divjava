package edu.cmu.cs.lti.diversity.parsing;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.ark.cle.Pair;
import edu.cmu.cs.lti.diversity.general.SequenceResult;

public interface DdHelper {

    public List<Map<Integer, Map<Integer, Double>>> init(int n);

    public Pair<List<Map<Integer, Map<Integer, Double>>>, double[][]> update(
            List<Map<Integer, Map<Integer, Double>>> dd,
            SequenceResult<Integer> cleResult,
            SequenceResult<Integer> fstResult,
            double stepSize,
            double[][] originalWeights);

    public boolean agree(
            SequenceResult<Integer> cleResult, SequenceResult<Integer> fstResult);

}
