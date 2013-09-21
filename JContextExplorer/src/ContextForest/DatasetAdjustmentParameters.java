package ContextForest;

import java.io.Serializable;

public class DatasetAdjustmentParameters implements Serializable {

	//fields
	private boolean AdjustmentPenalty;
	private boolean FreeMismatches;
	private int NumberOfFreeMatches;
	private double PenaltyperMismatch;
	private double ContextTreeSegmentationPoint;
	
	//setters and getters
	
	public boolean AllFieldsEqual(DatasetAdjustmentParameters DAPx){
		if (this.AdjustmentPenalty == DAPx.isAdjustmentPenalty()
			&& this.FreeMismatches == DAPx.isFreeMismatches()
			&& this.NumberOfFreeMatches == DAPx.getNumberOfFreeMatches()
			&& this.PenaltyperMismatch == DAPx.getPenaltyperMismatch()
			&& this.getContextTreeSegmentationPoint() == DAPx.getContextTreeSegmentationPoint()
				){
			return true;
		} else {
			return false;
		}
	}
	
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

	public double getContextTreeSegmentationPoint() {
		return ContextTreeSegmentationPoint;
	}

	public void setContextTreeSegmentationPoint(double contextTreeSegmentationPoint) {
		ContextTreeSegmentationPoint = contextTreeSegmentationPoint;
	}
	

	
	
}
