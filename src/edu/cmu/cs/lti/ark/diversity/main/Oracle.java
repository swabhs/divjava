package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class Oracle<T> {

	public Oracle() {
		// TODO Auto-generated constructor stub
	}
	
	public double evaluate(List<T> goldParse, List<T> parse) {
		double accuracy = 0.0;
		if (goldParse.size() != parse.size()) {
			System.err.println("ERROR: results are of different size!");
			//System.exit(1);
			return 0.0;
		}
		int i = 0;
		
		for (T parent : goldParse) {
			if (parent.equals(parse.get(i))) {
				accuracy += 1.0;
			}
			i += 1;
		}
		accuracy /= parse.size();
		return accuracy;
	}
	
	public double evaluateOracle(List<DdResult<T>> results, List<List<T>> gold, int K) {
		double avgAcc = 0.0;
		int n = 0;
		for (int i = 0; i < results.size(); i++) {
			
			if (results.get(i).kBest.size() <= K) {
				continue;
			}
			n++;
			double maxAcc = -1.0;
			//int best = -1;
			for (int k = 0; k < K; k++) {
				double acc = evaluate(gold.get(i), results.get(i).kBest.get(k));
				if (acc > maxAcc) {
					maxAcc = acc;
					//best = k;
				}
			}
			if (maxAcc == -1.0) {
				System.out.println("WTF!");
			}
			avgAcc += maxAcc;
		}
		return avgAcc/n;
	}
	
	public void findEffectiveK(List<DdResult<T>> results) {
		
		for (int k = 1; k < results.get(0).kBest.size(); k++) {
			double effectiveK = 0.0;
			int n = 0;
			for (DdResult<T> result : results) {
				
				if (result.kBest.size() <= k) {
					continue;
				}
				n += 1;
				int numDuplicates = 0;
				for (int i = 1; i < k; i++) {
					for (int j = 0; j < i; j++) {
						if (result.kBest.get(i).equals(result.kBest.get(j))) {
							numDuplicates++;
							break;
						}
					}
				}
				//System.out.println("num of duplicates at length " + k + " = " + numDuplicates);
				effectiveK += (k - numDuplicates);
			}
			//System.out.println("effective k at length " + (k+1) + " = " + effectiveK/results.size());
			if (k!=results.get(0).kBest.size()-1) {
				System.out.print(effectiveK/n + ", ");
			} else {
				System.out.print(effectiveK/n);
			}
			
		}
		System.out.print(")\n");
	}

}
