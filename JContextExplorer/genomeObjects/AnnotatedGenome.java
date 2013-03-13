package genomeObjects;

import java.io.*;
import java.text.Collator;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.Strand;
import org.biojava3.core.sequence.io.FastaReaderHelper;

public class AnnotatedGenome {
	
	//Fields 
    private String Genus;               						//-Biological-organization-------------
    private String Species;             						//									
    private LinkedList<GenomicElement> Elements;		 		//-Genes, SigSeqs, and groups of genes--
    private LinkedList<MotifGroup> Motifs 						//
    	= new LinkedList<MotifGroup>();						    //
    private LinkedList<ContextSet> Groupings = new LinkedList<ContextSet>();					//-Predicted Groupings-----------------
    private File GenomeFile; 									//-Associated genome file --------------
    private boolean TryToComputeOperons;
	private LinkedList<String> IncludeTypes;					//-Types of data worth importing/processing
	private LinkedList<String> DisplayOnlyTypes;
	
// ----------------------- Construction ------------------------//
      
//Constructor
public AnnotatedGenome() {
	super();
	}

//import annotated elements from a .GFF file.
public void importElements(String filename){
	
	//define a null linked list
	LinkedList<GenomicElement> Elements = new LinkedList<GenomicElement>();
	
		try{
			//import buffered reader
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String Line = null;
			int Counter = 0;
			
			while((Line = br.readLine()) != null){
				
					//increment Counter
					Counter++;
					
					//import each line of the .gff file
					String ImportedLine[] = Line.split("\t");
					
					//GFF files must contain exactly at least 9 fields
					if (ImportedLine.length < 9){
						throw new Exception();
					}
					
					//check and see if this element should be retained at all
					//check include types
					boolean RetainElement = false;
					for (String s : this.IncludeTypes){
						if (ImportedLine[2].trim().contentEquals(s)){
							RetainElement = true;
							break;
						}
					}
					//if this fails, check for display types
					if (!RetainElement){
						for (String s : this.DisplayOnlyTypes){
							if (ImportedLine[2].trim().contentEquals(s)){
								RetainElement = true;
								break;
							}
						}
					}
					
					//add this element to the list, if necessary
					if (RetainElement){
						
						//if a line or two are not formatted correctly, just ignore these lines.
						try {
							
							//create a new element
							GenomicElement E = new GenomicElement();
							
							//set appropriate fields of this genomic element with inputs achieved from the GFF file
							E.setContig(ImportedLine[0]);
							E.setType(ImportedLine[2]);
							E.setStart(Integer.parseInt(ImportedLine[3]));
							E.setStop(Integer.parseInt(ImportedLine[4]));
							E.setElementID(Counter);
							
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
								E.setClusterID(Integer.parseInt(ImportedLine[9]));
								//System.out.println("Set!");
								if (ImportedLine.length > 10){
									E.setGeneID(Integer.parseInt(ImportedLine[10]));
								}
							}
							
							//add to list
							Elements.add(E);
							
						} catch (Exception ex) {}

					}
			}
			br.close();		
			
		}catch(Exception ex){
			//System.out.println("fail!");
			//System.exit(1);
		}
		
		//sort elements
		Collections.sort(Elements, new GenomicElementComparator());
		
		//set elements to the newly parsed elements.
		this.Elements = Elements;

	}

//----------------------- add cluster number -----------------------//

//Organism - Gene Name - Cluster Number [OR] Gene Name - Cluster Number [OR] Gene Name
public void addClusterNumber(String Annotation, int Clusternumber){
	for (GenomicElement E : Elements){
		if (E.getAnnotation().toUpperCase().contains(Annotation.toUpperCase().trim())){
			E.setClusterID(Clusternumber);
		}
	}
}

//Organism - Contig - Gene Name - Cluster Number
public void addClusterNumber(String Contig, String Annotation, int Clusternumber){
	for (GenomicElement E : Elements){
		if (E.getContig().contentEquals(Contig) &&
				E.getAnnotation().toUpperCase().contains(Annotation.toUpperCase().trim())){
			E.setClusterID(Clusternumber);
		}
	}
}

//Organism - Contig - Gene Start - Gene Stop - Cluster Number
public void addClusterNumber(String Contig, int Start, int Stop, int Clusternumber){
	for (GenomicElement E : Elements){
		if (E.getContig().contentEquals(Contig) &&
				E.getStart() == Start &&
				E.getStop() == Stop){
			E.setClusterID(Clusternumber);
			break;
		}
	}
}

//----------------------- Context Set computation ------------------//

//single gene context set
public void MakeSingleGeneContextSet(String CSName){
	
	//initialize a new context set
	ContextSet CS = new ContextSet(CSName, "SingleGene");
	CS.setPreProcessed(true);
	HashMap<Integer, LinkedList<GenomicElement>> csmap 
	= new HashMap<Integer, LinkedList<GenomicElement>>();
	
	//iterate through all elements, add each to single-gene context set
	int Counter = 0;
	for (GenomicElement E : this.Elements){
		Counter++;
		LinkedList<GenomicElement> L = new LinkedList<GenomicElement>();
		L.add(E);
		csmap.put(Counter, L);
	}
	
	//add completed hash map to context set object
	CS.setContextMapping(csmap);
	
	//add this new context set to the Groupings field.
	if (Groupings == null){
		Groupings = new LinkedList<ContextSet>();
	} 
	this.Groupings.add(CS);

}

//estimate contexts based on distance
public void ComputeContextSet(String CSName, int tolerance, boolean RequireSameStrain){
	
	//initialize a new context set
	ContextSet CS = new ContextSet(CSName, "IntergenicDist");
	CS.setPreProcessed(true);
	HashMap<Integer, LinkedList<GenomicElement>> csmap 
		= new HashMap<Integer, LinkedList<GenomicElement>>();
	
	// start counter, initialize each operon (as a LL).
	int OperonCounter = 1;
	LinkedList<GenomicElement> LL = new LinkedList<GenomicElement>();
	
	//examine elements, and put into operons
	//this method assumes that the elements are in order
	for (int i=0; i < Elements.size()-1; i++){

		//check against user-defined set of valid types
		boolean ElementIsValid = false;
		for (String s : this.IncludeTypes){
			if (Elements.get(i).getType().contentEquals(s)){
				ElementIsValid = true;
				break;
			}
		}
		
		//require valid type
//		if (Elements.get(i).getType().contentEquals("CDS") ||
//				Elements.get(i).getType().contentEquals("tRNA") ||
//				Elements.get(i).getType().contentEquals("rRNA")){		
		
		if (ElementIsValid){
			
			//if the element is valid, place into an operon.
			//Comment: technically, a pointer to the element
			LL.add(Elements.get(i));
			
			//find the next valid type in the list
			boolean validType = false;
			int NextValid = i+1;
			
			//discover the next valid element in the Elements field.
			while(validType == false){
				
				//determine if next element is valid (should be included)
				boolean NextElementIsValid = false;
				for (String s : this.IncludeTypes){
					if (Elements.get(NextValid).getType().contentEquals(s)){
						NextElementIsValid = true;
						break;
					}
				}

				//case: next element is valid
				if (NextElementIsValid){		
					validType = true;
				}else if (NextValid < Elements.size()-1) { // case: next element is not valid, look further in file
					NextValid++;
				}
				else { //case: there are no more valid elements in the file
					NextValid = -1;
					validType = true;
				}
			}
		
			//Assuming that there are valid elements to compare against,
			if (NextValid != -1){
			
			//next element is in a new operon if any of the following are true: 
			//(1) different strand, (2) different contig, (3) too far away from current element
				
				boolean newOperon = false;
				
				//Comparison blocks - may or may not require the same strain
				if (RequireSameStrain == true) {
				
					if (Elements.get(i).getStrand() == Strand.POSITIVE){
			
						if ((Elements.get(NextValid).getStrand() == Strand.NEGATIVE) ||
								(Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())==false) || 
								(Elements.get(NextValid).getStrand() == Strand.POSITIVE && 
								Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())
								&& Elements.get(NextValid).getStart()-Elements.get(i).getStop() > tolerance))
						{
							newOperon = true;
						}
			
					} else {
			
						if ((Elements.get(NextValid).getStrand() == Strand.POSITIVE) || 
								(Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())==false) ||
								(Elements.get(NextValid).getStrand() == Strand.NEGATIVE 
								&& Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())
								&& Elements.get(NextValid).getStart()-Elements.get(i).getStop() > tolerance))
						{
							newOperon = true;
						}
			
					}
				
				} else {
					
						//Only compare contig names and distance, when not considering strain.
						if ((Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())==false) || 
								(Elements.get(i).getContig().contentEquals(Elements.get(NextValid).getContig())
								&& Elements.get(NextValid).getStart()-Elements.get(i).getStop() > tolerance))
						{
							newOperon = true;
						}
					
				}
				
				//if the next valid element defines a new operon:
				// store the old operon, reset the LL, increment the operon counter.
				if (newOperon == true){

					 csmap.put(OperonCounter, LL);
					 LL = new LinkedList<GenomicElement>();
					 OperonCounter++;
				} 
			
			//Last element in the file
			} else {
				
				//place element into an operon, and store the operon in the hash map.
				LL.add(Elements.get(i));
				csmap.put(OperonCounter,LL);
				
			}
		}
	}	
	
	//add completed hash map
	CS.setContextMapping(csmap);
	
	//add this new context set to the Groupings field.
	if (Groupings == null){
		Groupings = new LinkedList<ContextSet>();
	} 
	this.Groupings.add(CS);
}

//add pre-computed contexts from file
public void ImportContextSet(String CSName, String fileName) {

	this.TryToComputeOperons = true;
	
	try{
		//import buffered reader
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String Line = null;
		
		//initialize a new context set
		ContextSet CS = new ContextSet(CSName, "Loaded");
		CS.setPreProcessed(true);
		LinkedHashMap<Integer, LinkedList<GenomicElement>> CSMap 
			= new LinkedHashMap<Integer, LinkedList<GenomicElement>>();
		
		while((Line = br.readLine()) != null){
			
			//import line
			String ImportedLine[] = Line.split("\t");
			
			//if the ID is 0, then skip this entry entirely and move on the next one.
			int Key = Integer.parseInt(ImportedLine[3]);
			if (Key != 0){
			
				//create new list, if it doesn't already exist
				if (CSMap.get(Key) == null){
					CSMap.put(Key, new LinkedList<GenomicElement>());
				}
				
				//search through genomes to find the correct element, add to list
				for (GenomicElement e : this.Elements){
					if (e.getContig().equals(ImportedLine[0]) &&
							e.getStart() == Integer.parseInt(ImportedLine[1]) &&
							e.getStop() == Integer.parseInt(ImportedLine[2])){
						CSMap.get(Key).add(e);
						break;
					}
				}
			
			}
		}
		
		//add completed mapping to context set
		CS.setContextMapping(CSMap);
		
		//add this context set to existing context sets.
		if (this.Groupings == null){
			Groupings = new LinkedList<ContextSet>();
		}
		Groupings.add(CS);
		
	} catch  (Exception ex) {
		this.TryToComputeOperons = false;
		String Message = "The Genome Context File " + "\n" +
				fileName + "\n" +
				"was improperly formatted. Please re-format this file and try again.";
		JOptionPane.showMessageDialog(null, Message, "Invalid File Format", JOptionPane.ERROR_MESSAGE);
	}
	
	}

//----------------------- Sorting ------------------------//

//sort genomic elements by (1) contig name, and within contigs, (2) start position.
public class GenomicElementComparator implements Comparator<GenomicElement> {

	  public int compare(GenomicElement E1, GenomicElement E2) {
	     int nameCompare = E1.getContig().compareToIgnoreCase(E2.getContig());
	     if (nameCompare != 0) {
	        return nameCompare;
	     } else {
	       return Integer.valueOf(E1.getStart()).compareTo(Integer.valueOf(E2.getStart()));
	     }
	  }
	}

// ----------------------- Export ----------------------------------//

// this function simply returns a DNA sequence from a particular genome file.
public String retrieveSequence(String contig, int start, int stop, Strand strand){
	
	//initialize and instantiate variable
	String seq=null;
	
	//load genome, and recover sequence
	LinkedHashMap<String, DNASequence> genome;
	try {
		
		//import genome
		genome = FastaReaderHelper.readFastaDNASequence(GenomeFile);
		
		//retrieve string value + extract subsequence
		for (Entry<String, DNASequence> entry : genome.entrySet()) {
			if (entry.getValue().getOriginalHeader().contains(contig)){
				seq = entry.getValue().getSequenceAsString(start, stop, strand);
				break;
			}
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return seq;
}

public void ExportExtendedGFFFile(String FileName){
	
	try {
		//filewriter
		BufferedWriter bw = new BufferedWriter(new FileWriter(FileName));
		String Line;
		String TheStrand;
		for (GenomicElement E : this.Elements){
			if (E.getStrand().equals(Strand.POSITIVE)){
				TheStrand = "1";
			} else {
				TheStrand = "-1";
			}
				
			Line = E.getContig() + "\tGenBank\t" + E.getType() 
					+ "\t" + E.getStart() + "\t" + E.getStop() + "\t+\t"
					+ TheStrand + "\t.\t" + E.getAnnotation() + "\t" + E.getClusterID() + "\n";
			
			bw.write(Line);
			bw.flush();
		}
		bw.close();
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

//----------------------- Search/Retrieval ------------------------//

//preprocessed == true
//return a hashset of gene groupings - annotation
public HashSet<LinkedList<GenomicElementAndQueryMatch>> AnnotationMatches(String[] query, String ContextSetName){
	
	//initialize
	ContextSet CS = new ContextSet();
	
	//determine the correct context set, and make a copy
	for (ContextSet selectCS : Groupings){
		if (selectCS.getName().equals(ContextSetName)){
			CS = selectCS;
			break;
		}
	}
	
	//System.out.println(this.Species + " " + CS.getName());
	
	//create a tree set to contain individual element matches
	HashSet<LinkedList<GenomicElementAndQueryMatch>> Hits = 
			new HashSet<LinkedList<GenomicElementAndQueryMatch>>();

	boolean AddtheSet;
	
	//determine all matches
	for (LinkedList<GenomicElement> LL: CS.getContextMapping().values()){
		
		//default: do not add the set
		AddtheSet = false;
		
		//initialize the list
		LinkedList<GenomicElementAndQueryMatch> TheList = new LinkedList<GenomicElementAndQueryMatch>();		
		
		//search for all direct matches, and mark them
		for (int i = 0; i < LL.size(); i++){
			
			//initialize a new GenomicElementAndQueryMatch
			GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
			GandE.setE(LL.get(i));
			
			//check each query
			for (int j = 0; j < query.length; j++){
			
				if (LL.get(i).getAnnotation().toUpperCase().contains(query[j].trim().toUpperCase())){
					AddtheSet = true;
					GandE.setQueryMatch(true);
					//Hits.add(LL);
				} else {
					GandE.setQueryMatch(false);
				}
			
			}
			
			//add this element to the list
			TheList.add(GandE);
		}
		
		//if even one match was discovered in an LL, add the whole LL.
		if (AddtheSet == true){
			Hits.add(TheList);
		}
		
	}

	//return HashSet
	return Hits;
}

//return a hashset of gene groupings - homology cluster
public HashSet<LinkedList<GenomicElementAndQueryMatch>> ClusterMatches(int[] ClusterNumber, String ContextSetName){
	
	//initialize
	ContextSet CS = new ContextSet();
	
	//determine the correct context set
	for (ContextSet selectCS : Groupings){
		if (selectCS.getName().equals(ContextSetName)){
			CS = selectCS;
			break;
		}
	}
	
	//create a tree set to contain individual element matches
	HashSet<LinkedList<GenomicElementAndQueryMatch>> Hits = 
			new HashSet<LinkedList<GenomicElementAndQueryMatch>>();
	
	boolean AddtheSet;
	
	//determine all matches
	for (LinkedList<GenomicElement> LL: CS.getContextMapping().values()){
		
		//initialize the list
		LinkedList<GenomicElementAndQueryMatch> TheList = new LinkedList<GenomicElementAndQueryMatch>();	
		
		//reset value to false
		AddtheSet = false;
		
		//search for all direct matches, and mark them
		for (int i = 0; i < LL.size(); i++){
			
			//initialize a new GenomicElementAndQueryMatch
			GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
			GandE.setE(LL.get(i));
			
			//defaults: do not take
			GandE.setQueryMatch(false);
			
			//check every cluster number, for query match
			for (int j = 0; j < ClusterNumber.length; j++){
			
				if (LL.get(i).getClusterID()==ClusterNumber[j]){
					AddtheSet = true;
					GandE.setQueryMatch(true);	
				}
			}
			
			//add this element to the list
			TheList.add(GandE);
			
		}
	
		//if even one match was discovered in an LL, add the whole LL.
		if (AddtheSet == true){
			Hits.add(TheList);
			//System.out.println("added a new set with " + TheList.size() + " genes, from " + LL.size());
			
		}
	}

	//return HashSet
	return Hits;
}

//preprocessed == false
//return a hashset of gene groupings
public HashSet<LinkedList<GenomicElementAndQueryMatch>> MatchesOnTheFly(String[] Queries,
		int[] ClusterNumbers, 
		ContextSetDescription CSD){
	
	//create a tree set to contain individual element matches
	HashSet<LinkedList<GenomicElementAndQueryMatch>> Hits = 
			new HashSet<LinkedList<GenomicElementAndQueryMatch>>();
	
	//determine appropriate form of searches
	boolean IsCluster = false;
	if (Queries == null){
		IsCluster = true;
	} 
	
	//find query match
	boolean QueryMatch = false;

	//group genes together according to the specificed gene grouping protocol.
	if (CSD.getType().contentEquals("Range")) {
		
		//iterate through all elements
		for (int i = 0; i <this.Elements.size(); i++){
				
			//determine if the element is a query match.
			QueryMatch = false;
			if (IsCluster){
				for (int j = 0; j < ClusterNumbers.length; j++){
					if (this.Elements.get(i).getClusterID() == ClusterNumbers[j]){
						QueryMatch = true;
						break;
					}
				}
			} else {
				for (int j = 0; j < Queries.length; j++){
					if (this.Elements.get(i).getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						QueryMatch = true;
						break;
					}
				}
			}
		
			//if it is, extract the appropriate range
			if (QueryMatch){
				
				//define a new GenomicElementAndQueryMatch
				LinkedList<GenomicElementAndQueryMatch> LL = new LinkedList<GenomicElementAndQueryMatch>();
				GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
				GandE.setE(this.Elements.get(i)); GandE.setQueryMatch(true); LL.add(GandE);
				int Center = (int)Math.round(0.5*(double)(GandE.getE().getStart()+GandE.getE().getStop()));
				
				//continue adding genes until sufficient
				//before genes
				int BeforeQuery = Center - this.Elements.get(i).getStart(); 
				int BeforeCounter = 0;
				boolean EndOfContig = false;
				String CurrentContig = this.Elements.get(i).getContig();
				while (BeforeQuery < CSD.getNtRangeBefore() && EndOfContig == false){
					BeforeCounter++;
					GandE = new GenomicElementAndQueryMatch();
							
					//first element in file
					if (i-BeforeCounter >= 0) {
							
					GandE.setE(this.Elements.get(i-BeforeCounter));
					GandE.setQueryMatch(false);
					BeforeQuery = Center - GandE.getE().getStart();
					
					//check against user-defined set of valid types
					boolean ElementIsValid = false;
					for (String s : this.IncludeTypes){
						if (GandE.getE().getType().contentEquals(s)){
							ElementIsValid = true;
							break;
						}
					}
						
					if (ElementIsValid){
						
						//check for end of contig
						if (CurrentContig.equals(GandE.getE().getContig())){
							LL.add(0,GandE);
						} else {
							EndOfContig = true;
						}
							
						} else {
							EndOfContig = true;
						}
					} else {
						EndOfContig = true;
					}

				}
						
				//after genes
				int AfterQuery = this.Elements.get(i).getStop() - Center; 
				int AfterCounter = 0;
				EndOfContig = false;
				CurrentContig = this.Elements.get(i).getContig();
				while (AfterQuery < CSD.getNtRangeAfter() && EndOfContig == false){
					AfterCounter++;
					GandE = new GenomicElementAndQueryMatch();
							
					//last element in file
					if (i+AfterCounter < this.Elements.size()){
							
					GandE.setE(this.Elements.get(i+AfterCounter));
					GandE.setQueryMatch(false);
					AfterQuery = GandE.getE().getStop() - Center;
					
					//check against user-defined set of valid types
					boolean ElementIsValid = false;
					for (String s : this.IncludeTypes){
						if (GandE.getE().getType().contentEquals(s)){
							ElementIsValid = true;
							break;
						}
					}
					
					if (ElementIsValid){	
						
						//check for end of contig
						if (CurrentContig.equals(GandE.getE().getContig())){
							LL.add(GandE);
						} else {
							EndOfContig = true;
						}
							
						} else {
							EndOfContig = true;
						}

					} else {
						EndOfContig = true;
					}
				}

				//finally, add this to the hit list
				Hits.add(LL);
						
			}
		}
		
	} else if (CSD.getType().contentEquals("GenesAround")) {

		//iterate through all elements
		for (int i = 0; i <this.Elements.size(); i++){
				
			//determine if the element is a query match.
			QueryMatch = false;
			if (IsCluster){
				for (int j = 0; j < ClusterNumbers.length; j++){
					if (this.Elements.get(i).getClusterID() == ClusterNumbers[j]){
						QueryMatch = true;
						break;
					}
				}
			} else {
				for (int j = 0; j < Queries.length; j++){
					if (this.Elements.get(i).getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						QueryMatch = true;
						break;
					}
				}
			}
		
			//if it is, extract the appropriate range
			if (QueryMatch){
					
			//define a new GenomicElementAndQueryMatch
			LinkedList<GenomicElementAndQueryMatch> LL = new LinkedList<GenomicElementAndQueryMatch>();
			GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
			GandE.setE(this.Elements.get(i)); GandE.setQueryMatch(true); LL.add(GandE);
					
			//continue adding genes until sufficient
			//before genes
			int BeforeCounter = 0;
			boolean EndOfContig = false;
			String CurrentContig = this.Elements.get(i).getContig();
			while (BeforeCounter < CSD.getGenesBefore() && EndOfContig == false){
				BeforeCounter++;
				GandE = new GenomicElementAndQueryMatch();
						
				//first element in file
				if (i-BeforeCounter > 0) {
						
				GandE.setE(this.Elements.get(i-BeforeCounter));
				GandE.setQueryMatch(false);
				
				//check against user-defined set of valid types
				boolean ElementIsValid = false;
				for (String s : this.IncludeTypes){
					if (GandE.getE().getType().contentEquals(s)){
						ElementIsValid = true;
						break;
					}
				}
				
				//only add elements of the appropriate type - otherwise, skip
				if (ElementIsValid){
					
					//check for end of contig
					if (CurrentContig.equals(GandE.getE().getContig())){
						LL.add(GandE);
					} else {
						EndOfContig = true;
					}
						
					} else {
						EndOfContig = true;
					}

				}

			}
					
			//after genes
			int AfterCounter = 0;
			EndOfContig = false;
			CurrentContig = this.Elements.get(i).getContig();
			while (AfterCounter < CSD.getGenesAfter() && EndOfContig == false){
				AfterCounter++;
				GandE = new GenomicElementAndQueryMatch();
						
				//last element in file
				if (i+AfterCounter < this.Elements.size()){
						
				GandE.setE(this.Elements.get(i+AfterCounter));
				GandE.setQueryMatch(false);
						
				//check against user-defined set of valid types
				boolean ElementIsValid = false;
				for (String s : this.IncludeTypes){
					if (GandE.getE().getType().contentEquals(s)){
						ElementIsValid = true;
						break;
					}
				}
				
				//only add elements of the appropriate type - otherwise, skip
				if (ElementIsValid){
					
					//check for end of contig
					if (CurrentContig.equals(GandE.getE().getContig())){
						LL.add(GandE);
					} else {
						EndOfContig = true;
					}
						
					} else {
						EndOfContig = true;
					}

				}

			}
					
			//finally, add this to the hit list
			Hits.add(LL);
					
			}
		}
		
	} else if (CSD.getType().contentEquals("GenesBetween")) {
		
		LinkedList<GenomicElement> FirstQueries = new LinkedList<GenomicElement>();
		LinkedList<GenomicElement> SecondQueries = new LinkedList<GenomicElement>();
		
		//iterate through all elements, find first + second queries
		for (int i = 0; i <this.Elements.size(); i++){
			
			//determine if the element is a query match.
			QueryMatch = false;
			if (IsCluster){
				for (int j = 0; j <ClusterNumbers.length; j++){
					if (this.Elements.get(i).getClusterID() == ClusterNumbers[j]){
						if (j == 0){
							FirstQueries.add(Elements.get(i));
						} else {
							SecondQueries.add(Elements.get(i));
						}

					}
				}

			} else {
				for (int j = 0; j < Queries.length; j++){
					if (this.Elements.get(i).getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						if (j ==0 ){
							FirstQueries.add(Elements.get(i));
						} else {
							SecondQueries.add(Elements.get(i));
						}
					}
				}
			}
		}
		
		//pairings of genomic element query matches
		HashSet<LinkedList<GenomicElement>> Pairs = 
				new HashSet<LinkedList<GenomicElement>>();
		
		//find first set matches
		int ClosestDistance = 999999999;
		GenomicElement Partner = null;
		for (GenomicElement E1 : FirstQueries){
			//reset values
			Partner = null;
			ClosestDistance = 999999999;
			
			//find closest
			for (GenomicElement E2 : SecondQueries){
				if (E1.getContig().contentEquals(E2.getContig()) &&
						Math.abs(E1.getStart() - E2.getStart()) < ClosestDistance) {
					ClosestDistance = E1.getStart() - E2.getStart();
					Partner = E2;
				}
			}
			
			//there must be a partner for this to even matter.
			if (Partner != null){
				
				//add to hash set
				LinkedList<GenomicElement> Partnership = new LinkedList<GenomicElement>();
				Partnership.add(E1); Partnership.add(Partner);
				Pairs.add(Partnership);
				
			}
		}
		
		//find second set matches
		ClosestDistance = 999999999;
		Partner = null;
		for (GenomicElement E2 : SecondQueries){
			//reset values
			Partner = null;
			ClosestDistance = 999999999;
			
			//find closest
			for (GenomicElement E1 : FirstQueries){
				if (E2.getContig().contentEquals(E1.getContig()) &&
						Math.abs(E2.getStart() - E1.getStart()) < ClosestDistance) {
					ClosestDistance = E2.getStart() - E1.getStart();
					Partner = E1;
				}
			}
			
			//there must be a partner for this to even matter.
			if (Partner != null){
				
				//add to hash set
				LinkedList<GenomicElement> Partnership = new LinkedList<GenomicElement>();
				Partnership.add(Partner); Partnership.add(E2); 
				Pairs.add(Partnership);
				
			}
		}
		
		//for all pairs, add all genomic elements
		Iterator<LinkedList<GenomicElement>> it = Pairs.iterator();
		while(it.hasNext()){
			LinkedList<GenomicElement> Pair = it.next();
			
			//find starting /ending points
			int StartingE = -1; int StoppingE = -1;
			for (int i = 0; i < Elements.size(); i++){
				if (this.Elements.get(i).equals(Pair.get(0))){
					StartingE = i;
				} 
				if (this.Elements.get(i).equals(Pair.get(1))){
					StoppingE = i;
				}
			}
			
			//initialize an output linked list
			LinkedList<GenomicElementAndQueryMatch> LL = new LinkedList<GenomicElementAndQueryMatch>();
			
			//re-order correctly
			if (StartingE > StoppingE){
				int temp = StartingE;
				StartingE = StoppingE;
				StoppingE = temp;
			}
			
			//add all intermediate elements
			GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
			GandE.setE(this.Elements.get(StartingE)); GandE.setQueryMatch(true); LL.add(GandE);
			int ElementNumber = StartingE + 1;
			while (ElementNumber < StoppingE){
				GandE = new GenomicElementAndQueryMatch();
				GandE.setE(Elements.get(ElementNumber)); GandE.setQueryMatch(false);
				
				//check against user-defined set of valid types
				boolean ElementIsValid = false;
				for (String s : this.IncludeTypes){
					if (GandE.getE().getType().contentEquals(s)){
						ElementIsValid = true;
						break;
					}
				}
				
				//only add elements of the appropriate type - otherwise, skip
				if (ElementIsValid){
	
					LL.add(GandE);
				}

				ElementNumber++;
			}
			GandE = new GenomicElementAndQueryMatch();
			GandE.setE(this.Elements.get(StoppingE)); GandE.setQueryMatch(true); LL.add(GandE);
			
			//add this list to the hash map.
			Hits.add(LL);
		}

	
	} else if (CSD.getType().contentEquals("MultipleQuery")) {
	
		//all genomic element matches
		LinkedList<GenomicElementAndQueryMatch> MQMatches = new LinkedList<GenomicElementAndQueryMatch>();
		
		//iterate through all elements, find all matches
		for (GenomicElement E : Elements){
			
			//determine if the element is a query match.
			if (IsCluster){
				for (int j = 0; j <ClusterNumbers.length; j++){
					if (E.getClusterID() == ClusterNumbers[j]){
						GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
						GandE.setE(E); 
						GandE.setQueryMatch(true);
						
						
						//check against user-defined set of valid types
						boolean ElementIsValid = false;
						for (String s : this.IncludeTypes){
							if (GandE.getE().getType().contentEquals(s)){
								ElementIsValid = true;
								break;
							}
						}
						
						if (ElementIsValid){
							MQMatches.add(GandE);
						}
					}
				}

			} else {
				for (int j = 0; j < Queries.length; j++){
					if (E.getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
						GandE.setE(E); 
						GandE.setQueryMatch(true);
						
						//check against user-defined set of valid types
						boolean ElementIsValid = false;
						for (String s : this.IncludeTypes){
							if (GandE.getE().getType().contentEquals(s)){
								ElementIsValid = true;
								break;
							}
						}
						
						if (ElementIsValid){
							MQMatches.add(GandE);
						}
					}
				}
			}
		}
		
		//add all non-null linked lists
		if (MQMatches != null){
			Hits.add(MQMatches);
		}
	
//		//old way - split by contig
//		//all genomic element matches
//		LinkedList<GenomicElement> ElementMatches = new LinkedList<GenomicElement>();
//		
//		//all contigs featured in all matches
//		HashSet<String> ContigNames = new HashSet<String>();
//		
//		//iterate through all elements, find all matches
//		for (GenomicElement E : Elements){
//			
//			//determine if the element is a query match.
//			QueryMatch = false;
//			if (IsCluster){
//				for (int j = 0; j <ClusterNumbers.length; j++){
//					if (E.getClusterID() == ClusterNumbers[j]){
//						ElementMatches.add(E);
//						ContigNames.add(E.getContig());
//					}
//				}
//
//			} else {
//				for (int j = 0; j < Queries.length; j++){
//					if (E.getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
//						ElementMatches.add(E);
//						ContigNames.add(E.getContig());
//					}
//				}
//			}
//		}
//		
//		//create an iterator for the HashSet
//		Iterator<String> it = ContigNames.iterator();
//		
//		//each contig receives it's own linked list
//		while(it.hasNext()){
//			
//			//retrieve the contig
//			String Contig = it.next();
//			
//			//find all genomic elements with this contig
//			LinkedList<GenomicElementAndQueryMatch> LL = new LinkedList<GenomicElementAndQueryMatch>();
//			
//			for (GenomicElement E : ElementMatches){
//				if (E.getContig().contentEquals(Contig)){
//					GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
//					GandE.setE(E); GandE.setQueryMatch(true);
//					
//					//check against user-defined set of valid types
//					boolean ElementIsValid = false;
//					for (String s : this.IncludeTypes){
//						if (GandE.getE().getType().contentEquals(s)){
//							ElementIsValid = true;
//							break;
//						}
//					}
//					
//					//only add elements of the appropriate type - otherwise, skip
//					if (ElementIsValid){
//		
//						LL.add(GandE);
//					}
//					
//				}
//			}
//			
//			//add all non-null linked lists
//			if (LL != null){
//				Hits.add(LL);
//			}
//			
//		}
		
	} else if (CSD.getType().contentEquals("Combination")) {
		

		
	} // various gene grouping strategies

	return Hits;
}

//----------------------- GETTERS+SETTERS ------------------------//

//----------------------- Getters and Setters ----------------------//


//Getters and Setters
 public String getGenus() {
	return Genus;
}
public void setGenus(String genus) {
	Genus = genus;
}
public String getSpecies() {
	return Species;
}
public void setSpecies(String species) {
	Species = species;
}
public LinkedList<GenomicElement> getElements() {
	return Elements;
}
public void setElements(LinkedList<GenomicElement> elements) {
	Elements = elements;
}

public File getGenomeFile() {
	return GenomeFile;
}
public void setGenomeFile(File genomeFile) {
	GenomeFile = genomeFile;
}

public LinkedList<ContextSet> getGroupings() {
	if (Groupings == null){
		Groupings = new LinkedList<ContextSet>();
	}
	return Groupings;
}

public void setGroupings(LinkedList<ContextSet> groupings) {
	Groupings = groupings;
}

public boolean isTryToComputeOperons() {
	return TryToComputeOperons;
}

public void setTryToComputeOperons(boolean tryToComputeOperons) {
	TryToComputeOperons = tryToComputeOperons;
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

public LinkedList<MotifGroup> getMotifs() {
	return Motifs;
}

public void setMotifs(LinkedList<MotifGroup> motifs) {
	Motifs = motifs;
}

//-----------------------Deprecated ----------------------//

} //completes classbody
