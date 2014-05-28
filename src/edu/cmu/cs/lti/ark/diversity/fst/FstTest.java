package edu.cmu.cs.lti.ark.diversity.fst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class FstTest {
    @Test
    public void testAdditiveHammingDistanceFst() {
        List<SequenceResult<Integer>> bests = Lists.newArrayList();
        bests.add(new SequenceResult<Integer>(Lists.newArrayList(0, 1, 2), 0.0));
        bests.add(new SequenceResult<Integer>(Lists.newArrayList(3, 1, 0), 0.0));
        bests.add(new SequenceResult<Integer>(Lists.newArrayList(2, 3, 0), 0.0));

        List<Map<Integer, Double>> dd = Lists.newArrayList();
        Map<Integer, Double> someMap = Maps.newHashMap();
        someMap.put(new Integer(0), 1.0);
        someMap.put(new Integer(1), 0.0);
        someMap.put(new Integer(2), -1.0);
        someMap.put(new Integer(3), 0.0);
        dd.add(someMap);
        someMap = Maps.newHashMap();
        someMap.put(new Integer(0), 0.0);
        someMap.put(new Integer(1), 1.0);
        someMap.put(new Integer(2), -1.0);
        someMap.put(new Integer(3), 0.0);
        dd.add(someMap);
        someMap = Maps.newHashMap();
        someMap.put(new Integer(0), 0.0);
        someMap.put(new Integer(1), -1.0);
        someMap.put(new Integer(2), 1.0);
        someMap.put(new Integer(3), 0.0);
        dd.add(someMap);

        TagSet<Integer> tagSet = new TagSet<Integer>(Lists.newArrayList(0, 1, 2, 3));

        AdditiveUniHamDistFst<Integer> fst = new AdditiveUniHamDistFst<Integer>(1.0);
        SequenceResult<Integer> actual = fst.getResult(bests, dd, tagSet);
        assertEquals(new Integer(0), actual.getSequence().get(0));
        assertEquals(new Integer(0), actual.getSequence().get(1));
        assertEquals(new Integer(2), actual.getSequence().get(2));
        assertEquals(0.0, actual.getScore(), 0.001);
    }

    @Test
    public void testPpAttachFst() {
        fail("not yet implemented");
    }

    @Test
    public void testCoordinatingConjunctsFst() {
        fail("not yet implemented");
    }

    @Test
    public void testGrandParentsFst() {
        fail("not yet implemented");
    }

    @Test
    public void testSiblingsFst() {
        fail("not yet implemented");
    }

    @Test
    public void testFindSibling() {
        SequenceResult<Integer> tree = new SequenceResult<Integer>(
                Lists.newArrayList(2, 3, 6, 3, 6, 0, 6), 0.0);
        int childPos = 4;
        assertEquals(2, SiblingFst.findSibling(childPos, tree));
    }

    @Test
    public void testFindSibling_noSibling() {
        SequenceResult<Integer> tree = new SequenceResult<Integer>(
                Lists.newArrayList(2, 3, 4, 5, 6, 0, 6), 0.0);
        int childPos = 4;
        assertEquals(-1, SiblingFst.findSibling(childPos, tree));
    }
}
