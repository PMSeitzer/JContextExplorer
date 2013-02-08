package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class JaccardbyClusterID implements OperonDissimilarityMeasure{

	@Override
	public double computeDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2) {
		
		//initialize lists
		ArrayList<Integer> O1Values = new ArrayList<Integer>();
		ArrayList<Integer> O2Values = new ArrayList<Integer>();
		
		int NegativeCounter = -10;
		
		//add elements
		//if clusterID = 0, this is really probably unique, treat all cluster == 0 as unique sets.
		for (GenomicElementAndQueryMatch E: O1){
			if (E.getE().getClusterID() == 0){
				NegativeCounter--;
				O1Values.add(NegativeCounter);
			} else {
				O1Values.add(E.getE().getClusterID());
			}

		}
		
		for (GenomicElementAndQueryMatch E: O2){
			if (E.getE().getClusterID() == 0){
				NegativeCounter--;
				O2Values.add(NegativeCounter);
			} else {
				O2Values.add(E.getE().getClusterID());
			}
		}
		
		//Initialize values
		double Dissimilarity = 0;
		double NumIntersecting = 0;
		double SizeO1;
		double SizeO2;
		double SizeUnion = 0;
		
		//Hash Sets
		HashSet<Object> O1Hash = new HashSet<Object>(O1Values);
		HashSet<Object> O2Hash = new HashSet<Object>(O2Values);
		HashSet<Object> IntersectionHash = new HashSet<Object>(O1Values);
		HashSet<Object> UnionHash = new HashSet<Object>(O1Values);
		IntersectionHash.retainAll(O2Hash);
		UnionHash.addAll(O2Hash);
		
		SizeO1 = O1.size();
		SizeO2 = O2.size();

		//Find all intersecting types, and find the number that intersect.
		for (Object O : IntersectionHash){
			NumIntersecting = NumIntersecting + Math.min(Collections.frequency(O1Values, O), Collections.frequency(O2Values, O));
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
