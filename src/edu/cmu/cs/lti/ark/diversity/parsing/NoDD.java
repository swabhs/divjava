package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.DdResult;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;

public class NoDD {
	
	/*private static String weightsFileName = "data/dev_edges.weights";
	private static String depsFileName = "data/dev.deps";

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
			/*System.err.println("example no. "+ example);
			
			
			// Assuming graphs are all sufficiently structured to be different
			List<Integer> goldParse = goldDepParses.get(weights.indexOf(graph));
			
			accuracies[0] += ParserMain.evaluate(goldParse, result.kBest.get(0));
			convRates[0] += 1;
			avgIterations[0] += 1;
			System.out.println("1 best = " + result.kBest.get(0));
			
			boolean hasDuplicates = false;
			for (int i = 1; i < k; i++) {
				if (result.iterations[i] == -1) {
					System.err.println(i + " best does NOT converge");
					continue;
				}
				
				if (result.kBest.get(i).equals(result.kBest.get(i-1))) {
					 hasDuplicates = true;
				}
				accuracies[i] += ParserMain.evaluate(goldParse, result.kBest.get(i));
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
				
	}*/

}
