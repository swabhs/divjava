package edu.cmu.cs.lti.ark.diversity.fst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.ark.diversity.main.TagSet;

/**
 * FST search algorithm to return a sequence strictly not equal to a given sequence
 * @author sswayamd
 *
 */
public class ExactFst<T> {

	public ExactFst() {
		// TODO Auto-generated constructor stub
	}
	
	public List<T> run(List<T> given, TagSet<T> tagSet, List<Map<T, Double>> dd) {		
		List<Double> piTrue = new ArrayList<Double>();
		piTrue.add(dd.get(0).get(given.get(0)));
		
		List<Double> piFalse = new ArrayList<Double>();
		List<T> different = new ArrayList<T>();
		
		int i = 1;
		double a,b;
		T alt;
		for (T element : given.subList(1, given.size())) {
			Map<T, Double> ddI = dd.get(i);
			List<T> topTwo = getTopTwoKeys(ddI);
			double topScore = ddI.get(topTwo.get(0));
			double secondScore = ddI.get(topTwo.get(1));
			
			if (topTwo.get(0) == element) {
				alt = topTwo.get(1);
				a = piFalse.get(piFalse.size()-1) + topScore;
				b = piTrue.get(piTrue.size()-1) + secondScore;
			} else {
				alt = topTwo.get(0);
				a = piFalse.get(piFalse.size()-1) + topScore;
				b = piTrue.get(piTrue.size()-1) + topScore;
			}
			
			if (a > b) {
				piFalse.add(a);
				different.add(topTwo.get(0));
			} else {
				piFalse.add(b);
				different = new ArrayList<T>();
				different = given.subList(0, i); // check indices
				different.add(alt);
			}
			
			piTrue.add(piTrue.get(piTrue.size()-1) + dd.get(i).get(element));
			i += 1;
		}
		
		return different;
	}

	private List<T> getTopTwoKeys(Map<T, Double> map) {
		Iterator<T> iterator = map.keySet().iterator();
		T best = iterator.next();
		T secondBest = iterator.next();
		
		if (map.get(best) < map.get(secondBest)) {
			iterator = map.keySet().iterator();
			secondBest = iterator.next();
			best = iterator.next();
		}
		
		while (iterator.hasNext()) {
			T key = iterator.next();
			double val = map.get(key);
			if (val > map.get(best)) {
				secondBest = best;
				best = key;
			} else if (val > map.get(secondBest) || val == map.get(best)) {
				secondBest = key;
			}
		}
		List<T> result = new ArrayList<T>();
		result.add(best);
		result.add(secondBest);
		return result;
	}
	
	public static void main(String[] args) {
		ExactFst<String> fst = new ExactFst<String>();
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("a", 0.0);
		map.put("b", 0.0);
		map.put("c", 0.00);
		map.put("d", 0.0001);
		double a = 0.00;
		System.out.println(a == 0.00);
		System.out.println(fst.getTopTwoKeys(map));
	}
}
