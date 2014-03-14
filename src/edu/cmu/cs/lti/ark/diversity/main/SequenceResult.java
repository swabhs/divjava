package edu.cmu.cs.lti.ark.diversity.main;

import java.util.List;

public class SequenceResult<T> {
	
	private List<T> sequence;
	private double score;

	public SequenceResult(List<T> sequence,	double score) {
		this.sequence = sequence;
		this.score = score;
	}
	
	public List<T> getSequence() {
		return sequence;
	}
	
	public double getScore() {
		return score;
	}
}
