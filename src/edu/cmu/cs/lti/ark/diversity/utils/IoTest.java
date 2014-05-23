package edu.cmu.cs.lti.ark.diversity.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IoTest {

    @Test
    public void testConllReading() {
        String str = "i\tlove\train";
        str = str.replaceAll("\\s+", "");
        assertTrue(str.equals("iloverain"));
    }
}
