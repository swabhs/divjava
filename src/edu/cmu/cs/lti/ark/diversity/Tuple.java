package edu.cmu.cs.lti.ark.diversity;

public class Tuple<X, Y> { 
	  
	public final X x; 
	public final Y y; 
	
	public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	} 
	
	public X getFirst() {
		return this.x;
	}
	
	public Y getSecond() {
		return this.y;
	}
	
}