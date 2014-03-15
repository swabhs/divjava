package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class AdditiveUniHamDistFst<T> implements Fst<T, List<T>>{

	private static final double NIN = Double.NEGATIVE_INFINITY;
	
	private List<Double> pi;
	private List<T> bp;
	private double maxScore = NIN;
	
	private final double hammingWeight;
	
	public AdditiveUniHamDistFst(double hammingWeight) {
		this.hammingWeight = hammingWeight;
	}
	
	public void init() {
		pi = new ArrayList<Double>();
		pi.add(0.0);
		
		bp = new ArrayList<T>();
	}
	
	/** Runs in O(ntk) */
	public void run(List<List<T>> kBest, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		init();
				
		int n = kBest.get(0).size();
		for (int i = 0; i < n; i++) {
			maxScore = NIN;
			T bestTag = null;
			for (T tag : tagSet.getTags()) {
				double score = getLocalScore(kBest, tag, i) + dd.get(i).get(tag);
				if (score > maxScore) {
					maxScore = score;
					bestTag = tag; 
				}
			}
			pi.add(maxScore);
		    bp.add(bestTag);
		}			
	}
	
	private double getLocalScore(List<List<T>> kBest, T tag, int pos) {
		double score = 0.0;
		for (List<T> best : kBest) {
			if (tag.equals(best.get(pos))) {
				score -= hammingWeight;
			}
		}
		return score;
	}
	
	public SequenceResult<T> getSequence(
			List<List<T>> kBest, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		run(kBest, dd, tagSet);
		return new SequenceResult<T>(bp, maxScore);
	}
}
