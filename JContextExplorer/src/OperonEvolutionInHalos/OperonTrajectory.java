package OperonEvolutionInHalos;

import genomeObjects.GenomicElement;

import java.util.LinkedHashMap;
import java.util.LinkedList;

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
	
}
