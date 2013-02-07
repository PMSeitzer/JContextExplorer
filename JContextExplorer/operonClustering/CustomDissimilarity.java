package operonClustering;

import java.awt.Point;
import java.util.LinkedList;

public class CustomDissimilarity {

	//Fields
	//General
	private String Name;
	private String AmalgamationType;
	private LinkedList<String> Factors;
	
	//Factor 1: Presence/absence of common genes
	private String CGCompareType;
	private boolean CGDuplicatesUnique;
	private double CGWeight;
	private int CGImportance;
	
	//Factor 2: Presence/absence of common motifs
	private LinkedList<String> CMMotifNames;
	private String CMCompareType;
	private boolean CMDuplicatesUnique;
	private double CMWeight;
	private int CMImportance;
	
	//Factor 3: Gene order
	private String GOCompareType;
	private double GOWeight;
	private int GOImportance;
	
	//Factor 4: Intragenic Gap Sizes
	private LinkedList<Point> GGDissMapping;
	private double GGWeight;
	private int GGImportance;
	
	//Factor 5: Changes in strandedness
	private boolean SSIndividualGenes;
	private boolean SSWholeGroup;
	private double SSRelWeightIndGenes;
	private double SSRelWeightWholeGroup;
	private double SSWeight;
	private int SSImportance;
	
	//Constructor
	public CustomDissimilarity(String name2,String amalgamationType2,LinkedList<String> factors2,
			String cGCompareType2,boolean cGDuplicatesUnique2,double cGWeight2,int cGImportance2,
			LinkedList<String> cMMotifNames2,String cMCompareType2,boolean cMDuplicatesUnique2,double cMWeight2,int cMImportance2,
			String gOCompareType2, double gOWeight2, int gOImportance2, 
			LinkedList<Point> gapSizeDissMapping2, double gGWeight2,int gGImportance2,
			boolean individualGenes2, boolean wholeGroup2, double relWeightIndGenes2, double relWeightWholeGroup2, double sSWeight2, int sSImportance2){
		
		//parameters
		//general.
		this.Name = name2;
		this.Factors = factors2;
		this.AmalgamationType = amalgamationType2;
		
		//factor 1: common genes
		this.CGCompareType = cGCompareType2;
		this.CGDuplicatesUnique = cGDuplicatesUnique2;
		this.CGImportance = cGImportance2;
		this.CGWeight = cGWeight2;
		
		//factor 2: common motifs
		this.CMMotifNames = cMMotifNames2;
		this.CMDuplicatesUnique = cMDuplicatesUnique2;
		this.CMImportance = cMImportance2;
		this.CMWeight = cMWeight2;
		
		//factor 3: gene order
		this.GOCompareType = gOCompareType2;
		this.GOImportance = gOImportance2;
		this.GOWeight = gOWeight2;
		
		//factor 4: gene gaps
		this.GGDissMapping = gapSizeDissMapping2;
		this.GGImportance = gGImportance2;
		this.GGWeight = gGWeight2;
		
		//factor 5: Strandedness
		this.SSIndividualGenes = individualGenes2;
		this.SSWholeGroup = wholeGroup2;
		this.SSRelWeightIndGenes = relWeightIndGenes2;
		this.SSRelWeightWholeGroup = relWeightWholeGroup2;
		this.SSImportance = sSImportance2;
		this.SSWeight = sSWeight2;
	}

	//Compute Dissimilarity
	double ComputeDissimilarity(){
		return 0;
	}
}
