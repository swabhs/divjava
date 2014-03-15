package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class UniHamDistFst<T> implements Fst<T, T>{
	
	private static final double NIN = Double.NEGATIVE_INFINITY;
	
	private List<Double> pi;
	private List<T> bp;
	
	private final double hammingWeight;
	
	public UniHamDistFst(double hammingWeight) {
		this.hammingWeight = hammingWeight;
	}
	
	private void init() {
		pi = new ArrayList<Double>();
		pi.add(0.0);
		
		bp = new ArrayList<T>();
	}
		
	/** Runs in O(nt)*/
	private double run(List<T> bestSeq, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		init();
		double maxScore = NIN;
		
		int n = bestSeq.size();
		for (int i = 0; i < n; i++) {
			maxScore = NIN;
			T bestTag = null;
			List<T> allTags = tagSet.getTags();
			for (T label : allTags) {
				double localScore = getLocalScore(label, bestSeq.get(i));
				double score = localScore + dd.get(i).get(label);
				if (score > maxScore) {
					maxScore = score;
					bestTag = label;
				}
			}
			pi.add(maxScore);
			bp.add(bestTag);
		}
		return maxScore;
	}
	
	private double getLocalScore(T label, T bestLabel) {
		if (label.equals(bestLabel)) {
			return -hammingWeight;
		} else {
			return 0.0;
		}
	}
	
	public SequenceResult<T> getSequence(List<T> given, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		double score = run(given, dd, tagSet);
		return new SequenceResult<T>(bp, score);
	}

}
