//import java.util.*;

package genomeObjects;

import org.biojava3.core.sequence.Strand;

public class GenomicElement {
	
	//Fields 
    private String Contig;              //-Biological-placement----------
    private int Start;					//-Coordinates-------------------
    private int Stop;					//
    private String Type;				//-Annotation Information--------
    private Strand Strand;				//
    private String Annotation;		    //
	private int ElementID;				//
    private int CDSID;					//-Other Designations------------
    private int ClusterID;				//

    //Constructor
    // ----------------------- Constructor-----------------------------//
    public GenomicElement() {
    	Contig = null;
    	Type = null;
    	Strand = null;
    	Start = 0;
    	Stop = 0;
    	CDSID  = 0;
    	ClusterID = 0;
    	Annotation = null;
    }

    // ----------------------- Comparisons ----------------------------//
    
    //compareTo? Deprecated?
    public int compareTo(GenomicElement E){
		return (this.Start - E.Start);
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
	public int getCDSID() {
		return CDSID;
	}
	public void setCDSID(int cDSID) {
		CDSID = cDSID;
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

}

