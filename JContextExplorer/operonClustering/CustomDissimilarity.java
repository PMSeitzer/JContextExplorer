package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.awt.Point;
import java.util.ArrayList;
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
		this.setName(name2);
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
	
	// -------- Compute Dissimilarity -------------------------//
	
	//Common Genes
	public double CGDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		double Dissimilarity = 0;
		double NumIntersecting = 0;
		double Op1Unique = 0;
		double Op2Unique = 0;	
		double Op1Size = 0;
		double Op2Size = 0;
		
		if (Type.equals("Annotation")){
			
			//initialize lists
			ArrayList<String> O1Values = new ArrayList<String>();
			ArrayList<String> O2Values = new ArrayList<String>();
			
			//add elements
			for (GenomicElementAndQueryMatch E: O1){
				O1Values.add(E.getE().getAnnotation().toUpperCase());
			}
			
			for (GenomicElementAndQueryMatch E: O2){
				O2Values.add(E.getE().getAnnotation().toUpperCase());
			}
			
			//Determine numbers of elements at various points
			for (int i = 0; i <O1Values.size(); i++){
				if (O2Values.contains(O1Values.get(i))){
					NumIntersecting++;
				} else {
					Op1Unique++;
				}
			}
			
			for (int i = 0; i < O2Values.size(); i++){
				if (O1Values.contains(O2Values.get(i)) == false){
					Op2Unique++;
				}
			}
			
			Op1Size = O1Values.size();
			Op2Size = O2Values.size();

		} else {
			
			//initialize lists
			ArrayList<Integer> O1Values = new ArrayList<Integer>();
			ArrayList<Integer> O2Values = new ArrayList<Integer>();
			
			//add elements
			for (GenomicElementAndQueryMatch E: O1){
				O1Values.add(E.getE().getClusterID());
			}
			
			for (GenomicElementAndQueryMatch E: O2){
				O2Values.add(E.getE().getClusterID());
			}
			
			for (int i = 0; i <O1Values.size(); i++){
				if (O2Values.contains(O1Values.get(i))){
					NumIntersecting++;
				} else {
					Op1Unique++;
				}
			}
			
			for (int i = 0; i < O2Values.size(); i++){
				if (O1Values.contains(O2Values.get(i)) == false){
					Op2Unique++;
				}
			}
			
			Op1Size = O1Values.size();
			Op2Size = O2Values.size();
			
		}
		
		if (CGCompareType.equals("Dice")){
			//Dice Measure
			Dissimilarity = 1-(2*NumIntersecting)/(Op1Size+Op2Size);
		} else if (CGCompareType.equals("Jaccard")){
			//Jaccard Measure
			Dissimilarity =  1-(NumIntersecting/(NumIntersecting+Op1Unique+Op2Unique));	
		}
	
		return Dissimilarity;
	}
	
	//Common Motifs
	public double CMDissimilarity(LinkedList<GenomicElementAndQueryMatch> G1, LinkedList<GenomicElementAndQueryMatch> G2, String Type){
		return 0;
	}
	
	//Gene Order
	public double GODissimilarity(LinkedList<GenomicElementAndQueryMatch> G1, LinkedList<GenomicElementAndQueryMatch> G2, String Type){
		return 0;
	}
	
	//Gene Gaps
	public double GGDissimilarity(LinkedList<GenomicElementAndQueryMatch> G1, LinkedList<GenomicElementAndQueryMatch> G2, String Type){
		return 0;
	}
	
	//Strandedness
	public double SSDissimilarity(LinkedList<GenomicElementAndQueryMatch> G1, LinkedList<GenomicElementAndQueryMatch> G2, String Type){
		return 0;
	}
	
	//Total Dissimilarity
	public double TotalDissimilarity(LinkedList<GenomicElementAndQueryMatch> G1, LinkedList<GenomicElementAndQueryMatch> G2, String T){
		
		//refactor, if appropriate
		
		//Linear Scale
		if (AmalgamationType.equals("Linear")){
			
			//determine total weight, to scale by.
			Double AllProvidedWeights = 0.0;
			
			//initialize values
			Double CGContribution = 0.0;
			Double CMContribution = 0.0;
			Double GOContribution = 0.0;
			Double GGContribution = 0.0;
			Double SSContribution = 0.0;
			
			//Determine Factors
			if (Factors.contains("CG")){
				AllProvidedWeights = AllProvidedWeights + CGWeight;
				CGContribution = CGDissimilarity(G1,G2,T);
			}
			if (Factors.contains("CM")){
				AllProvidedWeights = AllProvidedWeights + CMWeight;
				CMContribution = CMDissimilarity(G1,G2,T);
			}
			if (Factors.contains("GO")){
				AllProvidedWeights = AllProvidedWeights + GOWeight;
				GOContribution = GODissimilarity(G1,G2,T);
			}
			if (Factors.contains("GG")){
				AllProvidedWeights = AllProvidedWeights + GGWeight;
				GGContribution = GGDissimilarity(G1,G2,T);
			}
			if (Factors.contains("SS")){
				AllProvidedWeights = AllProvidedWeights + SSWeight;
				SSContribution = SSDissimilarity(G1,G2,T);
			}
			
			//Weight accordingly.
			return (CGWeight/AllProvidedWeights) * CGContribution +
				   (CMWeight/AllProvidedWeights) * CMContribution +
				   (GOWeight/AllProvidedWeights) * GOContribution +
				   (GGWeight/AllProvidedWeights) * GGContribution +
				   (SSWeight/AllProvidedWeights) * SSContribution;
			
		} else {
			return 0;
		}

	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
}
