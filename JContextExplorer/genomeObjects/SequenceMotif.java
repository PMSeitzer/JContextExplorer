package genomeObjects;

import java.util.LinkedList;

public class SequenceMotif {

	//Fields
	private int Start;							//Start coordinate
	private int Stop;							//Stop coordinate
	private String Sequence;					//This sequence
	private LinkedList<GenomicElement> AssociatedElement;	//Associated gene or genes with this element
	private double Confidence;					//Statistical likelihood that this sequence is an instance of the motif
	private String MotifName;					//Identification associated with this motif
	private String Notes;						//extra notes/information associated with this motif instance

	
	// ----------------------- Construction ------------------------//
	
	//Constructor
	public SequenceMotif(){
		
	}
	
	// ----------------------- Methods ------------------------//	
	
	// -------------------- Getters and Setters --------------------//	
	public int getStart() {
		return Start;
	}
	public void setStart(int start) {
		Start = start;
	}
	public int getStop() {
		return Stop;
	}
	public void setStop(int stop) {
		Stop = stop;
	}
	public String getSequence() {
		return Sequence;
	}
	public void setSequence(String sequence) {
		Sequence = sequence;
	}
	public LinkedList<GenomicElement> getAssociatedElement() {
		return AssociatedElement;
	}
	public void setAssociatedElement(LinkedList<GenomicElement> associatedElement) {
		AssociatedElement = associatedElement;
	}
	public double getConfidence() {
		return Confidence;
	}
	public void setConfidence(double confidence) {
		Confidence = confidence;
	}
	public String getMotifName() {
		return MotifName;
	}
	public void setMotifName(String motifName) {
		MotifName = motifName;
	}
	public String getNotes() {
		return Notes;
	}
	public void setNotes(String notes) {
		Notes = notes;
	}
	
}
