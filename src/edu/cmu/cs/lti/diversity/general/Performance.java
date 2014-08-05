package edu.cmu.cs.lti.diversity.general;

import java.util.List;

public class Performance<T> {

	public double evaluateAccuracy(List<T> goldParse, List<T> parse) {
		double accuracy = 0.0;
		assert goldParse.size() != parse.size();
		/*if (goldParse.size() != parse.size()) {
			System.err.println("ERROR: results are of different size!");
			return 0.0;
		}*/
		
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

}
