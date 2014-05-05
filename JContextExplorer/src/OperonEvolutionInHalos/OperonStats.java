package OperonEvolutionInHalos;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class OperonStats extends LoadData{

	/*
	 * This method performs stats on predict operons, looking for particular
	 * cross-species changes
	 * 
	 * uses JCE data structures, minus the GUI - background-type tasks etc
	 */

	//main method
	public static void main(String[] args) {
		
		// ======= For Histograms - March 17, 2014 ==== //
		
		//dummy operon set
		//OperonSet OS = new OperonSet();
		
//		//data files
//		String HaloDistFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/cyano_gamma_halo_dist/halo.dist";
//		String GammaDistFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/cyano_gamma_halo_dist/gamma.dist";
//		String CyanoDistFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/cyano_gamma_halo_dist/cyano.dist";
//		
//		//data in appropriate format
//		PhyloHistData Halos = OS.BuildGenericDistanceMapping(HaloDistFile);
//		PhyloHistData Gamma = OS.BuildGenericDistanceMapping(GammaDistFile);
//		PhyloHistData Cyano = OS.BuildGenericDistanceMapping(CyanoDistFile);
//		
//		//export files
//		String HaloHist = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/HistogramData_Mar17/HaloHistData.txt";
//		String GammaHist = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/HistogramData_Mar17/GammaHistData.txt";
//		String CyanoHist = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/HistogramData_Mar17/CyanoHistData.txt";
//		
//		//constants
//		int bins = 100;
//		
//		//determine the largest of the maximum distances.
//		Double LargestMaxDist = Math.max(Halos.MaxDist, Gamma.MaxDist);
//		LargestMaxDist = Math.max(LargestMaxDist, Cyano.MaxDist);
//		
//		//export data as file
//		OS.PhyloHist(HaloHist, bins, LargestMaxDist, Halos.PhyDistHash);
//		OS.PhyloHist(GammaHist, bins, LargestMaxDist, Gamma.PhyDistHash);
//		OS.PhyloHist(CyanoHist, bins, LargestMaxDist, Cyano.PhyDistHash);
		
		// ======= Build Data Set =========== //
		
		//build data set
		ImportGenomes();			//load genomic data
		BasicOperons(50);			//create basic operons
		//ShowLocalOperonDuplications(50);	//display duplication cases
		
		//String ContextSetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/Current/CS_BasicOperons_NoSingleGenes.txt";
		//ExportOperonsAsContextSet(ContextSetFile,"BasicOperons",false); //Export set
		
		//convert to set for trajectory analysis
		OperonSet BasicSet = new OperonSet(OS,"BasicOperons");
		BasicSet.BuildPhylogeneticDistanceMapping();
		
//		String HistDataFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/HistogramData_200.txt";
//		int bins = 200;
//		BasicSet.PhyloHist(HistDataFile, bins, BasicSet.MaxDist, BasicSet.PhyDistHash);
		BasicSet.BuildOperonTrajectories();
		
		//export single gene modifications counts
		String ExportFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Miscellaneous/ClearAIP_v2.txt";
		BasicSet.ExportClearSingleGeneModifications(ExportFile);
		
		//Export a query set for context forest analysis (Feb 6, 2014)
		//String QuerySetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/Current/QS_60Orgs_Op50.txt";
		//BasicSet.ExportQuerySet(QuerySetFile, BasicSet.Trajectories, 0.50, 1.1, 60);
		
//		//Export a query set for gene order analysis
//		String QuerySetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/Current/QS_AdjacentGenePairs.txt";
//		BasicSet.ExportAdjacentGenePairs(QuerySetFile, BasicSet.Trajectories);
		
		//BasicSet.ExportQuerySet(QuerySetFile, Trajectories, MinOperonicity, MaxNovelty)
		
//		String QuerySetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/QS/QS_Operonicity_0_90.txt";
//		BasicSet.ExportQuerySet(QuerySetFile, BasicSet.Trajectories, 0.90, 1.1);
		
		//export, excluding trajectories where the gene is always a singleton
		//String StatsTxt = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/OperonTrajectories/D50";
		//BasicSet.ExportByDifferentVariables(StatsTxt,false);
		
//		//determine agreement at this level
//		double margin = 0.10;
//		int NumAgree = 0;
//		for (Integer x : BasicSet.Trajectories.keySet()){
//			OperonTrajectory OT = BasicSet.Trajectories.get(x);
//			BasicSet.AddLowestLevelPhyloDisagreement(OT, margin, true);
//			if (OT.AgreesWithPhylogenyAtLowestLevel && !OT.AlwaysASingleGene){
//				NumAgree++;
//			} else {
//				if (!OT.AlwaysASingleGene){
//					System.out.println(OT.ClusterID + " disagrees with the phylogeny (ignore single genes).");
//				}
//			}
//		}
//		System.out.println(NumAgree + " agree in total.");
		
//		//new list
//		LinkedList<OperonTrajectory> Trajs = new LinkedList<OperonTrajectory>(BasicSet.Trajectories.values());
//		Collections.sort(Trajs, new OperonSet.SortbyEvoRate());
//		
//		for (OperonTrajectory OT : Trajs){
//			if (!OT.AlwaysASingleGene){
//				System.out.println(OT.ClusterID + "\t" + OT.EvoRate);
//			}
//		}
		
//		//determine level of agreement at various levels
//		LinkedHashMap<Double,Integer> AgreementCounts = new LinkedHashMap<Double,Integer>();
//		LinkedHashMap<Double,Integer> SingleAgreementCounts = new LinkedHashMap<Double,Integer>();
//		
//		for (int i = 0; i <= 19; i++){
//			
//			//re-set agreement counter
//			int NumAgree = 0;
//			int NumSingleAgree = 0;
//			int NumNonSingle = 0;
//			
//			//turn integer to double
//			double margin = 0.05* (double) i;
//			margin = (double)Math.round(margin * 100) / 100;
//			
//			//determine agreement at this level
//			for (Integer x : BasicSet.Trajectories.keySet()){
//				OperonTrajectory OT = BasicSet.Trajectories.get(x);
//				BasicSet.AddLowestLevelPhyloDisagreement(OT, margin, true); //added 3rd argument 5/2/2014
//				if (OT.AgreesWithPhylogenyAtLowestLevel && !OT.AlwaysASingleGene){
//					NumAgree++;
//				}
//				BasicSet.AddLowestLevelPhyloDisagreement(OT, margin, false);
//				if (OT.AgreesWithPhylogenyAtLowestLevel && ! OT.AlwaysASingleGene){
//					NumSingleAgree++;
//				}
//				if (!OT.AlwaysASingleGene){
//					NumNonSingle++;
//				}
//			}
//			
//			//store in hash
//			AgreementCounts.put(margin,NumAgree);
//			SingleAgreementCounts.put(margin, NumSingleAgree);
//			
//			//debugging
//			//System.out.println(NumNonSingle + " non-single");
//		}

		
//		//print
//		System.out.println("margin\tagree\tsingle_agree");
//		for (double d : AgreementCounts.keySet()){
//			System.out.println(d + "\t" + AgreementCounts.get(d) + "\t" + SingleAgreementCounts.get(d));
//		}
		
		//BasicSet.AddLowestLevelPhyloDisagreement(BasicSet.Trajectories.get(1500), 0.01);
		
		//Max Dist: 0.94212093 for Halococcus_hamelinensis,Haloquadratum_walsbyi
		
//		BistableParams BP = new BistableParams();
//		BP.MaxSameGrpPhyloDist = 99;
//		BP.MinDiffGrpPhyloDist = 0.0;
//		BP.MaxContentDiss = 0.1;
//		BP.MinOpSize = 2;
//		BP.MinGrpMemSize = 1;
//		
//		LinkedList<Integer> X = BasicSet.FindMultiStableOperonTopologies(BP);
//		
//		System.out.println(X.size() + " in total.");
		
		//helpful output message
		System.out.println("All Processes Successfully Completed!");
		
		// ================================== //
		// ======= Temporarily Unused ======= //
		// ================================== //
		
		//Export sets
		
		//basic
		//String ContextSetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/CS_BasicOperons.txt";
		//BasicSet.ExportTrajectoriesAsContextSet(ContextSetFile, true, BasicSet.Trajectories); //about ~30 min
		
		// Export Query Sets -> for use with analysis with JCE
		//String QuerySetFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/QS/QS_AllOperons.txt";
		//BasicSet.ExportQuerySet(QuerySetFile, BasicSet.Trajectories, 0.001, 1.1);
		
		// ======= Analyze Data Set =========== //
		//String StatsTxt = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/OperonTrajectories/D50";
		//BasicSet.ExportByDifferentVariables(StatsTxt,false);
		
//		//COD analysis
//		CODParameters COD = new CODParameters();
//		COD.RangeAroundOperon = 5000;
//		COD.RequireSameStrand = true;
//		LinkedHashMap<Integer, OperonTrajectory> COD_Hash = BasicSet.CreateCODSet(COD);
//
//		//COD set file name
//		String ContextSetFileCODs = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/CS_CODs_5K_t.txt";
//		BasicSet.ExportTrajectoriesAsContextSet(ContextSetFileCODs, true, COD_Hash);
	
		//Debugging of file parsing
		//LinkedHashMap<String,Integer> ComparisonHash = BasicSet.BuildPhylogeneticDistanceMapping();
//		//print data - working!
//		for (String s : ComparisonHash.keySet()){
//			System.out.println(s + ": " + ComparisonHash.get(s));
//		}
//		System.out.println(ComparisonHash.size());
		
//		double MaxDist = -1.0;
//		LinkedList<String> Lmax = new LinkedList<String>();
//		for (LinkedList<String> L : BasicSet.PhyDistHash.keySet()){
//			double d = BasicSet.PhyDistHash.get(L);
//			if (d > MaxDist){
//				MaxDist = d;
//				Lmax = L;
//			}
//		}
//		
//		System.out.println("Max Dist: " + MaxDist + " for " + Lmax.get(0) + " , " + Lmax.get(1));

		//Monday, Jan 7, 2013
//		//Import a list of all clusters of interest in this investigation
//		String ClustersUsedFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/JCE/Current/QS_NonSingle.txt";
//		ImportClustersToInclude(ClustersUsedFile);
		
	}

}

