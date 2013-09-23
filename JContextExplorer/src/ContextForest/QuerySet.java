package ContextForest;

import importExport.DadesExternes;

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
	private LinkedList<QueryData> ContextTrees;		//Component Trees
	
	//Context Forest Processing - 3 stages

	//(1) Context Trees (all data is in QueryData structure)
	private boolean ContextTreesComputed = false;
	
	//(2) Dissimilarity matrices
	private LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> DissMatrices;
	
	//(3) Computed Dendrograms
	private LinkedHashMap<DissimilarityMatrixData, DadesExternes> Dendrograms;
	
	//Scan processing
	
	//Comparisons of context trees with ref trees /DGs
	private LinkedHashMap<String, LinkedList<ScanReport>> TreeScans; 
	
	//constructor
	public QuerySet(){
		TreeScans = new LinkedHashMap<String, LinkedList<ScanReport>>();
		DissMatrices = new LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData>();
		Dendrograms = new LinkedHashMap<DissimilarityMatrixData, DadesExternes>();
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

	public boolean isContextTreesComputed() {
		return ContextTreesComputed;
	}

	public void setContextTreesComputed(boolean contextTreesComputed) {
		ContextTreesComputed = contextTreesComputed;
	}

	public LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> getDissMatrices() {
		return DissMatrices;
	}

	public void setDissMatrices(LinkedHashMap<DatasetAdjustmentParameters, DissimilarityMatrixData> dissMatrices) {
		DissMatrices = dissMatrices;
	}

	public LinkedHashMap<DissimilarityMatrixData, DadesExternes> getDendrograms() {
		return Dendrograms;
	}

	public void setDendrograms(LinkedHashMap<DissimilarityMatrixData, DadesExternes> dendrograms) {
		Dendrograms = dendrograms;
	}
	
	
}
