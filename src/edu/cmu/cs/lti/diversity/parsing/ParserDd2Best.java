package edu.cmu.cs.lti.diversity.parsing;


public class ParserDd2Best {

    // private static final int ROOT = 0;
    // private static final double MAX_ITERATIONS = 50;
    // private static final double HAMMING_WT = 1.0;
    //
    // private static List<Integer> getTree(double[][] weights) {
    // Weighted<Map<Integer, Integer>> result =
    // ChuLiuEdmonds.getMaxSpanningTree(weights, ROOT);
    // Map<Integer, Integer> value = result.val;
    // List<Integer> parents = new ArrayList<Integer>();
    //
    // // Print maximum branching per node.
    // // System.out.println("Maximum branching:");
    //
    // // parents.add(0);
    // for (int node = 1; node <= value.size(); ++node) {
    // // System.out.println(parent.get(node) + " -> " + node);
    // parents.add(value.get(node));
    // }
    // // System.out.println(result.weight);
    // // System.out.println(parents);
    // return parents;
    // }
    //
    // private static void updateWeights(double[][] weights, List<Map<Integer,
    // Double>> dd) {
    // for (Integer parent : dd.get(0).keySet()) {
    // for (int child = 0; child < dd.size(); child++) {
    // weights[parent][child] -= dd.get(child).get(parent);
    // // System.out.print(weights[parent][child]+"\t");
    // }
    // // System.out.println();
    // }
    // }
    //
    // private static TagSet<Integer> createTagset(int n) {
    // List<Integer> tags = new ArrayList<Integer>();
    // for (int i = 0; i < n; i++) {
    // tags.add(i);
    // }
    // return new TagSet<Integer>(tags);
    // }
    //
    // public static Result run(double[][] weights) {
    //
    // // n = length of sentence, n+1 = length of tagset
    // final int n = weights[0].length - 1;
    //
    // TagSet<Integer> tagSet = createTagset(n + 1);
    //
    // Fst<Integer, Integer> fst = new UniHamDistFst<Integer>(HAMMING_WT);
    // DdHelper<Integer> helper = new DdHelper<Integer>();
    //
    // List<Integer> bestTree = getTree(weights);
    // List<Map<Integer, Double>> dd = helper.init(n, tagSet);
    //
    // List<Integer> tags1 = null;
    // List<Integer> tags2 = null;
    // int iter = 1;
    // while (iter <= MAX_ITERATIONS) {
    // double stepSize = 1.0 / Math.sqrt(iter);
    //
    // tags2 = fst.getResult(bestTree, dd, tagSet).getSequence();
    // tags1 = getTree(weights);
    //
    // if (helper.agree(tags1, tags2)) {
    // return new Result(bestTree, tags1, tags2, iter);
    // } else {
    // dd = helper.update(dd, tags1, tags2, stepSize, tagSet);
    // updateWeights(weights, dd);
    // }
    // iter += 1;
    // }
    // return new Result(bestTree, tags1, tags2, -1);
    // }
    //
    // static class Result {
    // List<Integer> bestTree;
    // List<Integer> cleTree;
    // List<Integer> fstTree;
    //
    // int iterations;
    //
    // Result(List<Integer> bestTree, List<Integer> cleTree,
    // List<Integer> fstTree, int iterations) {
    // super();
    // this.bestTree = bestTree;
    // this.cleTree = cleTree;
    // this.fstTree = fstTree;
    // this.iterations = iterations;
    // }
    // }

}
