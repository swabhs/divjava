package edu.cmu.cs.lti.ark.diversity.parsing;

import edu.cmu.cs.lti.ark.diversity.main.KBest;

public interface Dd {

    public KBest<Integer> runDualDecomposition(final double[][] weights);
}
