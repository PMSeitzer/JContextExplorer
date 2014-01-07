package OperonEvolutionInHalos;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.GenomicElement;
import genomeObjects.OrganismSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.biojava3.core.sequence.Strand;

public class LoadData {

	/**
	 * @param args
	 */
	
	//Fields
	public static String GFFDir = "/Users/phillipseitzer/Dropbox/GenomeSets/Sets/Haloarchaea";
	public static String SpeciesNamesFile = "/Users/phillipseitzer/Dropbox/GenomeSets/Process/Halo/SpeciesNames.txt";
	public static String TranslationFile = "/Users/phillipseitzer/Documents/Halophiles_2012/EightyHalophiles/MarkerGenesAnalysis/TranslationFiles.txt";
	public static String GenomeFile = "/Users/phillipseitzer/Documents/Halophiles_2012/EightyHalophiles/GeneMisannotations/GenomeFiles.txt";
	
	//cluster ID counts
	public static LinkedHashMap<String, LinkedList<Integer>> ClusterIDsinOrgs;
	public static LinkedHashMap<String, LinkedList<Integer>> OperonClusterIDsinOrgs;
	public static LinkedHashMap<String, String> TranslationFiles;	// OrgName,File
	public static LinkedHashMap<String, String> GenomeFiles; //OrgName, File
	public static LinkedList<Integer> AllClusters;
	public static LinkedList<Integer> NonSingleCopyClusters;
	public static int MaxClusterNum = 0;
	public static int FastaLineLength = 70;
	
	//gene order processing
	public static LinkedList<Integer> Clusters2Include;
	
	//Organism set in this case is just a hash map
	public static OrganismSet OS;

	//------- Set up OS --------//
	
	//Import all genomes using JCE data structures
	public static void ImportGenomes(){
		
		//initialize organism set
		OS = new OrganismSet();
		
		//instructions on how to process various types
		LinkedList<String> IncludeTypes = new LinkedList<String>();
		IncludeTypes.add("CDS");
		
		LinkedList<String> FeatureOnlyTypes = new LinkedList<String>();
		FeatureOnlyTypes.add("tRNA");
		FeatureOnlyTypes.add("rRNA");
		FeatureOnlyTypes.add("mobile_element");
		FeatureOnlyTypes.add("IS_element");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(SpeciesNamesFile));
			String Line = null;
			while ((Line = br.readLine()) != null){
				
				//object from JCE library + with appropriate data included
				AnnotatedGenome AG = new AnnotatedGenome();
				String OrgFile = GFFDir + "/" + Line + ".gff";
				AG.setSpecies(Line);
				AG.setIncludeTypes(IncludeTypes);
				AG.setDisplayOnlyTypes(FeatureOnlyTypes);
				AG.importFromGFFFile(OrgFile);
				
				//add to list
				OS.getSpecies().put(Line, AG);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//message
		System.out.println("Genomes loaded from .GFF files.");
		
	}
	
	//Define the intergenic distance context set
	public static void BasicOperons(int OpD){
		
		//iterate through all.
		for (AnnotatedGenome AG : OS.getSpecies().values()){
			AG.ComputeContextSet("BasicOperons", OpD, true);
			//System.out.println("Basic operons computed for " + AG.getSpecies() + ".");
		}
		
		//message
		//System.out.println("All basic operons computed!");
		
	}
	
	// ------ Export ------//
	
	//export operons as context set - for use with JCE, for visualization
	public static void ExportOperonsAsContextSet(String OperonCSFile, String CSName, boolean IncludeSingleGeneOperonInstances){
		
		try {
			
			//open file stream
			BufferedWriter bw = new BufferedWriter(new FileWriter(OperonCSFile));
			
			//Initialize counter
			int ContextCounter = 0;
			
			//write for each organism in the set of organisms.
			for (String s : OS.getSpecies().keySet()){
				
				//retrieve the context set
				ContextSet TheContextSet = null;
				AnnotatedGenome AG = OS.getSpecies().get(s);
				for (ContextSet CS : AG.getGroupings()){
					if (CS.getName().equals(CSName)){
						TheContextSet = CS;
						break;
					}
				}
				
				//retrieve mapping
				HashMap<Integer,LinkedList<GenomicElement>> Mapping =
						TheContextSet.getContextMapping();
				
				//iterate through mapping + write to file
				for (LinkedList<GenomicElement> L : Mapping.values()){
					
					if (IncludeSingleGeneOperonInstances || (!IncludeSingleGeneOperonInstances && L.size() > 1)){
						
						//Increment the context counter
						ContextCounter++;
						
						//Write each line to file.
						for (GenomicElement E : L){
							String Line = s + "\t" 
									 + E.getContig() + "\t"
									 + E.getStart() + "\t"
									 + E.getStop() + "\t"
									 + String.valueOf(ContextCounter) + "\n";
							bw.write(Line);
							bw.flush();
						}
						
					}
					

				}
				
			}
			
			//close file stream
			 bw.close();
			 
			 //output statement
			 System.out.println("Operons successfully exported!");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//Export a gene order pair appropriate query set
	public static void ExportGeneOrderPairQuerySet(String QuerySetFile){
		
		/*
		 * 
		 */
		
	}
	
	//import a list of the clusters under investigation
	public static void ImportClustersToInclude(String ClusterFile){
		Clusters2Include = new LinkedList<Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(ClusterFile));
			String Line = null;
			while ((Line = br.readLine()) != null){
				int Cluster = Integer.parseInt(Line.trim());
				Clusters2Include.add(Cluster);
			}
			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	// ------ Deprecated ------ //
	
	//Old import way - not taking advantage of JCE
	//Import all genomic info
	public static void ImportGenomesOld(){
		OS = new OrganismSet();
		try {
			//First, build empty hash map
			BufferedReader br = new BufferedReader(new FileReader(SpeciesNamesFile));
			String Line = null;
			while ((Line = br.readLine()) != null){
				AnnotatedGenome AG = new AnnotatedGenome();
				OS.getSpecies().put(Line, AG);
			}
			br.close();
			
			for (String s : OS.getSpecies().keySet()){
				
				//info
				String OrgFile = GFFDir + "/" + s + ".gff";
				LinkedList<GenomicElement> Elements = new LinkedList<GenomicElement>();
				
				//read file, build list
				BufferedReader br2 = new BufferedReader(new FileReader(OrgFile));
				String Line2 = null;
				while ((Line2 = br2.readLine()) != null){
					
					//import each line of the .gff file
					String ImportedLine[] = Line2.split("\t");
					
					//create a new element
					GenomicElement E = new GenomicElement();

					//set appropriate fields of this genomic element with inputs achieved from the GFF file
					E.setGenome(s);
					E.setContig(ImportedLine[0]);
					E.setType(ImportedLine[2]);
					E.setStart(Integer.parseInt(ImportedLine[3]));
					E.setStop(Integer.parseInt(ImportedLine[4]));
					E.DetermineCenter();

					try {
						if(Integer.parseInt(ImportedLine[6])==1){
							E.setStrand(Strand.POSITIVE);
						}else{
							E.setStrand(Strand.NEGATIVE);
						}
					} catch (Exception ex) {
						if (ImportedLine[6].contentEquals("+")){
							E.setStrand(Strand.POSITIVE);
						} else {
							E.setStrand(Strand.NEGATIVE);
						}
					} 

					//set annotation
					E.setAnnotation(ImportedLine[8]);

					//add gene IDs + homology clusters, if available
					if (ImportedLine.length > 9){
						int ClustID = Integer.parseInt(ImportedLine[9]);
						E.setClusterID(ClustID);

						//System.out.println("Set!");
						if (ImportedLine.length > 10){
							E.setGeneID(ImportedLine[10]);
						}
					}

					//add to list, if it doesn't already exist.
					Elements.add(E);
				}
				
				//close file stream
				br2.close();
				
				//update annotatedgenome
				OS.getSpecies().get(s).setElements(Elements);
				
			}
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		//output message
		System.out.println("All organisms loaded.");
	}
	
	//Determine clusters only
	public static void DetermineClustersInOrgs(){
		
		//initialize output structures
		ClusterIDsinOrgs = new LinkedHashMap<String, LinkedList<Integer>>();
		AllClusters = new LinkedList<Integer>();
		NonSingleCopyClusters = new LinkedList<Integer>();
		
		try {
			
			//First, build empty hash map
			BufferedReader br = new BufferedReader(new FileReader(SpeciesNamesFile));
			String Line = null;
			while ((Line = br.readLine()) != null){
				ClusterIDsinOrgs.put(Line, new LinkedList<Integer>());
			}
			br.close();
			
			//next, import each file
			for (String s : ClusterIDsinOrgs.keySet()){
				
				//info
				String OrgFile = GFFDir + "/" + s + ".gff";
				LinkedList<Integer> Clusters = new LinkedList<Integer>();
				
				//read file, build list
				BufferedReader br2 = new BufferedReader(new FileReader(OrgFile));
				String Line2 = null;
				while ((Line2 = br2.readLine()) != null){
					String[] L = Line2.split("\t");
					
					//consider only CDS
					if (L[2].equals("CDS")){
						int ClusterNum = Integer.parseInt(L[9]);
						if (ClusterNum > MaxClusterNum){
							MaxClusterNum = ClusterNum;
						}
						//If the cluster is not already there, add
						if (!Clusters.contains(ClusterNum)){
							Clusters.add(ClusterNum);
						} else {
						//if it is, note this
							NonSingleCopyClusters.add(ClusterNum);
						}
						if (!AllClusters.contains(ClusterNum)){
							AllClusters.add(ClusterNum);
						}
					}

				}
				br2.close();
				
				//sort
				Collections.sort(Clusters);
				
				//store
				ClusterIDsinOrgs.put(s, Clusters);
				
				//System.out.println("Loaded " + s + ".");
				
			}
			
			System.out.println("Loaded Cluster ID Mappings.");

			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	//Import protein translation map
	public static void ImportTranslationFileList(){
		TranslationFiles = new LinkedHashMap<String,String>();
		try {
			BufferedReader br  = new BufferedReader(new FileReader(TranslationFile));
			String Line = null;
			while((Line = br.readLine()) != null){
				String[] Path = Line.split("/");
				String[] Name = Path[Path.length-1].split(".fasta");
				TranslationFiles.put(Name[0],Line);
			}
			br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	//Retrieve a single stretch of nucleotides
	public static String GetNucleotides(String Organism, String Contig, int StartCoord, int StopCoord){
		
		//Initialize output 
		String nt = "";
		
		try{
			
			//Retrieve genome
			BufferedReader br = new BufferedReader(new FileReader(GenomeFiles.get(Organism)));
			String Line = null;
			boolean FoundContig = false;
			boolean StartedSequence = false;
			boolean NewLine = true;
			int CoordCounter = 0;
			while ((Line = br.readLine()) != null){
				
				//every new line - set switch
				NewLine = true;
				
				//find appropriate header
				if (Line.startsWith(">")){
					if (Line.contains(Contig)){
						FoundContig = true;
					}
				} else if (FoundContig){
					
					 //check for starting a sequence
					 if (!StartedSequence){
						 if (StartCoord - CoordCounter <= FastaLineLength){
							 nt = Line.substring((StartCoord-CoordCounter-1), Line.length()).toUpperCase();
							 StartedSequence = true;
							 NewLine = false;
						 } 
					 } else {
						 NewLine = true;
					 }
					 
				     //once started, write until appropriate to stop
					 if (StartedSequence){
						 if (StopCoord - CoordCounter <= FastaLineLength){
							 nt = nt + Line.substring((StopCoord-CoordCounter-1),Line.length()).toUpperCase();
							 break;
						 } else if (NewLine){ //add whole line
							 nt = nt + Line.toUpperCase();
						 }
					 }
					 
					 //increment counter, unless stop is nearby
					 CoordCounter = CoordCounter + FastaLineLength;
				}
			}
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		//return statement
		return nt;
	}

	//Import genome seq map
	public static void ImportGenomeFileList(){
		GenomeFiles = new LinkedHashMap<String,String>();
		try {
			BufferedReader br  = new BufferedReader(new FileReader(GenomeFile));
			String Line = null;
			while((Line = br.readLine()) != null){
				String[] Path = Line.split("/");
				String[] Name = Path[Path.length-1].split(".fasta");
				GenomeFiles.put(Name[0],Line);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
//	//Retrieve a single protein sequence
//	public static LinkedList<String> ProteinSequence(GenomicElement E){
//		
//		//Initialize output
//		LinkedList<String> ProteinSequence = new LinkedList<String>();
//		
//		try {
//			
//			//Retrieve file name
//			String TransFile = TranslationFiles.get(E.Genome);
//			
//			BufferedReader br = new BufferedReader(new FileReader(TransFile));
//			String Line = null;
//			boolean FoundTheProtein = false;
//			
//			while ((Line = br.readLine()) != null){
//				
//				//find protein, or read protein sequence.
//				if (Line.startsWith(">")){
//					
//					//Recover components from line
//					String[] GetContig = Line.split("\\{");
//					String[] FinishContig = GetContig[1].split("\\}");
//					String Contig = FinishContig[0];
//					
//					String[] GetCoords = Line.split("\\[");
//					String[] FinishCoords = GetCoords[1].split("\\]");
//					String[] GetIndCoords = FinishCoords[0].split(":");
//					int StartPos = Integer.parseInt(GetIndCoords[0]);
//					int StopPos = Integer.parseInt(GetIndCoords[1]);
//					
//					//check for match
//					if (E.Contig.equals(Contig) &&
//							E.Start == StartPos && E.Stop == StopPos){
//						FoundTheProtein = true;
//						String LineAndNewLine = Line + "\n";
//						ProteinSequence.add(LineAndNewLine);
//					}
//					
//				} else if (FoundTheProtein){
//					String LineAndNewLine = Line + "\n";
//					ProteinSequence.add(LineAndNewLine);
//					
//					//after the empty line, break out of loop.
//					if (Line.equals("")){
//						break;
//					}
//				}
//				
//			}
//			
//			//close file stream
//			br.close();
//			
//		} catch (Exception ex){
//			ex.printStackTrace();
//		}
//		
//		//return statement
//		return ProteinSequence;
//	}
	
//	//Create a list of sets that are filtered
//	public static void CreateFilteredSets(int OpD){
//		
//		//initialize output structures
//		ClusterIDsinOrgs = new LinkedHashMap<String, LinkedList<Integer>>();
//		OperonClusterIDsinOrgs = new LinkedHashMap<String, LinkedList<Integer>>();
//		AllClusters = new LinkedList<Integer>();
//		
//		//create filtered sets
//		for (String s : OS.keySet()){
//			
//			//Initialize lists
//			LinkedList<Integer> Clusters = new LinkedList<Integer>();
//			LinkedList<Integer> OperonClusters = new LinkedList<Integer>();
//			//check every element
//			LinkedList<GenomicElement> Elements = OS.get(s);
//			
//			//check each for operon
//			for (int i = 0; i < Elements.size(); i++){
//				
//				//reset designation - default = false
//				boolean InAnOperon = false;
//				
//				//Element
//				GenomicElement ECurrent = Elements.get(i);
//				
//				//only consider coding regions
//				if (ECurrent.Type.equals("CDS")){
//					
//					//check previous, when applicable
//					if (i > 0){
//						
//						//retrieve previous element
//						GenomicElement EPrevious = Elements.get(i-1);
//						
//						//strandedness + distance match
//						if ((ECurrent.IntStrand == 1 && EPrevious.IntStrand == 1) ||
//								(ECurrent.IntStrand == -1 && EPrevious.IntStrand == -1)){
//							if (ECurrent.Start - EPrevious.Stop <= OpD){
//								InAnOperon = true;
//							}
//						}
//						
//					}
//					
//					//check following, when applicable
//					if (i < Elements.size() - 1){
//						
//						//retrieve following element
//						GenomicElement EFollowing = Elements.get(i+1);
//						
//						//strandedness + distance match
//						if ((ECurrent.IntStrand == 1 && EFollowing.IntStrand == 1) ||
//								(ECurrent.IntStrand == -1 && EFollowing.IntStrand == -1)){
//							if (EFollowing.Start - ECurrent.Stop <= OpD){
//								InAnOperon = true;
//							}
//						}
//					}
//					
//					//if the gene is still to be regarded as in an operon, proceed
//					if (InAnOperon){
//						if (!OperonClusters.contains(ECurrent.ClusterID)){
//							OperonClusters.add(ECurrent.ClusterID);
//						}
//					}
//					
//					//also note genes that may or may not be in an operon
//					if (!Clusters.contains(ECurrent.ClusterID)){
//						Clusters.add(ECurrent.ClusterID);
//					}
//					
//					//keep track of every CDS cluster across the whole set
//					if (!AllClusters.contains(ECurrent.ClusterID)){
//						AllClusters.add(ECurrent.ClusterID);
//					}
//
//				}
//				
//			}
//			
//			//store this in hash map
//			//sort
//			Collections.sort(Clusters);
//			
//			//store
//			ClusterIDsinOrgs.put(s, Clusters);
//			OperonClusterIDsinOrgs.put(s, OperonClusters);
//
//		}
//		
//		System.out.println("Determined organism-specific cluster IDs.");
//		
//	}
	
}
