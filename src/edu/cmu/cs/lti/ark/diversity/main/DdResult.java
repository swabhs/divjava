package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class DdResult<T> {

	public List<List<T>> kBest;		
	public int[] iterations;
	
	public DdResult(List<List<T>> kBest, int[] iterations) {
		this.kBest = kBest;
		this.iterations = iterations;
	}

}
