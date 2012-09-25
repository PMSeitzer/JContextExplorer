package operonClustering;

import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class DicebyAnnotation implements OperonDissimilarityMeasure{

	@Override
	public double computeDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2)  {
		
		//initialize lists
		ArrayList<String> O1Annotations = new ArrayList<String>();
		ArrayList<String> O2Annotations = new ArrayList<String>();
		
		//add elements
		for (GenomicElementAndQueryMatch E: O1){
			O1Annotations.add(E.getE().getAnnotation().toUpperCase());
		}
		
		for (GenomicElementAndQueryMatch E: O2){
			O2Annotations.add(E.getE().getAnnotation().toUpperCase());
		}
		
		//Determine numbers of elements at various points
		double NumIntersecting = 0;
		double Op1Unique = 0;
		double Op2Unique = 0;	
		
		for (int i = 0; i <O1Annotations.size(); i++){
			if (O2Annotations.contains(O1Annotations.get(i))){
				NumIntersecting++;
			} else {
				Op1Unique++;
			}
		}
		
		for (int i = 0; i < O2Annotations.size(); i++){
			if (O1Annotations.contains(O2Annotations.get(i)) == false){
				Op2Unique++;
			}
		}
			
		//Dice Measure
		return 1-(2*NumIntersecting)/(O1Annotations.size()+O2Annotations.size());
		
		}
	}


