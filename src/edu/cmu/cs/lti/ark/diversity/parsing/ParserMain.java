package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;

import edu.cmu.cs.lti.ark.diversity.parsing.ParserDD.Result;
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
		
		double duplicates = 0.0;
		//double turboConflicts = 0.0;
		double avgBestAcc = 0.0;
		double avgSecondAcc = 0.0;
		double convRate = 0.0;
		double avgIterations = 0.0;
		
		int k = 0;
		for (double[][] graph : weights) {
			/*if (graph.length > 10) {
				continue;
			}*/
			System.err.println("example no. "+ k);
						
			Result result = ParserDD.run(graph);
			if (result.iterations == -1) {
				System.err.println("Does NOT converge");
				continue;
			}
			
			avgIterations += result.iterations;
			if (result.fstTree.equals(result.bestTree)) {
				duplicates += 1;
				//System.err.println("duplicate");
			}
			convRate += 1;
			//System.err.println("Converges in " + result.iterations + " iterations\n");
			
			// could be buggy but assuming graphs are all sufficiently structured to be different
			List<Integer> goldParse = goldDepParses.get(weights.indexOf(graph));
			avgBestAcc += evaluate(goldParse, result.bestTree);
			avgSecondAcc += evaluate(goldParse, result.fstTree);
			
			System.out.println("gold = " + goldParse);
			System.out.println("best = " + result.bestTree);
			System.out.println("next = " + result.fstTree);
			
			k += 1;
			//break;
		}
		
		System.out.println("% of duplicates = " + duplicates*100/convRate);
		System.out.println("Accuracy of best = " + avgBestAcc/convRate);
		System.out.println("Accuracy of second best = " + avgSecondAcc/convRate);
		System.out.println("Convergence rate = " + convRate/k);
		System.out.println("Average Iterations = " + avgIterations/convRate);
		//System.out.println("Turbo conflicts = " + turboConflicts*100/k);
	}
}
