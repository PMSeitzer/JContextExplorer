package OperonEvolutionInHalos;

import java.util.LinkedList;

public class OperonStabilityReport {

	//by component operon groups
	public int ClusterID;
	public LinkedList<OperonCluster> OutputGroups;
	public boolean MultiStable = false;
	
	//by relationship
	public LinkedList<LinkedList<OperonCluster>> StabilityRelationships;

	//Constructor
	public OperonStabilityReport(int clusterID) {
		ClusterID = clusterID;
		OutputGroups = new LinkedList<OperonCluster>();
		StabilityRelationships = new LinkedList<LinkedList<OperonCluster>>();
	}
	
	//method
	public void addOperonClusters(LinkedList<OperonCluster> L){
		
		//add to stability relationships
		StabilityRelationships.add(L);
		
		//add components
		for (OperonCluster OC : L){
			if (!OutputGroups.contains(OC)){
				OutputGroups.add(OC);
			}
		}
		
		//this operon cluster is multi-stable.
		MultiStable = true;
	}
}
