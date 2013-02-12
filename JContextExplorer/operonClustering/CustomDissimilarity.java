package operonClustering;

import genomeObjects.GenomicElementAndQueryMatch;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
		this.CMCompareType = cMCompareType2;
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
	
	//Generalized Dice/Jaccard
	public double GeneralizedDiceOrJaccard(LinkedList<Object> O1, LinkedList<Object> O2, boolean TreatDuplicatesAsUnique, String Type){
		
		//Initialize values
		double Dissimilarity = 0;
		double NumIntersecting = 0;
		double SizeO1;
		double SizeO2;
		double SizeUnion = 0;
		
		//Hash Sets
		HashSet<Object> O1Hash = new HashSet<Object>(O1);
		HashSet<Object> O2Hash = new HashSet<Object>(O2);
		HashSet<Object> IntersectionHash = new HashSet<Object>(O1);
		HashSet<Object> UnionHash = new HashSet<Object>(O1);
		IntersectionHash.retainAll(O2Hash);
		UnionHash.addAll(O2Hash);
		
		if (TreatDuplicatesAsUnique){
			SizeO1 = O1.size();
			SizeO2 = O2.size();

			//Find all intersecting types, and find the number that intersect.
			for (Object O : IntersectionHash){
				NumIntersecting = NumIntersecting + Math.min(Collections.frequency(O1, O), Collections.frequency(O2, O));
			}
			
			//compute union
			SizeUnion = SizeO1 + SizeO2 - NumIntersecting;
			
		} else {

			SizeO1 = O1Hash.size();
			SizeO2 = O2Hash.size();
			NumIntersecting = IntersectionHash.size();
			SizeUnion = UnionHash.size();
		}

		//compute dissimilarity from computed size / union / intersection values
		if (Type.equals("Dice")){
			if (!((SizeO1 == 0) && (SizeO2 == 0))){
				Dissimilarity = 1-(2*NumIntersecting)/(SizeO1+SizeO2);
			} else { //divide by zero case
				Dissimilarity = 0;
			}
		} else {	//Jaccard
			if (SizeUnion != 0) {
				Dissimilarity =  1-(NumIntersecting/SizeUnion);
			} else { //divide by zero case
				Dissimilarity = 0;
			}
			
		}
		
		//debugging
//		if (O1.contains("BOP") && O1.contains("Promoter") &&
//				O2.contains("BOP") && O2.contains("Promoter")){
//			
//			System.out.println("O1 Objects:");
//			for (Object o : O1){
//				System.out.println(o.toString());
//			}
//			System.out.println("O2 Objects:");
//			for (Object o : O2){
//				System.out.println(o.toString());
//			}
//			
//			System.out.println("|O1|: " + SizeO1);
//			System.out.println("|O2|: " + SizeO2);
//			System.out.println("|O1 A O2|: " + NumIntersecting);
//			System.out.println("Diss: " + Dissimilarity);
//		}

		return Dissimilarity;
	}

	//Common Genes
	public double CGDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		double Dissimilarity = 0;
		
		LinkedList<Object> O1Values = new LinkedList<Object>();
		LinkedList<Object> O2Values = new LinkedList<Object>();
		
		//determine appropriate data types
		if (Type.equals("annotation")){
			
			//add elements
			for (GenomicElementAndQueryMatch E: O1){
				O1Values.add(E.getE().getAnnotation().toUpperCase());
			}
			
			for (GenomicElementAndQueryMatch E: O2){
				O2Values.add(E.getE().getAnnotation().toUpperCase());
			}
			
		} else {

			int NegativeCounter = -10;
			
			//add elements
			//if clusterID = 0, this is really probably unique, treat all cluster == 0 as unique sets.
			for (GenomicElementAndQueryMatch E: O1){
				if (E.getE().getClusterID() == 0){
					NegativeCounter--;
					O1Values.add(NegativeCounter);
				} else {
					O1Values.add(E.getE().getClusterID());
				}

			}
			
			for (GenomicElementAndQueryMatch E: O2){
				if (E.getE().getClusterID() == 0){
					NegativeCounter--;
					O2Values.add(NegativeCounter);
				} else {
					O2Values.add(E.getE().getClusterID());
				}
			}
			
		}
		
		//pass into method
		Dissimilarity = GeneralizedDiceOrJaccard(O1Values,O2Values,CGDuplicatesUnique,CGCompareType);

		return Dissimilarity;

	}
		
	//Common Motifs
	public double CMDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		double Dissimilarity = 0;
		
		/*
		 * Approach: Find all common gene pairs, and assess all associated motifs,
		 * using dice or jaccard.  Take average of all individual common gene motif-related 
		 * dissimilarities.
		 * 
		 * In the case of multiple duplicate common genes existing between gene groupings,
		 * compute all possible pairwise dissimilarities between genes, and use the
		 * Munkres-Hungarian algorithm to determine the assignments that minimize the sum
		 * of all dissimilarities.
		 * 
		 * If there is only one common element across sets (the most common case), then
		 * the matrix is not very interesting and the Hungarian algorithm just returns the element.
		 */

		if (Type.equals("annotation")){
			
			//First, isolate all common types.
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
			
			//determine intersection of common genes (by annotation)
			HashSet<String> O1Hash = new HashSet<String>(O1Values);
			HashSet<String> O2Hash = new HashSet<String>(O2Values);
			HashSet<String> Intersection = new HashSet<String>(O1Values);
			Intersection.retainAll(O2Hash);
			Intersection.retainAll(O1Hash);
			
			//Sum of Dissimilarities
			double SumDissimilarity = 0;
			
			//determine sum of dissimilarities
			for (String s : Intersection){
			
				//find all instances in each set
				LinkedList<GenomicElementAndQueryMatch> InstancesIn1 = new LinkedList<GenomicElementAndQueryMatch>();
				for (GenomicElementAndQueryMatch E: O1){
					if (E.getE().getAnnotation().toUpperCase().equals(s)){
						InstancesIn1.add(E);
					}
				}
				
				LinkedList<GenomicElementAndQueryMatch> InstancesIn2 = new LinkedList<GenomicElementAndQueryMatch>();
				for (GenomicElementAndQueryMatch E: O2){
					if (E.getE().getAnnotation().toUpperCase().equals(s)){
						InstancesIn2.add(E);
					}
				}
				
				//compute motif dissimilarity of all pairwise, to determine best matching set
				double[][] MotifComparisons = new double[InstancesIn1.size()][InstancesIn2.size()];
				
				for (int i = 0; i < InstancesIn1.size(); i++){
					for (int j = 0; j < InstancesIn2.size(); j++){
						
						//retrieve all motifs
						LinkedList<Object> MotifsIn1 = InstancesIn1.get(i).getE().getAssociatedMotifsAsObjects(CMMotifNames);
						LinkedList<Object> MotifsIn2 = InstancesIn2.get(j).getE().getAssociatedMotifsAsObjects(CMMotifNames);
						
						//fill in array
						MotifComparisons[i][j] = 
								this.GeneralizedDiceOrJaccard(MotifsIn1, MotifsIn2, CMDuplicatesUnique, CMCompareType);
						
//						if (MotifsIn1.contains("BOP") && MotifsIn1.contains("Promoter") &&
//								MotifsIn2.contains("BOP") && MotifsIn2.contains("Promoter")){
//							System.out.println("Diss: " + MotifComparisons[i][j]);
//						}
					}
				}
				
				//Call Hungarian algorithm, sum dissimilarities, add to running total
				SumDissimilarity = SumDissimilarity 
						+ (HungarianAlgorithm.JCEAssignment(MotifComparisons) / (double) Math.min(InstancesIn1.size(), InstancesIn2.size()));
				
			}
			
			//Normalize
			Dissimilarity = SumDissimilarity / Intersection.size();
			
		} else { //Common Cluster ID
			
			//initialize lists
			ArrayList<Integer> O1Values = new ArrayList<Integer>();
			ArrayList<Integer> O2Values = new ArrayList<Integer>();
			
			int NegativeCounter = -10;
			
			//add elements
			//if clusterID = 0, this is really probably unique, treat all cluster == 0 as unique sets.
			for (GenomicElementAndQueryMatch E: O1){
				if (E.getE().getClusterID() == 0){
					NegativeCounter--;
					O1Values.add(NegativeCounter);
				} else {
					O1Values.add(E.getE().getClusterID());
				}

			}
			
			for (GenomicElementAndQueryMatch E: O2){
				if (E.getE().getClusterID() == 0){
					NegativeCounter--;
					O2Values.add(NegativeCounter);
				} else {
					O2Values.add(E.getE().getClusterID());
				}
			}
			
			//determine intersection of common genes (by cluster ID)
			HashSet<Integer> O1Hash = new HashSet<Integer>(O1Values);
			HashSet<Integer> O2Hash = new HashSet<Integer>(O2Values);
			HashSet<Integer> Intersection = new HashSet<Integer>(O1Values);
			Intersection.retainAll(O2Hash);
			Intersection.retainAll(O1Hash);
			
			//Sum of Dissimilarities
			double SumDissimilarity = 0;
			
			//determine sum of dissimilarities
			for (Integer s : Intersection){
			
				//find all instances in each set
				LinkedList<GenomicElementAndQueryMatch> InstancesIn1 = new LinkedList<GenomicElementAndQueryMatch>();
				for (GenomicElementAndQueryMatch E: O1){
					if (E.getE().getClusterID() == s){
						InstancesIn1.add(E);
					}
				}
				
				LinkedList<GenomicElementAndQueryMatch> InstancesIn2 = new LinkedList<GenomicElementAndQueryMatch>();
				for (GenomicElementAndQueryMatch E: O2){
					if (E.getE().getClusterID() == s){
						InstancesIn2.add(E);
					}
				}
				
				//compute motif dissimilarity of all pairwise, to determine best matching set
				double[][] MotifComparisons = new double[InstancesIn1.size()][InstancesIn2.size()];
				
				for (int i = 0; i < InstancesIn1.size(); i++){
					for (int j = 0; j < InstancesIn2.size(); j++){
						
						//retrieve all motifs
						LinkedList<Object> MotifsIn1 = InstancesIn1.get(i).getE().getAssociatedMotifsAsObjects(CMMotifNames);
						LinkedList<Object> MotifsIn2 = InstancesIn2.get(j).getE().getAssociatedMotifsAsObjects(CMMotifNames);
						
						//fill in array
						MotifComparisons[i][j] = 
								this.GeneralizedDiceOrJaccard(MotifsIn1, MotifsIn2, CMDuplicatesUnique, CMCompareType);
					}
				}
				
				//Call Hungarian algorithm, sum dissimilarities, add to running total
				SumDissimilarity = SumDissimilarity 
						+ (HungarianAlgorithm.JCEAssignment(MotifComparisons) / (double) Math.min(InstancesIn1.size(), InstancesIn2.size()));

			}
			
			//Normalize
			Dissimilarity = SumDissimilarity / Intersection.size();
			
		}
		
		return Dissimilarity;
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
