package ContextForest;

import java.io.Serializable;

public class DatasetAdjustmentParameters implements Serializable {

	//fields
	private boolean AdjustmentPenalty;
	private boolean FreeMismatches;
	private int NumberOfFreeMatches;
	private double PenaltyperMismatch;
	
	
	
	//setters and getters
	
	public boolean isAdjustmentPenalty() {
		return AdjustmentPenalty;
	}

	public void setAdjustmentPenalty(boolean adjustmentPenalty) {
		AdjustmentPenalty = adjustmentPenalty;
	}
	public boolean isFreeMismatches() {
		return FreeMismatches;
	}
	public void setFreeMismatches(boolean freeMismatches) {
		FreeMismatches = freeMismatches;
	}
	public int getNumberOfFreeMatches() {
		return NumberOfFreeMatches;
	}
	public void setNumberOfFreeMatches(int numberOfFreeMatches) {
		NumberOfFreeMatches = numberOfFreeMatches;
	}
	public double getPenaltyperMismatch() {
		return PenaltyperMismatch;
	}
	public void setPenaltyperMismatch(double penaltyperMismatch) {
		PenaltyperMismatch = penaltyperMismatch;
	}
	

	
	
}
