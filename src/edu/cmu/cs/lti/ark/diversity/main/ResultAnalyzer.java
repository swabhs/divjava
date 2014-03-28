package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class ResultAnalyzer<T> {
	
	private List<DdResult<T>> predictions;
	private List<List<T>> goldParses;
	
	private int k;
	
	private Oracle<T> oracle = new Oracle<T>();

	public ResultAnalyzer(List<DdResult<T>> predictions, List<List<T>> goldParses, int k) {
		this.predictions = predictions;
		this.goldParses = goldParses;
		this.k = k;
	}
	
	public void analyzeThis(double hammingWt) {
		
		double accuracies[] = calculateAverageAccuracy();
		/*double convRates[] = calculateConvergenceRate();
		double oracleAcc[] = calculateOracleAccuracy();
		double genOracleAcc[] = calculateGenerousOracleAccuracy();*/
		
		for (int i = 0; i < accuracies.length; i++) {
			System.out.println(accuracies[i]);
		}
	}
	
	private double[] calculateAverageAccuracy() {
		double accuracies[] = new double[k];
		double convrates[] = new double[k];
		
		for (int example = 0; example < goldParses.size(); example++) {
			DdResult<T> result = predictions.get(example);
			List<T> goldParse = goldParses.get(example);
			
			
	        for (int i = 0; i < result.kBest.size(); i++) {
				accuracies[i] += oracle.evaluate(goldParse, result.kBest.get(i));
				convrates[i] += 1;
	        }
		}
		for (int i = 0; i < k; i++) {
			System.out.println(convrates[i]);
        	accuracies[i] /= convrates[i];
        }
		return accuracies;
	}
	
	private double[] calculateOracleAccuracy() {
		return null;
	}
	
	private double[] calculateGenerousOracleAccuracy() {
		double genAcc[] = new double[k];
		GenerousOracle<T> genOracle = new GenerousOracle<T>();
		return genAcc;
	}

	private double[] calculateAverageIterations(){
		double avgIterations[] = new double[k];
		return avgIterations;
	}
	
	private double[] calculateConvergenceRate(){
		double convRates[] = new double[k]; 
		return convRates;
	}
	
	private double[] calculateEffectiveK() {
		return null;
	}
	
	private double calculatePercentDuplicates() {
		double exWithDuplicates = 0.0;
		return exWithDuplicates;
	}
}
