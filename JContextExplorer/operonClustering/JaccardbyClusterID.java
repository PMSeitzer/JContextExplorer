package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.util.ArrayList;
import java.util.LinkedList;

public class JaccardbyClusterID implements OperonDissimilarityMeasure{

	@Override
	public double computeDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2) {
		
		//initialize lists
		ArrayList<Integer> O1Clusters = new ArrayList<Integer>();
		ArrayList<Integer> O2Clusters = new ArrayList<Integer>();
		
		//add elements
		for (GenomicElementAndQueryMatch E: O1){
			O1Clusters.add(E.getE().getClusterID());
		}
		
		for (GenomicElementAndQueryMatch E: O2){
			O2Clusters.add(E.getE().getClusterID());
		}
		
		//Determine numbers of elements at various points
		double NumIntersecting = 0;
		double Op1Unique = 0;
		double Op2Unique = 0;	
		
		for (int i = 0; i <O1Clusters.size(); i++){
			if (O2Clusters.contains(O1Clusters.get(i))){
				NumIntersecting++;
			} else {
				Op1Unique++;
			}
		}
		
		for (int i = 0; i < O2Clusters.size(); i++){
			if (O1Clusters.contains(O2Clusters.get(i)) == false){
				Op2Unique++;
			}
		}
			
		//Jaccard Measure
		return 1-(NumIntersecting/(NumIntersecting+Op1Unique+Op2Unique));
		
		}

}
