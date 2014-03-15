package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public interface Fst<T,U> {
	
	public SequenceResult<T> getSequence(List<U> given, List<Map<T, Double>> dd, TagSet<T> tagSet);

}
