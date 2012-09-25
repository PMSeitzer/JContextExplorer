package genomeObjects;

import java.util.HashMap;
import java.util.LinkedList;

public class ContextSet {
	
	//fields
	private String Name;
	private int DistanceThreshold;
	private HashMap<Integer, LinkedList<GenomicElement>> ContextMapping;
	
// ----------------------- Construction ------------------------//
	

	//Constructor
	public ContextSet(String name, int tolerance){
		this.Name = name;
		this.DistanceThreshold = tolerance;
	}
	
	public ContextSet(){
		super();
	}
	
	//Setters and Getters
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getDistanceThreshold() {
		return DistanceThreshold;
	}

	public void setDistanceThreshold(int distanceThreshold) {
		DistanceThreshold = distanceThreshold;
	}

	public HashMap<Integer, LinkedList<GenomicElement>> getContextMapping() {
		return ContextMapping;
	}

	public void setContextMapping(
			HashMap<Integer, LinkedList<GenomicElement>> contextMapping) {
		ContextMapping = contextMapping;
	}

}
