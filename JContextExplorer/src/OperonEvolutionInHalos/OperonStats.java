package OperonEvolutionInHalos;

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
		
		// ======= Build Data Set =========== //
		
		//build data set
		ImportGenomes();			//load genomic data
		BasicOperons(50);			//create basic operons
		
		//convert to set for trajectory analysis
		OperonSet BasicSet = new OperonSet(OS,"BasicOperons");
		BasicSet.BuildPhylogeneticDistanceMapping();
		BasicSet.BuildOperonTrajectories();

		//Max Dist: 0.94212093 for Halococcus_hamelinensis,Haloquadratum_walsbyi
		
		BistableParams BP = new BistableParams();
		BP.MaxSameGrpPhyloDist = 99;
		BP.MinDiffGrpPhyloDist = 0.0;
		BP.MaxContentDiss = 0.1;
		BP.MinOpSize = 2;
		BP.MinGrpMemSize = 1;
		
		LinkedList<Integer> X = BasicSet.FindMultiStableOperonTopologies(BP);
		
		System.out.println(X.size() + " in total.");
		
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

	}

}

