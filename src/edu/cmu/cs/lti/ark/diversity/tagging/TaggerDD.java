package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.fst.UniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;
import edu.cmu.cs.lti.ark.diversity.main.TestInstance;
import edu.cmu.cs.lti.ark.diversity.main.Viterbi;

public class TaggerDD {
	
	private final double maxIterations = 350;

	private TagSet<String> tagSet;
    private Map<String, Double> hmm;
    private DdHelper<String> helper;
    
	public TaggerDD(TagSet<String> tagSet, Map<String, Double> hmm) {
		this.tagSet = tagSet;
		this.hmm = hmm;
		this.helper = new DdHelper<String>();
	}
	
	public Result run(List<String> tokens) {
		final int n = tokens.size();
		final TestInstance instance = new TestInstance(tokens, hmm, tagSet);
		
		List<Map<String, Double>> dd = helper.init(n, tagSet);
		Viterbi viterbi = new Viterbi(tagSet, hmm);
		List<String> bestTags = viterbi.getTagSeq(instance, null).getSequence();
		UniHamDistFst<String> dist = new UniHamDistFst<String>();
		
		
		int iter = 1;
		while (iter <= maxIterations) {
			double stepSize = 1.0 / Math.sqrt(iter);
			//System.out.println(iter);
			List<String> tags2 = dist.run(bestTags, dd, tagSet);
			//System.out.println(tags2);
			
			List<String> tags1 = viterbi.getTagSeq(instance, dd).getSequence();
			//System.out.println(tags1);
			
			
			if (helper.agree(tags1, tags2)) {
				return new Result(iter, bestTags, tags2);
			} else {
				helper.update(dd, tags1, tags2, stepSize, tagSet);
			}
			//System.out.println();
			iter += 1;
		}
		return new Result(-1, bestTags, null);	
	}
	
	class Result {
		int iterations;
		List<String> bestTagSeq;
		List<String> secondBestTagSeq;
		
		public Result(int iterations, List<String> bestTagSeq,	List<String> secondBestTagSeq) {
			this.iterations = iterations;
			this.bestTagSeq = bestTagSeq;
			this.secondBestTagSeq = secondBestTagSeq;
		}
		
	}

}
