package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.util.LinkedList;

public class TotalSize implements OperonDissimilarityMeasure{

	@Override
	public double computeDissimilarity(
			LinkedList<GenomicElementAndQueryMatch> CS1,
			LinkedList<GenomicElementAndQueryMatch> CS2) {
		
		//Calculate the size of each context set, earliest to latest
		//assume that the elements are not in order.
		int SizeOf1; int StartOf1 = 99999999; int StopOf1 = -1; 
		int SizeOf2; int StartOf2 = 99999999; int StopOf2 = -1; 

		//determine earliest and latest 
		for (int i = 0; i <CS1.size(); i++){
			
			//earliest start
			if (CS1.get(i).getE().getStart() < StartOf1){
				StartOf1 = CS1.get(i).getE().getStart();
			} 
			
			//latest stop
			if (CS1.get(i).getE().getStop() > StopOf1){
				StopOf1 = CS1.get(i).getE().getStop();
			}
		}
		
		//calculate Size
		SizeOf1 = StopOf1 - StartOf1;
		
		//determine earliest and latest 
		for (int i = 0; i <CS2.size(); i++){
			
			//earliest start
			if (CS2.get(i).getE().getStart() < StartOf2){
				StartOf2 = CS2.get(i).getE().getStart();
			} 
			
			//latest stop
			if (CS2.get(i).getE().getStop() > StopOf2){
				StopOf2 = CS2.get(i).getE().getStop();
			}
		}
		
		//calculate Size
		SizeOf2 = StopOf2 - StartOf2;
		
		//compute difference and average
		double average = ((double) (SizeOf1 + SizeOf2))/2.0;
		double difference = Math.abs((double)SizeOf1 - (double)SizeOf2);
		
		//return difference/average
		return difference/average;
	}

}
