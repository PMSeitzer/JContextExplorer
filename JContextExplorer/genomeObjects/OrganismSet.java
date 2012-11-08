package genomeObjects;

import importExport.DadesExternes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import definicions.MatriuDistancies;

public class OrganismSet {

	//fields
	private LinkedHashMap<String, AnnotatedGenome> Species;		//-Species-information--------
	private LinkedList<String> SpeciesNames;					//-Species-Names--------------
	private LinkedList<ContextSetDescription> CSDs;				//-Info-about-Context-Sets----
	private boolean GeneClustersLoaded = false;					//-Gene-Clusters--------------
	public int LargestCluster = 0;
	private boolean ContinueImportingOperons = true;			
	private LinkedList<String> IncludeTypes;					//-Types of data worth importing/processing
	private LinkedList<String> DisplayOnlyTypes;
	
	// ----------------------- Construction ------------------------//
	 	
	//constructor
	public OrganismSet() {
		super();
	}

	//import species
	public void importSpecies(String SpeciesFiles){
		
		//define a new linked list, for each annotated genome
		LinkedHashMap<String, AnnotatedGenome> Species = new LinkedHashMap<String, AnnotatedGenome>();
		
		//define a new list, for each species name
		LinkedList<String> SpeciesNames = new LinkedList<String>();
					
			try{
				//import buffered reader
				BufferedReader br = new BufferedReader(new FileReader(SpeciesFiles));
				String Line = null;

				while((Line = br.readLine()) != null){
					
						String[] ImportedLine = Line.split("\t");
								
						//create a new AnnotatedGenome
						AnnotatedGenome AG = new AnnotatedGenome();
						
						//Annotation information
						AG.importElements(ImportedLine[0]);
						
						//reference to genome file
						AG.setGenomeFile(new File(ImportedLine[1]));
						
						//Species name
						AG.setSpecies(ImportedLine[2]);
						//System.out.println("Species " + ImportedLine[2] + " Completed.");
						
						//Genus name
						String SpeciesAndGenus[] = ImportedLine[2].split("_");
						AG.setGenus(SpeciesAndGenus[0]);
						
						//add to hash map
						Species.put(ImportedLine[2], AG);
						
						//add name to array of species
						SpeciesNames.add(ImportedLine[2]);
						
						//optional print statement
						//System.out.println("Loaded " + ImportedLine[2] + ".");
						
				}
				br.close();		
				
			}catch(Exception ex){
				System.exit(1);
			}

			//save results to OS structure.
			this.Species = Species;
			this.SpeciesNames = SpeciesNames;	
			
		}

	//determine total number of species
	public int determineNumberOfSpecies(String SpeciesFiles){
		
		//Counters for output scroll bars
		int TotalOrganisms = 0;
		
			try{
				//import buffered reader
				BufferedReader br = new BufferedReader(new FileReader(SpeciesFiles));
				String Line = null;

				while((Line = br.readLine()) != null){
					TotalOrganisms++;
				}
			}catch(Exception ex){
				System.exit(1);
			}
		
			return TotalOrganisms;
	}
	
	//add context set to all species
	public void computeContextSet(String CSName, int tolerance, boolean RequireSameStrain){
		int Counter = 0;
		for (Entry<String, AnnotatedGenome> entry: Species.entrySet()){
			entry.getValue().ComputeContextSet(CSName, tolerance, RequireSameStrain);
			Counter++;
			System.out.println(Counter + "/" + Species.entrySet().size() + " Completed.");
		}
	}

	//import pre-computed cluster information
	public void importClusters(String ClustersFile){
		try {
			
			//First: count lines in the file
			//import buffered reader
			BufferedReader br_count = new BufferedReader( new FileReader(ClustersFile));
			
			//Second: import/process lines in the file
			//import buffered reader
			BufferedReader br = new BufferedReader(new FileReader(ClustersFile));
			String Line = null;
			
			while ((Line = br.readLine()) != null){
				
				//import each line
				String[] ImportedLine = Line.split("\t");
				
				int GeneStart = Integer.parseInt(ImportedLine[2]);
				int GeneStop = Integer.parseInt(ImportedLine[3]);
				int GeneClusterNum = Integer.parseInt(ImportedLine[4]);
				
				//set largest cluster number
				if (LargestCluster < GeneClusterNum){
					LargestCluster = GeneClusterNum;
				}
				
				//add cluster number 
				getSpecies().get(ImportedLine[0])
					.addClusterNumber(ImportedLine[1], GeneStart, GeneStop, GeneClusterNum);

			}
		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//----------------------- Searches --------------------------------//
	
	//display clusters
	public void displayClusterMembers(int ClusterNumber){
		for (AnnotatedGenome AG : getSpecies().values()){
			for (GenomicElement E : AG.getElements()){
				if (E.getClusterID() == ClusterNumber){
					System.out.println(AG.getSpecies() + ": " + E.getAnnotation());
				}
			}
		}
	}
	
	//----------------------- Extended CRON computation ---------------//
	
	//annotation search
	public DadesExternes AnnotationSearch(String[] Queries, String ContextSetName, String DissimilarityMethod, String Name) throws Exception{
			
		ContextSetDescription CurrentCSD = null;
		
		//recover the context set description
		for (ContextSetDescription csd : this.CSDs){
			if (csd.getName().contentEquals(ContextSetName)){
				CurrentCSD = csd;
				break;
			}
		}
		
		//initialize output
		ExtendedCRON EC = new ExtendedCRON();
		
		//set name and type of CRON.
		EC.setName(Name);
		EC.setContextSetName(ContextSetName);
		EC.setSearchType("annotation");
		EC.setQueries(Queries);
		
		//initialize output
		//actual context mapping
		LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList = 
				new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
		
		//species names
		LinkedHashMap<String, String> SourceNames =
				new LinkedHashMap<String, String>();
		
		//contig names
		LinkedHashMap<String, String> ContigNames = 
				new LinkedHashMap<String, String>();
		
		//initialize a counter variable
		int Counter = 0;
		
		//System.out.println("before iteration");
		
		//iterate through species.
		for (Entry<String, AnnotatedGenome> entry : Species.entrySet()) {

			//initialize output
			HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = null;
			
			if (CurrentCSD.isPreprocessed()){
				
				//pre-processed cases
				Matches = entry.getValue().AnnotationMatches(Queries, ContextSetName);

			} else {
				
				//on-the-fly
				if (CurrentCSD.getType().contentEquals("GenesBetween") && Queries.length != 2) {
					JOptionPane.showMessageDialog(null, "This gene grouping requires exactly two search queries.",
							"Inappropriate Number of Queries",JOptionPane.ERROR_MESSAGE);
				} else {
					Matches = entry.getValue().MatchesOnTheFly(Queries, null, CurrentCSD);
				}

			}
			
			//create an iterator for the HashSet
			Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
			 
			//iterate through HashSet, with string-based keys
			int OperonCounter = 0; //reset operon counter
			while(it.hasNext()){
				
				//context unit object
				LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
				
				//increment counters
				OperonCounter++;	
				Counter++;
				
				//define key
				String Key = entry.getKey() + "-" + Integer.toString(OperonCounter);
				
				//put elements into hashmap
				ContextSetList.put(Key, ContextSegment);
				
				//record other info
				SourceNames.put(Key, entry.getValue().getSpecies());
				ContigNames.put(Key, ContextSegment.getFirst().getE().getContig());
			}
			
		}
		
		//add hash map to extended CRON
		EC.setContexts(ContextSetList);
		EC.setNumberOfEntries(Counter);
		
		//add source info
		EC.setSourceSpeciesNames(SourceNames);
		EC.setSourceContigNames(ContigNames);
		
		//System.out.println("ECRons computed + values set");
		
		//compute distances, and format correctly within the ExtendedCRON object.
		EC.computePairwiseDistances(DissimilarityMethod);
		EC.exportDistancesToField();
		
		//System.out.println("OrganismSet().AnnotationSearch() Up to making de");
		
		//initialize DadesExternes
		DadesExternes de = new DadesExternes(EC);
		
		return de;
	}

	//cluster number search
	public DadesExternes ClusterSearch(int[] ClusterNumber, String ContextSetName, String DissimilarityMethod, String Name) throws Exception{
				
		ContextSetDescription CurrentCSD = null;
		
		//recover the context set description
		for (ContextSetDescription csd : this.CSDs){
			if (csd.getName().contentEquals(ContextSetName)){
				CurrentCSD = csd;
				break;
			}
		}
		
		//initialize output
		ExtendedCRON EC = new ExtendedCRON();
		
		//set name and type of CRON.
		EC.setName("Clusters " + Name);
		EC.setContextSetName(ContextSetName);
		EC.setSearchType("cluster");
		EC.setContextType(CurrentCSD.getType());
		EC.setClusterNumbers(ClusterNumber);
		
		//initialize output
		LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList = 
				new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
		
		//species names
		LinkedHashMap<String, String> SourceNames =
				new LinkedHashMap<String, String>();
		
		//contig names
		LinkedHashMap<String, String> ContigNames = 
				new LinkedHashMap<String, String>();
		
		//initialize a counter variable
		int Counter = 0;
		
		//iterate through species.
		for (Entry<String, AnnotatedGenome> entry : Species.entrySet()) {

			HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = null;
			
			if (CurrentCSD.isPreprocessed()){
				
				//pre-processed cases
				Matches = entry.getValue().ClusterMatches(ClusterNumber, ContextSetName);

			} else {
				
				//on-the-fly
				if (CurrentCSD.getType().contentEquals("GenesBetween") && ClusterNumber.length != 2) {
					JOptionPane.showMessageDialog(null, "This gene grouping requires exactly two search queries.",
							"Inappropriate Number of Queries",JOptionPane.ERROR_MESSAGE);
				} else {
					Matches = entry.getValue().MatchesOnTheFly(null, ClusterNumber, CurrentCSD);
				}

			}
			
			//create an iterator for the HashSet
			Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
			
			//iterate through HashSet, with string-based keys
			int OperonCounter = 0; //reset operon counter
			while(it.hasNext()){
				
				//context unit object
				LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
				
				//increment counters
				OperonCounter++;	
				Counter++;
				
				//define key
				String Key = entry.getKey() + "-" + Integer.toString(OperonCounter);
				
				//put elements into hashmap
				ContextSetList.put(Key, ContextSegment);
				
				//record other info
				SourceNames.put(Key, entry.getValue().getSpecies());
				ContigNames.put(Key, ContextSegment.getFirst().getE().getContig());
			}
			
		}
		
		//add hash map to extended CRON
		EC.setContexts(ContextSetList);
		EC.setNumberOfEntries(Counter);
		
		//add source info
		EC.setSourceSpeciesNames(SourceNames);
		EC.setSourceContigNames(ContigNames);
		
		//System.out.println("ECRons computed + values set");
		
		//compute distances, and format correctly within the ExtendedCRON object.
		EC.computePairwiseDistances(DissimilarityMethod);
		EC.exportDistancesToField();
		
		//System.out.println("OrganismSet().ClusterSearch() Up to making de");
		
		//initialize DadesExternes
		DadesExternes de = new DadesExternes(EC);
		
		return de;
	}

	// ----- Getters and Setters --------------------------------------------------//
	
	public LinkedHashMap<String, AnnotatedGenome> getSpecies() {
		return Species;
	}

	public void setSpecies(LinkedHashMap<String, AnnotatedGenome> species) {
		Species = species;
	}

	public LinkedList<String> getSpeciesNames() {
		return SpeciesNames;
	}

	public void setSpeciesNames(LinkedList<String> speciesNames) {
		SpeciesNames = speciesNames;
	}
	
	public LinkedList<ContextSetDescription> getCSDs() {
		return CSDs;
	}

	public void setCSDs(LinkedList<ContextSetDescription> cSDs) {
		CSDs = cSDs;
	}
	
	public boolean isGeneClustersLoaded() {
		return GeneClustersLoaded;
	}

	public void setGeneClustersLoaded(boolean geneClustersLoaded) {
		GeneClustersLoaded = geneClustersLoaded;
	}

	public boolean isContinueImportingOperons() {
		return ContinueImportingOperons;
	}

	public void setContinueImportingOperons(boolean continueImportingOperons) {
		ContinueImportingOperons = continueImportingOperons;
	}

	public LinkedList<String> getIncludeTypes() {
		return IncludeTypes;
	}

	public void setIncludeTypes(LinkedList<String> includeTypes) {
		IncludeTypes = includeTypes;
	}

	public LinkedList<String> getDisplayOnlyTypes() {
		return DisplayOnlyTypes;
	}

	public void setDisplayOnlyTypes(LinkedList<String> displayOnlyTypes) {
		DisplayOnlyTypes = displayOnlyTypes;
	}

} //completes classbody