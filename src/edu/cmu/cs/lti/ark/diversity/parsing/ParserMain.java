package edu.cmu.cs.lti.ark.diversity.parsing;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.ark.diversity.main.DdResult;
import edu.cmu.cs.lti.ark.diversity.main.GenerousOracle;
import edu.cmu.cs.lti.ark.diversity.main.Oracle;
import edu.cmu.cs.lti.ark.diversity.main.ResultAnalyzer;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;

public class ParserMain {
	
	private static String weightsFileName = "data/dev_edges.weights";
	private static String depsFileName = "data/dev.deps";
	//private static String depsFileName = "data/turbo_basic_dev.pred";
	
	private static Oracle<Integer> oracle = new Oracle<Integer>();
	private static GenerousOracle<Integer> genOracle = new GenerousOracle<Integer>();
	
	public static void analyse(double hammingWt) {
		List<double[][]> weights = DataReader.readEdgeWeights(weightsFileName);
		List<List<Integer>> goldDepParses = DataReader.readDepParse(depsFileName);
		
		final int k = 15;
		//final double hammingWt = 0.05;
		final int round = (int)(hammingWt*10000);
		
		double exWithDuplicates = 0.0;
		double accuracies[] = new double[k];
		double genAcc[] = new double[k];
		
		double convRates[] = new double[k]; 
		double avgIterations[] = new double[k];
		
		List<DdResult<Integer>> results = new ArrayList<DdResult<Integer>>();
		
		for(int example = 0; example < weights.size(); example ++) {
			double[][] graph = weights.get(example);
						
			ParserDD parserdd = new ParserDD(hammingWt);
			DdResult<Integer> result = parserdd.run(graph, k);
			results.add(result);
		}
		
		ResultAnalyzer<Integer> analyzer = new ResultAnalyzer<Integer>(results, goldDepParses, k);
		analyzer.analyzeThis(hammingWt);
			// Assuming graphs are all sufficiently structured to be different
			
			/*List<Integer> goldParse = goldDepParses.get(example);
			
			accuracies[0] += oracle.evaluate(goldParse, result.kBest.get(0));
			convRates[0] += 1;
			avgIterations[0] += 1;
			genAcc[0] += genOracle.evaluateOracle(results, goldDepParses, 1);
			System.out.println("1 best = " + result.kBest.get(0));
			
			boolean hasDuplicates = false;
			for (int i = 1; i < k; i++) {
				if (result.iterations[i] <= 0) {
					System.err.println(i + "th best does NOT converge");
					continue;
				}
				convRates[i] += 1;
			}
			valid += 1;
			
			for (int i = 1; i < result.kBest.size(); i++) {
				
				if (result.kBest.get(i).equals(result.kBest.get(i-1))) {
					 hasDuplicates = true;
				}
				accuracies[i] += oracle.evaluate(goldParse, result.kBest.get(i));
								
				avgIterations[i] += result.iterations[i];		
				//System.out.println(i+1 + " best = " + result.kBest.get(i));		
			}
			if (hasDuplicates) {
				exWithDuplicates += 1.0;
			}
			//System.out.println();
			
			break;
		}
		//System.out.println("% examples with duplicates =" + exWithDuplicates*100/example);
		
		System.out.println("\nalpha = " + hammingWt + "\n");
		
		//System.out.println("\noracle experiment\n------------------------------");
		System.out.print("oracle"+round+"=c(");
		for (int i = 1; i < k-1; i ++) {
			double orAcc = oracle.evaluateOracle(results, goldDepParses, i);
			//System.out.println("Oracle acc at " + i + " = " + orAcc);
			System.out.print(orAcc + ", ");
		}
		System.out.print(oracle.evaluateOracle(results, goldDepParses, k-1)+")\n");
		
		//System.out.println("\neffective k\n-----------------------------------");
		System.out.print("effk"+round+"=c(");
		oracle.findEffectiveK(results);
		
		//System.out.println("\navg accuracy\n-------------------------");
		System.out.print("avgacc"+round+"=c(");
		for (int i = 0; i < k-1; i++) {
			System.out.println(accuracies[i]/convRates[i] + ", ");
		}
		System.out.print(accuracies[k-1]/convRates[k-1]+")\n");
		
		//System.out.println("\nconvergence\n------------------------------");
		System.out.print("convrate"+round+"=c(");
		for (int i = 0; i < k-1; i++) {
			System.out.print(convRates[i]*100/valid + ", ");
		}
		System.out.print(convRates[k-1]*100/valid +")\n");
		
		//System.out.println("\navg iterations\n-------------------------------------");
		System.out.print("it"+round+"=c(");
		for (int i = 0; i < k-1; i++) {
			System.out.print(avgIterations[i]/convRates[i] + ", ");
		}
		System.out.print(avgIterations[k-1]/convRates[k-1]+")\n");
		
		//System.out.println("\n generous oracle experiment\n------------------------------");
	    System.out.print("genoracle"+round+"=c(");
        for (int i = 1; i < k-1; i ++) {
			double orAcc = genOracle.evaluateOracle(results, goldDepParses, i);
					//System.out.println("Oracle acc at " + i + " = " + orAcc);
			System.out.print(orAcc + ", ");
		}
		System.out.print(genOracle.evaluateOracle(results, goldDepParses, k-1)+")\n");*/
				
	}
	
	public static void main(String[] args) {
		double hammingWts[] = new double[]{0.0};//{0.0, 0.005, 0.01, 0.05, 0.1, 0.5, 1.0, 2.0};
		int i = 1;
		for (double hammingWt : hammingWts) {
			analyse(hammingWt);
			i += 1;
		}
		
	}
}
