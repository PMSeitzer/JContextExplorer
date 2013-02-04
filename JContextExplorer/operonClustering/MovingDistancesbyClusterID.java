package operonClustering;

import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import java.util.ArrayList;
import java.util.LinkedList;

import org.biojava3.core.sequence.Strand;

public class MovingDistancesbyClusterID implements OperonDissimilarityMeasure {

	//distance-related parameters
	double MismatchPenalty = 0.2;
	int GenomicRearrangementOrInsertion = 200;
	double GenomicRearrangementOrInsertionPenalty = 0.2;
	int ProteinBindingSiteChange = 25;
	double ProteinBindingSiteChangePenalty = 0.05;
	int TranscriptionUnitChange = 10;
	double TranscriptionUnitChangePenalty = 0.02;
	int CloselyPackedGenesDistance = 10;
	
	@Override
	public double computeDissimilarity(
			LinkedList<GenomicElementAndQueryMatch> O1,
			LinkedList<GenomicElementAndQueryMatch> O2) {
		
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
		
		//only compute distances for cases where the same number of elements are relevant
		if (Op1Unique == 0 && Op2Unique == 0 && O1.size() == O2.size()){
			
			//determine strandedness of each context set
			//determine reverse or straight genomic order for O2
			int QueryStrandPlusO1 = 0; int QueryStrandMinusO1 = 0;
			int QueryStrandPlusO2 = 0; int QueryStrandMinusO2 = 0;
			
			for (GenomicElementAndQueryMatch e : O1){
				if (e.isQueryMatch()){
					if (e.getE().getStrand().equals(Strand.POSITIVE)){
						QueryStrandPlusO1++;
					} else {
						QueryStrandMinusO1++;
					}
				}
			}

			for (GenomicElementAndQueryMatch e : O2){
				if (e.isQueryMatch()){
					if (e.getE().getStrand().equals(Strand.POSITIVE)){
						QueryStrandPlusO2++;
					} else {
						QueryStrandMinusO2++;
					}
				}
			}
			
			//if strand = +, put in genomic order
			//if strand = -, put into reverse genomic order
			
			if (QueryStrandPlusO1 >= QueryStrandMinusO1){
				for (int i = 0; i < O1.size()-1; i++){
					for (int j = 0; j < O1.size()-1; j++){
						if (O1.get(j).getE().getStart() > O1.get(j+1).getE().getStart()){
							//switch elements
							GenomicElementAndQueryMatch tempE = O1.get(j+1);
							O1.set(j+1, O1.get(j));
							O1.set(j, tempE);
						}
					}
				}
			} else {
				for (int i = 0; i < O1.size()-1; i++){
					for (int j = 0; j < O1.size()-1; j++){
						if (O1.get(j).getE().getStart() < O1.get(j+1).getE().getStart()){
							//switch elements
							GenomicElementAndQueryMatch tempE = O1.get(j+1);
							O1.set(j+1, O1.get(j));
							O1.set(j, tempE);
						}
					}
				}
			}

			//disagreement in strandedness of elements
			if (QueryStrandPlusO2 >= QueryStrandMinusO2){
				
				for (int i = 0; i < O2.size()-1; i++){
					for (int j = 0; j < O2.size()-1; j++){
						if (O2.get(j).getE().getStart() > O2.get(j+1).getE().getStart()){
							//switch elements
							GenomicElementAndQueryMatch tempE = O2.get(j+1);
							O2.set(j+1, O2.get(j));
							O2.set(j, tempE);
						}
					}
				}
			} else {
				for (int i = 0; i < O2.size()-1; i++){
					for (int j = 0; j < O2.size()-1; j++){
						if (O2.get(j).getE().getStart() < O2.get(j+1).getE().getStart()){
							//switch elements
							GenomicElementAndQueryMatch tempE = O2.get(j+1);
							O2.set(j+1, O2.get(j));
							O2.set(j, tempE);
						}
					}
				}
			}

			//Any inversions among elements?
			int MisMatches = 0;
			for (int i = 0; i <O1.size(); i++){
				if (O1.get(i).getE().getGeneID() != O2.get(i).getE().getGeneID()){
					MisMatches++;
				}
			}
			
			//if no mismatches, check protein binding site changes
			if (MisMatches == 0){

				double ChangesInDistance = 0.0;
				for (int i = 0; i <O1.size()-1; i++){
					
					double dist1 = 0.0; double dist2 = 0.0;
					
					//make sure to compare the correct quantities - depending on the strandedness of the CS.
					if (QueryStrandPlusO1 >= QueryStrandMinusO1){
						dist1 = Math.abs((double)(O1.get(i+1).getE().getStart()-O1.get(i).getE().getStop()));
						if (QueryStrandPlusO2 >= QueryStrandMinusO2){
							dist2 = Math.abs((double)(O2.get(i+1).getE().getStart()-O2.get(i).getE().getStop()));
						} else {
							dist2 = Math.abs((double)(O2.get(i).getE().getStart()-O2.get(i+1).getE().getStop()));
						}
					} else {
						dist1 = Math.abs((double)(O1.get(i).getE().getStart()-O1.get(i+1).getE().getStop()));
						if (QueryStrandPlusO2 >= QueryStrandMinusO2){
							dist2 = Math.abs((double)(O2.get(i+1).getE().getStart()-O2.get(i).getE().getStop()));
						} else {
							dist2 = Math.abs((double)(O2.get(i).getE().getStart()-O2.get(i+1).getE().getStop()));
						}
					}
					
					//large change : genomic rearrangement or insertion event
					//intermediate: gain/loss of protein binding site/regulatory site
					//small: transcription unit rearrangement
					if (Math.abs(dist1-dist2) > GenomicRearrangementOrInsertion){ //large change
						ChangesInDistance = ChangesInDistance + GenomicRearrangementOrInsertionPenalty;
					} else if (Math.abs(dist1-dist2) > ProteinBindingSiteChange){ //intermediate change
						ChangesInDistance = ChangesInDistance + ProteinBindingSiteChangePenalty;
					} else if (Math.abs(dist1-dist2) > TranscriptionUnitChange && //small change
							((dist1 < CloselyPackedGenesDistance) || (dist2 < CloselyPackedGenesDistance))){
						ChangesInDistance = ChangesInDistance + TranscriptionUnitChangePenalty;
					}
				}
				if (ChangesInDistance > 1){
					return 1;
				} else {
					return ChangesInDistance;
				}
				
			} else { 
				//mismatches override dissimilarity differences
				if (MisMatches * MismatchPenalty > 1){
					return 1;
				} else {
					return MisMatches * MismatchPenalty;
				}
			}
			
		} else {
			
			//if there is some difference in elements, these are maximally distance from each other
			return 1;
		}
		
	}

}
