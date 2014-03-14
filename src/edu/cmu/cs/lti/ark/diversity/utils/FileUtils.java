package edu.cmu.cs.lti.ark.diversity.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Does things like read files 
 * @author sswayamd
 *
 */
public class FileUtils {
	
	/**
	 * Reads a file and returns an array of strings, one string per line
	 */
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
		} catch(IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}
