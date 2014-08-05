package edu.cmu.cs.lti.diversity.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.diversity.general.KBest;
import edu.cmu.cs.lti.diversity.utils.Conll.ConllElement;

/**
 * Given Conll object and the corresponding k-best results, writes them into k
 * different files for a dataset.
 * 
 * @author sswayamd
 * 
 */

public class DataWriter {

    public static void prepareConll(
            List<KBest<Integer>> predictions,
            List<Conll> inputs,
            List<String[][]> labels,
            int k,
            String outDirectory) {
        File outDir = new File(outDirectory);
        boolean created;
        if (!outDir.exists()) {
            created = outDir.mkdir();
        } else {
            try {
                deleteDirectory(outDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            outDir = new File(outDirectory);
            created = outDir.mkdir();
        }
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
                            labels.get(dataItem)[parentOfElement][element.getPosition()]);
                    child++;
                }
                outputs.add(input);
            }
            if (created) {
                MyFileUtils.writeConll(outputs, outDirectory + (j + 1) + "thBest.conll");
            } else {
                System.err.println("Can't write to file");
            }
        }
    }

    public static void deleteDirectory(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteDirectory(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}
