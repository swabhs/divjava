package edu.cmu.cs.lti.ark.diversity.utils;

import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.main.KBest;
import edu.cmu.cs.lti.ark.diversity.utils.Conll.ConllElement;

/**
 * Given Conll object and the corresponding k-best results, writes them into k
 * different files for a dataset.
 * 
 * @author sswayamd
 * 
 */

public class DataWriter {

    public static void prepareConll(
            List<KBest<Integer>> predictions, List<Conll> inputs, List<String[][]> labels, int k) {
        for (int j = 0; j < k; j++) {
            List<Conll> outputs = Lists.newArrayList();
            for (int dataItem = 0; dataItem < predictions.size(); dataItem++) {
                Conll input = inputs.get(dataItem);
                KBest<Integer> prediction = predictions.get(dataItem);

                // TODO: how to deal with these cases?
                List<Integer> kthTree;
                if (prediction.kBest.size() < k) {
                    kthTree = prediction.kBest.get(prediction.kBest.size() - 1)
                            .getSequence();
                } else {
                    kthTree = prediction.kBest.get(j).getSequence();
                }

                int child = 0;
                assert (input.getElements().size() == kthTree.size());
                for (ConllElement element : input.getElements()) {
                    int parentOfElement = kthTree.get(child);
                    element.setParent(parentOfElement);
                    element.setDepLabel(
                            labels.get(dataItem)[element.getPosition()][parentOfElement]);
                    child++;
                }
                outputs.add(input);
            }
            FileUtils.writeConll(outputs, "data/parsing/out/" + (j + 1) + "thBest.conll");
        }

    }

}
