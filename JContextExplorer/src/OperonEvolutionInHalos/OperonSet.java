package OperonEvolutionInHalos;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.GenomicElement;
import genomeObjects.OrganismSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import operonClustering.CustomDissimilarity;


//An object to contain the operons in an organism.
public class OperonSet {

	//Fields
	
	//source data structure
	public OrganismSet OS;
	
	//Initial import data structures
	public LinkedHashMap<String,HashMap<Integer,LinkedList<GenomicElement>>> OperonHash;
	
	//Mapping of distances to phylogeny
	public LinkedHashMap<LinkedList<String>,Double> PhyDistHash;
	public static final String PhyDistHashFile = "/Users/phillipseitzer/Dropbox/OperonEvolutionInHalophiles/Phylogeny/halo.phy_phyml_sh.txt";
	
	//Trajectories
	public LinkedHashMap<Integer, OperonTrajectory> Trajectories;
	public LinkedHashMap<LinkedList<Integer>, DoubleOperonTrajectory> DoubleTrajectories;
	public int RoundingConstant = 10000;
	
	//constructor for direct loading of OS context to new object
	public OperonSet(OrganismSet oS, String CSName){
		
		//note data
		this.OS = oS;
		
		//Initialize hash
		OperonHash = new LinkedHashMap<String,HashMap<Integer,LinkedList<GenomicElement>>>();
		
		//build hash
		for (String s : OS.getSpecies().keySet()){
			
			//retrieve genome
			AnnotatedGenome AG = OS.getSpecies().get(s);
			
			//retrieve map
			LinkedList<ContextSet> GroupNames = AG.getGroupings();
			
			//add data
			for (ContextSet s1: GroupNames){
				if (s1.getName().equals(CSName)){
					OperonHash.put(s, s1.getContextMapping());
					break;
				}
			}
		}
		
		//output message
		System.out.println("Created set of basic operons");
		
	}
	
	// ====================== //
	// ===== Methods ======== //
	// ====================== //
	
	// ===== Creation ======= //
	
	//re-organize hash into list of trajectories
	public void BuildOperonTrajectories(){
		
		/*
		 * (1) Creates a mapping of cluster ID number to cross-species operon trajectories.
		 * (2) Simultaneously, basic statistics about the operon are also noted.
		 * 
		 * Cluster ID has to be found in at least 10 different organisms (<= 5276)
		 * Cluster may not have a total number of instances greater than 100 (>= 272)
		 * 
		 */
		
		//Initialize trajectory mapping + associated statistics
		Trajectories = new LinkedHashMap<Integer, OperonTrajectory>();
		
		//Counter variable
		int TrajectoryCounter = 0;
		
		//check all clusters
		for (int i = 272; i <= 5276; i++){
		//for (int i = 1800; i <= 1800; i++){
			
			//Initialize this operon trajectory
			OperonTrajectory OT = new OperonTrajectory();
			
			//Initialize the map for this cluster ID
			LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>> Trajectory
				= new LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>>();
			
			//Initialize a mapping to the operon data
			LinkedHashMap<String, LinkedList<OperonData>> OpDataTraj
				= new LinkedHashMap<String, LinkedList<OperonData>>();
			
			//initialize organism counter
			int OrgCounter = 0;

			int TotalOpCounter = 0;
			int NumNonSingle = 0;
			LinkedList<Integer> Clusts = new LinkedList<Integer>();
			LinkedList<Integer> Genes = new LinkedList<Integer>();
			
			//check operons in each organism
			for (String s : OperonHash.keySet()){
				
				//recover operon mapping
				HashMap<Integer, LinkedList<GenomicElement>> Operons = OperonHash.get(s);
				
				//Initialize a list of operons to include
				LinkedList<LinkedList<GenomicElement>> OrgList = new LinkedList<LinkedList<GenomicElement>>();
				LinkedList<OperonData> OpDataList = new LinkedList<OperonData>();
				
				//check each and every operon in the mapping
				for (Integer x : Operons.keySet()){
					
					//default: operon does not match
					boolean OperonContainsCluster = false;
					
					//recover genes in each operon
					LinkedList<GenomicElement> L = Operons.get(x);
					
					//check each gene for cluster ID
					for (GenomicElement E : L){
						if (E.getClusterID() == i){
							OperonContainsCluster = true;
							break;
						}
					}
					
					//if the operon contains the cluster, add to list
					if (OperonContainsCluster){
						
						//increment counter
						TotalOpCounter++;
						
						//note if this "operon" is really just a singleton gene
						if (L.size() > 1){
							NumNonSingle++;
						}
						
						//Initialize a new operon data object
						OperonData OD = new OperonData();
						
						//add all appropriate cluster ids to the list of cluster ids
						for (GenomicElement E : L){
							
							//exclude self
							if (E.getClusterID() != i){
								
								//list of clusters
								if (!Clusts.contains(E.getClusterID())){
									Clusts.add(E.getClusterID());
								}
								
								//all genes - so, include every instance
								Genes.add(E.getClusterID());
								
							}
							
							//adjust operon data
							
							//adjust operon start coordinate
							if (E.getStart() < OD.Start){
								OD.Start = E.getStart();
							}
							
							//adjust operon end coordinate
							if (E.getStop() > OD.Stop){
								OD.Stop = E.getStop();
							}
							
							//strand
							OD.TheStrand = E.getStrand();
							
							//contig
							OD.Contig = E.getContig();
							
						}
						
						//add data to lists
						OrgList.add(L);
						OpDataList.add(OD);

					}
					
				}
				
				//add these to the trajectory mapping + inc org counter
				if (OrgList.size() != 0){
					Trajectory.put(s, OrgList);
					OpDataTraj.put(s, OpDataList);
					OrgCounter++;
				}
				
			}
			
			//Write the entire trajectory to the hash map, if appropriate.
			if (OrgCounter >= 10){
				
				// ==== Add data to trajectory ====== //

				//(2) ratio of non-single gene 'operons'
				Double RatioNonSingle = (double) NumNonSingle / (double) TotalOpCounter;
				RatioNonSingle = (double) Math.round(RoundingConstant*RatioNonSingle)/RoundingConstant;
				
				//(3) total number of additional protein families included with cluster across set
				Double RatioNovel;
				if (Genes.size() != 0){
					RatioNovel = (double) Clusts.size() / (double) Genes.size();
				} else{
					//genes that are always single copy don't mean much in our discussion of operon evolution.
					RatioNovel = -1.0;
					OT.AlwaysASingleGene = true;
				}
				RatioNovel = (double) Math.round(RoundingConstant*RatioNovel)/RoundingConstant;
				
				//associate data
				OT.ClusterID = i;
				OT.OrgsFeatured = OrgCounter;
				OT.Operonicity = RatioNonSingle;
				OT.Variety = RatioNovel;
				OT.TrajectoryHash = Trajectory;
				OT.amalgamate();				//puts all ops in same organisms together
				OT.OperonHash = OpDataTraj;
				OT.OtherClusters = Clusts;
				
				//Determine overall phylogenetic spread of protein family
				LinkedList<String> ListOfOrganisms = new LinkedList<String>(OT.OperonHash.keySet());
				OT.ClusterPhylogeneticRange = DetermineMaxDist(ListOfOrganisms);
				
				//Segregate trajectory - intensive, not always necessary
				OT.OperonGroups = SegregateTrajectory(OT);
				
				//compute evo rate - open to re-analysis
				OT.computeEvoRate();
				
				//add to map + inc counter
				Trajectories.put(i,OT);
				TrajectoryCounter++;
				
			}
			
			//output message.
			if (i%100 == 0){
				
				System.out.println("Processed " + i +"/5276 operon trajectories.");
//				System.out.println("Present in: " + Trajectories.get(i).OrgsFeatured);
//				System.out.println("Non-singleton ratio: " + Trajectories.get(i).NonsingletonRatio);
//				System.out.println("Novel cluster ratio: " + Trajectories.get(i).Novelty);
			}
			

		}
		
		//final output message
		System.out.println(TrajectoryCounter + " operon trajectories determined.");
		
	}

	//Calculate for disagreements at lowest level
	public void AddLowestLevelPhyloDisagreement(OperonTrajectory OT, double PhyloMargin, boolean ignoreSingleGenes){
		/*
		 * This algorithm checks the organismic sources of operons
		 * of identical topology, and identifies cases where a more
		 * distant relative exists that is not found in the group.
		 * 
		 * In that case, the operon is said not to agree exactly with
		 * the phylogeny.
		 * 
		 * If the "distance relative" is within a provided margin of
		 * error, this offender can be discarded.
		 * 
		 */
		
		//default: this operon follows the phylogeny
		boolean FollowsPhylogeny = true;
		
		//only need to perform comparison if no alternative operon topologies featured
		if (OT.OperonGroups.size() > 1){
			
			for (OperonCluster OC1 : OT.OperonGroups){
				
				//if an operon cluster contains only one organism,
				//no intra-cluster divergence distance to measure.
				if (OC1.Operons.size() != 1){
					
					//optionally ignore single gene topology
					if (!ignoreSingleGenes || (ignoreSingleGenes && OC1.OperonSize>1)){
						
						//find the smallest inter-cluster distance
						double MinDist = 999;
						
						//compare this cluster to every other cluster
						for (OperonCluster OC2: OT.OperonGroups){
							
							//don't compare same
							if (!OC1.equals(OC2)){
								
								//optionally, ignore single gene topology
								if (!ignoreSingleGenes || (ignoreSingleGenes && OC2.OperonSize>1)){
									
									//compare
									double NewDist = DetermineMinDist(OC1.Organisms,OC2.Organisms);
									
									//update minimum
									if (NewDist < MinDist){
										MinDist = NewDist;
									}
									
								}
								
							}
						}

						//compare internal phylo distances to smallest external distance
						if (OC1.MaxInternalDist - PhyloMargin > MinDist){
							FollowsPhylogeny = false;
							break;
						}
						
					}
										
				}

			}
		}
		
		//update operon trajectory field
		OT.AgreesWithPhylogenyAtLowestLevel = FollowsPhylogeny;
		
	}
	
	//calculate cassette for each trajectory: measure of operonic dispersion across species
	public OperonTrajectory BuildCODFromBasic(OperonTrajectory Basic_OT, CODParameters CP){
		
		/*
		 * a Cassette is a grouping encompassing local dispersion of genes, across species.
		 * 
		 * Specifically, genes that are ever predicted to be in an operon in any species are
		 * searched nearby to the operon in species where they are not found.
		 * 
		 * Same strand may be required, or not.
		 * 
		 * It is NOT AN OPERON PREDICTON APPROACH, but rather, a technique to examine local
		 * dispersion. BTW by setting the "local" parameter very high, this could effectively
		 * encompass the whole genome.
		 */
		
		//Initialize output
		OperonTrajectory Cassette_OT = new OperonTrajectory();
		
		//this analysis is only relevant to actual operons.
		if (!Basic_OT.AlwaysASingleGene){
			
			//Transfer Identification information
			Cassette_OT.ClusterID = Basic_OT.ClusterID;
			Cassette_OT.OtherClusters = Basic_OT.OtherClusters;
			Cassette_OT.OrgsFeatured = Basic_OT.OrgsFeatured;

			//Initialize (important) gene mapping
			LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>> CassetteTrajectoryHash 
				= new LinkedHashMap<String, LinkedList<LinkedList<GenomicElement>>>();
			
			//Find new genomic mapping
			for (String s : Basic_OT.OperonHash.keySet()){
				
				//Retrieve initial lists
				LinkedList<OperonData> DataList = Basic_OT.OperonHash.get(s);
				
				//Initialize output
				LinkedList<LinkedList<GenomicElement>> OrgList = new LinkedList<LinkedList<GenomicElement>>();
				
				//check each operon in the list
				for (OperonData OD : DataList){
					
					//Initialize a list of genes
					LinkedList<GenomicElement> L = new LinkedList<GenomicElement>();
					
					//a flag for starting writing
					boolean StartedWriting = false;

					//check against genes in original organism
					AnnotatedGenome AG = OS.getSpecies().get(s);
					
					//check all elements
					for (GenomicElement E : AG.getElements()){
						
						//match same contig, collect all potentially interesting genes
						if (E.getContig().equals(OD.Contig)){
							
							//approaching from the left
							if ((E.getCenter() + CP.RangeAroundOperon >= OD.Start) && 
									(E.getClusterID() == Cassette_OT.ClusterID || 
									Cassette_OT.OtherClusters.contains(E.getClusterID()))){
								
								//First: flag is on
								StartedWriting = true;
								
								//record the gene, for later processing
								L.add(E);
							} 
							
							//passing through
							if (StartedWriting){
								
								//within range
								if ((E.getCenter() - CP.RangeAroundOperon <= OD.Stop) && 
										(E.getClusterID() == Cassette_OT.ClusterID || 
										Cassette_OT.OtherClusters.contains(E.getClusterID()))){
									
									//record gene, for later processing
									L.add(E);
									
								} else {
									
									//stop searching through genes - all candidates have been noted.
									break;
								}
								
							}
								
						}

					}
					
					//post-processing of list
					if (CP.RequireSameStrand){
						OrgList.add(L);
					} else {
						LinkedList<GenomicElement> StrandCorrected = new LinkedList<GenomicElement>();
						for (GenomicElement E : L){
							if (E.getStrand().equals(OD.TheStrand)){
								StrandCorrected.add(E);
							}
						}
						OrgList.add(StrandCorrected);
					}
					
				}
				
				//add these to the trajectory mapping
				if (OrgList.size() != 0){
					CassetteTrajectoryHash.put(s, OrgList);
				}
				
			}
			
			//add values
			Cassette_OT.TrajectoryHash = CassetteTrajectoryHash;

			
		} else {
			Cassette_OT = Basic_OT;
		}
		
		//output message.
		//System.out.println("Cassette trajectory determined for " + Cassette_OT.ClusterID + ".");
		
		//return statement
		return Cassette_OT;
	}
	
	//Import phylogenetic mapping info
	public void BuildPhylogeneticDistanceMapping(){
		
		//Initialize output
		//LinkedHashMap<String,Integer> ComparisonHash = new LinkedHashMap<String,Integer>();
		
		//Initialize hash map
		PhyDistHash = new LinkedHashMap<LinkedList<String>,Double>();
		
		//First, load in file
		try {
			//open file stream
			BufferedReader br = new BufferedReader(new FileReader(PhyDistHashFile));
			String Line = null;
			
			//for comparisons
			//int ComparisonCounter = 0;

			//read through lines
			while ((Line = br.readLine()) != null) {
				
				//split by tabs
				String[] L = Line.split("\t");
				
				//check matches
				boolean OneMatch = false;
				boolean TwoMatch = false;
				
				//org names
				String Org1 = "";
				String Org2 = "";
				
				//check first entry
				for (String s : OS.getSpecies().keySet()){
					if (L[0].equals(s)){
						OneMatch = true;
						Org1 = s;
						break;
					}
				}
				
				//check second entry
				for (String s : OS.getSpecies().keySet()){
					if (L[1].equals(s)){
						TwoMatch = true;
						Org2 = s;
						break;
					}
				}
				
				if (OneMatch && TwoMatch){
					
					//build key
					LinkedList<String> OrgRelation = new LinkedList<String>();
					OrgRelation.add(Org1);
					OrgRelation.add(Org2);
					Collections.sort(OrgRelation);
					
					//build value
					double Dist = Double.parseDouble(L[3]);
					
					//write to hash
					PhyDistHash.put(OrgRelation, Dist);
					
				}

				//DEBUGGING
//				//only mappings between organisms we care about
//				if (OneMatch && TwoMatch){
//					//System.out.println(Line);
//					ComparisonCounter++;
//					
//					//store these comparisons in the comparison hash
//					
//					//org 1
//					if (ComparisonHash.get(Org1) != null){
//						int Val = ComparisonHash.get(Org1);
//						Val++;
//						ComparisonHash.put(Org1, Val);
//					} else {
//						ComparisonHash.put(Org1, 1);
//					}
//					
//					//org 2
//					if (ComparisonHash.get(Org2) != null){
//						int Val = ComparisonHash.get(Org2);
//						Val++;
//						ComparisonHash.put(Org2, Val);
//					} else {
//						ComparisonHash.put(Org2, 1);
//					}
//				}

			}
			
			//print summary
			System.out.println(PhyDistHash.size() + " phylogenetic distances mapped.");
			
			//3160 = 79*80/2, lower triangle of dissimilarity matrix
			
			//close file stream
			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		//this was for debugging purposes
		//return ComparisonHash;
		
	}
	
	//Create a set of CODs
	public LinkedHashMap<Integer, OperonTrajectory> CreateCODSet(CODParameters COD){
		
		//Initialize output
		LinkedHashMap<Integer, OperonTrajectory> CODset = new LinkedHashMap<Integer, OperonTrajectory>();
		
		for (Integer x : Trajectories.keySet()){
			
			OperonTrajectory Basic = Trajectories.get(x);
			CODset.put(x, BuildCODFromBasic(Basic,COD));
			if (x%100 ==0){
				System.out.println("Computed " + x + "/" + Trajectories.size() + " CODs.");
			}
		}
		
		//return
		return CODset;
		
	}
	
	//Create a list of multistable operon topologies
	public LinkedList<Integer> FindMultiStableOperonTopologies(BistableParams BP){
		
		/*
		 * Algorithm:
		 * (1) Segment each operon trajectory into non-overlapping clusters of identical
		 * 	   protein family content
		 * (2) Filter set by minimum number of operonic members in each group
		 * (3) Filter set by minimum size requirement for operons
		 * (4) For each operon, determine the range of phylogenetic variety
		 * (5) Filter set by minimum internal range of phylogenetic variety
		 * (6) Compute gene content variety between each group (Dice/Jaccard for non-seed)
		 * (7) Compute the minimal phylogenetic distance between two groups (compare all indiv. members)
		 * (8) Retain protein families that match content variety + min grp distance specifications
		 * (9) Num of operon groups that are retained from this process describes stability
		 */
	
		//Initialize output
		LinkedList<Integer> MultiStableTrajectories = new LinkedList<Integer>();
		
		for (OperonTrajectory OT : Trajectories.values()){
			
			//only check actual operons
			if (!OT.AlwaysASingleGene){
				
				//generate report
				OperonStabilityReport OSR = MultiStableOperonTopology(BP,OT);
				
				if (OSR.MultiStable){
					MultiStableTrajectories.add(OSR.ClusterID);
					System.out.println("Multistable trajectory discovered: " + OT.ClusterID + ".");
				}
			}

		}
		//return output
		return MultiStableTrajectories;
	}
	
	//Single multistable trajectory
	public OperonStabilityReport MultiStableOperonTopology(BistableParams BP, OperonTrajectory OT){
		/*
		 * Algorithm:
		 * (1) Segment operon trajectory into non-overlapping clusters of identical
		 * 	   protein family content
		 * (2) For each operon, determine the range of phylogenetic variety
		 * (3) Filter set by minimum number of operonic members in each group
		 * (4) Filter set by minimum size requirement for operons
		 * (5) Filter set by maximum internal range of phylogenetic variety
		 * (6) Compute gene content variety between each group (Dice/Jaccard for non-seed)
		 * (7) Compute the minimal phylogenetic distance between two groups (compare all indiv. members)
		 * (8) Retain protein families that match content variety + min grp distance specifications
		 * (9) Num of operon groups that are retained from this process describes stability
		 */
		
		//(1) + (2) Create operon groups
		LinkedList<OperonCluster> OperonGroups = SegregateTrajectory(OT);
		
		//(3) - (5) Filtering by operon group properties
		LinkedList<OperonCluster> FilteredOperonGroups = new LinkedList<OperonCluster>();
		
		for (OperonCluster OC : OperonGroups){
			if (OC.Operons.size() >= BP.MinGrpMemSize &&
					OC.OperonSize >= BP.MinOpSize &&
					OC.MaxInternalDist <= BP.MaxSameGrpPhyloDist){
				FilteredOperonGroups.add(OC);
			}
		}
		
		//(6) - (8) Compare cluster groups + filter appropriately
		
		//Initialize output
		OperonStabilityReport OSR = new OperonStabilityReport(OT.ClusterID);
				
		//Compare + build output report
		for (OperonCluster OC1 : FilteredOperonGroups){
			for (OperonCluster OC2 : FilteredOperonGroups){
				if (!OC1.equals(OC2)){
					
					//Build list
					LinkedList<OperonCluster> OCL = new LinkedList<OperonCluster>();
					OCL.add(OC1);
					OCL.add(OC2);
					Collections.sort(OCL, new OCSorter());
					
					//System.out.println("Breakpoint!");
					
					//evaluate distances
					double PhyDist = DetermineMinDist(OCL.get(0).Organisms,OCL.get(1).Organisms);
					double ContentDist = ContentDissimilarity(OCL.get(0), OCL.get(1));
					
					//write if the filtering matches
					if (PhyDist >= BP.MinDiffGrpPhyloDist &&
							ContentDist <= BP.MaxContentDiss){
						
						//add stability relationships + components
						OSR.addOperonClusters(OCL);
					}
				}
			}
		}
		
		//(9) Return output
		return OSR;
		
	}
	
	//determine the maximum distance between elements in a list of organisms
	public double DetermineMaxDist(LinkedList<String> Organisms){
		
		//Initialize
		double MaxDist = -1;
		
		for (String s1 : Organisms){
			for (String s2: Organisms){
				if (!s1.equals(s2)){
					
					//retrieve value
					LinkedList<String> OrgList = new LinkedList<String>();
					OrgList.add(s1);
					OrgList.add(s2);
					Collections.sort(OrgList);
					double d = PhyDistHash.get(OrgList);
					
					//compare + possibly adjust max
					if (d > MaxDist){
						MaxDist = d;
					}
				}
			}
		}
		
		//return updated value
		return MaxDist;
	}
	
	//determine the minimum distance between elements in a list of organisms
	//determine the minimum distance between elements in two separate lists of organisms
	public double DetermineMinDist(LinkedList<String> L1, LinkedList<String> L2){
		
		//Initialize
		double MinDist = 9999;
		
		for (String s1 : L1){
			for (String s2: L2){
				if (!s1.equals(s2)){
					
					//retrieve value
					LinkedList<String> OrgList = new LinkedList<String>();
					OrgList.add(s1);
					OrgList.add(s2);
					Collections.sort(OrgList);
					double d = PhyDistHash.get(OrgList);
					
					//compare + possibly adjust max
					if (d < MinDist){
						MinDist = d;
					}
				}
			}
		}
		
		//return updated value
		return MinDist;
		
	}
	
	//determine the content dissimilarity
	public double ContentDissimilarity(OperonCluster OC1, OperonCluster OC2){
			
		//at present: just evaluate by protein content using generalize dice/jaccard
		CustomDissimilarity CD = new CustomDissimilarity();
		double Dice = CD.GeneralizedDiceOrJaccard(OC1.ClustersFeatured, OC2.ClustersFeatured, true, "Dice");
		
		//return dissimilarity
		return Dice;
	}
	
	//segregate a single operon trajectory into multiple clusters
	public LinkedList<OperonCluster> SegregateTrajectory(OperonTrajectory OT){
				
		//initialize hash
		LinkedHashMap<LinkedList<Integer>,OperonCluster> Segregation
			= new LinkedHashMap<LinkedList<Integer>, OperonCluster>();
		
		//initialize sorting number
		int SortingNumber = 0;
		
//		//(1) Segregate operons into appropriate groups
//		for (String s : OT.TrajectoryHash.keySet()){
//			for (LinkedList<GenomicElement> L : OT.TrajectoryHash.get(s)){
//		
//				// determine featured protein families
//				LinkedList<Integer> FamClust = new LinkedList<Integer>();
//				
//				//an operon
//				for (GenomicElement E : L){
//					FamClust.add(E.getClusterID());
//				}
//				
//				//build the key for the hash
//				Collections.sort(FamClust);
//				
//				OperonCluster OC;
//				if (Segregation.get(FamClust) != null){
//					OC = Segregation.get(FamClust);
//				} else {
//					OC = new OperonCluster();
//					SortingNumber++;
//				}
//				
//				//adjust contents + store in hash
//				OC.SeedCluster = OT.ClusterID;
//				OC.SortingNumber = SortingNumber;
//				OC.addOrg(s);
//				OC.Operons.add(L);
//				OC.addClustersFeatured(FamClust);
//				
//				Segregation.put(FamClust, OC);
//			}
//		}
		
		//(1) UPDATE - use species-amalgamated operons instead of separate operons
		for (String s : OT.AmalgamatedOperons.keySet()){
			LinkedList<GenomicElement> L = OT.AmalgamatedOperons.get(s);
			
			// determine featured protein families
			LinkedList<Integer> FamClust = new LinkedList<Integer>();
			
			//an operon
			for (GenomicElement E : L){
				FamClust.add(E.getClusterID());
			}
			
			//build the key for the hash
			Collections.sort(FamClust);
			
			OperonCluster OC;
			if (Segregation.get(FamClust) != null){
				OC = Segregation.get(FamClust);
			} else {
				OC = new OperonCluster();
				SortingNumber++;
			}
			
			//adjust contents + store in hash
			OC.SeedCluster = OT.ClusterID;
			OC.SortingNumber = SortingNumber;
			OC.addOrg(s);
			OC.Operons.add(L);
			OC.addClustersFeatured(FamClust);
			
			Segregation.put(FamClust, OC);
		}
		
		//(2) Calculate features of each operon cluster.
		LinkedList<OperonCluster> OpGroups = new LinkedList<OperonCluster>();
		
		//iterate through each, compute values, store in new list
		for (OperonCluster OC : Segregation.values()){
			OC.OperonSize = OC.ClustersFeatured.size();
			OC.MaxInternalDist = DetermineMaxDist(OC.Organisms);
			OpGroups.add(OC);
		}
	
		//return statement
		return OpGroups;
		
	}
	
	//segregate a single operon trajectory into multiple clusters - no amalg
	public LinkedList<OperonCluster> SegregateTrajectoryNoAmalg(OperonTrajectory OT){
				
		//initialize hash
		LinkedHashMap<LinkedList<Integer>,OperonCluster> Segregation
			= new LinkedHashMap<LinkedList<Integer>, OperonCluster>();
		
		//initialize sorting number
		int SortingNumber = 0;
		
		//(1) Segregate operons into appropriate groups
		for (String s : OT.TrajectoryHash.keySet()){
			for (LinkedList<GenomicElement> L : OT.TrajectoryHash.get(s)){
		
				// determine featured protein families
				LinkedList<Integer> FamClust = new LinkedList<Integer>();
				
				//an operon
				for (GenomicElement E : L){
					FamClust.add(E.getClusterID());
				}
				
				//build the key for the hash
				Collections.sort(FamClust);
				
				OperonCluster OC;
				if (Segregation.get(FamClust) != null){
					OC = Segregation.get(FamClust);
				} else {
					OC = new OperonCluster();
					SortingNumber++;
				}
				
				//adjust contents + store in hash
				OC.SeedCluster = OT.ClusterID;
				OC.SortingNumber = SortingNumber;
				OC.addOrg(s);
				OC.Operons.add(L);
				OC.addClustersFeatured(FamClust);
				
				Segregation.put(FamClust, OC);
			}
		}
		
		//(2) Calculate features of each operon cluster.
		LinkedList<OperonCluster> OpGroups = new LinkedList<OperonCluster>();
		
		//iterate through each, compute values, store in new list
		for (OperonCluster OC : Segregation.values()){
			OC.OperonSize = OC.ClustersFeatured.size();
			OC.MaxInternalDist = DetermineMaxDist(OC.Organisms);
			OpGroups.add(OC);
		}
	
		//print statement
		//System.out.println("Breakpoint!");
		
		//return statement
		return OpGroups;
		
	}
	
	//amalgamate a set of clusters into a new set of clusters
	public LinkedList<OperonCluster> GeneOrderSetAmalgamate(LinkedList<OperonCluster> UnfilteredSet){
		
		//Initialize output
		LinkedList<OperonCluster> FilteredSet = new LinkedList<OperonCluster>();
		
		//Remove single sets
		for (OperonCluster OC : UnfilteredSet){
			if (OC.OperonSize > 0){
				FilteredSet.add(OC);
			}
		}
		
		//Iterate amalgamation protocol
		
		//Initialize - try to amalgamate
		boolean FinishedAmalgamation = false;
		
		while (!FinishedAmalgamation){
			
			//re-set place-holder set, and re-initialize filtered set
			LinkedList<OperonCluster> PlaceHolder = FilteredSet;
			FilteredSet = new LinkedList<OperonCluster>();
			
			//check for 2-element overlaps
			
			//check every operon cluster...
			for (int i = 0; i < PlaceHolder.size(); i++){
				
				//retrieve one
				OperonCluster OC1 = PlaceHolder.get(i);
				
				//Check number of amalgamations
				int AmalgCounter = 0;
				
				//... against every other operon cluster
				for (int j = i+1; j < PlaceHolder.size(); j++){

					//retrieve the other
					OperonCluster OC2 = PlaceHolder.get(j);
					
					//check for at least two common elements, if so, schedule for amalgamation
					
				}
			}
			
		}
		
		//return output
		return FilteredSet;
	}
	
	//build 2-gene trajectories - for use with gene order set, etc
	public void BuildDoubleTrajectories(){
		
	}
	
	//split a single trajectory into a set of double trajectories
	public LinkedList<DoubleOperonTrajectory> Single2DoubleTrajectory(OperonTrajectory OT){
		
		//Initialize output
		LinkedList<DoubleOperonTrajectory> SplitTrajectory = new LinkedList<DoubleOperonTrajectory>();
		
		//create a hash map -> data for trajectories
		LinkedHashMap<LinkedList<Integer>, LinkedHashMap<String,LinkedList<LinkedList<GenomicElement>>>> Mapping
			= new LinkedHashMap<LinkedList<Integer>, LinkedHashMap<String,LinkedList<LinkedList<GenomicElement>>>>();
		
		//Initialize empty hash maps
		
		
		//Iterate through all species featured
		for (String s : OT.TrajectoryHash.keySet()){
			
			//all operons from speices s
			LinkedList<LinkedList<GenomicElement>> OpSet = OT.TrajectoryHash.get(s);
			
			//every individual operon
			for (LinkedList<GenomicElement> L : OpSet){
				
				//keep track of clusters featured
				LinkedList<Integer> ClustersFeatured = new LinkedList<Integer>();
				
				
				
				
			}
			
		}
		
		
		//return output
		return SplitTrajectory;
		
	}
	
	// ===== Export ======== //

	//Create a list of operon trajectories appropriate for gene-order analysis
	public LinkedList<LinkedList<GenomicElement>> GenerateGeneOrderAppropriateGeneSets(LinkedHashMap<Integer, OperonTrajectory> Trajectories){
		
		/*
		 * Not all operon trajectories have the potential to exhibit an interesting change
		 * in gene order.  Trajectories should be filtered+split so that all have at least two common
		 * elements - only if there are two elements can there be a relative change in gene order.
		 * 
		 * This may mean that trajectories are divided up into multiple groups.  That's okay -
		 * groups are stored based on lists of genes, so no danger at naming confusion
		 * 
		 * How to properly compare a set of component operons should be built into the JCE software:
		 * For example, assessing changes in "before" and "after" needs to be compared considering strand
		 * commonality: in other words, a reverse in order of the genes in an operon reflects a
		 *  change in gene order iff the strand is the same in both operons.
		 * 
		 * Algorithm:
		 * (1) For each operon trajectory, divide into non-overlapping clusters based on at least 2 common genes
		 * (2) Each non-overlapping cluster is a single context set group.
		 * (3) Store all groups in a master list, checking for overlaps.
		 * (4) 
		 */
		
		//Inititalize output
		LinkedList<LinkedList<GenomicElement>> NonOverlappingGeneGroups =
				new LinkedList<LinkedList<GenomicElement>>();
		
		//Iterate through each trajectory
		for (Integer x : Trajectories.keySet()){
			
			//Retrieve trajectory
			OperonTrajectory OT = Trajectories.get(x);
			
			//split all operons into groups
			LinkedList<OperonCluster> IsolatedGroups = SegregateTrajectoryNoAmalg(OT);
			
			//build amalgamated groups from these
			LinkedList<OperonCluster> AmalgamatedGroups = GeneOrderSetAmalgamate(IsolatedGroups);
			
		}
		
		//return completed hash map
		return NonOverlappingGeneGroups;
		
	}
	
	//Export a whole set of operon stats sorted by several different variables
	public void ExportByDifferentVariables(String BaseFile, boolean IncludeSingletons){
		
		//Retrieve list of trajectories
		LinkedList<OperonTrajectory> L = new LinkedList<OperonTrajectory>(Trajectories.values());
		
		//sort + export
		
		//(1) by NSR
		Collections.sort(L, new SortbyOperonicity());
		String NSRFile = BaseFile + "_byOperonicity.txt";
		ExportTrajectoryStatistics(NSRFile,L,IncludeSingletons);
		
		//(2) by Novelty
		Collections.sort(L, new SortbyVariety());
		String NoveltyFile = BaseFile + "_byVariety.txt";
		ExportTrajectoryStatistics(NoveltyFile,L,IncludeSingletons);
		
		//(3) by OrgsFeatured
		Collections.sort(L, new SortbyOrgsFeatured());
		String OrgsFeaturedFile = BaseFile + "_byOrgsFeatured.txt";
		ExportTrajectoryStatistics(OrgsFeaturedFile,L,IncludeSingletons);
		
		//(4) NSR, Novelty
		Collections.sort(L, new SortbyOperonicityThenVariety());
		String NSR_then_NoveltyFile = BaseFile + "_byOperonicity_then_Variety.txt";
		ExportTrajectoryStatistics(NSR_then_NoveltyFile,L,IncludeSingletons);
		
		//(5) Novelty, NSR
		Collections.sort(L, new SortbyVarietyThenOperonicity());
		String Novelty_then_NSRFile = BaseFile + "_byNVariety_then_Operonicity.txt";
		ExportTrajectoryStatistics(Novelty_then_NSRFile,L,IncludeSingletons);
		
		//(6) by evolutionary rate
		Collections.sort(L, new SortbyEvoRate());
		String EvoRateFile = BaseFile + "_byRate.txt";
		ExportTrajectoryStatistics(EvoRateFile,L,IncludeSingletons);
		
		//output message
		System.out.println("Files Successfully Exported!");
		
	}
	
	//Export a single set of operon trajectory statistics
	public void ExportTrajectoryStatistics(String FileName, LinkedList<OperonTrajectory> SortedTrajectories, boolean IncludeSingletons){
		try {
			
			//initialize file writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(FileName));
			
			//initialize header + write to file
			String Header = "cluster_num\tnum_orgs\toperonicity\tvariety\tevolutionary_rate\n";
			bw.write(Header);
			bw.flush();
			
			//enumerate through genomes
			for (OperonTrajectory OT : SortedTrajectories){
								
				//build line
				String ln = String.valueOf(OT.ClusterID) +"\t"
					+ String.valueOf(OT.OrgsFeatured) + "\t"
					+ String.valueOf(OT.Operonicity) + "\t"
					+ String.valueOf(OT.Variety) + "\t"
					+ String.valueOf(OT.EvoRate) + "\n";
				
				//write line to file
				if (IncludeSingletons || (!IncludeSingletons && !OT.AlwaysASingleGene)){
					bw.write(ln);
					bw.flush();
				}
				
			}
			bw.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//export a query set, with options to vary operonicity + novelty (towards highly conserved)
	
	//Export a query set of trajectories
	public void ExportQuerySet(String QuerySetFile, LinkedHashMap<Integer,OperonTrajectory> Trajectories, Double MinOperonicity, Double MaxNovelty){
		try {
			
			//open file stream
			BufferedWriter bw = new BufferedWriter(new FileWriter(QuerySetFile));
			
			//initialize counter.
			int Counter = 0;
			
			//export all appropriate trajectories
			for (Integer x : Trajectories.keySet()){
				
				//retrieve trajectory
				OperonTrajectory OT = Trajectories.get(x);
				
				//check parameters
				if (OT.Variety <= MaxNovelty &&
						OT.Operonicity >= MinOperonicity){
					
					//build string, write and export
					String ln = x + "\n";
					bw.write(ln);
					bw.flush();
					
					//increment counter
					Counter++;
				}
				
			}
			
			//close file stream
			bw.close();
			
			//output message
			System.out.println("Exported query set containing " + Counter + " trajectories.");
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
		
	//Export a set of pairwise searches appropriate for export
	public void ExportGeneOrderAnalysisQuerySet(String QuerySetFile, LinkedHashMap<Integer,OperonTrajectory> Trajectories, Double MinOperonicity){
		
		//Initialize a list of query-pairs
		LinkedList<LinkedList<Integer>> QueryPairs = new LinkedList<LinkedList<Integer>>();
		
		//iterate through all values
		for (Integer x : Trajectories.keySet()){
			
			//retrieve trajectory
			OperonTrajectory OT = Trajectories.get(x);
			
			//proceed if appropriate
			if (OT.Operonicity >= MinOperonicity){
				
				//create each pair
				for (Integer y : OT.OtherClusters){
					
					//re-format pair as sorted linked list
					LinkedList<Integer> GenePair = new LinkedList<Integer>();
					GenePair.add(x);
					GenePair.add(y);
					Collections.sort(GenePair);
					
					//add list to set of all pairs
					if (!QueryPairs.contains(GenePair)){
						QueryPairs.add(GenePair);
					}
				}
				
			}
			
			//output message.
			if (x%100 == 0){
				System.out.println("Built Query Pairs for " + x +"/5276 operon trajectories.");
			}
			
		}
		
		//System.out.println("Sorting!");
		
		//Sort the list
		Collections.sort(QueryPairs, new SortListOfPairs());
		
		//export to file
		try {
			
			//open file stream
			BufferedWriter bw = new BufferedWriter(new FileWriter(QuerySetFile));
			
			//export each query pair
			for (LinkedList<Integer> L : QueryPairs){
				
				//build string
				String str = String.valueOf(L.get(0)) + " $$ " + String.valueOf(L.get(1)) + "\n";
				
				//write to file stream
				bw.write(str);
				bw.flush();
				
			}
			
			//close file stream
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Export a set of n-wise searches appropriate for export
	public void ExportIdenticalContentQuerySet(String QuerySetFile, LinkedHashMap<Integer,OperonTrajectory> Trajectories, boolean IncludeSingles){
		
		//Initialize a list of query-pairs
		LinkedList<LinkedList<Integer>> QueryGroups = new LinkedList<LinkedList<Integer>>();
		
		//iterate through all values
		for (Integer x : Trajectories.keySet()){
			
			//retrieve trajectory
			OperonTrajectory OT = Trajectories.get(x);
			
			//determine all non-overlapping groups
			LinkedList<OperonCluster> Clusters = SegregateTrajectoryNoAmalg(OT);
			
			//iterate through + create groups
			for (OperonCluster OC : Clusters){
				
				//create group
				LinkedList<Integer> ClusterGrp = new LinkedList<Integer>();
				
				//add this cluster to the list
				ClusterGrp.add(x);
				
				//build list
				for (Object o : OC.ClustersFeatured){
					Integer objint = (Integer) o;
					ClusterGrp.add(objint);
				}
				
				//arrange in ascending order
				Collections.sort(ClusterGrp);
				
				//add list to set of all pairs
				if ((!QueryGroups.contains(ClusterGrp) 
						&& ClusterGrp.size() > 0
						&& OC.Operons.size() > 1) &&
						(IncludeSingles || !IncludeSingles && ClusterGrp.size() > 1) //either include singles, or not
						){
					QueryGroups.add(ClusterGrp);
				}

			}
			
			//output message.
			if (x%100 == 0){
				System.out.println("Built Query Groups for " + x +"/5276 operon trajectories.");
			}
			
		}

		//Sort the list
		Collections.sort(QueryGroups, new SortListOfGroups());
		
		//export to file
		try {
			
			//open file stream
			BufferedWriter bw = new BufferedWriter(new FileWriter(QuerySetFile));
			
			//export each query pair
			for (LinkedList<Integer> L : QueryGroups){
				
				//initialize an index counter
				int IndexCounter = 0;
				
				//initialize string
				String str = "&&only ";
				
				while (IndexCounter < L.size()) {
					
					//add next index
					str = str + String.valueOf(L.get(IndexCounter));
					
					//anticipate additional genes, if necessary
					if (IndexCounter+1 < L.size()){
						str = str + " $$ ";
					} 
					
					//increment counter
					IndexCounter++;
				}
				
				//add new line
				str = str + "\n";
				
				//write to file stream
				bw.write(str);
				bw.flush();
				
			}
			
			//close file stream
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Export the set of adjacent gene pairs that are represented in all operon topologies
	public void ExportAdjacentGenePairs(String QuerySetFile, LinkedHashMap<Integer, OperonTrajectory> Trajectories){
		
		//Initialize a list of query-pairs
		LinkedList<LinkedList<Integer>> QueryGroups = new LinkedList<LinkedList<Integer>>();
		
		//iterate through all values
		for (Integer x : Trajectories.keySet()){
			
			//retrieve trajectory
			OperonTrajectory OT = Trajectories.get(x);
			
			//determine all non-overlapping groups
			LinkedList<OperonCluster> Clusters = SegregateTrajectoryNoAmalg(OT);
			
			//iterate through + create groups
			for (OperonCluster OC : Clusters){

				//check every operon instance
				for (LinkedList<GenomicElement> L : OC.Operons){
					
					//iterate through operon
					for (int i = 0; i < L.size()-1; i++){
						
						//note every pair
						LinkedList<Integer> ClusterGrp = new LinkedList<Integer>();
						ClusterGrp.add(L.get(i).getClusterID());
						ClusterGrp.add(L.get(i+1).getClusterID());
						
						//sort + add to set
						Collections.sort(ClusterGrp);
						if (!QueryGroups.contains(ClusterGrp)){
							QueryGroups.add(ClusterGrp);
						}
						
					}

				}
				
			}
			
			//output message.
			if (x%100 == 0){
				System.out.println("Built Query Groups for " + x +"/5276 operon trajectories.");
			}
			
		}

		//Sort the list
		Collections.sort(QueryGroups, new SortListOfPairs());
		
		//export to file
		try {
			
			//open file stream
			BufferedWriter bw = new BufferedWriter(new FileWriter(QuerySetFile));
			
			//export each query pair
			for (LinkedList<Integer> L : QueryGroups){
				
				//create string
				String str = String.valueOf(L.get(0)) + " ; " + String.valueOf(L.get(1)) + "\n";
				
				//write to file stream
				bw.write(str);
				bw.flush();
				
			}
			
			//close file stream
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	// ===== Sorting Classes ====== //
	
	//sort by NSR
	
	// ===== Sorting Classes ======== //
	
	public class SortbyOperonicity implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.Operonicity<o2.Operonicity) return 1;
			if (o1.Operonicity>o2.Operonicity) return -1;
			if (o1.Operonicity==o2.Operonicity){
				return o1.ClusterID-o2.ClusterID;
			}
			return 0;
		}
		
	}
	
	//sort by novelty
	public class SortbyVariety implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.Variety<o2.Variety) return -1;
			if (o1.Variety>o2.Variety) return 1;
			if (o1.Variety == o2.Variety){
				return o1.ClusterID - o2.ClusterID;
			}
			return 0;
		}
		
	}
	
	//sort by number of orgs
	public class SortbyOrgsFeatured implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.OrgsFeatured != o2.OrgsFeatured){
				return -1*(o1.OrgsFeatured-o2.OrgsFeatured);
			} else {
				return -1*(o1.ClusterID - o2.ClusterID);
			}

		}
		
	}

	//NSR, novelty
	public class SortbyOperonicityThenVariety implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.Operonicity<o2.Operonicity){
				return 1;
			}
			if (o1.Operonicity>o2.Operonicity){
				return -1;
			}
			if (o1.Operonicity==o2.Operonicity){
				if (o1.Variety<o2.Variety){
					return -1;
				}
				if (o1.Variety>o2.Variety){
					return 1;
				}
				if (o1.Variety == o2.Variety){
					return o1.ClusterID - o2.ClusterID;
				}
			}
			return 0;
		}
		
	}

	//Novelty, NSR
	//NSR, novelty
	public class SortbyVarietyThenOperonicity implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.Variety<o2.Variety) return -1;
			if (o1.Variety>o2.Variety) return 1;
			if (o1.Variety == o2.Variety){
				if (o1.Operonicity<o2.Operonicity) return 1;
				if (o1.Operonicity>o2.Operonicity) return -1;
				if (o1.Operonicity==o2.Operonicity){
					return o1.ClusterID - o2.ClusterID;
				}
			}
			return 0;
		}
		
	}
	public static class SortbyEvoRate implements Comparator<OperonTrajectory>{

		@Override
		public int compare(OperonTrajectory o1, OperonTrajectory o2) {
			if (o1.EvoRate<o2.EvoRate) return -1;
			if (o1.EvoRate>o2.EvoRate) return 1;
			if (o1.EvoRate == o2.EvoRate){
				return o1.ClusterID - o2.ClusterID;
			}
			return 0;
		}
		
	}
	public class OCSorter implements Comparator<OperonCluster>{

		@Override
		public int compare(OperonCluster o1, OperonCluster o2) {
			return o1.SortingNumber-o2.SortingNumber;
		}
		
	}
	
	public class SortListOfPairs implements Comparator<LinkedList<Integer>>{

		@Override
		public int compare(LinkedList<Integer> o1, LinkedList<Integer> o2) {
			
			try {
				if (o1.get(0) == o2.get(0)){
					return o1.get(1) - o2.get(1);
				} else {
					return o1.get(0) - o2.get(0);
				}
			} catch (Exception ex){
				return 0;
			}

		}
		
	}
	
	public class SortListOfGroups implements Comparator<LinkedList<Integer>>{

		@Override
		public int compare(LinkedList<Integer> o1, LinkedList<Integer> o2) {
			
			//march down list, while elements still around
			int ReturnVal = 0;
			int ElementCounter = 0;

			//sort the list for as long as possible.
			while(o1.size() > ElementCounter && o2.size() > ElementCounter){
				if (o1.get(ElementCounter) != o2.get(ElementCounter)){
					ReturnVal = o1.get(ElementCounter) - o2.get(ElementCounter);
					break;
				} else{
					ElementCounter++;
				}
			}

			//return the determined value!
			return ReturnVal;

		}
		
	}
	
	// ===== Deprecated ======== //
	
	//DEPRECATED
	//Export trajectories as context set
	public void ExportTrajectoriesAsContextSet(String ContextSetFile, boolean OperonsOnly, LinkedHashMap<Integer, OperonTrajectory> Trajectories){
		try {
			
			//file writing
			BufferedWriter bw = new BufferedWriter(new FileWriter(ContextSetFile));
			
			//Initialize a hash set of string
			LinkedList<String> Lines2Export = new LinkedList<String>();
			
			 //Initialize a line + counter
			 String Line = "";
			 String LineKey = "";
			 int ProcessCounter = 0;
			 
			 for (Integer x : Trajectories.keySet()){
				 
				 //retrieve trajectory
				 OperonTrajectory OT = Trajectories.get(x);
				 
				 //option to only export operons
				 if (!OperonsOnly || (OperonsOnly && !OT.AlwaysASingleGene)){
					 
					 //increment counter
					 ProcessCounter++;
					 
					 //for each organism's gene instances in the trajectory
					 for (String s : OT.TrajectoryHash.keySet()){
						 
						 //Export amalgamated set
						 LinkedList<GenomicElement> L = OT.AmalgamatedOperons.get(s);
							 for (GenomicElement E : L){
								 
								 //build line
								 //key - the data itself
								 LineKey = s + "\t" 
										 + E.getContig() + "\t"
										 + E.getStart() + "\t"
										 + E.getStop() + "\t";
								 
								 //the line itself in context set file
								 Line = LineKey + x + "\n";
								 
								 //if this data point has not yet been exported, export.
								 if (!Lines2Export.contains(LineKey)){
									 
									 //store key
									 Lines2Export.add(LineKey);
									 
									 //write line to file
									 bw.write(Line);
									 bw.flush();
								 }
							 }
						 
					 }
					 
				 }
				
				 //output message.
				if (ProcessCounter%100 == 0){
					System.out.println("Exported " + ProcessCounter +"/"+ Trajectories.size() +" operon trajectories.");
				}
				 
			 }
			 
			 //close file writer
			 bw.close();
			 
			 //last message
			 System.out.println("Export complete!");
			 
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
