package ContextForest;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.SwingWorker;

import definicions.MatriuDistancies;

import moduls.frm.QueryData;

public class QuerySet implements Serializable {
	
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
	private LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> Dissimilarities;

	//Dissimilarities + methodology, to retrieve computed context forests
	private LinkedHashMap<DissimilarityMatrixData, MatriuDistancies> ContextForests;
	
	//constructor
	public QuerySet(){
		TreeScans = new LinkedHashMap<String, LinkedList<ScanReport>>();
		Dissimilarities = new LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData>();
		ContextForests = new LinkedHashMap<DissimilarityMatrixData, MatriuDistancies>();
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

	public LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> getDissimilarities() {
		return Dissimilarities;
	}

	public void setDissimilarities(LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> dissimilarities) {
		Dissimilarities = dissimilarities;
	}

	public LinkedHashMap<DissimilarityMatrixData, MatriuDistancies> getContextForests() {
		return ContextForests;
	}

	public void setContextForests(LinkedHashMap<DissimilarityMatrixData, MatriuDistancies> contextForests) {
		ContextForests = contextForests;
	}
	
	
}
