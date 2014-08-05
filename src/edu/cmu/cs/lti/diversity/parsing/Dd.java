package edu.cmu.cs.lti.diversity.parsing;

import edu.cmu.cs.lti.diversity.general.KBest;

public interface Dd {

    public KBest<Integer> runDualDecomposition(final double[][] weights);
}
