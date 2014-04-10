package edu.cmu.cs.lti.ark.diversity.main;

public class HalfNHalfUtil {

    public static void modifyGraph(double graph[][]) {

        for (int parent = 0; parent < graph.length; parent++) {
            for (int child = 1; child < graph[0].length; child++) {
                graph[parent][child] /= 2;
            }
        }
    }

}
