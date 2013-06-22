package ContextForest;

import java.util.HashSet;
import java.util.LinkedList;

public class FowlkesMallows {

	//Fields
	
	//data
	private LinkedList<LinkedList<String>> Set1;
	private LinkedList<String> Set1LS;
	private HashSet<String> Set1HS;
	private LinkedList<LinkedList<String>> Set2;
	private LinkedList<String> Set2LS;
	private HashSet<String> Set2HS;
	private HashSet<String> CombinedHash;
	
	//Adjustment options
	
	//Summed mismatch
	private boolean SummedMismatchPenalty;
	private boolean FreeMismatches;
	private int NumberOfFreeMatches;
	private double PenaltyperMismatch;
	
	//Dice/Jaccard scale factor
	private boolean DicePenalty;
	
	//Matching statistics
	private int Set1Only;
	private int Set2Only;
	private int Intersection;
	private int Union;
	
	//computation
	private double B;
	
	//Constructor
	public FowlkesMallows(LinkedList<LinkedList<String>> Set1, LinkedList<LinkedList<String>> Set2){
		
		//parameters
		this.Set1 = Set1;
		this.Set2 = Set2;
		this.Set1LS = Set2List(Set1);
		this.Set2LS = Set2List(Set2);
		this.Set1HS = new HashSet<String>(Set1LS);
		this.Set2HS = new HashSet<String>(Set2LS);
		
		//build combined hash
		LinkedList<String> Combined = new LinkedList<String>();
		Combined.addAll(Set1LS);
		Combined.addAll(Set2LS);
		this.CombinedHash = new HashSet<String>(Combined);
		
	}
	

	// ------ Dissimilarity Processing -----------//

	//Compute dissimilarity
	public double Compute(){
		
		//determine elements counts (for scale factor)
		ElementCounts();
		
		//determine adjustment factor
		double AdjustmentFactor;
		if (SummedMismatchPenalty){
			AdjustmentFactor = SummedMismatchPenalty();
		} else {
			AdjustmentFactor = DiceOrJaccardPenalty();
		}
		
		//retrieve original value
		double OriginalFowlkesMallows = OriginalFowlkesMallows();
		
		//adjust value
		B = OriginalFowlkesMallows * AdjustmentFactor;
		
		//return value
		return B;
	}

	// --- Preprocessing ------ //
	
	//Determine number of elements intersecting, matching, etc
	public void ElementCounts(){
		int Intersect = 0;
		int Only1 = 0;
		int Only2 = 0;
		
		for (String s : CombinedHash){
			
			//re-initialize values
			int Count1 = 0;
			int Count2 = 0;
			
			//determine 1 counts
			for (String s1: Set1LS){
				if (s.equals(s1)){
					Count1++;
				}
			}
			
			//determine 2 counts
			for (String s2: Set2LS){
				if (s.equals(s2)){
					Count2++;
				}
			}
			
			//check for intersection
			if (Count1 > 0 && Count2 > 0){
				
				if (Count1 > Count2){
					Intersect = Intersect + Count2;
					Only1 = Only1 + (Count1 - Count2);
				} else if (Count1 < Count2){
					Intersect = Intersect + Count1;
					Only2 = Only2 + (Count2 - Count1);
				} else {
					Intersect = Intersect + Count1;	//arbitrary because Count1==Count2
				}

			//only in Count 1
			} else if (Count1 > 0 && Count2 == 0){
				
				Only1 = Only1 + Count1;
			
			//only in Count 2
			} else if (Count1 == 0 && Count2 > 0){
				
				Only2 = Only2 + Count2;
			}
			
		}
		
		//update values
		Set1Only = Only1;
		Set2Only = Only2;
		Intersection = Intersect;
		Union = Only1 + Only2 + Intersect;
		

	}
	
	// ---- AdjustmentStep ----- //
	
	//Summed mismatch penalty
	public double SummedMismatchPenalty(){
		
		//Initialize output
		double penalty = 0.0;
		
		//Compute mismatches, and adjust
		int TotalMismatches = Set1Only + Set2Only;
		
		//Adjust for free matches
		if (FreeMismatches){
			TotalMismatches = TotalMismatches - NumberOfFreeMatches;
		}
		
		//adjust accordingly
		penalty = (double) TotalMismatches * PenaltyperMismatch;
		
		//adjust penalty value into scale factor
		if (penalty < 0){
			penalty = 1;
		} else {
			penalty = 1 - penalty;
		}
		
		//return computed penalty
		return penalty;
	}
	
	//Dice Or Jaccard penalty
	public double DiceOrJaccardPenalty(){
		
		//Initialize output
		double penalty = 0.0;
		
		//determine appropriate value
		if (DicePenalty){	//Dice penalty
			penalty = 1 - (2.0 * (double) Intersection /
					((double) Set1LS.size() + (double) Set2LS.size()));
		} else { 	//Jaccard penalty
			penalty = 1 - ((double) Intersection / (double) Union);
		}
		
		//return value
		return penalty;
	}
	
	//---- Processing ------ //
	public double OriginalFowlkesMallows(){
		
		
		double dissimilarity = 0.0;
		
		return 0.0;
	}
	
	// ------ CONVERSIONS --------------//
	
	//Convert each cluster set into a linked list
	public LinkedList<String> Set2List(LinkedList<LinkedList<String>> L){
		
		//Initialize output
		LinkedList<String> Output = new LinkedList<String>();
		
		//add items to Set
		for (LinkedList<String> list : L){
			Output.addAll(list);
		}
		
		return Output;
	}
	
	//------ SETTERS AND GETTERS -------//

	public LinkedList<LinkedList<String>> getSet1() {
		return Set1;
	}

	public void setSet1(LinkedList<LinkedList<String>> set1) {
		Set1 = set1;
	}

	public LinkedList<LinkedList<String>> getSet2() {
		return Set2;
	}

	public void setSet2(LinkedList<LinkedList<String>> set2) {
		Set2 = set2;
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
	}

	public LinkedList<String> getSet1LS() {
		return Set1LS;
	}

	public void setSet1LS(LinkedList<String> set1LS) {
		Set1LS = set1LS;
	}

	public LinkedList<String> getSet2LS() {
		return Set2LS;
	}

	public void setSet2LS(LinkedList<String> set2LS) {
		Set2LS = set2LS;
	}
	
}
