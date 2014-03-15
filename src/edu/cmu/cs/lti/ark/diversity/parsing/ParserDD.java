package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.lti.ark.cle.Weighted;
import edu.cmu.cs.lti.ark.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.fst.Fst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.DdResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class ParserDD {

	private static final int ROOT = 0;
	private static final double MAX_ITERATIONS = 50;
	private static final double HAMMING_WT = 1.0;
	
	private double[][] weights;
	private DdHelper<Integer> helper = new DdHelper<Integer>();

	private List<Integer> getTree() {
		Weighted<Map<Integer, Integer>> result = ChuLiuEdmonds.getMaxSpanningTree(weights, ROOT);
		Map<Integer, Integer> value = result.val;
		List<Integer> tree = new ArrayList<Integer>();
		
		for (int child = 1; child <= value.size(); ++child) {
			tree.add(value.get(child));
		}
		return tree;
	}
	
	private void updateWeights(List<Map<Integer, Double>> dd) {
		for (Integer parent : dd.get(0).keySet()) {
		     for (int child = 0; child < dd.size(); child++) {
				weights[parent][child] -= dd.get(child).get(parent);
			}
		}
	}
	
	private static TagSet<Integer> createTagset(int n) {
		List<Integer> tags = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			tags.add(i);
		}
		return new TagSet<Integer>(tags);
	}
	
	public DdResult<Integer> run(double[][] givenWeights, int k) {
		weights = givenWeights;
		// n = length of sentence, n+1 = length of tagset
		final int n = weights[0].length - 1; 
		
		TagSet<Integer> tagSet = createTagset(n+1);
		
		Fst<Integer, List<Integer>> fst = new AdditiveUniHamDistFst<Integer>(HAMMING_WT);
				
		List<List<Integer>> kBestTrees = new ArrayList<List<Integer>>();
		List<Integer> bestTree = getTree();
		kBestTrees.add(bestTree);
		
		int iterations[] = new int[k];
		
		for (int i = 1; i < k; i++) {
			List<Integer> cleTree = null;
			List<Integer> fstTree = null;
			List<Map<Integer, Double>> dd = helper.init(n, tagSet);
			iterations[i] = 1;
			while (iterations[i] <= MAX_ITERATIONS) {
				double stepSize = 1.0 / Math.sqrt(iterations[i]);
							
				fstTree = fst.getSequence(kBestTrees, dd, tagSet).getSequence();	
				cleTree = getTree();
				
				if (helper.agree(cleTree, fstTree)) {
					kBestTrees.add(cleTree);
					break;
				} else {
					dd = helper.update(dd, cleTree, fstTree, stepSize, tagSet);
					updateWeights(dd);
				}
				iterations[i] += 1;
			}
			if (iterations[i] == MAX_ITERATIONS+1) { // did not converge
				iterations[i] = -1;
			}
		}	
		return new DdResult<Integer>(kBestTrees, iterations);		
	}
}
