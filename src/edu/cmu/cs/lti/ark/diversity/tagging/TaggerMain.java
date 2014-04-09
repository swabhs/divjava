package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;
import edu.cmu.cs.lti.ark.diversity.utils.DataReader;
import edu.cmu.cs.lti.ark.diversity.utils.Tuple;

public class TaggerMain {
	
	private static String dataFile = "data/ptb_dev.word_tag";
	private static String hmmFile = "data/ptb_hmm.txt";
	private static String tagFile = "data/ptb_tagset.txt";
		
	static double evaluate(List<String> trueTagSeq, List<String> predTagSeq) {
		if (trueTagSeq.size() != predTagSeq.size()) {
			System.err.println("Error: predicted tag sequence of a wrong size!");
			return 0.0;
		}
		double accuracy = 0.0;
		for (int i = 0; i < trueTagSeq.size(); i++) {
			if (trueTagSeq.get(i).equals(predTagSeq.get(i))) {
				accuracy += 1.0;
			}
		}
		accuracy /= trueTagSeq.size();
		return accuracy;
	}
		
	public static void main(String[] args) {
		System.err.println("Reading data...");
		Tuple<List<List<String>>, List<List<String>>> data = DataReader.readData(dataFile);
		List<List<String>> sentences = data.getFirst();
		List<List<String>> trueTagSeqs = data.getSecond();
		
		Map<String, Double> hmm = DataReader.readHmmParams(hmmFile);
		TagSet<String> tagSet = DataReader.readTagset(tagFile);
		
		final int k = 5;
		
		double exWithDuplicates = 0.0;
		double accuracies[] = new double[k];
		
		double convRates[] = new double[k]; 
		double avgIterations[] = new double[k];
		
		int example = 0;
		for (example = 0; example < sentences.size(); example++) {
			List<String> sentence = sentences.get(example);
			if (sentence.size() > 15) {
				continue;
			}
			System.err.println("example : " + example + " : length = " + sentence.size());
			System.out.println(sentence);
			
			TaggerDD taggerDD = new TaggerDD(tagSet, hmm);
			KBest<String> result = taggerDD.run(sentence, k);
			List<String> goldTagSeq = trueTagSeqs.get(example);
			
			accuracies[0] += evaluate(goldTagSeq, result.kBest.get(0));
			convRates[0] += 1;
			avgIterations[0] += 1;
			System.out.println("1 best = " + result.kBest.get(0));
			
			boolean hasDuplicates = false;
			for (int i = 1; i < k; i++) {
				if (result.iterations[i] == -1) {
					System.err.println(i+1 + " best does NOT converge");
					break;
				}
				
				if (result.kBest.get(i).equals(result.kBest.get(i-1))) {
					 hasDuplicates = true;
				}
				accuracies[i] += evaluate(goldTagSeq, result.kBest.get(i));
				convRates[i] += 1;
				//System.err.println(i + " best converges in " + result.iterations[i] + " iterations\n");
				avgIterations[i] += result.iterations[i];		
				System.out.println(i+1 + " best = " + result.kBest.get(i));		
			}
			if (hasDuplicates) {
				exWithDuplicates += 1.0;
			}
			System.out.println();
			
			/*int iter = result.iterations;
			
			List<String> bestTagSeq = result.bestTagSeq;
			System.out.println(bestTagSeq);
			avgAccBest += evaluate(trueTagSeqs.get(c), bestTagSeq);
			
			if (iter == -1) {
				System.err.println("Does NOT converge\n");
			} else {
				List<String> predTagSeq = result.secondBestTagSeq;
				System.out.println(predTagSeq);
				System.err.println("Converges in " + iter + " iterations\n");
				convergenceRatio += 1;
				averageIterations += iter;
				averageAccuracySec += evaluate(trueTagSeqs.get(c), predTagSeq);
			}*/		
		}
		
		System.out.println("% of duplicates = " + exWithDuplicates*100/sentences.size());
		for (int i = 0; i < k; i++) {
			System.out.println(i+1 + "\n-----------------\n");
			System.out.println("avg accuracy = " + accuracies[i]/convRates[i]);
			System.out.println("convergence  = " + convRates[i]*100/example);
			System.out.println("iterations   = " + avgIterations[i]/convRates[i]);
		}
	}
}
