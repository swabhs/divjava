package edu.cmu.cs.lti.ark.diversity.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.cs.lti.ark.diversity.utils.Conll.ConllElement;

/**
 * Does things like read files
 * 
 * @author sswayamd
 * 
 */
public class FileUtils {

    /** Reads a file and returns an array of strings, one string per line */
    static List<String> readFile(String fileName) {
        File file = new File(fileName);
        List<String> lines = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                lines.add(strLine);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Reads a file in CONLL format and returns a list of Conll objects, one for
     * each sentence.
     */
    public static List<Conll> readConllFile(String fileName) {
        List<Conll> conlls = Lists.newArrayList();
        File file = new File(fileName);
        List<String> conllBlock = Lists.newArrayList();
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.split("\t").length <= 2) {
                    conlls.add(new Conll(conllBlock));
                    conllBlock = Lists.newArrayList();
                    continue;
                }
                conllBlock.add(strLine);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conlls;
    }

    /** Writes conll file */
    public static void writeConll(List<Conll> outputConlls, String fileName) {
        try {
            File file = new File(fileName);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (Conll outConll : outputConlls) {
                for (ConllElement element : outConll.getElements()) {
                    bw.write(element.getPosition() + "\t"
                            + element.getToken() + "\t"
                            + element.getLemma() + "\t"
                            + element.getCoarsePosTag() + "\t"
                            + element.getPosTag() + "\t_\t"
                            + element.getParent() + "\t"
                            + element.getDepLabel() + "\t_\t_\n");
                }
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
