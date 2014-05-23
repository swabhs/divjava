package edu.cmu.cs.lti.ark.diversity.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.main.KBest;

public class ParserTest {
    private final static double DELTA = 0.001;
    private final static double NINF = Double.NEGATIVE_INFINITY;

    private final static double[][] weights = new double[][]{
            {NINF, 5, 1, 1},
            {NINF, NINF, 11, 4},
            {NINF, 10, NINF, 5},
            {NINF, 9, 8, NINF},
    };

    @Test
    public void testParserDD() {
        DdParser parser = new DdParser(1.0, 3, 0.1, false, 500);
        KBest<Integer> bests = parser.runDualDecomposition(weights);

        List<Integer> best0 = Lists.newArrayList(0, 1, 2);
        assertTrue(bests.kBest.get(0).getSequence().equals(best0));
        assertEquals(21.0, bests.kBest.get(0).getScore(), DELTA);

        List<Integer> best1 = Lists.newArrayList(3, 1, 0);
        assertTrue(bests.kBest.get(1).getSequence().equals(best1));
        assertEquals(20.0, bests.kBest.get(1).getScore(), DELTA);

        List<Integer> best2 = Lists.newArrayList(2, 3, 0);
        assertTrue(bests.kBest.get(2).getSequence().equals(best2));
        assertEquals(18.0, bests.kBest.get(2).getScore(), DELTA);
    }

    @Test
    public void testCostAugmentedParser() {
        CostAugmentedParser parser = new CostAugmentedParser(1.0);
        KBest<Integer> bests = parser.run(weights, 3);

        List<Integer> best0 = Lists.newArrayList(0, 1, 2);
        assertTrue(bests.kBest.get(0).getSequence().equals(best0));
        assertEquals(21.0, bests.kBest.get(0).getScore(), DELTA);

        List<Integer> best1 = Lists.newArrayList(3, 1, 0);
        assertTrue(bests.kBest.get(1).getSequence().equals(best1));
        assertEquals(20.0, bests.kBest.get(1).getScore(), DELTA);

        List<Integer> best2 = Lists.newArrayList(2, 3, 0);
        assertTrue(bests.kBest.get(2).getSequence().equals(best2));
        assertEquals(18.0, bests.kBest.get(2).getScore(), DELTA);
    }

    @Test
    public void testAgreeDdAndNoDd() {
        final int k = 10;
        final double hammingWt = 0.1;
        final double[][] weights = {
                {NINF, 10, 30, 10, NINF},
                {NINF, NINF, 10, NINF, 10},
                {NINF, 20, NINF, 7, 20},
                {NINF, NINF, 40, NINF, NINF},
                {NINF, NINF, NINF, NINF, NINF},
        };

        DdParser parserDD = new DdParser(hammingWt, k, 0.1, false, 500);
        KBest<Integer> bestsDD = parserDD.runDualDecomposition(weights);

        CostAugmentedParser parser = new CostAugmentedParser(hammingWt);
        KBest<Integer> bests = parser.run(weights, k);

        for (int i = 0; i < bestsDD.kBest.size(); i++) {
            assertTrue(bestsDD.kBest.get(i).getSequence().equals(bests.kBest.get(i).getSequence()));
            assertEquals(bestsDD.kBest.get(i).getScore(), bests.kBest.get(i).getScore(), DELTA);
        }
    }

}
