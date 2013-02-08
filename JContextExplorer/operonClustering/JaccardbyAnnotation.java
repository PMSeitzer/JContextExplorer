package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class JaccardbyAnnotation implements OperonDissimilarityMeasure {

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
		
		//Initialize values
		double Dissimilarity = 0;
		double NumIntersecting = 0;
		double SizeO1;
		double SizeO2;
		double SizeUnion = 0;
		
		//Hash Sets
		HashSet<Object> O1Hash = new HashSet<Object>(O1Annotations);
		HashSet<Object> O2Hash = new HashSet<Object>(O2Annotations);
		HashSet<Object> IntersectionHash = new HashSet<Object>(O1Annotations);
		HashSet<Object> UnionHash = new HashSet<Object>(O1Annotations);
		IntersectionHash.retainAll(O2Hash);
		UnionHash.addAll(O2Hash);
		
		SizeO1 = O1.size();
		SizeO2 = O2.size();

		//Find all intersecting types, and find the number that intersect.
		for (Object O : IntersectionHash){
			NumIntersecting = NumIntersecting + Math.min(Collections.frequency(O1Annotations, O), Collections.frequency(O2Annotations, O));
		}
		
		//compute union
		SizeUnion = SizeO1 + SizeO2 - NumIntersecting;
		
		if (SizeUnion != 0) {
			Dissimilarity =  1-(NumIntersecting/SizeUnion);
		} else { //divide by zero case
			Dissimilarity = 0;
		}
		
		return Dissimilarity;
		
		}

}
