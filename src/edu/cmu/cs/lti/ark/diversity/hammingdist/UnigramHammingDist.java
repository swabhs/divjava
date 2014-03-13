package edu.cmu.cs.lti.ark.diversity.hammingdist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.TagSet;

public class UnigramHammingDist<T> {
	
	private static final double NIN = Double.NEGATIVE_INFINITY;
	
	public List<T> run(List<T> bestSeq, List<Map<T, Double>> dd, TagSet<T> tagSet) {
		List<Double> pi = new ArrayList<Double>();
		pi.add(0.0);
		List<T> bp = new ArrayList<T>();
		
		int n = bestSeq.size();
		for (int i = 0; i < n; i++) {
			double maxScore = NIN;
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
		return bp;
	}
	
	//TODO: how to set the Hamming distance weight
	private double getLocalScore(T label, T bestLabel) {
		if (label.equals(bestLabel)) {
			return -5.0;
		} else {
			return 0.0;
		}
	}

}
