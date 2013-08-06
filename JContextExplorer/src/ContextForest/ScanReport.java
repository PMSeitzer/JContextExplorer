package ContextForest;

import java.io.Serializable;

public class ScanReport implements Serializable{

	//Fields
	
	//Info
	private String QueryName;
	
	//Scan-results
	private double Dissimilarity;
	private boolean IdenticalDataSet;
	private double AdjustmentFactor;
	private double PreAdjustedDissimilarity;
	private int TotalLeaves;
	
	
	// GETTERS AND SETTERS
	
	public double getDissimilarity() {
		return Dissimilarity;
	}

	public void setDissimilarity(double dissimilarity) {
		Dissimilarity = dissimilarity;
	}

	public boolean isIdenticalDataSet() {
		return IdenticalDataSet;
	}

	public void setIdenticalDataSet(boolean adjusted) {
		IdenticalDataSet = adjusted;
	}

	public double getAdjustmentFactor() {
		return AdjustmentFactor;
	}

	public void setAdjustmentFactor(double adjustmentFactor) {
		AdjustmentFactor = adjustmentFactor;
	}

	public double getPreAdjustedDissimilarity() {
		return PreAdjustedDissimilarity;
	}

	public void setPreAdjustedDissimilarity(double preAdjustedDissimilarity) {
		PreAdjustedDissimilarity = preAdjustedDissimilarity;
	}

	public String getQueryName() {
		return QueryName;
	}

	public void setQueryName(String queryName) {
		QueryName = queryName;
	}

	public int getTotalLeaves() {
		return TotalLeaves;
	}

	public void setTotalLeaves(int totalLeaves) {
		TotalLeaves = totalLeaves;
	}
	
	
}
