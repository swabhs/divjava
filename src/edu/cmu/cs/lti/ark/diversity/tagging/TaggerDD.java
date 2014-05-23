package edu.cmu.cs.lti.ark.diversity.tagging;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.fst.AdditiveUniHamDistFst;
import edu.cmu.cs.lti.ark.diversity.fst.Fst;
import edu.cmu.cs.lti.ark.diversity.main.DdHelper;
import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.main.SequenceResult;
import edu.cmu.cs.lti.ark.diversity.main.TagSet;

public class TaggerDD {

    private final double maxIterations = 100;
    private final double hammingWt = 1.0;

    private TagSet<String> tagSet;
    private Map<String, Double> hmm;
    private DdHelper<String> helper = new DdHelper<String>();

    public TaggerDD(TagSet<String> tagSet, Map<String, Double> hmm) {
        this.tagSet = tagSet;
        this.hmm = hmm;
    }

    public KBest<String> run(List<String> tokens, int k) {
        final int n = tokens.size();
        final TestInstance instance = new TestInstance(tokens, hmm, tagSet);

        Viterbi viterbi = new Viterbi(tagSet, hmm);

        Fst<String, List<String>> fst = new AdditiveUniHamDistFst<String>(hammingWt);
        List<SequenceResult<String>> kBestTagSeqs = Lists.newArrayList();
        SequenceResult<String> bestTags = viterbi.getTagSeq(instance, null);
        kBestTagSeqs.add(bestTags);

        int iterations[] = new int[k];

        for (int i = 1; i < k; i++) {
            SequenceResult<String> viterbiSeq = null;
            List<String> fstSeq = null;
            List<Map<String, Double>> dd = helper.init(n, tagSet);

            iterations[i] = 1;
            while (iterations[i] <= maxIterations) {
                double stepSize = 1.0 / Math.sqrt(iterations[i]);

                fstSeq = fst.getResult(kBestTagSeqs, dd, tagSet).getSequence();
                viterbiSeq = viterbi.getTagSeq(instance, dd);

                if (helper.agree(viterbiSeq.getSequence(), fstSeq)) {
                    kBestTagSeqs.add(viterbiSeq);
                    break;
                } else {
                    dd = helper.update(dd, viterbiSeq.getSequence(), fstSeq, stepSize, tagSet);
                }
                iterations[i] += 1;
            }
            if (iterations[i] == maxIterations + 1) { // did not converge
                System.err.println("did not converge");
                iterations[i] = -1;
                break;
            }
        }
        return new KBest<String>(kBestTagSeqs, iterations);
    }
}
