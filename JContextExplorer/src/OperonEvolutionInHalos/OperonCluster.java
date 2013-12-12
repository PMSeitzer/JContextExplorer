package OperonEvolutionInHalos;

import genomeObjects.GenomicElement;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class OperonCluster {

	//Fields
	//Generated
	public int SeedCluster;
	public int SortingNumber; 								//For use with sorting
	public LinkedList<String> Organisms;
	public LinkedList<LinkedList<GenomicElement>> Operons;
	public LinkedList<Object> ClustersFeatured;
	
	//Computed
	public double MaxInternalDist;
	public int OperonSize;
	
	//constructor
	public OperonCluster(){
		Organisms = new LinkedList<String>();
		Operons = new LinkedList<LinkedList<GenomicElement>>();
	}
	
	//add org, but don't overwrite
	public void addOrg(String s){
		if (!Organisms.contains(s)){
			Organisms.add(s);
		}
	}
	
	//add list of clusters -> cast to appropriate type
	public void addClustersFeatured(LinkedList<Integer> FamClust){
		LinkedList<Object> ObjList = new LinkedList<Object>();
		for (Integer x : FamClust){
			if (x != SeedCluster){
				Object obj = (Object) x;
				ObjList.add(obj);
			}
		}
		ClustersFeatured=ObjList;
	}
}
