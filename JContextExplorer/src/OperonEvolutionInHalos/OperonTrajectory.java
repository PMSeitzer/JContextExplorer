package OperonEvolutionInHalos;

import genomeObjects.GenomicElement;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class OperonTrajectory {

	//Fields
	public int ClusterID;
	public int OrgsFeatured = -1;
	public double Operonicity  = -1.0;
	public double Variety = -1.0;
	public boolean AgreesWithPhylogenyAtLowestLevel;
	public boolean AlwaysASingleGene = false;
	public LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>> TrajectoryHash;
	public LinkedHashMap<String, LinkedList<OperonData>> OperonHash;
	public LinkedList<Integer> OtherClusters;
	
	public LinkedList<OperonCluster> OperonGroups;
	//Methods
	
}
