package ContextSetRules;

import genomeObjects.GenomicElement;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class FilterRule {

	//Filter Types
	
	//Filter based on gene gangs
	public static LinkedHashMap<String, LinkedList<GenomicElement>> GeneGangs(LinkedHashMap<String, LinkedList<GenomicElement>> Map, double Fraction){
		
		//Initialize output
		LinkedHashMap<String, LinkedList<GenomicElement>> Output =
				new LinkedHashMap<String, LinkedList<GenomicElement>>();
		
		//return output map
		return Output;
		
	}
	
}
