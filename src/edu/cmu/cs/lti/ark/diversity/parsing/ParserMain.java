package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.DdResult;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;

public class ParserMain {
	
	private static String weightsFileName = "data/dev_edges.weights";
	private static String depsFileName = "data/dev.deps";
	//private static String depsFileName = "data/turbo_basic_dev.pred";
	
	private static double evaluate(List<Integer> goldParse, List<Integer> parse) {
		double accuracy = 0.0;
		if (goldParse.size() != parse.size()) {
			System.err.println("ERROR: parses are of different size!");
			System.exit(1);
			return 0.0;
		}
		int i = 0;
		
		for (Integer parent : goldParse) {
			if (parent == parse.get(i)) {
				accuracy += 1.0;
			}
			i += 1;
		}
		accuracy /= parse.size();
		return accuracy;
	}
	
	public static void main(String[] args) {
		List<double[][]> weights = DataReader.readEdgeWeights(weightsFileName);
		List<List<Integer>> goldDepParses = DataReader.readDepParse(depsFileName);
		
		final int k = 5;
		
		double exWithDuplicates = 0.0;
		double accuracies[] = new double[k];
		
		double convRates[] = new double[k]; 
		double avgIterations[] = new double[k];
		
		int example = 0;
		for (double[][] graph : weights) {
			/*if (graph.length > 10) {
				continue;
			}*/
			System.err.println("example no. "+ example);
			
			ParserDD parserdd = new ParserDD();
			DdResult<Integer> result = parserdd.run(graph, k);
			// Assuming graphs are all sufficiently structured to be different
			List<Integer> goldParse = goldDepParses.get(weights.indexOf(graph));
			
			accuracies[0] += evaluate(goldParse, result.kBest.get(0));
			convRates[0] += 1;
			avgIterations[0] += 1;
			System.out.println("1 best = " + result.kBest.get(0));
			
			boolean hasDuplicates = false;
			for (int i = 1; i < k; i++) {
				if (result.iterations[i] == -1) {
					System.err.println(i + "th best does NOT converge");
					continue;
				}
				
				if (result.kBest.get(i).equals(result.kBest.get(i-1))) {
					 hasDuplicates = true;
				}
				accuracies[i] += evaluate(goldParse, result.kBest.get(i));
				convRates[i] += 1;
				//System.err.println(i + " best converges in " + result.iterations[i] + " iterations\n");
				avgIterations[i] += result.iterations[i];		
				System.out.println(i+1 + " best = " + result.kBest.get(i));		
			}
			if (hasDuplicates) {
				exWithDuplicates += 1.0;
			}
			System.out.println();
			example += 1;
			//break;
		}
		
		System.out.println("% of duplicates = " + exWithDuplicates*100/example);
		for (int i = 0; i < k; i++) {
			System.out.println(i+1 + "\n-----------------\n");
			System.out.println("avg accuracy = " + accuracies[i]/example);
			System.out.println("convergence  = " + convRates[i]*100/example);
			System.out.println("iterations   = " + avgIterations[i]/example);
		}
				
	}
}
