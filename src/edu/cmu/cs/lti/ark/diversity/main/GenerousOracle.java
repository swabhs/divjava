package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

/** Rewards if correct tag has been assigned to position at least once in the k-best list */
public class GenerousOracle<T> {
	
	private boolean seen[];

	private void evaluate(List<T> goldParse, List<T> parse) {
		if (goldParse.size() != parse.size()) {
			System.err.println("ERROR: results are of different size!");
			System.exit(1);
		}
		int i = 0;
		
		for (T parent : goldParse) {
			if (parent.equals(parse.get(i))) {
				seen[i]=true;
			}
			i += 1;
		}
	}
	
	public double evaluateOracle(List<DdResult<T>> results, List<List<T>> gold, int K) {
		double avgAcc = 0.0;
		int example = 0;
		for (int i = 0; i < results.size(); i++) {
			DdResult<T> result = results.get(i);
			if (result.kBest.size() <= K) {
				continue;
			}
			int n = result.kBest.get(0).size();
			seen = new boolean[n];
						
			for (int k = 0; k < K; k++) {
				evaluate(gold.get(i), results.get(i).kBest.get(k));
			}
			double acc = 0.0;
			for (int j=0; j <n; j++) {
				if (seen[j] == true) { acc += 1;}
			}
			avgAcc += acc/n;
			
			example++;
		}
		return avgAcc/example;
	}

}
