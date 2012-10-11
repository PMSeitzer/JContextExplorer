package genomeObjects;

import java.util.HashMap;
import java.util.LinkedList;

public class ContextSet {
	
	//fields
	private String Name;
	private String Type;
	private boolean PreProcessed;
	private HashMap<Integer, LinkedList<GenomicElement>> ContextMapping;
	
	/*
	 * Types of Context Sets:
	 *  A: PreProcessed = true
	 * (1) IntergenicDist (+SingleGene)
	 * (2) Loaded
	 * 
	 *  B: PreProcessed = false;
	 * (3) Range
	 * (4) GenesAround
	 * (5) Combination
	 * (6) GenesBetween
	 * (7) MultipleQuery
	 */
// ----------------------- Construction ------------------------//
	

	//Constructor
	public ContextSet(String name, String Type){
		this.Name = name;
		this.Type = Type;
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

	public HashMap<Integer, LinkedList<GenomicElement>> getContextMapping() {
		return ContextMapping;
	}

	public void setContextMapping(
			HashMap<Integer, LinkedList<GenomicElement>> contextMapping) {
		ContextMapping = contextMapping;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public boolean isPreProcessed() {
		return PreProcessed;
	}

	public void setPreProcessed(boolean preProcessed) {
		PreProcessed = preProcessed;
	}

}
