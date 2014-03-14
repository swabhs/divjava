package edu.cmu.cs.lti.ark.diversity.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestInstance {
	
	private List<String> testTokens;

	public TestInstance(List<String> testTokens, Map<String, Double> hmm, TagSet<String> tagSet) {
		this.testTokens = replaceWithRare(testTokens, hmm, tagSet);
	}
	
	/**
	 * If a token is never seen in training data, replace with RARE
	 */
	private List<String> replaceWithRare(List<String> testTokens, 
			Map<String, Double> hmm, TagSet<String> tagSet) {
		List<String> tokens = new ArrayList<String>();
		for (String token : testTokens) {
			boolean seen = false;
			List<String> allTags = tagSet.getTags();
			for (String label : allTags) {
				String em = "em:" + label + "~>" + token;
				if (hmm.containsKey(em)) {
					seen = true;
					break;
				}
			}
			if (seen == false) {
				tokens.add("_RARE_");
			}
			else {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	List<String> getTestTokens() {
		return testTokens;
	}

}
