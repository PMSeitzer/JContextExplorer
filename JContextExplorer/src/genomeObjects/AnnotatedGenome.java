package genomeObjects;

import haloGUI.GBKFieldMapping;

import java.io.*;
import java.text.Collator;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.Strand;
import org.biojava3.core.sequence.io.FastaReaderHelper;

public class AnnotatedGenome implements Serializable {
	
	//Fields 
    private String Genus;               						//-Biological-organization-------------
    private String Species;             						//									
    private LinkedList<GenomicElement> Elements;		 		//-Genes, SigSeqs, and groups of genes--
    private LinkedList<MotifGroup> Motifs 						//
    	= new LinkedList<MotifGroup>();						    //
    private LinkedList<ContextSet> Groupings = new LinkedList<ContextSet>();					//-Predicted Groupings-----------------
    private File GenomeFile; 									//-Associated genome file --------------
    private boolean TryToComputeOperons;
	private LinkedList<String> FeatureIncludeTypes;					//-Types of data worth importing/processing
	private LinkedList<String> FeatureDisplayTypes;
	private boolean AGClustersLoaded = false;
	private String TextDescription = "";								//-Info about the genome
	private String GenbankID;
	private GBKFieldMapping GFM;
	private LinkedHashMap<String, Integer> ContigEnds
		= new LinkedHashMap<String, Integer>();
	
// ----------------------- Construction ------------------------//
      
//Constructor
public AnnotatedGenome() {
	super();
	}

//import annotated elements from a .GFF file.
public void importFromGFFFile(String filename){
	
	//define a null linked list
	LinkedList<GenomicElement> Elements = new LinkedList<GenomicElement>();
	
		try{
			//import buffered reader
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String Line = null;
			int Counter = 0;
			
			//Information for statistics - type counts
			LinkedHashMap<String, Integer> Counts 
				= new LinkedHashMap<String, Integer>();
			HashSet<String> ContigCount = new HashSet<String>();
			
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
					for (String s : this.FeatureIncludeTypes){
						if (ImportedLine[2].trim().contentEquals(s)){
							RetainElement = true;
							break;
						}
					}
					//if this fails, check for display types
					if (!RetainElement){
						for (String s : this.FeatureDisplayTypes){
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
								this.AGClustersLoaded = true;
								
								//System.out.println("Set!");
								if (ImportedLine.length > 10){
									E.setGeneID(ImportedLine[10]);
								}
							}
							
							//add to list
							Elements.add(E);
							
							//add contig ends
							if (ContigEnds.get(E.getContig()) != null){
								if (ContigEnds.get(E.getContig()) < E.getStop()){
									ContigEnds.put(E.getContig(), E.getStop());
								}
							} else {
								ContigEnds.put(E.getContig(), E.getStop());
							}
							
							//Record counts of types
							if (Counts.get(E.getType()) != null){
								int OldCount = Counts.get(E.getType());
								Counts.put(E.getType(),(OldCount+1));
							} else {
								Counts.put(E.getType(), 1);
							}
							
							//Record counts of contigs
							ContigCount.add(E.getContig());
							
						} catch (Exception ex) {}

					}
			}
			
			//Convert feature counts to string, for display.
			//Number of contigs / plasmids / chromosomes
			TextDescription = "Sequences (" + String.valueOf(ContigCount.size()) + "):\n";
			for (String s : ContigCount){
				TextDescription = TextDescription + s + "\n";
			}

			//Feature tabulation
			TextDescription = TextDescription + "\nFeature Types (" + String.valueOf(Counts.values().size()) + "):\n";
			for (String s : Counts.keySet()){
				TextDescription = TextDescription + s + " (" + String.valueOf(Counts.get(s)) + ")\n";
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

//import annotated elements from a .GBK file.
public void importFromGBKFile(String filename){
	
	//call reader!
	
	try {

	     //create a buffered reader to read the sequence file specified by args[0]
	     BufferedReader br = new BufferedReader(new FileReader(filename));
	      
	     //call the reader!
	     importFromGBKReader(br);
	      
	} catch (Exception ex) {
		ex.printStackTrace();
	}
//	
//	try {
//
//		//Information for statistics - type counts
//		LinkedHashMap<String, Integer> Counts 
//			= new LinkedHashMap<String, Integer>();
//		HashSet<String> ContigCount = new HashSet<String>();
//		
//	      //create a buffered reader to read the sequence file specified by args[0]
//	      BufferedReader br = new BufferedReader(new FileReader(filename));
//	      String Line = null;
//	      boolean ReadFeatures = false;
//	      boolean NewFeature = false;
//	      boolean DescriptiveInfo = false;
//
//	      //Fields for genomic features.
//	      String ContigName = "";
//	      String TypeName = "";
//	      GenomicElement E = new GenomicElement();
//	      String LocusTag = "";
//	      boolean WritingProduct = false;
//	      boolean WritingTranslation = false;
//	      
//	      //define types for import.
//	      LinkedList<String> Types = new LinkedList<String>();
//	      Types.addAll(FeatureIncludeTypes);
//	      Types.addAll(FeatureDisplayTypes);
//	      
//	      //prepare list for addition
//	      Elements = new LinkedList<GenomicElement>();
//	      
//	      while ((Line = br.readLine()) != null){
//	    	  
//	    	  //trim the line to remove white space.
//	    	  Line = Line.trim();
//	    	  //System.out.println(Line);
//	    	  
//	    	  //System.out.println(Line);
//    		  String[] L = Line.split("\\s+");
//	    	  
//	    	  //new contig
//	    	  if (Line.startsWith("LOCUS")){
//	    		  ContigName = L[1];
//	    		  ContigCount.add(ContigName);
//	    		  DescriptiveInfo = true;
//	    	  }
//	    	  
//	    	  //read lines for features
//	    	  if (ReadFeatures){
//	    		  
//	    		  //check if line is a new feature
//	    		  for (String s : Types){
//	    			  //System.out.println(s);
//	    			  if (Line.startsWith(s) && !WritingProduct && !WritingTranslation && L[0].equals(s)){
//	    				  NewFeature = true;
//	    				  TypeName = s;
//	    				  break;
//	    			  }
//	    		  }
//	    		  
//	    		  //line is a new feature
//	    		  if (NewFeature){
//
//	    			  //write previous feature
//	    			  if (E != null){
//	    				  if (E.getType() != null){
//		    				  Elements.add(E);
//		    				  
//								//Record counts of types
//								if (Counts.get(E.getType()) != null){
//									int OldCount = Counts.get(E.getType());
//									Counts.put(E.getType(),(OldCount+1));
//								} else {
//									Counts.put(E.getType(), 1);
//								}
//	    				  }
//
//	    			  }
//	    			  
//	    			  //create new feature
//	    			  E = new GenomicElement();
//	    			  NewFeature = false;
//	    			  
//	    			  //reset switches
//	    		      WritingProduct = false;
//	    		      WritingTranslation = false;
//	    			  
//	    			  //type info
//	    			  E.setType(TypeName);
//	    			  E.setContig(ContigName);
//	    			  
//	    			  //fwd or reverse strand
//	    			  if (L[1].contains("complement")){
//	    				  
//	    				  //completely assembled or not
//	    				  if (L[1].contains("join")){
//	    					  
//	    					  //complement(join(729725..730909,730913..731044))
//	    					  String[] X = ((String) L[1].trim().subSequence(16,L[1].length()-2)).split("\\..");
//
//			    			  if (X[0].contains(">") || X[0].contains("<")){
//			    				  X[0] = X[0].substring(1);
//			    			  }
//			    			  
//			    			  if (X[X.length-1].contains(">") || X[1].contains("<")){
//			    				  X[X.length-1] = X[X.length-1].substring(1);
//			    			  }
//			    			  
//			    			  E.setStart(Integer.parseInt(X[0]));
//			    			  E.setStop(Integer.parseInt(X[X.length-1]));
//			    			  E.setStrand(Strand.NEGATIVE);
//	    				
//			    		  //no join	  
//	    				  } else {
//	    					  
//			    			  String[] X = ((String) L[1].trim().subSequence(11,L[1].length()-1)).split("\\..");
//			    			  
//			    			  if (X[0].contains(">") || X[0].contains("<")){
//			    				  X[0] = X[0].substring(1);
//			    			  }
//			    			  
//			    			  if (X[1].contains(">") || X[1].contains("<")){
//			    				  X[1] = X[1].substring(1);
//			    			  }
//			    			  
//			    			  E.setStart(Integer.parseInt(X[0]));
//			    			  E.setStop(Integer.parseInt(X[1]));
//			    			  E.setStrand(Strand.NEGATIVE);
//	    					  
//	    				  }
//
//	    			  } else {
//	    				  
//	    				  //join
//	    				  if (L[1].contains("join")){
//	    					  
//	    					  String[] X = ((String) L[1].trim().subSequence(5,L[1].length()-1)).split("\\..");
//			    			  
//			    			  if (X[0].contains(">") || X[0].contains("<")){
//			    				  X[0] = X[0].substring(1);
//			    			  }
//			    			  
//			    			  if (X[X.length-1].contains(">") || X[X.length-1].contains("<")){
//			    				  X[X.length-1] = X[X.length-1].substring(1);
//			    			  }
//			    			  
//			    			  E.setStart(Integer.parseInt(X[0]));
//			    			  E.setStop(Integer.parseInt(X[X.length-1]));
//			    			  E.setStrand(Strand.POSITIVE);
//	    					  
//	    				  //no join	  
//	    				  } else {
//	    					  
//			    			  String[] X = L[1].trim().split("\\..");
//			    			  
//			    			  if (X[0].contains(">") || X[0].contains("<")){
//			    				  X[0] = X[0].substring(1);
//			    			  }
//			    			  
//			    			  if (X[1].contains(">") || X[1].contains("<")){
//			    				  X[1] = X[X.length-1].substring(1);
//			    			  }
//			    			  
//			    			  E.setStart(Integer.parseInt(X[0]));
//			    			  E.setStop(Integer.parseInt(X[1]));
//			    			  E.setStrand(Strand.POSITIVE);
//			    			  
//	    				  }
//	    				  
//	    			  }
//	    			  
//	    		  //line is not a new feature	  
//	    		  } else {
//	    			  NewFeature = false;
//	    		  }
//	    		  
//	    		  //add to an existing feature
//	    		  if (!NewFeature){
//	    			  
//	    			 //check if currently writing things, first
//	    		     if(WritingProduct){
//	    		    	 
//	    		    	//add the current line.
//	    		    	String UpdatedAnnotation = E.getAnnotation() + " " + Line;
//	    		    	E.setAnnotation(UpdatedAnnotation);
//	    		    	
//	    		    	//if a quotation mark is the last character, this is the end of writing product.
//	    		     	if (Line.substring(Line.length()-1).equals("\"")){
//	    		    		 WritingProduct = false;
//	    		     	}
//	    		    	 
//	    		     } else if (WritingTranslation){
//	    		    	 
//	    		    	 //last line in translation
//	    		    	 if (Line.substring(Line.length()-1).equals("\"")){
//	    		    		 String UpdatedTranslation = E.getTranslation() + Line.substring(0,Line.length()-1);
//	    		    		 E.setTranslation(UpdatedTranslation);
//	    		    		 WritingTranslation = false;
//	    		    	 } else {
//	    		    		 String UpdatedTranslation = E.getTranslation() + Line;
//	    		    		 E.setTranslation(UpdatedTranslation);
//	    		    	 }
//	    		    	 
//	    		     //not writing anything - possibly open things up	 
//	    		     } else {
//	    		    	 
//	    		    	 //start product
//	    		    	 if (L[0].startsWith(GFM.Annotation)){
//	    		    		  
//	    		    		  WritingProduct = true;
//	    		    		  E.setAnnotation(Line.substring(1));
//		    				  
//			    		    	//if a quotation mark is the last character, this is the end of writing product.
//			    		     	if (Line.substring(Line.length()-1).equals("\"")){
//			    		    		 WritingProduct = false;
//			    		     	}
//		    				  
//	    		    		  
//	    		         //start translation
//	    		    	 } else if (GFM.GetTranslation && L[0].startsWith("/translation=")){
//	    		    		 
//	    		    		 WritingTranslation = true;
//	    		    		 
//	    		    		 //short translation - ends in quote
//	    		    		 if (Line.substring(Line.length()-1).equals("\"")){
//	    		    		 
//	    		    			 E.setTranslation((String) Line.substring(14, Line.length()-1));
//	    		    			 WritingTranslation = false;
//	    		    			 
//	    		    	     //normal translation - extends multiple lines
//	    		    		 } else {
//	    		    			 
//	    		    			 E.setTranslation(Line.substring(14));
//	    		    			 WritingTranslation = true;
//	    		    		 }
//	    		    	 
//	    		         //attempt to parse cluster tag
//	    		    	 } else if (GFM.GetCluster && L[0].startsWith(GFM.GetClusterTag)){
//	    		    		 String Info = Line.substring(GFM.GetClusterTag.length());
//	    		    		 Info = Info.replaceAll("\"", "");
//	    		    		 String[] InfoSplit = Info.split("\\s+");
//	    		    		 for (String s : InfoSplit){
//	    		    			 if (s.startsWith("COG")){
//	    		    				 try{
//	    		    					 E.setClusterID(Integer.parseInt(s.substring(3)));
//		    		    				 break;
//	    		    				 }catch (Exception ex){}
//	    		    			 }
//	    		    		 }
//	    		    		 
//	    		         //add gene ID
//	    		    	 } else if (L[0].startsWith(GFM.GeneID)){
//	    		    		 try {
//	    		    			 String GIDNoQuotes = Line.substring(GFM.GeneID.length()).replaceAll("\"", "");
//	    		    			 E.setGeneID(GIDNoQuotes);
//	    		    		 } catch (Exception ex) {}
//	    		    	 }
//	    		    	 
//	    		     }
//
//	    		  }
//	    	  } else {
//	    		  
//	    		  if (DescriptiveInfo){
//		    		  //Add introductory info to the text description.
//		    		  if (!(TextDescription).equals("")){
//		    			  TextDescription = TextDescription + "\n" + Line;
//		    		  } else{
//		    			  TextDescription = Line;
//		    		  }
//	    		  }
//
//	    	  }
//	    	  
//	    	  //turn on feature-reading
//	    	  if (Line.startsWith("FEATURES")){
//	    		  ReadFeatures = true;
//	    	  }
//	    	  
//	    	  //turn off feature-reading
//	    	  if (Line.startsWith("BASE COUNT")){
//	    		  DescriptiveInfo = false;
//	    		  ReadFeatures = false;
//	    	  }
//	    	  
//	      }
//	      
//			//Convert feature counts to string, for display.
//			//Number of contigs / plasmids / chromosomes
//			TextDescription = TextDescription +"\n\nSequences (" + String.valueOf(ContigCount.size()) + "):\n";
//			for (String s : ContigCount){
//				TextDescription = TextDescription + s + "\n";
//			}
//
//			//Feature tabulation
//			TextDescription = TextDescription + "\nFeature Types (" + String.valueOf(Counts.values().size()) + "):\n";
//			for (String s : Counts.keySet()){
//				TextDescription = TextDescription + s + " (" + String.valueOf(Counts.get(s)) + ")\n";
//			}
//			
//			br.close();		
//	      
//	} catch (Exception ex){
//		ex.printStackTrace();
//	}
//	
//	//sort elements
//	Collections.sort(Elements, new GenomicElementComparator());

}

//import annotated elements streamed in from .GBK website.
public void importFromGBKReader(BufferedReader br){

	//Information for statistics - type counts
	LinkedHashMap<String, Integer> Counts 
		= new LinkedHashMap<String, Integer>();
	HashSet<String> ContigCount = new HashSet<String>();
	
      String Line = null;
      boolean ReadFeatures = false;
      boolean NewFeature = false;
      boolean DescriptiveInfo = false;

      //Fields for genomic features.
      String ContigName = "";
      String TypeName = "";
      GenomicElement E = new GenomicElement();
      String LocusTag = "";
      boolean WritingProduct = false;
      boolean WritingTranslation = false;
      
      //define types for import.
      LinkedList<String> Types = new LinkedList<String>();
      Types.addAll(FeatureIncludeTypes);
      Types.addAll(FeatureDisplayTypes);
      
      //prepare list for addition
      Elements = new LinkedList<GenomicElement>();
      
      try {
		while ((Line = br.readLine()) != null){
			  
			  //trim the line to remove white space.
			  Line = Line.trim();
			  //System.out.println(Line);
			  
			  //System.out.println(Line);
			  String[] L = Line.split("\\s+");
			  
			  //new contig
			  if (Line.startsWith("LOCUS")){
				  ContigName = L[1];
				  ContigCount.add(ContigName);
				  try {
					  ContigEnds.put(ContigName, Integer.parseInt(L[2]));
				  } catch (Exception ex){}
				  DescriptiveInfo = true;
			  }
			  
			  //read lines for features
			  if (ReadFeatures){
				  
				  //check if line is a new feature
				  for (String s : Types){
					  //System.out.println(s);
					  if (Line.startsWith(s) && !WritingProduct && !WritingTranslation && L[0].equals(s)){
						  NewFeature = true;
						  TypeName = s;
						  break;
					  }
				  }
				  
				  //line is a new feature
				  if (NewFeature){
					  
					  //write previous feature
					  if (E != null){
						  if (E.getType() != null){
		    				  Elements.add(E);
		    				  
								//Record counts of types
								if (Counts.get(E.getType()) != null){
									int OldCount = Counts.get(E.getType());
									Counts.put(E.getType(),(OldCount+1));
								} else {
									Counts.put(E.getType(), 1);
								}
						  }

					  }
					  
					  //create new feature
					  E = new GenomicElement();
					  NewFeature = false;
					  
					  //reset switches
				      WritingProduct = false;
				      WritingTranslation = false;
					  
					  //type info
					  E.setType(TypeName);
					  E.setContig(ContigName);
					  
					  //fwd or reverse strand
					  if (L[1].contains("complement")){
						  
						  //completely assembled or not
						  if (L[1].contains("join")){
							  
							  //complement(join(729725..730909,730913..731044))
							  String[] X = ((String) L[1].trim().subSequence(16,L[1].length()-2)).split("\\..");

			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[X.length-1].contains(">") || X[1].contains("<")){
			    				  X[X.length-1] = X[X.length-1].substring(1);
			    			  }
			    			  
			    			  E.setStart(Integer.parseInt(X[0]));
			    			  E.setStop(Integer.parseInt(X[X.length-1]));
			    			  E.setStrand(Strand.NEGATIVE);
						
			    		  //no join	  
						  } else {
							  
			    			  String[] X = ((String) L[1].trim().subSequence(11,L[1].length()-1)).split("\\..");
			    			  
			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[1].contains(">") || X[1].contains("<")){
			    				  X[1] = X[1].substring(1);
			    			  }
			    			  
			    			  E.setStart(Integer.parseInt(X[0]));
			    			  E.setStop(Integer.parseInt(X[1]));
			    			  E.setStrand(Strand.NEGATIVE);
							  
						  }

					  } else {
						  
						  //join
						  if (L[1].contains("join")){
							  
							  String[] X = ((String) L[1].trim().subSequence(5,L[1].length()-1)).split("\\..");
			    			  
			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[X.length-1].contains(">") || X[X.length-1].contains("<")){
			    				  X[X.length-1] = X[X.length-1].substring(1);
			    			  }
			    			  
			    			  E.setStart(Integer.parseInt(X[0]));
			    			  E.setStop(Integer.parseInt(X[X.length-1]));
			    			  E.setStrand(Strand.POSITIVE);
							  
						  //no join	  
						  } else {
							  
			    			  String[] X = L[1].trim().split("\\..");
			    			  
			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[1].contains(">") || X[1].contains("<")){
			    				  X[1] = X[X.length-1].substring(1);
			    			  }
			    			  
			    			  E.setStart(Integer.parseInt(X[0]));
			    			  E.setStop(Integer.parseInt(X[1]));
			    			  E.setStrand(Strand.POSITIVE);
			    			  
						  }
						  
					  }
					  
				  //line is not a new feature	  
				  } else {
					  NewFeature = false;
				  }
				  
				  //add to an existing feature
				  if (!NewFeature){
					  
					 //check if currently writing things, first
				     if(WritingProduct){
				    	 
				    	//add the current line.
				    	String UpdatedAnnotation = E.getAnnotation() + " " + Line;
				    	E.setAnnotation(UpdatedAnnotation);
				    	
				    	//if a quotation mark is the last character, this is the end of writing product.
				     	if (Line.substring(Line.length()-1).equals("\"")){
				    		 WritingProduct = false;
				     	}
				    	 
				     } else if (WritingTranslation){
				    	 
				    	 //last line in translation
				    	 if (Line.substring(Line.length()-1).equals("\"")){
				    		 String UpdatedTranslation = E.getTranslation() + Line.substring(0,Line.length()-1);
				    		 E.setTranslation(UpdatedTranslation);
				    		 WritingTranslation = false;
				    	 } else {
				    		 String UpdatedTranslation = E.getTranslation() + Line;
				    		 E.setTranslation(UpdatedTranslation);
				    	 }
				    	 
				     //not writing anything - possibly open things up	 
				     } else {
				    	 
				    	 //start product
				    	 if (L[0].startsWith(GFM.Annotation)){
				    		  
				    		  WritingProduct = true;
				    		  E.setAnnotation(Line.substring(1));
		    				  
			    		    	//if a quotation mark is the last character, this is the end of writing product.
			    		     	if (Line.substring(Line.length()-1).equals("\"")){
			    		    		 WritingProduct = false;
			    		     	}
		    				  
				    		  
				         //start translation
				    	 } else if (GFM.GetTranslation && L[0].startsWith("/translation=")){
				    		 
				    		 WritingTranslation = true;
				    		 
				    		 //short translation - ends in quote
				    		 if (Line.substring(Line.length()-1).equals("\"")){
				    		 
				    			 E.setTranslation((String) Line.substring(14, Line.length()-1));
				    			 WritingTranslation = false;
				    			 
				    	     //normal translation - extends multiple lines
				    		 } else {
				    			 
				    			 E.setTranslation(Line.substring(14));
				    			 WritingTranslation = true;
				    		 }
				    	 
				         //attempt to parse cluster tag
				    	 } else if (GFM.GetCluster && L[0].startsWith(GFM.GetClusterTag)){
				    		 String Info = Line.substring(GFM.GetClusterTag.length());
				    		 Info = Info.replaceAll("\"", "");
				    		 String[] InfoSplit = Info.split("\\s+");
				    		 for (String s : InfoSplit){
				    			 if (s.startsWith("COG")){
				    				 try{
				    					 E.setClusterID(Integer.parseInt(s.substring(3)));
		    		    				 break;
				    				 }catch (Exception ex){}
				    			 }
				    		 }
				    		 
				         //add gene ID
				    	 } else if (L[0].startsWith(GFM.GeneID)){
				    		 try {
				    			 String GIDNoQuotes = Line.substring(GFM.GeneID.length()).replaceAll("\"", "");
				    			 E.setGeneID(GIDNoQuotes);
				    		 } catch (Exception ex) {}
				    	 }
				    	 
				     }

				  }
			  } else {
				  
				  if (DescriptiveInfo){
		    		  //Add introductory info to the text description.
		    		  if (!(TextDescription).equals("")){
		    			  TextDescription = TextDescription + "\n" + Line;
		    		  } else{
		    			  TextDescription = Line;
		    		  }
				  }

			  }
			  
			  //turn on feature-reading
			  if (Line.startsWith("FEATURES")){
				  ReadFeatures = true;
			  }
			  
			  //turn off feature-reading
			  if (Line.startsWith("BASE COUNT")){
				  DescriptiveInfo = false;
				  ReadFeatures = false;
			  }
			  
		  }
		
		
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
      
		//Convert feature counts to string, for display.
		//Number of contigs / plasmids / chromosomes
		TextDescription = TextDescription +"\n\nSequences (" + String.valueOf(ContigCount.size()) + "):\n";
		for (String s : ContigCount){
			TextDescription = TextDescription + s + "\n";
		}

		//Feature tabulation
		TextDescription = TextDescription + "\nFeature Types (" + String.valueOf(Counts.values().size()) + "):\n";
		for (String s : Counts.keySet()){
			TextDescription = TextDescription + s + " (" + String.valueOf(Counts.get(s)) + ")\n";
		}
		
		//close opened stream.
		try {
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
      
		//sort elements
		Collections.sort(Elements, new GenomicElementComparator());
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
		for (String s : this.FeatureIncludeTypes){
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
				for (String s : this.FeatureIncludeTypes){
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
			
			//build line
			Line = E.getContig() + "\tGenBank\t" + String.valueOf(E.getType())
					+ "\t" + String.valueOf(E.getStart()) + "\t" + String.valueOf(E.getStop()) + "\t+\t"
					+ TheStrand + "\t.\t" + E.getAnnotation() + "\t" + String.valueOf(E.getClusterID());
			
			//possibly add homology cluster
			if (E.getGeneID() != ""){
				Line = Line + "\t" + E.getGeneID();
			}
			
			Line = Line + "\n";
			
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
			
				//check annotation first
				if (LL.get(i).getAnnotation().toUpperCase().contains(query[j].trim().toUpperCase())){
					AddtheSet = true;
					GandE.setQueryMatch(true);
					
				//next, check gene IDs
				} else if (LL.get(i).getGeneID().toUpperCase().equals(query[j].trim().toUpperCase())){

					AddtheSet = true;
					GandE.setQueryMatch(true);
					
				// no match!
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
					} else if (this.Elements.get(i).getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
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
					for (String s : this.FeatureIncludeTypes){
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
					for (String s : this.FeatureIncludeTypes){
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
					} else if (this.Elements.get(i).getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
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
				for (String s : this.FeatureIncludeTypes){
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
				for (String s : this.FeatureIncludeTypes){
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
						if (j == 0){
							FirstQueries.add(Elements.get(i));
						} else {
							SecondQueries.add(Elements.get(i));
						}
					} else if (this.Elements.get(i).getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
						if (j == 0){
							FirstQueries.add(Elements.get(i));
						} else {
							SecondQueries.add(Elements.get(i));
						};
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
			
			//compute stats about starting element
			double StartingECenter = this.Elements.get(StartingE).getStart() 
					+ (0.5*(this.Elements.get(StartingE).getStop() - this.Elements.get(StartingE).getStart()));
			
			//add all intermediate elements
			GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
			GandE.setE(this.Elements.get(StartingE)); GandE.setQueryMatch(true); LL.add(GandE);
			int ElementNumber = StartingE + 1;
			while (ElementNumber < StoppingE){
				GandE = new GenomicElementAndQueryMatch();
				GandE.setE(Elements.get(ElementNumber)); GandE.setQueryMatch(false);
				
				//check against user-defined set of valid types
				boolean ElementIsValid = false;
				for (String s : this.FeatureIncludeTypes){
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
			
			//compute stats about stopping element
			double StoppingECenter = this.Elements.get(StoppingE).getStart() 
					+ (0.5*(this.Elements.get(StoppingE).getStop() - this.Elements.get(StoppingE).getStart()));
			
			//add list to hash map.  Check for inappropriate cases.
			if (CSD.isGapLimit()){
				//System.out.println("Starting: " + StartingECenter + " Stopping: " + StoppingECenter);
				if (Math.abs(StoppingECenter - StartingECenter) <= CSD.getGapLimitSize()){
					Hits.add(LL);
				}
			} else {
				Hits.add(LL);
			}
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
						for (String s : this.FeatureIncludeTypes){
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
					//check annotation
					if (E.getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
						GandE.setE(E); 
						GandE.setQueryMatch(true);
						
						//check against user-defined set of valid types
						boolean ElementIsValid = false;
						for (String s : this.FeatureIncludeTypes){
							if (GandE.getE().getType().contentEquals(s)){
								ElementIsValid = true;
								break;
							}
						}
						
						if (ElementIsValid){
							MQMatches.add(GandE);
						}
						
					//check gene ID
					} else if (E.getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
						GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
						GandE.setE(E); 
						GandE.setQueryMatch(true);
						
						//check against user-defined set of valid types
						boolean ElementIsValid = false;
						for (String s : this.FeatureIncludeTypes){
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
		
	} else if (CSD.getType().contentEquals("IntergenicDist-pre")) {
				
		//Initialize a hashset for query matches, and for linked lists of genomic elements.
		HashSet<GenomicElement> QueryMatchSet 
			= new HashSet<GenomicElement>();
		
		HashSet<LinkedList<GenomicElement>> E_Hits = 
				new HashSet<LinkedList<GenomicElement>>();
		
		//iterate through all elements
		for (int i = 0; i < this.Elements.size(); i++){
				
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
					} else if (this.Elements.get(i).getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
						QueryMatch = true;
						break;
					}
				}
			}
		
			//if it is, extract the appropriate range
			if (QueryMatch){
				
				//System.out.println("Breakpoint!");
				
				//current element is the query match.
				GenomicElement E_curr = this.Elements.get(i);
				
				//add to list of query matches
				QueryMatchSet.add(E_curr);
				
				//define a new GenomicElementAndQueryMatch
				LinkedList<GenomicElement> LL = new LinkedList<GenomicElement>();
				LL.add(E_curr);

				// ----- global switches ---- //
				
				boolean AddUpstream = true;
				boolean AddDownstream = true;
				int GeneNumber;
				boolean ValidElementsRemain;
				GenomicElement E_can;
				boolean Add2Operon;
				
				// ----- Add upstream ---- //
				
				//switches
				GeneNumber = i;
				ValidElementsRemain = false;
				
				//Initialize a candidate genomic element for operon addition.
				E_can = null;
				
				//add to operon switch.
				Add2Operon = false;
				
				//add upstream elements to list, if appropriate.
				while (AddUpstream){
					
					//default: no more valid elements, do not add to operon
					ValidElementsRemain = false;
					Add2Operon = false;
					
					//find next valid element
					for (int q = GeneNumber-1; q >= 0; q--){
						E_can = Elements.get(q);
						for (String s : this.FeatureIncludeTypes){
							if (E_can.getType().contentEquals(s)){
								ValidElementsRemain = true;
								GeneNumber = q;
								break;
							}
						}
						
						//break out of outer loop
						if (ValidElementsRemain){
							break;
						}
					}
					
					//compare to current to candidate.
					if (ValidElementsRemain){
						
						//check operon requirements.
						if (E_can.getContig().equals(E_curr.getContig()) &&			//Contig Match
								E_curr.getStart()-E_can.getStop() <= CSD.getIntGenSpacing()){ 	//Distance match
							
							//check for same strand.
							if (CSD.isNeedSameStrand()){
								if (E_can.getStrand().equals(E_curr.getStrand())){
									Add2Operon = true;
								}
							} else {
								Add2Operon = true;
							}
							
						}
						
						//add, if appropriate
						if (Add2Operon){
							
							//add genomic element to growing operon chain
							LL.add(0,E_can);
							
							//re-set counter
							E_curr = E_can;
							
						} else{
							
							//once you stop adding, no going back.
							AddUpstream = false;
							
						}
						
					} else {
						
						//finished with operon.
						AddUpstream = false;
					}

				}
						
				// ----- Add downstream ---- //
				
				//switches
				GeneNumber = i;
				ValidElementsRemain = false;
				
				//Re-initialize genomic elements for comparison.
				E_can = null;
				E_curr = this.Elements.get(i);
				
				//add to operon switch.
				Add2Operon = false;
				
				//add upstream elements to list, if appropriate.
				while (AddDownstream){
					
					//default: no more valid elements, do not add to operon
					ValidElementsRemain = false;
					Add2Operon = false;
					
					//find next valid element
					for (int q = GeneNumber+1; q < Elements.size(); q++){
						E_can = Elements.get(q);
						for (String s : this.FeatureIncludeTypes){
							if (E_can.getType().contentEquals(s)){
								ValidElementsRemain = true;
								GeneNumber = q;
								break;
							}
						}
						
						//break out of outer loop
						if (ValidElementsRemain){
							break;
						}
					}
					
					//compare to current to candidate.
					if (ValidElementsRemain){
						
						//check operon requirements.
						if (E_can.getContig().equals(E_curr.getContig()) &&			//Contig Match
								E_can.getStart()-E_curr.getStop() <= CSD.getIntGenSpacing()){ 	//Distance match
							
							//check for same strand.
							if (CSD.isNeedSameStrand()){
								if (E_can.getStrand().equals(E_curr.getStrand())){
									Add2Operon = true;
								}
							} else {
								Add2Operon = true;
							}
							
						}
						
						//add, if appropriate
						if (Add2Operon){
							
							//add genomic element to growing operon chain
							LL.add(E_can);
							
							//re-set counter
							E_curr = E_can;
							
						} else{
							
							//once you stop adding, no going back.
							AddDownstream = false;
							
						}
						
					} else {
						
						//finished with operon.
						AddDownstream = false;
					}

				}
				
				//finally, add this to the hit list (pre-query match tags)
				E_Hits.add(LL);
						
			}

		}
		
		//build up actual hits - add query information
		for (LinkedList<GenomicElement> LL : E_Hits){
			
			//initialize list
			LinkedList<GenomicElementAndQueryMatch> LLq 
				= new LinkedList<GenomicElementAndQueryMatch>();
			
			//iterate through elements, add query tag
			for (GenomicElement E : LL){
				
				//initialize genomic element and query match
				GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
				GandE.setE(E);
				
				//if this element is in the set of query matches, tag
				if (QueryMatchSet.contains(E)){
					GandE.setQueryMatch(true);
				} else {
					GandE.setQueryMatch(false);
				}
				
				//add query-updated element to list
				LLq.add(GandE);
			}
			
			//add completed list to final output set.
			Hits.add(LLq);
			
		}
		

	} else if (CSD.getType().contentEquals("SingleGene")) {
		
		//iterate through all elements
		for (GenomicElement E : Elements){
			
			//re-set for each gene.
			QueryMatch = false;
			
			//check for match
			if (IsCluster){
				for (int j = 0; j < ClusterNumbers.length; j++){
					if (E.getClusterID() == ClusterNumbers[j]){
						QueryMatch = true;
						break;
					}
				}
			} else {
				for (int j = 0; j < Queries.length; j++){
					if (E.getAnnotation().toUpperCase().contains(Queries[j].trim().toUpperCase())){
						QueryMatch = true;
						break;
					} else if (E.getGeneID().toUpperCase().equals(Queries[j].trim().toUpperCase())){
						QueryMatch = true;
						break;
					}
				}
			}
			
			//add to list
			if (QueryMatch){
				//System.out.println("Breakpoint!");
				
				//Define Match
				GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
				GandE.setE(E); 
				GandE.setQueryMatch(true);
				
				//gene should be in a class all of its own
				LinkedList<GenomicElementAndQueryMatch> LL 
					= new LinkedList<GenomicElementAndQueryMatch>();
				
				//add gene to list
				LL.add(GandE);
				
				//add list to set of lists
				Hits.add(LL);
			}

		}
		
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
	return FeatureIncludeTypes;
}

public void setIncludeTypes(LinkedList<String> includeTypes) {
	FeatureIncludeTypes = includeTypes;
}

public LinkedList<String> getDisplayOnlyTypes() {
	return FeatureDisplayTypes;
}

public void setDisplayOnlyTypes(LinkedList<String> displayOnlyTypes) {
	FeatureDisplayTypes = displayOnlyTypes;
}

public LinkedList<MotifGroup> getMotifs() {
	return Motifs;
}

public void setMotifs(LinkedList<MotifGroup> motifs) {
	Motifs = motifs;
}

public boolean isAGClustersLoaded() {
	return AGClustersLoaded;
}

public void setAGClustersLoaded(boolean aGClustersLoaded) {
	AGClustersLoaded = aGClustersLoaded;
}

public String getTextDescription() {
	return TextDescription;
}

public void setTextDescription(String textDescription) {
	TextDescription = textDescription;
}

public String getGenbankID() {
	return GenbankID;
}

public void setGenbankID(String genbankID) {
	GenbankID = genbankID;
}

public GBKFieldMapping getGFM() {
	return GFM;
}

public void setGFM(GBKFieldMapping gFM) {
	GFM = gFM;
}

public LinkedHashMap<String, Integer> getContigEnds() {
	return ContigEnds;
}

public void setContigEnds(LinkedHashMap<String, Integer> contigEnds) {
	ContigEnds = contigEnds;
}

//-----------------------Deprecated ----------------------//

} //completes classbody
