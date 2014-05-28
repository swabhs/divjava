package edu.cmu.cs.lti.ark.diversity.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;

public class DdGrandTest {

    @Test
    public void testInit() {
        Map<Integer, Map<Integer, Double>> parentMap = Maps.newHashMap();
        Map<Integer, Double> grandMap = Maps.newHashMap();
        // special case : parent is ROOT
        grandMap.put(-1, 0.0);
        parentMap.put(0, grandMap);
        // normal case
        grandMap = Maps.newHashMap();
        grandMap.put(0, 0.0);
        grandMap.put(1, 0.0);
        grandMap.put(2, 0.0);
        grandMap.put(3, 0.0);
        parentMap.put(1, grandMap);
        parentMap.put(2, grandMap);
        parentMap.put(3, grandMap);
        List<Map<Integer, Map<Integer, Double>>> expected = Lists.newArrayList();
        expected.add(parentMap);
        expected.add(parentMap);
        expected.add(parentMap);
        assertEquals(expected, DdGrandHelper.init(3));
    }

    @Test
    public void testUpdate() {
        Map<Integer, Map<Integer, Double>> parentMap = Maps.newHashMap();
        Map<Integer, Double> grandMap = Maps.newHashMap();
        // special case : parent is ROOT
        grandMap.put(-1, 0.0);
        parentMap.put(0, grandMap);
        // normal case
        grandMap = Maps.newHashMap();
        grandMap.put(0, 0.0);
        grandMap.put(1, 0.0);
        grandMap.put(2, 0.0);
        grandMap.put(3, 0.0);
        parentMap.put(1, grandMap);
        parentMap.put(2, grandMap);
        parentMap.put(3, grandMap);
        final List<Map<Integer, Map<Integer, Double>>> dd = Lists.newArrayList();
        dd.add(parentMap);
        dd.add(parentMap);
        dd.add(parentMap);
        List<Map<Integer, Map<Integer, Double>>> expected = Lists.newArrayList();
        expected.addAll(dd);
        expected.get(0).get(0).put(-1, 1.0);
        expected.get(0).get(2).put(2, -1.0);
        expected.get(1).get(1).put(0, 1.0);
        expected.get(1).get(1).put(2, -1.0);
        SequenceResult<Integer> cleResult = new SequenceResult<Integer>(
                Lists.newArrayList(0, 1, 2), 0.0);
        SequenceResult<Integer> fstResult = new SequenceResult<Integer>(
                Lists.newArrayList(2, 1, 2), 0.0);
        assertEquals(expected, DdGrandHelper.update(dd, cleResult, fstResult, 1.0));
    }

    @Test
    public void testAgree() {
        SequenceResult<Integer> cleResult = new SequenceResult<Integer>(
                Lists.newArrayList(0, 1, 2), 0.0);
        SequenceResult<Integer> fstResult = new SequenceResult<Integer>(
                Lists.newArrayList(2, 2, 2), 0.0);
        assertFalse(DdGrandHelper.agree(cleResult, fstResult));
        fstResult = new SequenceResult<Integer>(Lists.newArrayList(0, 1, 2), 0.0);
        assertTrue(DdGrandHelper.agree(cleResult, fstResult));
    }

}
