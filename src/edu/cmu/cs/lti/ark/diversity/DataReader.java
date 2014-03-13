package edu.cmu.cs.lti.ark.diversity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReader {
	
	/**
	 * Reads the HMM params from a file
	 * @param weightsFile
	 */
	static Map<String, Double> readHmmParams(String weightsFileName) {
		List<String> lines = Helper.readFile(weightsFileName);
		Map<String, Double> hmmWeights = new HashMap<String, Double>();
		for (String line : lines) {
			String elements[] = line.split(" ");
			hmmWeights.put(elements[0], Double.parseDouble(elements[1]));
		}
		return hmmWeights;
	}

	/**
	 * Reads the tagsets from a file
	 * @param weightsFile
	 */
	static TagSet<String> readTagset(String tagSetFileName) {
		return (new TagSet<String>(Helper.readFile(tagSetFileName)));
	}
	
	static Tuple<List<List<String>>, List<List<String>>> readData(String dataFileName) {
		List<List<String>> sentences = new ArrayList<List<String>>();
		List<List<String>> posTagSeqs = new ArrayList<List<String>>();
		
		List<String> sentence = new ArrayList<String>();
		List<String> posTagSeq = new ArrayList<String>();
		
		List<String> lines = Helper.readFile(dataFileName);
		for (String line : lines) {
			String elements[] = line.split("\t");
			
			if (elements.length < 2) {
				sentences.add(sentence);
				posTagSeqs.add(posTagSeq);
				sentence = new ArrayList<String>();
				posTagSeq = new ArrayList<String>();
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
	
	/** TODO: generalize this method and the above somehow
	 * One sentence per line
	 * @param sentFileName
	 * @return
	 */
	static List<List<String>> readSentences(String sentFileName) {
		List<List<String>> sentences = new ArrayList<List<String>>();
		List<String> lines = Helper.readFile(sentFileName);
		for (String line: lines) {
			List<String> sentence = new ArrayList<String>();
			String elements[] = line.split("\t");
			for (String element : elements) {
				sentence.add(element);
			}
			sentences.add(sentence);
		}
		return sentences;
	}
	
	/**
	 * Reads edgeWeights from a file for parsing
	 * @param edgeWeightFileName
	 * @return
	 */
	static List<double[][]> readEdgeWeights(String edgeWeightFileName) {
		List<double[][]> matrices = new ArrayList<double[][]>();
		double[][] matrix = null;
				
		List<String> lines = Helper.readFile(edgeWeightFileName);
		int i = 0;
		for (String line: lines) {
			if (line.startsWith("##")) {
				if (matrix!= null)
				    matrices.add(matrix);
				i = 0;
				continue;
			}
			
			String ele[] = line.split("\t");
			int n = ele.length;
			if (i==0) {
				matrix = new double[ele.length][ele.length];
			}
			for (int j =0; j<n; j++) {
				if (ele[j].contains("-inf")) {
					matrix[i][j] = Double.NEGATIVE_INFINITY;
				} else {
					matrix[i][j] = Double.parseDouble(ele[j]);
				}
			}
			i += 1;
		}
		matrices.add(matrix);
		return matrices;
	}
	
	static List<List<Integer>> readDepParse(String depFileName) {
		List<List<Integer>> depParses = new ArrayList<List<Integer>>();
		List<Integer> depParse = new ArrayList<Integer>();
		
		List<String> lines = Helper.readFile(depFileName);
		for (String line: lines) {
			if (line.equals("")) {
				depParses.add(depParse);
				depParse = new ArrayList<Integer>();
				continue;
			}
			String[] parts = line.split("\t");
			depParse.add(Integer.parseInt(parts[6]));
		}
		depParses.add(depParse);
		return depParses;
	}
}
