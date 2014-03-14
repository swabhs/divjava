package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.TagSet;
import edu.cmu.cs.lti.ark.diversity.tagging.TaggerDD.Result;
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
		
		//System.out.println(sentences.size() + " " + tagSet.getSize());
		double convergenceRatio = 0.0;
		double averageIterations = 0.0;
		double averageAccuracySec = 0.0;
		double avgAccBest = 0.0;
		
		int c = 0;
		for (List<String> sentence : sentences) {
			System.err.println(c + " : length = " + sentence.size());
			System.out.println(sentence);
			TaggerDD dd = new TaggerDD(tagSet, hmm);
			
			Result result = dd.run(sentence);
			int iter = result.iterations;
			
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
			}
			
			c += 1;
			if (c==50) {
				break;
			}
		}
		
		averageIterations /= convergenceRatio;
		averageAccuracySec /= convergenceRatio;
		System.err.println("\n\nConvergence Ratio = " + convergenceRatio/c);
		System.err.println("Average Iterations = " + averageIterations);
		System.err.println("Average Accuracy of Best = " + avgAccBest/c);
		System.err.println("Average Accuracy = " + averageAccuracySec);
	}
}
