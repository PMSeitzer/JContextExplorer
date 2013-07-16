package ContextForest;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.SwingWorker;

import definicions.MatriuDistancies;

import moduls.frm.QueryData;

public class QuerySet {
	
	// ==================================================//
	// ======= Fields ===================================//
	// ==================================================//
	
	//Logistics
	private String Name;								//Name of this Query Set
	
	//Processing
	
	//Context Trees (all data is in QueryData structure)
	private LinkedList<QueryData> ContextTrees;		//Component Trees
	
	//Results
	
	//Comparisons of context trees with ref trees /DGs
	private LinkedHashMap<String, LinkedList<ScanReport>> TreeScans; 
	
	//Dissimilarities computed for context forest
	private LinkedHashMap<DatasetAdjustmentParameters, LinkedList<Double>> Dissimilarities;

	//Dissimilarities + methodology, to retrieve computed context forests
	private LinkedHashMap<DissimilarityAndMethod, MatriuDistancies> ContextForests;
	
	//constructor
	public QuerySet(){
		TreeScans = new LinkedHashMap<String, LinkedList<ScanReport>>();
		setDissimilarities(new LinkedHashMap<DatasetAdjustmentParameters, LinkedList<Double>>());
		
	}
	
	// ==================================================//
	// ======= Getters and Setters ======================//
	// ==================================================//
	
	public LinkedList<QueryData> getContextTrees() {
		return ContextTrees;
	}

	public void setContextTrees(LinkedList<QueryData> contextTrees) {
		ContextTrees = contextTrees;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public LinkedHashMap<String, LinkedList<ScanReport>> getTreeComparisons() {
		return TreeScans;
	}

	public void setTreeComparisons(LinkedHashMap<String, LinkedList<ScanReport>> treeComparisons) {
		TreeScans = treeComparisons;
	}

	public LinkedHashMap<DatasetAdjustmentParameters, LinkedList<Double>> getDissimilarities() {
		return Dissimilarities;
	}

	public void setDissimilarities(LinkedHashMap<DatasetAdjustmentParameters, LinkedList<Double>> dissimilarities) {
		Dissimilarities = dissimilarities;
	}

	public LinkedHashMap<DissimilarityAndMethod, MatriuDistancies> getContextForests() {
		return ContextForests;
	}

	public void setContextForests(LinkedHashMap<DissimilarityAndMethod, MatriuDistancies> contextForests) {
		ContextForests = contextForests;
	}
	
	
}
