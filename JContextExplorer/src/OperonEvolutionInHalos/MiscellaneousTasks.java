package OperonEvolutionInHalos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public class MiscellaneousTasks {

	/**
	 * @param args
	 */
	
	//Parse Console output, remove duplicate entries
	public static void InternalPromoterGapConsoleOutput(String ConsoleFile, String OutputFile){
		
		//initialize map of counts
		LinkedHashMap<String, Integer> QuerieswCounts = new LinkedHashMap<String, Integer>();

		try {
			//read file
			BufferedReader br = new BufferedReader(new FileReader(ConsoleFile));
			String Line = null;
			while ((Line = br.readLine())!= null){
				int NewCount = 0;
				if (QuerieswCounts.get(Line) != null){
					NewCount = QuerieswCounts.get(Line);
					NewCount++;
				} else {
					NewCount = 1;
				}
				QuerieswCounts.put(Line, NewCount);
			}
			br.close();
			
			//output file, w counts
			BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));
			for (String s : QuerieswCounts.keySet()){
				String line = s + "\t" + String.valueOf(QuerieswCounts.get(s)) + "\n";
				bw.write(line);
				bw.flush();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//main method
	public static void main(String[] args) {
		
		/*
		 * Thursday, January 8, 2013
		 */
		String ConsoleFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/IntergenicGaps/Console_Output.txt";
		String OutputFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/IntergenicGaps/Output_w_Counts.txt";
		InternalPromoterGapConsoleOutput(ConsoleFile,OutputFile);
	}

}
