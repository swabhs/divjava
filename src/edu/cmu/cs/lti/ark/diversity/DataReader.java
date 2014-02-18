package edu.cmu.cs.lti.ark.diversity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReader {
	
	/**
	 * Reads the HMM params from a file
	 * @param weightsFile
	 */
	static Map<String, Double> readHmmParams(File weightsFile) {
		Map<String, Double> hmmWeights = new HashMap<String, Double>();
		for (String line : Helper.readFile(weightsFile)) {
			String elements[] = line.split(" ");
			hmmWeights.put(elements[0], Double.parseDouble(elements[1]));
		}
		return hmmWeights;
	}

	/**
	 * Reads the tagsets from a file
	 * @param weightsFile
	 */
	static List<String> readTagset(File tagSetFile) {
		return Helper.readFile(tagSetFile);
	}
	
	static Tuple<List<List<String>>, List<List<String>>> readData(File dataFile) {
		List<List<String>> sentences = new ArrayList<List<String>>();
		List<List<String>> posTagSeqs = new ArrayList<List<String>>();
		
		List<String> sentence = new ArrayList<String>();
		List<String> posTagSeq = new ArrayList<String>();
		
		for (String line : Helper.readFile(dataFile)) {
			String elements[] = line.split(" ");
			if (elements.length < 2) {
				sentences.add(sentence);
				posTagSeqs.add(posTagSeq);
				sentence.clear();
				posTagSeq.clear();
			}
			else {
				sentence.add(elements[0]);
				posTagSeq.add(elements[1]);
			}
		}
		
		Tuple<List<List<String>>, List<List<String>>> data = 
				new Tuple<List<List<String>>, List<List<String>>>(sentences, posTagSeqs);
		return data;
	}
	
	public static void main(String[] args) {
		Tuple<List<List<String>>, List<List<String>>> data = readData(new File(args[0]));
		List<List<String>> sentences = data.getFirst();
		List<List<String>> posTagSeqs = data.getSecond();
		
		Map<String, Double> hmm = readHmmParams(new File(args[1]));
		
		List<String> tags = readTagset(new File(args[2]));
		
		System.out.println(sentences.size() + " " + tags.size());
		
	}
	
}
