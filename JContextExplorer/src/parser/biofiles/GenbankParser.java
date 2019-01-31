package parser.biofiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;


public class GenbankParser {

	public static String GenbankFile = "/Users/phillipseitzer/OperonForest_Jan2019/Genomes/Myxococcus_fulvus_124B02_genomic.gbff";
	public static String OutputFile = "/Users/phillipseitzer/Documents/Halophiles_2012/Salmonella_Enterica/Annotations/SQ0227.gff";
	public static String TranslationOutputFile = "";
	public static LinkedList<FeatureEntry> Entries;
	public static String[] Types;
	public static String SpeciesName;
	public static String ContigName = "";
	
	public static boolean isVerbose = false;
	public static boolean printSummaryStats = false;
	
	public static Map<String, Integer> typeCounts = new HashMap<String, Integer>();
	
	public static boolean OutputTranslations = false;
	public static boolean OutputGFF = false;
	
	public class FeatureEntry{
		
		//Fields
		public String Contig;
		public String Type;
		public String Start;
		public String Stop;
		public String Strand;
		public String Annotation = ".";	//basically, product
		public String Translation;
		public String LocusTag = "test";
		
	}
	
	//import + store file 
	public static void ImportAndParseGenbank(String FileName){
		
		GenbankParser g = new GenbankParser();
		
		Entries = new LinkedList<FeatureEntry>();
		
		try {

		      //create a buffered reader to read the sequence file specified by args[0]
		      BufferedReader br = new BufferedReader(new FileReader(FileName));

		      String Line = null;
		      boolean IgnoreFeatureData = true;
		      boolean ContinuingFeature = false;
		      FeatureEntry FE = null;
		      boolean AnnotationAchieved = false;
		      boolean ProductStarted = false;
		      boolean TranslationStarted = false;
		      boolean TranslationAchieved = false;
		      String Annotation = "";
		      String Translation = "";
		      String ContigName = "Chromosome";
		      
		      int Counter = 0;
	    	  String LocusTag = "";
	    	  
		      while ((Line = br.readLine()) != null){
		    	  
		    	  Counter++;
		    	  
		    	  //split line
		    	  String[] L = Line.trim().split("\\s+");
    			  
		    	  //origin
		    	  if (L[0].equals("ORIGIN")){
		    		  TranslationAchieved = true;
		    		  //AnnotationAchieved = true;
		    	  }

		    	  //being caring about features
		    	  if (IgnoreFeatureData==false){
		    		  
		    		  if (Line.contains("..")){
		    			  ContinuingFeature = false;
		    		  }
		    		  
		    		  //start a new feature
		    		  if (!L[0].startsWith("/") && !ContinuingFeature){
		    			  
		    			  //write previous feature
		    			  if (FE != null){
		    				  FE.LocusTag = LocusTag;
		    				  Entries.add(FE);
		    			  }
		    			  
		    			  //create new feature
		    			  FE = g.new FeatureEntry();
		    			  
		    			  //reset switches
		    			  AnnotationAchieved = false;
		    			  ProductStarted = false;
		    			  TranslationStarted = false;
		    			  TranslationAchieved = false;
		    			  
		    			  //type info
		    			  FE.Type = L[0].trim();
		    			  FE.Contig = ContigName;
		    			  
		    			  //System.out.println(L[2]);
		    			  //start, stop
		    			  if (L[1].contains("complement")){
			    			  String[] X = ((String) L[1].trim().subSequence(11,L[1].length()-1)).split("\\..");
			    			  
			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[1].contains(">") || X[1].contains("<")){
			    				  X[1] = X[1].substring(1);
			    			  }
			    			  
			    			  FE.Start = X[0];
			    			  FE.Stop = X[1];
			    			  FE.Strand = "-1";
		    			  } else {
			    			  String[] X = L[1].trim().split("\\..");
			    			  
			    			  if (X[0].contains(">") || X[0].contains("<")){
			    				  X[0] = X[0].substring(1);
			    			  }
			    			  
			    			  if (X[1].contains(">") || X[1].contains("<")){
			    				  X[1] = X[1].substring(1);
			    			  }
			    			  
			    			  FE.Start = X[0];
			    			  FE.Stop = X[1];
			    			  FE.Strand = "1";
		    			  }

		    			  ContinuingFeature = false;
		    			  
		    		  } else {

		    			  //write to field
		    			  ContinuingFeature = true;
		    			  
		    		  }
		    		  
		    		  //continue collecting feature data
		    		  if (ContinuingFeature){
		    			  
		    			  if (Line.contains("/locus_tag")){
		    				  LocusTag = Line.trim().substring(12);
		    				  LocusTag = LocusTag.substring(0, LocusTag.length()-1);
		    				 // System.out.println(LocusTag);
		    			  }
		    			  
//		    			  if (Line.contains("/gene")){
//		    				  LocusTag = Line.trim().substring(7);
//		    				  LocusTag = LocusTag.substring(0, LocusTag.length()-1);
//		    				  LocusTag = "PBCV1_"+ LocusTag;
//		    			  }
		    			  
		    			  if (!AnnotationAchieved){
			    			  if (Line.contains("/product")){
		    				 // if (Line.contains("/locus_tag")){
			    				  ProductStarted = true;
			    				  Annotation = Line.trim().substring(1);
			    				  FE.Annotation = Annotation;
			    				  //Annotation = Line.trim().substring(12);
			    				 // Annotation = Annotation.substring(0, Annotation.length()-1);
			    			  } else {
			    				  if (ProductStarted){
			    					 
			    					  if (Line.trim().startsWith("/") || Line.contains("BASE")){
				    					  if (FE != null){
				    						  FE.Annotation = Annotation;
				    					  }
				    					  AnnotationAchieved = true;
			    					  } else {
			    						  Annotation = Annotation + " " + Line.trim();
			    						  FE.Annotation = Annotation;
			    					  }
			    					  
//			    					  //If the line is not a new tag, keep adding
//				    				  if (Line.trim().startsWith("/") == false){
//				    					  Annotation = Annotation + " " + Line.trim();					    					  
//				    				  } else {
//				    					  if (FE != null){
//				    						  FE.Annotation = Annotation;
//				    					  }
//				    					  AnnotationAchieved = true;
//				    				  }
			    				  }

			    			  } 
		    			  }
		    			  
		    			  if (!TranslationAchieved){
		    				  if (Line.contains("/translation=")){
		    					  TranslationStarted = true;
		    					  
		    					  //protein
		    					  if (Translation.contains("\"")){
		    						  Translation = (String) Line.trim().substring(14).subSequence(0, Line.trim().substring(14).length()-2);
		    					  } else {
		    						  Translation = Line.trim().substring(14);
		    					  }
		    					  
    							  if (FE != null){
    								  //System.out.println("Complete: " + Translation);
    								  FE.Translation = Translation;
    							  }
		    					  
		    					 // System.out.println("First: " + Translation);
		    				  } else {
		    					  if (TranslationStarted){
		    						  
		    						  //continue
		    						  if (Line.trim().startsWith("/") == false){
		    							  
		    							  //append sequence
				    					  if (Line.contains("\"")){
				    						  if (Line.trim().length() > 1){
				    							  Translation = Translation + (String) Line.trim().subSequence(0, Line.trim().length()-1);
				    						  }
				    					  } else {
				    						  Translation = Translation + Line.trim();
				    					  }
				    					  
				    					 // System.out.println("Complete: " + Translation);
		    							  if (FE != null){
		    								  //System.out.println("Complete: " + Translation);
		    								  FE.Translation = Translation;
		    							  }
		    						  } else {

	    								  TranslationAchieved = true;
		    						  }
		    					  }
		    				  }
		    			  }

		    		  }

		    	  }
		    	  
		    	  //don't start caring about things until this point
		    	  if (Line.contains("FEATURES")){
		    		  IgnoreFeatureData = false;
				      AnnotationAchieved = false;
				      ProductStarted = false;
				      TranslationStarted = false;
				      TranslationAchieved = false;
		    	  }
		    	  
		    	  //contig name
		    	  if (L[0].equals("LOCUS")){
		    		  ContigName = L[1];
		    	  }

		      
		      }
		      
		      br.close();
		      
		} catch (Exception ex) {
		      //can't find the file specified by args[0]
		      ex.printStackTrace();
	    }

	}
	
	public static void printSummaryStats(){
		for (FeatureEntry FE : Entries){
			
			int count = 0;
			if (null != typeCounts.get(FE.Type)){
				count = typeCounts.get(FE.Type) + 1;
			} else {
				count = 1;
			}
			typeCounts.put(FE.Type, count);
		}
		
		System.out.println("********************************");
		System.out.println("*********** SUMMARY ************");
		System.out.println("Species: " + SpeciesName);
		int totalSum = 0;
		for (Entry<String, Integer> entry : typeCounts.entrySet()){
			totalSum += entry.getValue();
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println("total: " + totalSum);
		System.out.println("********************************");
		System.out.println("********************************");
	}
	//export
	public static void ExportGenbankAsGFF(String FileName){
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FileName));
			
			//export
			for (FeatureEntry FE : Entries){
			
//				String Feature = FE.Contig + "\t" + "GenBank" 
//				  + "\t" + FE.Type + "\t" + FE.Start + "\t" + FE.Stop + "\t+\t"
//				  + FE.Strand + "\t.\t" + FE.Annotation + "\t0\t"+ FE.LocusTag +"\n";
				
				String Feature = FE.Contig + "\t" + "GenBank" 
						  + "\t" + FE.Type + "\t" + FE.Start + "\t" + FE.Stop + "\t+\t"
						  + FE.Strand + "\t.\t" + FE.Annotation + "\t0\n";
				//System.out.println(FE.Annotation);
				
				//only write certain types to file, if this option is specified.
				if (Types != null){
					boolean KeepType = false;
					for (int i = 0; i < Types.length; i++){
						if (FE.Type.equals(Types[i])){
							KeepType = true;
							break;
						}
					}
					
					if (KeepType){
						bw.write(Feature);
					}
				} else {
					bw.write(Feature);
				}

				bw.flush();
			}
			
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//export
	public static void ParseGenbankv2(String FileName){
		
		
		
		GenbankParser g = new GenbankParser();
		Entries = new LinkedList<FeatureEntry>();
		
		try {

		      //create a buffered reader to read the sequence file specified by args[0]
		      BufferedReader br = new BufferedReader(new FileReader(FileName));
		      String Line = null;
		      boolean ReadFeatures = false;
		      boolean NewFeature = false;
		      //persistent data
		      String ContigName = "";
		      String TypeName = "";
		      FeatureEntry FE = g.new FeatureEntry();
		      String LocusTag = "";
		      boolean WritingProduct = false;
		      boolean WritingTranslation = false;
		      
		      while ((Line = br.readLine()) != null){
		    	  
		    	  if (isVerbose){
			    	  System.out.println(Line);
		    	  }
		    	  
		    	  //trim the line to remove white space.
		    	  String unTrimmedLine = Line;
		    	  Line = Line.trim();
		    	 // System.out.println(Line);
		    	  
		    	  //System.out.println(Line);
	    		  String[] L = Line.split("\\s+");
		    	  
		    	  //new contig
		    	  if (Line.startsWith("LOCUS")){
		    		  ContigName = L[1];
		    	  }
		    	  
		    	  //read lines for features
		    	  if (ReadFeatures){
		    		  
		    		  //check if line is a new feature
		    		  for (String s : Types){
		    			  //System.out.println(s);
		    			  if (unTrimmedLine.startsWith("     " + s) && !WritingProduct && !WritingTranslation){
		    				  NewFeature = true;
		    				  TypeName = s;
	    				  break;
	    			  }
//		    			  if (Line.startsWith(s) && !WritingProduct && !WritingTranslation){
//		    				  NewFeature = true;
//		    				  TypeName = s;
//		    				  break;
//		    			  }
		    		  }
		    		  
		    		  //line is a new feature
		    		  if (NewFeature){
		    			 
		    			  //write previous feature
		    			  if (FE != null){
		    				  if (FE.Start != null){
			    				  Entries.add(FE);
		    				  }

		    			  }
		    			  
		    			  //create new feature
		    			  FE = g.new FeatureEntry();
		    			  NewFeature = false;
		    			  
		    			  //reset switches
		    		      WritingProduct = false;
		    		      WritingTranslation = false;
		    			  
		    			  //type info
		    			  FE.Type = TypeName;
		    			  FE.Contig = ContigName;
		    			  
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
				    			  
				    			  FE.Start = X[0];
				    			  FE.Stop = X[X.length-1];
				    			  FE.Strand = "-1";
		    				
				    		  //no join	  
		    				  } else {
		    					  
				    			  String[] X = ((String) L[1].trim().subSequence(11,L[1].length()-1)).split("\\..");
				    			  
				    			  if (X[0].contains(">") || X[0].contains("<")){
				    				  X[0] = X[0].substring(1);
				    			  }
				    			  
				    			  if (X[1].contains(">") || X[1].contains("<")){
				    				  X[1] = X[1].substring(1);
				    			  }
				    			  
				    			  FE.Start = X[0];
				    			  FE.Stop = X[1];
				    			  FE.Strand = "-1";
		    					  
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
				    			  
				    			  FE.Start = X[0];
				    			  FE.Stop = X[X.length-1];
				    			  FE.Strand = "1";
		    					  
		    				  //no join	  
		    				  } else {
		    					  
				    			  String[] X = L[1].trim().split("\\..");
				    			  
				    			  if (X[0].contains(">") || X[0].contains("<")){
				    				  X[0] = X[0].substring(1);
				    			  }
				    			  
				    			  if (X[1].contains(">") || X[1].contains("<")){
				    				  X[1] = X[X.length-1].substring(1);
				    			  }
				    			  
				    			  FE.Start = X[0];
				    			  FE.Stop = X[1];
				    			  FE.Strand = "1";
				    			  
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
		    		    	FE.Annotation = FE.Annotation + " " + Line;
		    		    	
		    		    	//if a quotation mark is the last character, this is the end of writing product.
		    		     	if (Line.substring(Line.length()-1).equals("\"")){
		    		    		 WritingProduct = false;
		    		     	}
		    		    	 
		    		     } else if (WritingTranslation){
		    		    	 
		    		    	 //last line in translation
		    		    	 if (Line.substring(Line.length()-1).equals("\"")){
		    		    		 FE.Translation = FE.Translation + Line.substring(0,Line.length()-1);
		    		    		 WritingTranslation = false;
		    		    	 } else {
		    		    		 FE.Translation = FE.Translation + Line;
		    		    	 }
		    		    	 
		    		     //not writing anything - possibly open things up	 
		    		     } else {
		    		    	 
		    		    	 //start product
		    		    	 if (L[0].startsWith("/product=")){
		    		    		  
		    		    		  WritingProduct = true;
			    				  FE.Annotation = Line.substring(1);
			    				  
				    		    	//if a quotation mark is the last character, this is the end of writing product.
				    		     	if (Line.substring(Line.length()-1).equals("\"")){
				    		    		 WritingProduct = false;
				    		     	}
			    				  
		    		    		  
		    		         //start translation
		    		    	 } else if (L[0].startsWith("/translation=")){
		    		    		 
		    		    		 WritingTranslation = true;
		    		    		 
		    		    		 //short translation - ends in quote
		    		    		 if (Line.substring(Line.length()-1).equals("\"")){
		    		    		 
		    		    			 FE.Translation = (String) Line.substring(14, Line.length()-1);
		    		    			 WritingTranslation = false;
		    		    			 
		    		    	     //normal translation - extends multiple lines
		    		    		 } else {
		    		    			 
		    		    			 FE.Translation = Line.substring(14);
		    		    			 WritingTranslation = true;
		    		    		 }
		    		    		 
		    		    	 }
		    		    	 
		    		     }

		    		  }
		    	  }
		    	  
		    	  //turn on feature-reading
		    	  if (Line.startsWith("FEATURES")){
		    		  ReadFeatures = true;
		    	  }
		    	  
		    	  //turn off feature-reading
		    	  if (Line.startsWith("BASE COUNT")){
		    		  ReadFeatures = false;
		    	  }
		    	  
		      }
		      
		      br.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//Export translations
	private static void ExportTranslations(String FileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FileName));
			
			int Counter = 0;
			//export
			for (FeatureEntry FE : Entries){

				if (FE.Type.equals("CDS")){
					
					//increment counter
					Counter++;
					
					String Contig = "";
					
					if (ContigName.equals("")){
						Contig = FE.Contig;
					} else {
						Contig = ContigName;
					}
					
					//define header
					String Header;
					if (SpeciesName != null){
						//Format: SpeciesName--Contig--Start--Stop
						Header = ">" + SpeciesName + "--" + Contig + "--" + String.valueOf(FE.Start) + "--" + String.valueOf(FE.Stop) + "\n";
						
					} else { //exclude species name

						Header = ">" + Contig + "--" + String.valueOf(FE.Start) + "--" + String.valueOf(FE.Stop) + "\n";
						
					}
					
					if (null != FE.Translation){
						bw.write(Header);
						bw.write(FE.Translation);
						bw.write("\n\n");
						bw.flush();
					}

				}
				
			}
			
			bw.close();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		//default types
		Types = new String[3];
		Types[0] = "CDS";
		Types[1] = "tRNA";
		Types[2] = "rRNA";
		
		if (args.length ==0){
			printUsage();
		}
		
		try {
			for (int i = 0; i < args.length; i++){
				if (args[i].equals("-i")){
					GenbankFile = args[i+1];
				} else if (args[i].equals("-o")){
					OutputGFF = true;
					OutputFile = args[i+1];
				} else if (args[i].equals("-t")){
					Types = args[i+1].split(",");
				} else if (args[i].equals("-p")){
					OutputTranslations = true;
					TranslationOutputFile = args[i+1];
				} else if (args[i].equals("-s")){
					SpeciesName = args[i+1];
				} else if (args[i].equals("-c")){
					ContigName = args[i+1];
				} else if (args[i].equals("-v")){
					isVerbose = true;
				} else if (args[i].equals("-d")){
					printSummaryStats = true;
				}
			}
		} catch (Exception ex){
			printUsage();
		}

		
		//import
		//new method April 24, 2013
		//edited Friday, Nov. 13, 2015
		ParseGenbankv2(GenbankFile);
		
		//output gff
		if (OutputGFF){
			ExportGenbankAsGFF(OutputFile);
			System.out.println("Feature Annotations successfully exported.");
		}

		//output translations
		if (OutputTranslations){
			ExportTranslations(TranslationOutputFile);
			System.out.println("Protein Translations successfully exported.");
		}
		
		if (printSummaryStats){
			printSummaryStats();
		}
		System.out.println("All Processes Successfully Completed!");
		
	}

	public static void printUsage(){
		System.out.println("Usage: ");
		System.out.println("GenbankParser.jar -i <input-genbank-file> -o <desired-output-gff-file>");
		System.out.println("");
		System.out.println("Optional arguments:");
		System.out.println("	-t <types-to-include-in-gff-file>");
		System.out.println("		(default: CDS, tRNA, rRNA");	
		System.out.println("	-p <protein-translations-file>");
		System.out.println("	-s <provided-species-name>");
		System.out.println("		(if this option is not provided, will attempt to derive species name from genbank file).");
		System.out.println("	-c <provided-contig-name>");
		System.out.println("		(if this option is not provided, will attempt to derive contig name from genbank file).");
		System.out.println("	-v");
		System.out.println("		(verbose mode: will print out every line of the .gbk as the file is being parsed.");
		System.out.println("	-d");
		System.out.println("		(summary data mode: at the end of the run, will print the summary data.");
	}


}

