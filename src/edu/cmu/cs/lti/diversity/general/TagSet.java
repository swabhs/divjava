package edu.cmu.cs.lti.diversity.general;

import java.util.List;

public class TagSet<T> {
	
	private List<T> tags;

	public TagSet(List<T> tags) {
		this.tags = tags;
	}
	
	public List<T> getTags() {
		return tags;
	}
	
	public int getSize() {
		return tags.size();
	}

}
