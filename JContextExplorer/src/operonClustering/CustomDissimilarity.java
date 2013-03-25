package operonClustering;

import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import org.biojava3.core.sequence.Strand;

import moduls.frm.children.GapPoint;
import moduls.frm.children.GapPointMapping;

public class CustomDissimilarity {

	//Fields
	//General
	private String Name;
	private String AmalgamationType;
	private LinkedList<String> Factors;
	private Double ImportanceFraction;
	
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
	private boolean HeadPos;
	private boolean PairOrd;
	private double RelWeightHeadPos;
	private double RelWeightPairOrd;
	private double GOWeight;
	private int GOImportance;
	
	//Factor 4: Intragenic Gap Sizes
	private GapPointMapping GPM;
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
	public CustomDissimilarity(String name2,String amalgamationType2,LinkedList<String> factors2, double ImpFactor,
			String cGCompareType2,boolean cGDuplicatesUnique2,double cGWeight2,int cGImportance2,
			LinkedList<String> cMMotifNames2,String cMCompareType2,boolean cMDuplicatesUnique2,double cMWeight2,int cMImportance2,
			boolean HeadPos, boolean PairOrd, double HeadPoswt, double PairOrdwt, double gOWeight2, int gOImportance2,
			GapPointMapping gapSizeDissMapping2, double gGWeight2,int gGImportance2,
			boolean individualGenes2, boolean wholeGroup2, double relWeightIndGenes2, double relWeightWholeGroup2, double sSWeight2, int sSImportance2){
		
		//parameters
		//general.
		this.setName(name2);
		this.Factors = factors2;
		this.AmalgamationType = amalgamationType2;
		this.ImportanceFraction = ImpFactor;
		
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
		this.HeadPos = HeadPos;
		this.PairOrd = PairOrd;
		this.RelWeightHeadPos = HeadPoswt;
		this.RelWeightPairOrd = PairOrdwt;
		this.GOImportance = gOImportance2;
		this.GOWeight = gOWeight2;
		
		//factor 4: gene gaps
		this.GPM = gapSizeDissMapping2;
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
	// -------- General ----------//
	
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

	//Head position based dissimilarity (GO)
	public double HeadPosDiss(LinkedList<Object> O1Values, LinkedList<Object> O2Values){
		double HeadPosDissimilarity = 0.0;
		
		/*
		 * Algorithm:
		 * (1) determine # common elements (dup. unique)
		 * (2) Set first as pivot, head = start of first list
		 * (3) count common positions from second forwards
		 * (4) count common positions from second reversed
		 * (5) retain higher count
		 * (6) diss = 1 - (higher count) / (# common elements [dup unique])
		 */
		
		//re-sizing - O1 Values must always be larger
		if (O1Values.size() < O2Values.size()){
			LinkedList<Object> Temp = O1Values;
			O1Values = O2Values;
			O2Values = Temp;
		}

		//(1) common elements
		int NumIntersecting = 0;
		HashSet<Object> O2Hash = new HashSet<Object>(O2Values);
		HashSet<Object> IntersectionHash = new HashSet<Object>(O1Values);
		IntersectionHash.retainAll(O2Hash);
		
		//Find all intersecting types, and find the number that intersect.
		for (Object O : IntersectionHash){
			NumIntersecting = NumIntersecting + Math.min(Collections.frequency(O1Values, O), Collections.frequency(O2Values, O));
		}

		//(2)-(5) Counts
		int FwdCount = 0;
		int RevCount = 0;
		int MaxCount = 0;
		
		for (int i = 0; i < O2Values.size(); i++){
			if (O2Values.get(i).equals(O1Values.get(i))){
				FwdCount++;
			}
			if (O2Values.get(i).equals(O1Values.get(O1Values.size()-1-i))){
				RevCount++;
			}
		}
		MaxCount = Math.max(FwdCount, RevCount);
		
		//(6) Compute dissimilarity
		if (NumIntersecting != 0){
			HeadPosDissimilarity = 1 - ((double)MaxCount/(double)NumIntersecting);
		} else {
			HeadPosDissimilarity = 0;
		}

		return HeadPosDissimilarity;
	}
	
	//Pair ordering based dissimilarity (GO)
	public double PairOrdDiss(LinkedList<Object> O1Values, LinkedList<Object> O2Values){
		double PairOrdDissimilarity = 0;
		
		/*
		 * Algorithm:
		 * (1) Build O1 Adjacencies
		 * (2) Build O2 Adjacencies + O2 reverse adjacencies
		 * (3) Count common, and take higher
		 */
		
		//Initialize adjacencies
		LinkedList<LinkedList<Object>> O1Adjacencies = new LinkedList<LinkedList<Object>>();
		LinkedList<LinkedList<Object>> O2AdjacenciesFwd = new LinkedList<LinkedList<Object>>();
		LinkedList<LinkedList<Object>> O2AdjacenciesRev = new LinkedList<LinkedList<Object>>();
		
		//Build adjacencies
		for (int i = 0; i <O1Values.size()-1; i++){
			LinkedList<Object> SingleAdjacency = new LinkedList<Object>();
			SingleAdjacency.add(O1Values.get(i));
			SingleAdjacency.add(O1Values.get(i+1));
			O1Adjacencies.add(SingleAdjacency);
		}
		
		for (int i = 0; i <O2Values.size()-1; i++){
			LinkedList<Object> FwdAdjacency = new LinkedList<Object>();
			FwdAdjacency.add(O2Values.get(i));
			FwdAdjacency.add(O2Values.get(i+1));
			O2AdjacenciesFwd.add(FwdAdjacency);
		}
		
		for (int i = O2Values.size()-2; i >= 0; i--){
			LinkedList<Object> RevAdjacency = new LinkedList<Object>();
			RevAdjacency.add(O2Values.get(i+1));
			RevAdjacency.add(O2Values.get(i));
			O2AdjacenciesRev.add(RevAdjacency);
		}
		
		//find intersection
		O2AdjacenciesFwd.retainAll(O1Adjacencies);
		O2AdjacenciesRev.retainAll(O1Adjacencies);
		
		//compute dissimilarity
		int SmallerSize = Math.min(O1Values.size(), O2Values.size());
		int MostAdjacencies = Math.max(O2AdjacenciesFwd.size(), O2AdjacenciesRev.size());
		if (SmallerSize > 1){
			PairOrdDissimilarity = 1 - ((double)MostAdjacencies/(double)(SmallerSize-1));
		} else {
			PairOrdDissimilarity = 0;
		}

		return PairOrdDissimilarity;
	}
	
	// ------- Factors -----------//
	
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

		//Maximum dissimilarity is 1, minimum is 0.
		if (Dissimilarity > 1){
			Dissimilarity = 1;
		} else if (Dissimilarity <= 0){
			Dissimilarity = 0;
		}
		
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
				//double[][] MotifComparisons = new double[InstancesIn1.size()][InstancesIn2.size()];
				
				//make the matrix square, if it needs to be.
				int MatrixSize = Math.max(InstancesIn1.size(), InstancesIn2.size());
				double[][] MotifComparisons = new double[MatrixSize][MatrixSize];
				for (int i = 0; i < MatrixSize; i++){
					for (int j = 0; j < MatrixSize; j++){
						MotifComparisons[i][j] = 0;
					}
				}
				
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
			
			if (Intersection.size() > 0){
				//Normalize
				Dissimilarity = SumDissimilarity / Intersection.size();
			}

			
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
				//double[][] MotifComparisons = new double[InstancesIn1.size()][InstancesIn2.size()];
				
				//make the matrix square, if it needs to be.
				int MatrixSize = Math.max(InstancesIn1.size(), InstancesIn2.size());
				double[][] MotifComparisons = new double[MatrixSize][MatrixSize];
				for (int i = 0; i < MatrixSize; i++){
					for (int j = 0; j < MatrixSize; j++){
						MotifComparisons[i][j] = 0;
					}
				}

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
				
				
//				//debugging
//				for (int i = 0; i < MatrixSize; i++){
//					String Line = "";
//					for (int j =0; j <MatrixSize; j++){
//						Line = Line + " " + MotifComparisons[i][j];
//					}
//					Line = Line + ";";
//					System.out.println(Line);
//				}

				//Call Hungarian algorithm, sum dissimilarities, add to running total
				SumDissimilarity = SumDissimilarity 
						+ (HungarianAlgorithm.JCEAssignment(MotifComparisons) / (double) Math.min(InstancesIn1.size(), InstancesIn2.size()));


			}
			
			if (Intersection.size() > 0){
				//Normalize
				Dissimilarity = SumDissimilarity / Intersection.size();
			}
			
		}
		
		//adjust dissimilarity before returning
		if (Dissimilarity > 1){
			Dissimilarity = 1;
		} else if (Dissimilarity < 0){
			Dissimilarity = 0;
		}
		
		return Dissimilarity;
	}
	
	//Gene Order
	public double GODissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		//Initialize output
		double Dissimilarity = 0;
		double HeadPosDissimilarity = 0;
		double PairOrdDissimilarity = 0;
		
		//Strand counts
		int StrandMatches = 0;
		
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
		
		//(1) common elements
		int NumIntersecting = 0;
		HashSet<Object> O2Hash = new HashSet<Object>(O2Values);
		HashSet<Object> IntersectionHash = new HashSet<Object>(O1Values);
		IntersectionHash.retainAll(O2Hash);
		
		//Find all intersecting types, and find the number that intersect.
		for (Object O : IntersectionHash){
			NumIntersecting = NumIntersecting + Math.min(Collections.frequency(O1Values, O), Collections.frequency(O2Values, O));
		}
		
		//Determine relative weights
		double TotalRelativeWeights = 0;
		if (HeadPos){
			//increment total weights contribution
			TotalRelativeWeights = TotalRelativeWeights + RelWeightHeadPos;
			
			//Compute head position contribution
			HeadPosDissimilarity = this.HeadPosDiss(O1Values, O2Values);
			
		}
		if (PairOrd){
			//increment total weights contribution
			TotalRelativeWeights = TotalRelativeWeights + RelWeightPairOrd;
			
			//Compute number of common pairs contribution
			PairOrdDissimilarity = this.PairOrdDiss(O1Values, O2Values);
		}
		
		//Amalgamate into dissimilarity
		Dissimilarity = (RelWeightHeadPos/TotalRelativeWeights) * HeadPosDissimilarity +
						(RelWeightPairOrd/TotalRelativeWeights) * PairOrdDissimilarity;
		
		//Maximum dissimilarity is 1, minimum is 0.
		if (Dissimilarity > 1){
			Dissimilarity = 1;
		} else if (Dissimilarity <= 0){
			Dissimilarity = 0;
		}
		
		return Dissimilarity;
	}

	//Gene Gaps
	public double GGDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		//re-sizing - O1 Values must always be larger
		if (O1.size() < O2.size()){
			LinkedList<GenomicElementAndQueryMatch> Temp = O1;
			O1 = O2;
			O2 = Temp;
		}
		
		//Initial Dissimilarity
		double Dissimilarity = 0.0;
		
		//Initialize adjacencies
		LinkedList<LinkedList<GenomicElementAndQueryMatch>> O1Adjacencies = 
				new LinkedList<LinkedList<GenomicElementAndQueryMatch>>();
		LinkedList<LinkedList<GenomicElementAndQueryMatch>> O2AdjacenciesFwd = 
				new LinkedList<LinkedList<GenomicElementAndQueryMatch>>();
		LinkedList<LinkedList<GenomicElementAndQueryMatch>> O2AdjacenciesRev = 
				new LinkedList<LinkedList<GenomicElementAndQueryMatch>>();
		
		//Build adjacencies
		for (int i = 0; i < O1.size()-1; i++){
			LinkedList<GenomicElementAndQueryMatch> SingleAdjacency = 
					new LinkedList<GenomicElementAndQueryMatch>();
			SingleAdjacency.add(O1.get(i));
			SingleAdjacency.add(O1.get(i+1));
			O1Adjacencies.add(SingleAdjacency);
		}
		
		for (int i = 0; i <O2.size()-1; i++){
			LinkedList<GenomicElementAndQueryMatch> FwdAdjacency = 
					new LinkedList<GenomicElementAndQueryMatch>();
			FwdAdjacency.add(O2.get(i));
			FwdAdjacency.add(O2.get(i+1));
			O2AdjacenciesFwd.add(FwdAdjacency);
		}
		
		for (int i = O2.size()-2; i >= 0; i--){
			LinkedList<GenomicElementAndQueryMatch> RevAdjacency = 
					new LinkedList<GenomicElementAndQueryMatch>();
			RevAdjacency.add(O2.get(i+1));
			RevAdjacency.add(O2.get(i));
			O2AdjacenciesRev.add(RevAdjacency);
		}
		
		//initialize dissimilarities
		double ForwardDissimilarity = 0;
		double ReverseDissimilarity = 0;
		int FwdMatch = 0;
		int RevMatch = 0;
		
		//Walk along adjacencies.
		for (LinkedList<GenomicElementAndQueryMatch> Adjacency : O1Adjacencies){
			
			//check forward
			for (LinkedList<GenomicElementAndQueryMatch> FwdAdj : O2AdjacenciesFwd){
				boolean EquivalentAdjacency = false;
				if (Type.equals("annotation")){
					if (Adjacency.get(0).getE().getAnnotation().toUpperCase()
							.equals(FwdAdj.get(0).getE().getAnnotation().toUpperCase()) && //annotation match
							Adjacency.get(1).getE().getAnnotation().toUpperCase()
							.equals(FwdAdj.get(1).getE().getAnnotation().toUpperCase()) && //annotation match
							Adjacency.get(0).getE().getContig().equals(Adjacency.get(1).getE().getContig()) && //internal contig match
							FwdAdj.get(0).getE().getContig().equals(FwdAdj.get(1).getE().getContig())){ //internal contig match
						
						EquivalentAdjacency = true;
						FwdMatch++;
					}
				} else {
					if (Adjacency.get(0).getE().getClusterID() ==
							FwdAdj.get(0).getE().getClusterID() && //cluster ID match
							Adjacency.get(1).getE().getClusterID() ==
							FwdAdj.get(1).getE().getClusterID() && //cluster ID match
							Adjacency.get(0).getE().getContig().equals(Adjacency.get(1).getE().getContig()) && //internal contig match
							FwdAdj.get(0).getE().getContig().equals(FwdAdj.get(1).getE().getContig())){ //internal contig match
					
						EquivalentAdjacency = true;
						FwdMatch++;
					}
				}
				
				//If the two adjacencies are equivalent, compute a gap penalty
				if (EquivalentAdjacency){
					//gap computation
					int gap1 = Adjacency.get(1).getE().getStart() - Adjacency.get(0).getE().getStop();
					int gap2 = FwdAdj.get(1).getE().getStart() - FwdAdj.get(0).getE().getStop();
					int gapDiff = Math.abs(gap2-gap1);
					double gapDissimilarity = 0;
					
					//determine gap dissimilarity
					if (gapDiff > this.GPM.MaxGapLimit){
						gapDissimilarity = this.GPM.MaxDissimilarity;
					} else if (gapDiff > this.GPM.MinGaplimit){
						gapDissimilarity = this.GPM.Mapping.get(gapDiff);
					}
					
					//add to running total
					ForwardDissimilarity = ForwardDissimilarity + gapDissimilarity;
					
					//reset adjacency
					EquivalentAdjacency = false;
				}
			}
			
			//check reverse
			for (LinkedList<GenomicElementAndQueryMatch> RevAdj : O2AdjacenciesRev){
				boolean EquivalentAdjacencyRev = false;
				
				if (Type.equals("annotation")){
					if (Adjacency.get(0).getE().getAnnotation().toUpperCase()
							.equals(RevAdj.get(0).getE().getAnnotation().toUpperCase()) && //annotation match
							Adjacency.get(1).getE().getAnnotation().toUpperCase()
							.equals(RevAdj.get(1).getE().getAnnotation().toUpperCase()) && //annotation match
							Adjacency.get(0).getE().getContig().equals(Adjacency.get(1).getE().getContig()) && //internal contig match
							RevAdj.get(0).getE().getContig().equals(RevAdj.get(1).getE().getContig())){ //internal contig match
						
						EquivalentAdjacencyRev = true;
						RevMatch++;
					}
				} else {
					if (Adjacency.get(0).getE().getClusterID() ==
							RevAdj.get(0).getE().getClusterID() && //cluster ID match
							Adjacency.get(1).getE().getClusterID() ==
							RevAdj.get(1).getE().getClusterID() && //cluster ID match
							Adjacency.get(0).getE().getContig().equals(Adjacency.get(1).getE().getContig()) && //internal contig match
							RevAdj.get(0).getE().getContig().equals(RevAdj.get(1).getE().getContig())){ //internal contig match
					
						EquivalentAdjacencyRev = true;
						RevMatch++;
					}
				}
				
				//If the two adjacencies are equivalent, compute a gap penalty
				if (EquivalentAdjacencyRev){
					
					//gap computation
					int gap1 = Adjacency.get(1).getE().getStart() - Adjacency.get(0).getE().getStop();
					int gap2 = RevAdj.get(0).getE().getStart() - RevAdj.get(1).getE().getStop();
					int gapDiff = Math.abs(gap2-gap1);
					double gapDissimilarity = 0;
					
					//determine gap dissimilarity
					if (gapDiff > this.GPM.MaxGapLimit){
						gapDissimilarity = this.GPM.MaxDissimilarity;
					} else if (gapDiff > this.GPM.MinGaplimit){
						gapDissimilarity = this.GPM.Mapping.get(gapDiff);
					}
					
					//add to running total
					ReverseDissimilarity = ReverseDissimilarity + gapDissimilarity;
					
					//reset adjacency
					EquivalentAdjacencyRev = false;
				}
			}
			
		}
		
		//The proper orientation is the one with more common adjacent pairs with O1.
		if (FwdMatch >= RevMatch){
			Dissimilarity = ForwardDissimilarity;
		} else {
			Dissimilarity = ReverseDissimilarity;
		}
		
		//Maximum dissimilarity is 1, minimum is 0.
		if (Dissimilarity > 1){
			Dissimilarity = 1;
		} else if (Dissimilarity <= 0){
			Dissimilarity = 0;
		}
		
		//debugging
		//System.out.println("Dissimilarity: " + Dissimilarity);
//		System.out.println("breakpoint!");
		
		return Dissimilarity;
	}
	
	//Strandedness
	public double SSDissimilarity(LinkedList<GenomicElementAndQueryMatch> O1, LinkedList<GenomicElementAndQueryMatch> O2, String Type){
		
		//Initialize output
		double Dissimilarity = 0;
		double IndividualDissimilarity = 0;
		double WholeGroupDissimilarity = 0;
		double TotalRelativeWeights = 0;
		
		LinkedList<HashSet<GenomicElement>> MatchGroups = new LinkedList<HashSet<GenomicElement>>();
		
		for (GenomicElementAndQueryMatch GandE : O1){
			HashSet<GenomicElement> Match = new HashSet<GenomicElement>();
			Match.add(GandE.getE());
			boolean RetainMatchSet = false;
			for (GenomicElementAndQueryMatch GandE2 : O2){
				if (Type.equals("annotation")){
					if (GandE.getE().getAnnotation().toUpperCase().equals(GandE2.getE().getAnnotation().toUpperCase())){
						Match.add(GandE2.getE());
						RetainMatchSet = true;
					}
				} else {
					if (GandE.getE().getClusterID() == GandE2.getE().getClusterID()){
						Match.add(GandE2.getE());
						RetainMatchSet = true;
					}
				}
			}
			
			if (RetainMatchSet){
				
				//add duplicate matches
				for (GenomicElementAndQueryMatch GandE_dup : O1){
					
					//pre-requisites: not an exact duplicate, not already in a group
					if (!GandE.equals(GandE_dup)){
						boolean UniqueElement = true;
						for (HashSet<GenomicElement> Set : MatchGroups){
							if (Set.contains(GandE_dup.getE())){
								UniqueElement = false;
							}
						}
						
						if (UniqueElement){
							if (Type.equals("annotation")){
								if (GandE.getE().getAnnotation().toUpperCase().equals(GandE_dup.getE().getAnnotation().toUpperCase())){
									Match.add(GandE_dup.getE());
								}
							} else {
								if (GandE.getE().getClusterID() == GandE_dup.getE().getClusterID()){
									Match.add(GandE_dup.getE());
								}
							}
						}

					}
				}
				
				MatchGroups.add(Match);
			}
		}
		
		int GroupCounter = 0;
		
		for (HashSet<GenomicElement> HS : MatchGroups){
			boolean FoundForward = false;
			boolean FoundReverse = false;
			for (GenomicElement E : HS){
				if (E.getStrand().equals(Strand.POSITIVE)){
					FoundForward = true;
				}
				if (E.getStrand().equals(Strand.NEGATIVE)){
					FoundReverse = true;
				}
			}
			if (FoundForward && FoundReverse){
				GroupCounter++;
			}
		}
		
		if (SSIndividualGenes){
			
			IndividualDissimilarity = (GroupCounter/MatchGroups.size());
			
			//increment total weights contribution
			TotalRelativeWeights = TotalRelativeWeights + SSRelWeightIndGenes;
		}
		
		if (SSWholeGroup) {
			
			//whole group is switched
			if (GroupCounter >= MatchGroups.size()){
				WholeGroupDissimilarity = 1;
			}

			//increment total weights contribution
			TotalRelativeWeights = TotalRelativeWeights + SSRelWeightWholeGroup;
		}
		
		Dissimilarity = (SSRelWeightIndGenes/TotalRelativeWeights) * IndividualDissimilarity +
						(SSRelWeightWholeGroup/TotalRelativeWeights) * WholeGroupDissimilarity;
		
		//adjust dissimilarity
		if (Dissimilarity > 1){
			Dissimilarity = 1;
		} else if (Dissimilarity < 0){
			Dissimilarity = 0;
		}
		
		return Dissimilarity;
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
			/*
			 * Scale-hierarchy ensures that a dissimilarity contribution of lower importance
			 * never overtakes a contribution of higher importance.
			 * 
			 * Individual dissimilarities are computed, and factors are truncated at their maximum
			 * allowed fraction of the next ranking:
			 * 
			 * Dissimilarity From Lower Importance 
			 * 		<= (Dissimilarity from Higher Importance * Importance Fraction)
			 */
			//initialize values
			Double TotalContribution = 0.0;
			
			Double CGContribution = 0.0;
			Double CMContribution = 0.0;
			Double GOContribution = 0.0;
			Double GGContribution = 0.0;
			Double SSContribution = 0.0;
			
			
			LinkedList<ImportanceMapping> ImpMapping = new LinkedList<ImportanceMapping>();
			
			//Determine Factors
			if (Factors.contains("CG")){
				CGContribution = CGDissimilarity(G1,G2,T);
				ImportanceMapping IM = new ImportanceMapping();
				IM.FactorType = "CG";
				IM.Dissimilarity = CGContribution;
				IM.Importance = CGImportance;
				ImpMapping.add(IM);
			}
			if (Factors.contains("CM")){
				CMContribution = CMDissimilarity(G1,G2,T);
				ImportanceMapping IM = new ImportanceMapping();
				IM.FactorType = "CM";
				IM.Dissimilarity = CMContribution;
				IM.Importance = CMImportance;
				ImpMapping.add(IM);
			}
			if (Factors.contains("GO")){
				GOContribution = GODissimilarity(G1,G2,T);
				ImportanceMapping IM = new ImportanceMapping();
				IM.FactorType = "GO";
				IM.Dissimilarity = GOContribution;
				IM.Importance = GOImportance;
				ImpMapping.add(IM);
			}
			if (Factors.contains("GG")){
				GGContribution = GGDissimilarity(G1,G2,T);
				ImportanceMapping IM = new ImportanceMapping();
				IM.FactorType = "GG";
				IM.Dissimilarity = GGContribution;
				IM.Importance = GGImportance;
				ImpMapping.add(IM);
			}
			if (Factors.contains("SS")){
				SSContribution = SSDissimilarity(G1,G2,T);
				ImportanceMapping IM = new ImportanceMapping();
				IM.FactorType = "SS";
				IM.Dissimilarity = SSContribution;
				IM.Importance = SSImportance;
				ImpMapping.add(IM);
			}
			
			//sort importance mapping
			Collections.sort(ImpMapping, new IMComparator());
			
			if (ImpMapping.size() > 1){
				
				TotalContribution = ImpMapping.get(0).Dissimilarity;
				
				for (int i = 0; i < ImpMapping.size()-1; i++){
					
					//check for different levels
					if (ImpMapping.get(i).Importance != ImpMapping.get(i+1).Importance){
						if (ImpMapping.get(i).Dissimilarity * ImportanceFraction
								< ImpMapping.get(i+1).Dissimilarity){
							ImpMapping.get(i+1).Dissimilarity = ImpMapping.get(i).Dissimilarity * ImportanceFraction;
						}
						TotalContribution = TotalContribution + ImpMapping.get(i+1).Dissimilarity;
					}	
					
				}

				//adjust contribution by size
				TotalContribution = TotalContribution / (double)ImpMapping.size();
				
			} else {
				TotalContribution = ImpMapping.getFirst().Dissimilarity;
			}

			return TotalContribution;
		}

	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Double getImportanceFraction() {
		return ImportanceFraction;
	}

	public void setImportanceFraction(Double importanceFraction) {
		ImportanceFraction = importanceFraction;
	}
	
	
	//new class
	public class ImportanceMapping {
		
		public String FactorType;
		public double Dissimilarity;
		public int Importance;
	}
	
	public class IMComparator implements Comparator<ImportanceMapping>{

		@Override
		public int compare(ImportanceMapping IM1, ImportanceMapping IM2) {

			return IM1.Importance - IM2.Importance;
		}
	}
}
