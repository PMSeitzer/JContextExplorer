//import java.util.*;

package genomeObjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;

import org.biojava3.core.sequence.Strand;

public class GenomicElement implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2782792639687568243L;
	
	//Fields 
    private String Genome;
	private String Contig;              //-Biological-placement----------
    private int Start;					//-Coordinates-------------------
    private int Stop;					//
    private int Center;					//
    private String Type;				//-Annotation Information--------
    private Strand Strand;				//
    private String Annotation;		    //
	private int ElementID;				//-Other Designations------------
    private int ClusterID;				//
    private String GeneID;					//
    private HashSet<SequenceMotif> AssociatedMotifs = new HashSet<SequenceMotif>();
    private String Translation; 		//This translation is optionally recorded from genbank files.

    //Constructor
    // ----------------------- Constructor-----------------------------//
    public GenomicElement() {
    	Contig = null;
    	Type = null;
    	Strand = null;
    	Start = 0;
    	Stop = 0;
    	ClusterID = 0;
    	GeneID = "";
    	Annotation = "";
    }

    // ----------------------- Comparisons ----------------------------//
    
    //compareTo? Deprecated?
    public int compareTo(GenomicElement E){
		return (this.Start - E.Start);
    }

    //Determine center
    public void DetermineCenter(){
		double CenterPosition = Math.round(0.5*(double)(Stop - Start));
		Center = (int) CenterPosition + Start;
		//debugging - print statement
		//System.out.println("Start:\t" + Start + "\tStop:\t"+ Stop + "\tCenter:\t" + Center);
    }
    
    //Getters and Setters
    //------------------------Getters and Setters----------------------//

	public String getContig() {
		return Contig;
	}
	public void setContig(String contig) {
		Contig = contig;
	}
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
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public Strand getStrand() {
		return Strand;
	}
	public void setStrand(Strand strand) {
		Strand = strand;
	}
	public String getAnnotation() {
		return Annotation;
	}
	public void setAnnotation(String annotation) {
		Annotation = annotation;
	}
	public int getClusterID() {
		return ClusterID;
	}
	public void setClusterID(int clusterID) {
		ClusterID = clusterID;
	}
    public int getElementID() {
		return ElementID;
	}
	public void setElementID(int elementID) {
		ElementID = elementID;
	}

	public String getGeneID() {
		return GeneID;
	}

	public void setGeneID(String geneID) {
		GeneID = geneID;
	}

	public HashSet<SequenceMotif> getAssociatedMotifs() {
		return AssociatedMotifs;
	}
	
	public LinkedList<Object> getAssociatedMotifsAsObjects(LinkedList<String> MotifNames){
		LinkedList<Object> MotifsAsObjects = new LinkedList<Object>();
		if (AssociatedMotifs != null){
			for (SequenceMotif SM : AssociatedMotifs){
				if (MotifNames.contains(SM.getMotifName())){
					MotifsAsObjects.add(SM.getMotifName());
				}
			}
		}

		return MotifsAsObjects;
	}

	public HashSet<String> getAssociatedMotifNames(){
		HashSet<String> MotifNames = new HashSet<String>();
		for (SequenceMotif SM : AssociatedMotifs){
			MotifNames.add(SM.getMotifName().toUpperCase());
		}
		return MotifNames;
	}
	
	public void setAssociatedMotifs(HashSet<SequenceMotif> associatedMotifs) {
		AssociatedMotifs = associatedMotifs;
	}
	
	//add a motif, to be associated with this element
	public void addAMotif(SequenceMotif SM){
		this.AssociatedMotifs.add(SM);
	}

	public void removeAMotifByName(String Name){
		HashSet<SequenceMotif> UpdatedSet = new HashSet<SequenceMotif>();
			for (SequenceMotif SM : this.AssociatedMotifs){
				if (!SM.getMotifName().equals(Name)){
					UpdatedSet.add(SM);
				}
			}
		this.AssociatedMotifs = UpdatedSet;

	}
	
	public void removeAMotif(SequenceMotif SM) {
		try {
			AssociatedMotifs.remove(SM);
		} catch (Exception ex) {
			
		}
		
	}

	public String getTranslation() {
		return Translation;
	}

	public void setTranslation(String translation) {
		Translation = translation;
	}

	public int getCenter() {
		return Center;
	}

	public void setCenter(int center) {
		Center = center;
	}

	public String getGenome() {
		return Genome;
	}

	public void setGenome(String genome) {
		Genome = genome;
	}

}

