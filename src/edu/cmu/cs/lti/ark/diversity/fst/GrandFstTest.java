package edu.cmu.cs.lti.ark.diversity.fst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class GrandFstTest {

    private static final double DELTA = 0.0001;

    @Test
    public void testGetResult() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLocalScore() {
        List<SequenceResult<Integer>> kBest = Lists.newArrayList();
        kBest.add(new SequenceResult<Integer>(Lists.newArrayList(0, 1, 2), 0.0));
        GrandFst fst = new GrandFst(1.0);

        // parents are same, grands are not the same
        assertEquals(-1.0, fst.getLocalScore(kBest, 1, 3, 1), DELTA);
        kBest.add(new SequenceResult<Integer>(Lists.newArrayList(3, 1, 0), 0.0));
        // parents are same twice
        assertEquals(-2.0, fst.getLocalScore(kBest, 1, 2, 1), DELTA);

        // parents are not same, but grandparents are same
        kBest = Lists.newArrayList();
        kBest.add(new SequenceResult<Integer>(Lists.newArrayList(2, 0, 2), 0.0));
        assertEquals(-1.0, fst.getLocalScore(kBest, 2, 0, 0), DELTA);

        // parents are same
        assertEquals(-1.0, fst.getLocalScore(kBest, 2, 3, 0), DELTA);
    }

    @Test
    public void testGetFstOnlyScore() {
        List<SequenceResult<Integer>> kBest = Lists.newArrayList();
        kBest.add(new SequenceResult<Integer>(Lists.newArrayList(0, 1, 2), 0.0));
        GrandFst fst = new GrandFst(1.0);
        SequenceResult<Integer> result = new SequenceResult<Integer>(
                Lists.newArrayList(3, 1, 0), 0.0);
        assertEquals(-1.0, fst.getFstOnlyScore(result, kBest), DELTA);
        result = new SequenceResult<Integer>(
                Lists.newArrayList(3, 3, 0), 0.0);
        assertEquals(-1.0, fst.getFstOnlyScore(result, kBest), DELTA);
    }

    @Test
    public void testFindGran() {
        SequenceResult<Integer> tree = new SequenceResult<Integer>(
                Lists.newArrayList(2, 3, 6, 3, 6, 0, 6), 0.0);
        assertEquals(3, GrandFst.findGran(0, tree));
        assertEquals(6, GrandFst.findGran(1, tree));
        assertEquals(0, GrandFst.findGran(2, tree));
        assertEquals(6, GrandFst.findGran(3, tree));
        assertEquals(0, GrandFst.findGran(4, tree));
        assertEquals(-1, GrandFst.findGran(5, tree));
        assertEquals(0, GrandFst.findGran(6, tree));
    }

}
