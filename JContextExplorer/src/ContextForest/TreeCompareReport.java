package ContextForest;

public class TreeCompareReport {

	//Fields
	
	//Info
	private String QueryName;
	
	//Scan-results
	private double Dissimilarity;
	private boolean Adjusted;
	private double AdjustmentFactor;
	private double PreAdjustedDissimilarity;
	
	
	// GETTERS AND SETTERS
	
	public double getDissimilarity() {
		return Dissimilarity;
	}

	public void setDissimilarity(double dissimilarity) {
		Dissimilarity = dissimilarity;
	}

	public boolean isAdjusted() {
		return Adjusted;
	}

	public void setAdjusted(boolean adjusted) {
		Adjusted = adjusted;
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
	
	
}
