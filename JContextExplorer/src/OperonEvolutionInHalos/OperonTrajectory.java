package OperonEvolutionInHalos;

import genomeObjects.GenomicElement;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.biojava3.core.sequence.Strand;

public class OperonTrajectory {

	//Fields
	public int ClusterID;
	public double ClusterPhylogeneticRange;
	public int OrgsFeatured = -1;
	public double Operonicity  = -1.0;
	public double Variety = -1.0;
	public double EvoRate = -1;
	public boolean AgreesWithPhylogenyAtLowestLevel;
	public boolean AlwaysASingleGene = false;
	public LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>> TrajectoryHash;
	public LinkedHashMap<String, LinkedList<GenomicElement>> AmalgamatedOperons;
	public LinkedHashMap<String, LinkedList<OperonData>> OperonHash;
	public LinkedList<Integer> OtherClusters;
	public LinkedList<OperonCluster> OperonGroups;
	
	//append, prepend, and insertion -> check and see which of these are satisfied
	public boolean isAppend = false;
	public boolean isPrepend = false;
	public boolean isInsertion = false;
	public boolean couldbeAssessed = false;
	
	//Methods
	
	//compute evo rate - using amalgamated set
	public void computeEvoRate(){
//		System.out.println("PhyloRange: " + ClusterPhylogeneticRange);
//		double X = (double) OperonGroups.size() / ClusterPhylogeneticRange;
//		System.out.println("X: " + X);
//		double Round = 1000*X;
//		System.out.println("round: " + Round);
//		double Rounded = (double) Math.round(Round)/1000.0;
//		System.out.println("Rounded: " + Rounded);
//		
		
		EvoRate = Math.round(1000.0 * (((double) OperonGroups.size()) / ClusterPhylogeneticRange))/1000.0;
		//System.out.println("EvoRate: " + EvoRate);
	}
	
	//amalgamate operons into species-specific groups
	public void amalgamate(){
		AmalgamatedOperons = new LinkedHashMap<String, LinkedList<GenomicElement>>();
		for (String s : TrajectoryHash.keySet()){
			
			//Initialize output
			LinkedList<GenomicElement> L = new LinkedList<GenomicElement>();
			
			//retrieve list of lists
			LinkedList<LinkedList<GenomicElement>> AllOps = TrajectoryHash.get(s);
			
			//condense to single list
			for (LinkedList<GenomicElement> X : AllOps){
				L.addAll(X);
			}
			
			//store in hash map
			AmalgamatedOperons.put(s, L);
		}
	}
	
	//gene-order appropriate gene split
	public LinkedList<LinkedList<GenomicElement>> GeneOrderGeneSplit(){
		
		//Initialize output
		LinkedList<LinkedList<GenomicElement>> Operons = 
				new LinkedList<LinkedList<GenomicElement>>();
		
		//Initialize a hashmap, for use with storing
		LinkedHashMap<LinkedList<Integer>, LinkedList<GenomicElement>> ClusterIDHash
			= new LinkedHashMap<LinkedList<Integer>, LinkedList<GenomicElement>>();
		
		//Check all operon groups
		for (String s : TrajectoryHash.keySet()){
			
			//retrieve all operons from this organism
			LinkedList<LinkedList<GenomicElement>> OpList = TrajectoryHash.get(s);
			
			//iterate through operons
			for (LinkedList<GenomicElement> L : OpList){
				
				//determine clusters featured
				
				
			}
			
		}
		
		//TODO
		
		//return value
		return Operons;
		
	}
	
	//Predicted operon growth by single gene additions
	public void predictSingleGeneOperonModifications(){
		
		/*
		 * Determine what predicted mechanisms of operon growth are featured
		 * among the resulting operon trajectories.
		 * 
		 * Predictions are based on observed topologies, and especially very clear cases.
		 * This will not catch all of them! But at least this approach may be helpful.
		 * 
		 * Algorithm:
		 * (1) Identify all pairs of cluster groupings that contain at least 2 non-seed genes
		 * 	   and differ in size by one.
		 * 
		 * (2) Compare the order of all possible pairings. Tag trajectory accordingly:
		 * 		
		 * 		In this case, just focus on growth one gene at a time (gene X)
		 * 
		 * 		Given <[A,B,...]> and <[A,B,...]X>
		 * 		If in <A,B,...X> X occurs at the end only, predicted append.
		 * 		If in <A,B,...X> X occurs at the beginning only, predicted prepend.
		 * 		If in <A,B,...X> X is single and X occurs somewhere in the middle, 
		 * 			predicted insertion.
		 *
		 * (3) What this will miss:
		 * 		- multiple gene growth at a time - if the intermediate state is missing
		 * 		  (regardless of the mechanism)
		 * 		-
		 */
		
		//retrieve all clusters
		for (int i = 0; i < OperonGroups.size(); i++){
			
			//retrieve cluster
			OperonCluster OC1 = OperonGroups.get(i);
			
			//sufficient size
			if (OC1.ClustersFeatured.size() > 0){
				
				//all others
				for (int j = i+1; j < OperonGroups.size(); j++){
					
					//retrieve cluster
					OperonCluster OC2 = OperonGroups.get(j);
					
					//sufficient size
					if (OC2.ClustersFeatured.size() > 0){
					
						//find larger and smaller sets.
						OperonCluster Larger = new OperonCluster();
						OperonCluster Smaller = new OperonCluster();
						if (OC1.ClustersFeatured.size() > OC2.ClustersFeatured.size()){
							Larger = OC1;
							Smaller = OC2;	
						} else {
							Larger = OC2;
							Smaller = OC1;
						}
						
//						//debugging.
//						if (Larger.ClustersFeatured.size() == 3 &&
//								Smaller.ClustersFeatured.size() == 2){
//							System.out.println("Breakpoint!");
//						}
						
						//create intersection
						LinkedList<Object> Intersection = new LinkedList<Object>();
						Intersection.addAll(Larger.ClustersFeatured);
						Intersection.retainAll(Smaller.ClustersFeatured);
						
						//difference of a single gene, all others the same
						if (Larger.ClustersFeatured.size() - Smaller.ClustersFeatured.size() == 1
								&& Intersection.size() == Larger.ClustersFeatured.size() - 1){
							
							//could be assessed: two operons were discovered that differed by 1 gene
							couldbeAssessed = true;
							
							//find the unique gene, and look for ordering patterns
							//among the instances.
							for (Object x : Larger.ClustersFeatured){
								if (!Smaller.ClustersFeatured.contains(x)){
									
									//Identify the gene
									int NewGene = (Integer) x;
									
									//initialize counters
									int AppendNum = 0;
									int PrependNum = 0;
									int InsertNum = 0;
									
									//all instances of larger - check for order
									for (LinkedList<GenomicElement> L : Larger.Operons){
								
										//first element
										if (L.getFirst().getClusterID() == NewGene){
										
											//first gene is positive - it's a prepend
											if (L.getFirst().getStrand().equals(Strand.POSITIVE)){
												PrependNum++;
											} else {
												AppendNum++;
											}
											
										//last element
										} else if (L.getLast().getClusterID() == NewGene){
										
											//last gene is positive - it's an append
											if (L.getLast().getStrand().equals(Strand.POSITIVE)){
												AppendNum++;
											} else {
												PrependNum++;
											}
											
										//somewhere else!
										} else {
											InsertNum++;
										}
									}
									
									//Based on counters, make call.
									if (AppendNum > 0 && PrependNum == 0 && InsertNum == 0){
										isAppend = true;
									} else if (AppendNum == 0 && PrependNum > 0 && InsertNum == 0){
										isPrepend = true;
									} else if (AppendNum == 0 && PrependNum == 0 && InsertNum > 0){
										isInsertion = true;
									}
								}
							}
							
							
						}
					}
				}
			}
			
		}
		
		
	}
	
}
