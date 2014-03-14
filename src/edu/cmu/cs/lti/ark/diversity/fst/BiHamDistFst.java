package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class BiHamDistFst<T> implements Fst<T, T>{
	
	private List<Map<T, Double>> pi;
	private List<Map<T, T>> bp;
	
	private Map<T, Double> initialize(List<T> given, List<Map<T, Double>> dd, TagSet tagSet) {
		Map<T, Double> pi1 = new HashMap<T, Double>();
		return pi1;
	}
	
	private void run(List<T> given, List<Map<T, Double>> dd, TagSet tagSet) {
		pi = new ArrayList<Map<T,Double>>();
		pi.add(initialize(given, dd, tagSet));
		
	}

	@Override
	public SequenceResult<T> getSequence(List<T> given, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		// TODO Extract sequence result from backpointers
		return null;
	}

}
