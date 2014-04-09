package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.fst.Fst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;
import edu.cmu.cs.lti.ark.diversity.main.TestInstance;
import edu.cmu.cs.lti.ark.diversity.main.Viterbi;

public class TaggerDD {
	
	private final double maxIterations = 350;
	private final double hammingWt = 1.0;

	private TagSet<String> tagSet;
    private Map<String, Double> hmm;
    private DdHelper<String> helper = new DdHelper<String>();
    
	public TaggerDD(TagSet<String> tagSet, Map<String, Double> hmm) {
		this.tagSet = tagSet;
		this.hmm = hmm;
	}
	
	public KBest<String> run(List<String> tokens, int k) {
		final int n = tokens.size();
		final TestInstance instance = new TestInstance(tokens, hmm, tagSet);
				
		Viterbi viterbi = new Viterbi(tagSet, hmm);
		/*List<String> bestTags = viterbi.getTagSeq(instance, null).getSequence();
				
		Fst<String, String> fst = new UniHamDistFst<String>(hammingWt);
		int iter = 1;
		while (iter <= maxIterations) {
			double stepSize = 1.0 / Math.sqrt(iter);
			//System.out.println(iter);
			List<String> tags2 = fst.getSequence(bestTags, dd, tagSet).getSequence();
			List<String> tags1 = viterbi.getTagSeq(instance, dd).getSequence();
						
			if (helper.agree(tags1, tags2)) {
				return new Result(iter, bestTags, tags2);
			} else {
				helper.update(dd, tags1, tags2, stepSize, tagSet);
			}
			iter += 1;
		}
		return new Result(-1, bestTags, null);	*/
		
		Fst<String, List<String>> fst = new AdditiveUniHamDistFst<String>(hammingWt);
		List<List<String>> kBestTagSeqs = new ArrayList<List<String>>();
		List<String> bestTags = viterbi.getTagSeq(instance, null).getSequence();
		kBestTagSeqs.add(bestTags);
		
		int iterations[] = new int[k];
		
		for (int i = 1; i < k; i++) {
			List<String> viterbiSeq = null;
			List<String> fstSeq = null;
			List<Map<String, Double>> dd = helper.init(n, tagSet);
			
			iterations[i] = 1;
			while (iterations[i] <= maxIterations) {
				double stepSize = 1.0 / Math.sqrt(iterations[i]);
				
				fstSeq = fst.getSequence(kBestTagSeqs, dd, tagSet).getSequence();
				viterbiSeq = viterbi.getTagSeq(instance, dd).getSequence();
				
				if (helper.agree(viterbiSeq, fstSeq)) {
					kBestTagSeqs.add(viterbiSeq);
					break;
				} else {
					dd = helper.update(dd, viterbiSeq, fstSeq, stepSize, tagSet);
				}
				iterations[i] += 1;
			}
			if (iterations[i] == maxIterations+1) { // did not converge
				iterations[i] = -1;
				break;
			}
		}
		return new KBest<String>(kBestTagSeqs, iterations);
	}
}
