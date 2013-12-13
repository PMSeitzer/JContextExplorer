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
	
	//compute evo rate - open to investigation
	public void computeEvoRate(){
		EvoRate = Math.round(1000.0 * (double) OperonGroups.size() / ClusterPhylogeneticRange)/1000;
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
	
}
