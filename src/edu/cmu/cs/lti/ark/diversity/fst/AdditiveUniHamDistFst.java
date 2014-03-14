package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class AdditiveUniHamDistFst<T> {

	private static final double NIN = Double.NEGATIVE_INFINITY;
	
	public AdditiveUniHamDistFst() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Runs in O(ntk)
	 * @param kBest
	 * @param dd
	 * @param tagSet
	 * @return
	 */
	public SequenceResult<T> run(List<List<T>> kBest, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		List<Double> pi = new ArrayList<Double>();
		pi.add(0.0);
		
		List<T> bp = new ArrayList<T>();
		double maxScore = NIN;
		
		int n = kBest.get(0).size();
		for (int i = 0; i < n; i++) {
			maxScore = NIN;
			T bestTag = null;
			for (T tag : tagSet.getTags()) {
				double score = getLocalScore(kBest, tag, i);
				if (score > maxScore) {
					maxScore = score;
					bestTag = tag; 
				}
			}
			pi.add(maxScore);
		    bp.add(bestTag);
		}			
		return new SequenceResult<T>(bp, maxScore);
	}
	
	private double getLocalScore(List<List<T>> kBest, T tag, int pos) {
		double score = 0.0;
		for (List<T> best : kBest) {
			if (tag.equals(best.get(pos))) {
				score -= 1;
			}
		}
		return score;
	}
}
