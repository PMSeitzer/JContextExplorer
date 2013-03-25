package genomeObjects;

import java.util.LinkedList;
import org.biojava3.core.sequence.Strand;
public class SequenceMotif {

	//Fields
	private int Start;							//Start coordinate
	private int Stop;							//Stop coordinate
	private Strand Strand;						//Strandedness of motif
	private String Sequence;					//This sequence
	private String Contig;						//Name of sequence containing the motif
	private LinkedList<GenomicElement> AssociatedElement;	//Associated gene or genes with this element
	private double Score;						//Statistical likelihood that this sequence is an instance of the motif
	private double pvalue;						//associated with fimo
	private double qvalue;						//associated with fimo
	private String Source;						//Reference to motif discovery program
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

	public double getScore() {
		return Score;
	}

	public void setScore(double score) {
		Score = score;
	}

	public double getPvalue() {
		return pvalue;
	}

	public void setPvalue(double pvalue) {
		this.pvalue = pvalue;
	}

	public double getQvalue() {
		return qvalue;
	}

	public void setQvalue(double qvalue) {
		this.qvalue = qvalue;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getContig() {
		return Contig;
	}

	public void setContig(String contig) {
		Contig = contig;
	}

	public Strand getStrand() {
		return Strand;
	}

	public void setStrand(Strand strand) {
		Strand = strand;
	}
	
}
