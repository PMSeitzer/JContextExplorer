package operonClustering;

import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import java.util.LinkedList;
import java.util.List;

/**
 * A pairwise operon distance function returns a number between 0 and 1
 * describing the relationship between two operons
 * (an operon is a list of genomic objects).
 * 
 * @author pseitzer@ucdavis.edu
 */
public interface OperonDissimilarityMeasure {

	//all distance approaches compute a distance
	public double computeDissimilarity(LinkedList<GenomicElementAndQueryMatch> linkedList, LinkedList<GenomicElementAndQueryMatch> linkedList2);
	
}
