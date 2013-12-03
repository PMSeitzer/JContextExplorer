package moduls.frm.Panels;

	import genomeObjects.AnnotatedGenome;
import genomeObjects.CSDisplayData;
import genomeObjects.ContextSetDescription;
import genomeObjects.ExtendedCRON;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;
import genomeObjects.OrganismSet;
import importExport.DadesExternes;
import importExport.FitxerDades;
import inicial.Language;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.biojava3.core.sequence.Strand;

	import methods.Reagrupa;
import moduls.frm.ContextLeaf;
import moduls.frm.FrmInternalFrame;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.InternalFrameData;
import moduls.frm.PostSearchAnalyses;
import moduls.frm.QueryData;
import moduls.frm.Panels.Jpan_btn.MDComputation;
import moduls.frm.children.FrmGraph;
import moduls.frm.children.FrmPhylo;
import moduls.frm.children.FrmPiz;
import moduls.frm.children.FrmSearchResults;
import moduls.frm.children.FrmTabbed;
import moduls.frm.children.manageContextSetsv2;
import parser.EscalaFigures;
import parser.Fig_Pizarra;
import parser.figures.Marge;
import tipus.Orientation;
import tipus.metodo;
import tipus.tipusDades;
import definicions.CfgPanelMenu;
import definicions.Cluster;
import definicions.Config;
import definicions.MatriuDistancies;

	public class Jpan_btn_NEW extends JPanel implements ActionListener,
			InternalFrameListener, PropertyChangeListener {

	// ----- Fields -----------------------------------------------//
		
		private static final long serialVersionUID = 1L;

		// Desktop where the dendrogram is to be shown
		private final FrmPrincipalDesk fr;

		//this button
		protected Jpan_btn_NEW jb;

		// Text to show in the buttons
		private String strSubmit, strAnnsearch, strClusearch;
        private String strManageCS = "Add/Remove";
        private String strCancel = "Cancel";
        
		// radio buttons for search type
		private ButtonGroup searchType;
		private JRadioButton annotationSearch, clusterSearch;
		
		// Load and update buttons
		public static JButton btnManage, btnSubmit, btnUpdate;
		private JButton btnCancel;
		
		// Indicates if the buttons Load or Update are being clicked
		public static boolean buttonClicked = false;

		// Internal frame currently active
		private FrmInternalFrame currentInternalFrame = null;

		public FrmInternalFrame getCurrentInternalFrame() {
			return currentInternalFrame;
		}

		// File with the input data
		private static FitxerDades fitx = null; //path to file
		public DadesExternes de; //determined by annotation search / clustering
		
		private static JTextField txtFileName, contextSetHeader, searchField;
		private final Dimension searchFieldSize;
		
		// MultiDendrogram
		private MatriuDistancies multiDendro = null;

		// Progress bar for MultiDendrogram computation
		private JProgressBar progressBar;

		// Menu to select current context set
		private JComboBox<String> contextSetMenu;
		//private LinkedList<String> ContextList = new LinkedList<String>();

		// Indicate if the text fields have correct values
		public static boolean precisionCorrect = false;
		public static boolean axisMinCorrect = false;
		public static boolean axisMaxCorrect = false;
		public static boolean axisSeparationCorrect = false;
		public static boolean axisEveryCorrect = false;
		public static boolean axisDecimalsCorrect = false;
		
		private boolean ProceedWithSearch = false;
		
		//Standard font + big font
		private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
		private Font bigFont = new Font("Dialog", Font.BOLD, 14);
		
		//Section labels
		private JLabel ContextSetSelect, SearchGenomes;
		
		
	// ----- New Fields (1.0) --------------------------------------------//	
		
		//These fields modify the new scrollable tree
		
		public static int HorizontalScrollBuffer = 30;
		private int VerticalScrollValue = 1500;

		private String currentQuery;

	// ----- New Fields (1.1) --------------------------------------------//			
		
		//searches are handled by a single Swing Worker
		private SearchWorker CurrentSearch = null;
		private String strUpdate = "Update";
		
		//display search results
		private FrmSearchResults SearchResultsFrame = new FrmSearchResults();
		
		//parameters associated with phylo tree
		private double PhyloTreeLength;
		private int PhyloTreeLeaves;
	
	// ----- New Fields (1.2) --------------------------------------------//
		public static int ScrollInc = 30;
		
	// ----- Methods -----------------------------------------------//	
		
		// ---- Thread Workers for Searches ------------------------//

		//Unified SwingWorker for searches + multidendrogram computations
		public class SearchWorker extends SwingWorker<Void, Void>{

			//fields
			//search-related
			private String[] Queries;
			private int[] ClusterNumber;
			private String ContextSetName;
			private String DissimilarityMethod;
			private String Name;
			private boolean AnnotationSearch;
			private DadesExternes ProcessedDE;
			private QueryData WorkerQD;
			
			//options-related
			private PostSearchAnalyses AnalysesList;
			
			//dendrogram-related
			private final String action;
			private final tipusDades typeData;
			private final metodo method;
			private final int precision;
			private int nbElements;
			private double minBase;
			
			//display /process related
			private boolean DisplayOutput;
			private boolean ExceptionThrown = false;
			
			//final
			public Cluster RootCluster = null;
			public boolean ProcessCompleted = false;
			
			//constructor
			public SearchWorker(final QueryData QD, final String action,
					final tipusDades typeData, final metodo method,
					final int precision, boolean DisplayOutput){

				//Query Data (QD)
				this.WorkerQD = QD;
				this.Queries = QD.getQueries();
				this.ClusterNumber = QD.getClusters();
				this.ContextSetName = QD.getContextSetName();
				this.DissimilarityMethod = QD.getDissimilarityType();
				this.Name = QD.getName();
				this.AnnotationSearch = QD.isAnnotationSearch();
				
				//Analyses options
				this.AnalysesList = QD.getAnalysesList();
				
				//multidendrogram-related parameters
				this.action = action;
				this.typeData = typeData;
				this.method = method;
				this.precision = precision;
				
				//display-related
				this.DisplayOutput = DisplayOutput;

			}
			
			@Override
			protected Void doInBackground() throws Exception {
	
			try {	
				
				if (DisplayOutput){
					
					//visualization-related things
					fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					progressBar.setBorderPainted(true);
					progressBar.setString(null);
					multiDendro = null;
					de = null;
					
				}
				
				//Search organisms
				if (AnnotationSearch){
					SearchOrganismsbyAnnotation();
				} else {
					SearchOrganismsbyCluster();
				}
				
				//only display output if the thread was no interrupted.
				if (!Thread.currentThread().isInterrupted()){
					if (DisplayOutput){
						if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() == 1){
							System.out.println("Search Completed. " + this.WorkerQD.getCSD().getEC().getNumberOfEntries() + " Gene Grouping Recovered.");					
						} else {
							System.out.println("Search Completed. " + this.WorkerQD.getCSD().getEC().getNumberOfEntries() + " Gene Groupings Recovered.");					
						}
					}
					
					//adjust analyses options based on number of matches.
					if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() < 2){
						this.WorkerQD.getAnalysesList().setOptionComputeDendrogram(false);
						this.WorkerQD.getAnalysesList().setOptionComputeContextGraph(false);
						if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() == 0){
							String errMsg = "There were no matches to the query (or queries).";
							SearchResultsFrame = new FrmSearchResults();
							if (DisplayOutput){
								showError(errMsg);
							}
							ExceptionThrown = true;
							RootCluster = null;

						}
					}
				}
				
				//Analyses options
				//=================================//
				
				//check to ensure that this search worker has not been cancelled.
				//if (!fr.isSearchWorkerCancelled()){
				
				if (!ExceptionThrown){
					
					//(1) Construct search pane
					if (!Thread.currentThread().isInterrupted()){
						if (AnalysesList.isOptionDisplaySearches()){
							CreateSearchPanel();
						}
						
					} else {

						//re-set cursor, progress bar
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						setProgress(0);
						progressBar.setString("");
						progressBar.setBorderPainted(false);
						
						//set cancellation parameter for EDT
						fr.setSearchWorkerCancelled(true);
					}
					
					//(2) Compute dendrogram
					if (!Thread.currentThread().isInterrupted()){
						if (AnalysesList.isOptionComputeDendrogram()){
							ComputeDendrogram();
						}
						
					//if the process has been cancelled, restore defaults.
					} else {

						//re-set cursor, progress bar
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						setProgress(0);
						progressBar.setString("");
						progressBar.setBorderPainted(false);
						
						//set cancellation parameter for EDT
						fr.setSearchWorkerCancelled(true);
					}
					
					//(3) Render products into output
					if (!Thread.currentThread().isInterrupted()){
						
						//process completed!
						ProcessCompleted = true;
						fr.setTmpCluster(RootCluster);
						
						//(3) Display output
						if (DisplayOutput){
							
							//try to update values
							try {
								//update values for display
								if (AnalysesList.isOptionComputeDendrogram()){
									multiDendro.getArrel().setBase(minBase);
								}
								showCalls(action, this.WorkerQD); //pass on the QD + display options
							} catch (Exception ex) {
								
							}
						}
						
						//if the process has been cancelled, restore defaults.
						} else {

							//re-set cursor, progress bar
							fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							setProgress(0);
							progressBar.setString("");
							progressBar.setBorderPainted(false);
							
							//set cancellation parameter for EDT
							fr.setSearchWorkerCancelled(true);
						}
				}

			} catch (Exception ex) {
				if (DisplayOutput){
					showError("There were no matches to the query (or queries).");
				}
				ExceptionThrown = true;
				ex.printStackTrace();
			}

				//exit background thread
				return null;
			}
			
			//Required Operations
			//(A) Perform Search [Required]
			//============================================//
			//by annotation
			protected Void SearchOrganismsbyAnnotation(){
				
				//re-set progress value
				int progress = 0;
				
				//cassette versus non-cassette context sets
				boolean isCassette = false;
				
				//recover the context set description
				ContextSetDescription CurrentCSD = null;
				
				for (ContextSetDescription csd : fr.getOS().getCSDs()){
					if (csd.getName().contentEquals(this.ContextSetName)){
						CurrentCSD = csd;
						break;
					}
				}
				
				//System.out.println("CSD: " + CurrentCSD.getName());
				
				//set context set name
				String ContextSetName = this.ContextSetName;
				
				//Cassette Option - Switch to the cassette type
				if (CurrentCSD.isCassette()){
					isCassette = true;
					ContextSetName = CurrentCSD.getCassetteOf();
					
					//recover the context set description of the cassette
					for (ContextSetDescription csd : fr.getOS().getCSDs()){
						if (csd.getName().contentEquals(ContextSetName)){
							CurrentCSD = csd;
							break;
						}
					}
				} 
				
				//initialize output
				ExtendedCRON EC = new ExtendedCRON();
				
				//set name and type of CRON.
				EC.setName(this.Name);
				EC.setContextSetName(this.ContextSetName);
				EC.setSearchType("annotation");
				EC.setQueries(this.Queries);
								
				//initialize output
				//actual context mapping
				LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList = 
						new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
				
				//species names
				LinkedHashMap<String, String> SourceNames =
						new LinkedHashMap<String, String>();
				
				//contig names
				LinkedHashMap<String, HashSet<String>> ContigNames = 
						new LinkedHashMap<String, HashSet<String>>();
				
				//initialize a counter variable
				int Counter = 0;
				int SpeciesCounter = 0;
				
				//Initialize a hash map to use for the case of cassette contexts.
				HashSet<String> GenesForCassettes = new HashSet<String>();

				//Initialize a hash map for the use of RetainFraction-type analysis.
				//Annotation Counts
				LinkedHashMap<String, Integer> AnnCounts = new LinkedHashMap<String, Integer>();
	
				//increment number of genomic groupings
				int GGCounter = 0;
				
				//iterate through species.
				for (Entry<String, AnnotatedGenome> entry : fr.getOS().getSpecies().entrySet()) {

					//initialize output
					HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = null;
										
					if (CurrentCSD.isPreprocessed()){
						
						//pre-processed cases
						Matches = entry.getValue().AnnotationMatches(this.Queries, ContextSetName);

					} else {
						
						//on-the-fly
						if (CurrentCSD.getType().contentEquals("GenesBetween") && Queries.length != 2) {
							JOptionPane.showMessageDialog(null, "This gene grouping requires exactly two search queries.",
									"Inappropriate Number of Queries",JOptionPane.ERROR_MESSAGE);
						} else {
							Matches = entry.getValue().MatchesOnTheFly(this.Queries, null, CurrentCSD);
						}

					}
					
					//option: condense into single list + update matches
					if (CurrentCSD.isSingleOrganismAmalgamation()){
						Matches = Amalgamate(CurrentCSD.isSingleOrganismAmalgamation(), Matches);
					}
					
					//create an iterator for the HashSet
					Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
										
					//iterate through HashSet, with string-based keys
					int OperonCounter = 0; //reset operon counter
					while(it.hasNext()){
						
						//increment counter
						GGCounter++;
						
						//context unit object
						LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
						
						//Counts for retain fraction
						if (CurrentCSD.isRetainFractionEnabled()){
							
							//multiple copies of a single cluster num don't score better
							HashSet<String> ClusterAnns = new HashSet<String>();
							for (GenomicElementAndQueryMatch GandE : ContextSegment){
								String Ann = GandE.getE().getAnnotation().trim().toUpperCase();
								ClusterAnns.add(Ann);
							}
							Iterator<String> CS_it = ClusterAnns.iterator();
							
							while (CS_it.hasNext()){
								String ClusterAnn = CS_it.next();
								
								//add to hash map
								if (AnnCounts.get(ClusterAnn) == null){
									AnnCounts.put(ClusterAnn, 1);
								} else {
									int Count = AnnCounts.get(ClusterAnn);
									Count++;
									AnnCounts.put(ClusterAnn,Count);
								}
							}
						}
						
						//increment counters
						OperonCounter++;	
						Counter++;
						
						//define key
						String Key = entry.getKey() + "-" + Integer.toString(OperonCounter);
						
						//put elements into hashmap
						ContextSetList.put(Key, ContextSegment);
						
						//record other info
						SourceNames.put(Key, entry.getValue().getSpecies());
						
						HashSet<String> HSContigNames = new HashSet<String>();
						
						for (GenomicElementAndQueryMatch GandE : ContextSegment){
							HSContigNames.add(GandE.getE().getContig());
						}
						
						ContigNames.put(Key, HSContigNames);
					}
					
					//Retain Fraction
					if (CurrentCSD.isRetainFractionEnabled()){
						
						//New Data Objects
						//initialize output
						LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList_N = 
								new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
						
						//contig names
						LinkedHashMap<String, HashSet<String>> ContigNames_N = 
								new LinkedHashMap<String, HashSet<String>>();
						
						for (String s : ContextSetList.keySet()){
							
							//old
							LinkedList<GenomicElementAndQueryMatch> CS = ContextSetList.get(s);
							
							//new
							LinkedList<GenomicElementAndQueryMatch> CS_N = new LinkedList<GenomicElementAndQueryMatch>();
							HashSet<String> HSContigNames = new HashSet<String>();
							
							for (GenomicElementAndQueryMatch GandE : CS){
								
								//cluster ID
								String Annotation = GandE.getE().getAnnotation().trim().toUpperCase();
								
								//determine fraction
								Double Frac = (double) AnnCounts.get(Annotation) / (double) GGCounter;
								
								if (Frac >= CurrentCSD.getRetainFraction()){
									CS_N.add(GandE);
									HSContigNames.add(GandE.getE().getContig());
								}
							}
							
							//add new to set
							if (CS_N.size() > 0){
								ContextSetList_N.put(s,CS_N);
								ContigNames_N.put(s, HSContigNames);
							}

						}
						
						//update data - new contigs + genes
						ContextSetList = ContextSetList_N;
						ContigNames = ContigNames_N;
					}
					
					//Cassette cases: add genes
					if (isCassette){
						for (LinkedList<GenomicElementAndQueryMatch> MatchList : Matches){
							for (GenomicElementAndQueryMatch GandE : MatchList){
								GenesForCassettes.add(GandE.getE().getAnnotation());
							}
						}
					}
					
					SpeciesCounter++;
					progress = (int) (50*((double)SpeciesCounter/(double)fr.getOS().getSpecies().size()));
					//update progress
					setProgress(progress);
					
					//check for cancellations
					if (0 == progress%2){
						if (Thread.currentThread().isInterrupted()){
							setProgress(0);
							break;
						}
					}
					
				}
				
				//only proceed if the thread has not been cancelled.
				if (!Thread.currentThread().isInterrupted()){
					
					//re-computation
					if (CurrentCSD.getType().equals("GenesAround")){
						
						//attempt to standardize
						if (CurrentCSD.isRelativeBeforeAfter()){

							//first, retrieve an alternative list
							LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> AlternativeContextSetList = 
									new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
							
							//adjust values for alternative list
							int GenesBefore = CurrentCSD.getGenesBefore();
							int GenesAfter = CurrentCSD.getGenesAfter();
							
							CurrentCSD.setGenesBefore(GenesAfter);
							CurrentCSD.setGenesAfter(GenesBefore);

							//retrieve alternative set of hits
							for (Entry<String, AnnotatedGenome> entry : fr.getOS().getSpecies().entrySet()) {
								
								//Retrieve matches
								HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = 
										entry.getValue().MatchesOnTheFly(Queries, null, CurrentCSD);
								
								//create an iterator for the HashSet
								Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
								
								int AlternativeOperonCounter = 0; //reset operon counter
								while(it.hasNext()){
									
									//context unit object
									LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
									
									//increment counters
									AlternativeOperonCounter++;	
									
									//define key
									String Key = entry.getKey() + "-" + Integer.toString(AlternativeOperonCounter);
									
									//put elements into hashmap
									AlternativeContextSetList.put(Key, ContextSegment);
								}
								
							}
							
							LinkedHashMap<String, Strand> QueryHash = new LinkedHashMap<String, Strand>();
							
							//determine 'proper' orientation, based on number
							int StrandForward = 0; 
							int StrandReverse = 0;
							for (String s : ContextSetList.keySet()){
								
								LinkedList<GenomicElementAndQueryMatch> LL = ContextSetList.get(s);
								for (GenomicElementAndQueryMatch GandE : LL){
									if (GandE.isQueryMatch()){
										if (GandE.getE().getStrand().equals(Strand.POSITIVE)){
											StrandForward++;
											QueryHash.put(s, Strand.POSITIVE);
										} else {
											StrandReverse++;
											QueryHash.put(s, Strand.NEGATIVE);
										}
									}
								}
							}
							
							//initialize a final list
							LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> FinalContextSetList = 
									new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
							
							
							//write entries to the final list, if appropriate
							for (String s : ContextSetList.keySet()){
								LinkedList<GenomicElementAndQueryMatch> FwdLL = ContextSetList.get(s);
								LinkedList<GenomicElementAndQueryMatch> RevLL = AlternativeContextSetList.get(s);
								
								//don't flip Fwd
								if (StrandForward >= StrandReverse){
									if (QueryHash.get(s).equals(Strand.POSITIVE)){
										FinalContextSetList.put(s, FwdLL);
									} else {
										FinalContextSetList.put(s, RevLL);
									}
								//don't flip reverse	
								} else {
									if (QueryHash.get(s).equals(Strand.POSITIVE)){
										FinalContextSetList.put(s, RevLL);
									} else {
										FinalContextSetList.put(s, FwdLL);
									}
								}
							}
							
							//update EC + return CSD to original
							ContextSetList = FinalContextSetList;
							CurrentCSD.setGenesBefore(GenesBefore);
							CurrentCSD.setGenesAfter(GenesAfter);
						}
					}			
					
					//adjust values, if necessary, if context set type is a cassette
					if (isCassette){
						int CassetteCounter = 0;
						
						//create a new context list
						LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> CassetteContextSetList = 
								new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
						
						//species names
						LinkedHashMap<String, String> CassetteSourceNames =
								new LinkedHashMap<String, String>();
						
						//contig names
						LinkedHashMap<String, HashSet<String>> CassetteContigNames = 
								new LinkedHashMap<String, HashSet<String>>();
						
						//parameters for each
						String SpeciesKey;
						LinkedList<GenomicElementAndQueryMatch> SpeciesGenes;
						
						for (AnnotatedGenome AG : fr.getOS().getSpecies().values()){
							
							//Species Name
							SpeciesKey = AG.getSpecies() + "-1";
							SpeciesGenes = new LinkedList<GenomicElementAndQueryMatch>();
							
							//Contigs
							HashSet<String> Contigs = new HashSet<String>();
							
							for (GenomicElement E : AG.getElements()){
								if (GenesForCassettes.contains(E.getAnnotation())){
									
									//create appropriate GenomicElementAndQueryMatch
									GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
									GandE.setQueryMatch(true);
									GandE.setE(E);
									Contigs.add(E.getContig());
									
									//add to list
									SpeciesGenes.add(GandE);
								}
							}
							
							//add, if not empty
							if (!SpeciesGenes.isEmpty()){
								
								//update information
								CassetteContextSetList.put(SpeciesKey,SpeciesGenes);
								CassetteSourceNames.put(SpeciesKey, AG.getSpecies());
								CassetteContigNames.put(SpeciesKey, Contigs);
								
								CassetteCounter++;
							}
						}
						
						
						//When complete, add completed structures
						EC.setContexts(CassetteContextSetList);
						EC.setNumberOfEntries(CassetteCounter);
						
						//add source info
						EC.setSourceSpeciesNames(CassetteSourceNames);
						EC.setSourceContigNames(CassetteContigNames);
						
					} else {
						
						//add hash map to extended CRON
						EC.setContexts(ContextSetList);
						EC.setNumberOfEntries(Counter);
						
						//add source info
						EC.setSourceSpeciesNames(SourceNames);
						EC.setSourceContigNames(ContigNames);
					}

//					//debugging
//					System.out.println("Jpanbtn");
//					for (String s : EC.getContexts().keySet()){
//						HashSet<String> HSContigNames = EC.getSourceContigNames().get(s);
//						Iterator<String> it = HSContigNames.iterator();
//						while (it.hasNext()){
//							System.out.println(s + "-" + it.next());
//						}
//					}
					
					//Update the query data with all changes.
					CSDisplayData CSD = new CSDisplayData();
					CSD.setECandInitializeTreeLeaves(EC);
					WorkerQD.setCSD(CSD);
					
				}

				return null;
			}
			
			//by cluster
			protected Void SearchOrganismsbyCluster(){
				
				//re-set progress value
				int progress = 0;
				
				//cassette versus non-cassette context sets
				boolean isCassette = false;
				
				//recover the context set description
				ContextSetDescription CurrentCSD = null;
				for (ContextSetDescription csd : fr.getOS().getCSDs()){
					if (csd.getName().contentEquals(this.ContextSetName)){
						CurrentCSD = csd;
						break;
					}
				}
				
				//set context set name
				String ContextSetName = this.ContextSetName;
				
				//Cassette Option - Switch to the cassette type
				if (CurrentCSD.isCassette()){
					isCassette = true;
					ContextSetName = CurrentCSD.getCassetteOf();
					
					//note critical information
					boolean isNearbyOnly = CurrentCSD.isNearbyOnly();
					int NearbyOnlyVal = CurrentCSD.getNearbyLimit();
					
					//recover the context set description of the cassette
					for (ContextSetDescription csd : fr.getOS().getCSDs()){
						if (csd.getName().contentEquals(ContextSetName)){
							CurrentCSD = csd;
							break;
						}
					}
					
					CurrentCSD.setNearbyOnly(isNearbyOnly);
					CurrentCSD.setNearbyLimit(NearbyOnlyVal);
				} 

				//initialize output
				ExtendedCRON EC = new ExtendedCRON();
				
				//set name and type of CRON.
				EC.setName(this.Name);
				EC.setContextSetName(this.ContextSetName);
				EC.setSearchType("cluster");
				EC.setClusterNumbers(this.ClusterNumber);
				
				//initialize output
				LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList = 
						new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
				
				//species names
				LinkedHashMap<String, String> SourceNames =
						new LinkedHashMap<String, String>();
				
				//contig names
				LinkedHashMap<String, HashSet<String>> ContigNames = 
						new LinkedHashMap<String, HashSet<String>>();
				
				//initialize a counter variable
				int Counter = 0;
				int SpeciesCounter = 0;
				
				//Initialize a hash map to use for the case of cassette contexts.
				HashSet<Integer> GenesForCassettes = new HashSet<Integer>();
				
				//Initialize a hash map for the use of RetainFraction-type analysis.
							 //Cluster Counts
				LinkedHashMap<Integer, Integer> ClusterCounts 
					= new LinkedHashMap<Integer, Integer>();
				
				//increment number of genomic groupings
				int GGCounter = 0;
				
				//iterate through species.
				for (Entry<String, AnnotatedGenome> entry : fr.getOS().getSpecies().entrySet()) {

					HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = null;
					
					if (CurrentCSD.isPreprocessed()){
						
						//pre-processed cases
						Matches = entry.getValue().ClusterMatches(this.ClusterNumber, ContextSetName);

					} else {
						
						//on-the-fly
						if (CurrentCSD.getType().contentEquals("GenesBetween") && ClusterNumber.length != 2) {
							JOptionPane.showMessageDialog(null, "This gene grouping requires exactly two search queries.",
									"Inappropriate Number of Queries",JOptionPane.ERROR_MESSAGE);
						} else {

							Matches = entry.getValue().MatchesOnTheFly(null, this.ClusterNumber, CurrentCSD);

						}

					}
					
					//option: condense into single list + update matches
					if (CurrentCSD.isSingleOrganismAmalgamation()){
						Matches = Amalgamate(CurrentCSD.isSingleOrganismAmalgamation(), Matches);
					}
					
					//create an iterator for the HashSet
					Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
					
					//iterate through HashSet, with string-based keys
					int OperonCounter = 0; //reset operon counter
					while(it.hasNext()){
						
						//increment counter
						GGCounter++;
						
						//context unit object
						LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
						
						//Counts for retain fraction
						if (CurrentCSD.isRetainFractionEnabled()){
							
							//multiple copies of a single cluster num don't score better
							HashSet<Integer> ClusterNums = new HashSet<Integer>();
							for (GenomicElementAndQueryMatch GandE : ContextSegment){
								ClusterNums.add(GandE.getE().getClusterID());
							}
							Iterator<Integer> CS_it = ClusterNums.iterator();
							
							while (CS_it.hasNext()){
								int ClusterNum = CS_it.next();
								
								//add to hash map
								if (ClusterCounts.get(ClusterNum) == null){
									ClusterCounts.put(ClusterNum, 1);
								} else {
									int Count = ClusterCounts.get(ClusterNum);
									Count++;
									ClusterCounts.put(ClusterNum,Count);
								}
							}
						}
						
						//increment counters
						OperonCounter++;	
						Counter++;
						
						//define key
						String Key = entry.getKey() + "-" + Integer.toString(OperonCounter);
						
						//put elements into hashmap
						ContextSetList.put(Key, ContextSegment);
						
						//record other info
						SourceNames.put(Key, entry.getValue().getSpecies());
						
						HashSet<String> HSContigNames = new HashSet<String>();
						
						for (GenomicElementAndQueryMatch GandE : ContextSegment){
							HSContigNames.add(GandE.getE().getContig());
						}
						
						ContigNames.put(Key, HSContigNames);

					}
					
					//POST - FILTERING PART
					
					//Retain Fraction
					if (CurrentCSD.isRetainFractionEnabled()){
						
						//New Data Objects
						//initialize output
						LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextSetList_N = 
								new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
						
						//contig names
						LinkedHashMap<String, HashSet<String>> ContigNames_N = 
								new LinkedHashMap<String, HashSet<String>>();
						
						for (String s : ContextSetList.keySet()){
							
							//old
							LinkedList<GenomicElementAndQueryMatch> CS = ContextSetList.get(s);
							
							//new
							LinkedList<GenomicElementAndQueryMatch> CS_N = new LinkedList<GenomicElementAndQueryMatch>();
							HashSet<String> HSContigNames = new HashSet<String>();
							
							for (GenomicElementAndQueryMatch GandE : CS){
								
								//cluster ID
								int ClusterID = GandE.getE().getClusterID();
								
								//determine fraction
								Double Frac = (double) ClusterCounts.get(ClusterID) / (double) GGCounter;
								
								if (Frac >= CurrentCSD.getRetainFraction() && ClusterID != 0){
									CS_N.add(GandE);
									HSContigNames.add(GandE.getE().getContig());
								}
							}
							
							//add new to set
							if (CS_N.size() > 0){
								ContextSetList_N.put(s,CS_N);
								ContigNames_N.put(s, HSContigNames);
							}

						}
						
						//update data - new contigs + genes
						ContextSetList = ContextSetList_N;
						ContigNames = ContigNames_N;
					}
					
					//Cassette cases: add genes
					if (isCassette){
						for (LinkedList<GenomicElementAndQueryMatch> MatchList : Matches){
							for (GenomicElementAndQueryMatch GandE : MatchList){
								GenesForCassettes.add(GandE.getE().getClusterID());
							}
						}
					}
					
					//remove clusterID = 0 case
					GenesForCassettes.remove(0);
					
					SpeciesCounter++;
					progress = (int) (50*((double)SpeciesCounter/(double)fr.getOS().getSpecies().size()));
					
					//update progress
					setProgress(progress);
					
					//check for cancellations
					if (0 == progress%2){
						if (Thread.currentThread().isInterrupted()){
							setProgress(0);
							break;
						}
					}
					
				}
				
				//only proceed if the search has not already been cancelled.
				if (!Thread.currentThread().isInterrupted()){
					
					//re-computation
					if (CurrentCSD.getType().equals("GenesAround")){
						
						//attempt to standardize
						if (CurrentCSD.isRelativeBeforeAfter()){

							//first, retrieve an alternative list
							LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> AlternativeContextSetList = 
									new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
							
							//adjust values for alternative list
							int GenesBefore = CurrentCSD.getGenesBefore();
							int GenesAfter = CurrentCSD.getGenesAfter();
							
							CurrentCSD.setGenesBefore(GenesAfter);
							CurrentCSD.setGenesAfter(GenesBefore);

							//retrieve alternative set of hits
							for (Entry<String, AnnotatedGenome> entry : fr.getOS().getSpecies().entrySet()) {
								
								//Retrieve matches
								HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches = 
										entry.getValue().MatchesOnTheFly(null, this.ClusterNumber, CurrentCSD);
								
								//create an iterator for the HashSet
								Iterator<LinkedList<GenomicElementAndQueryMatch>> it = Matches.iterator();
								
								int AlternativeOperonCounter = 0; //reset operon counter
								while(it.hasNext()){
									
									//context unit object
									LinkedList<GenomicElementAndQueryMatch> ContextSegment = it.next();
																	
									//increment counters
									AlternativeOperonCounter++;	
									
									//define key
									String Key = entry.getKey() + "-" + Integer.toString(AlternativeOperonCounter);
									
									//put elements into hashmap
									AlternativeContextSetList.put(Key, ContextSegment);
								}
								
							}
							
							LinkedHashMap<String, Strand> QueryHash = new LinkedHashMap<String, Strand>();
							
							//determine 'proper' orientation, based on number
							int StrandForward = 0; 
							int StrandReverse = 0;
							for (String s : ContextSetList.keySet()){
								
								LinkedList<GenomicElementAndQueryMatch> LL = ContextSetList.get(s);
								for (GenomicElementAndQueryMatch GandE : LL){
									if (GandE.isQueryMatch()){
										if (GandE.getE().getStrand().equals(Strand.POSITIVE)){
											StrandForward++;
											QueryHash.put(s, Strand.POSITIVE);
										} else {
											StrandReverse++;
											QueryHash.put(s, Strand.NEGATIVE);
										}
									}
								}
							}
							
							//initialize a final list
							LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> FinalContextSetList = 
									new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
							
							
							//write entries to the final list, if appropriate
							for (String s : ContextSetList.keySet()){
								LinkedList<GenomicElementAndQueryMatch> FwdLL = ContextSetList.get(s);
								LinkedList<GenomicElementAndQueryMatch> RevLL = AlternativeContextSetList.get(s);
								
								//don't flip Fwd
								if (StrandForward >= StrandReverse){
									if (QueryHash.get(s).equals(Strand.POSITIVE)){
										FinalContextSetList.put(s, FwdLL);
									} else {
										FinalContextSetList.put(s, RevLL);
									}
								//don't flip reverse	
								} else {
									if (QueryHash.get(s).equals(Strand.POSITIVE)){
										FinalContextSetList.put(s, RevLL);
									} else {
										FinalContextSetList.put(s, FwdLL);
									}
								}
							}
							
							//update EC + return CSD to original
							ContextSetList = FinalContextSetList;
							CurrentCSD.setGenesBefore(GenesBefore);
							CurrentCSD.setGenesAfter(GenesAfter);
						}
					}	
				
					//adjust values, if necessary, if context set type is a cassette
					if (isCassette){
						int CassetteCounter = 0;
						
						//create a new context list
						LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> CassetteContextSetList = 
								new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
						
						//species names
						LinkedHashMap<String, String> CassetteSourceNames =
								new LinkedHashMap<String, String>();
						
						//contig names
						LinkedHashMap<String, HashSet<String>> CassetteContigNames = 
								new LinkedHashMap<String, HashSet<String>>();
						
						//parameters for each
						String SpeciesKey;
						LinkedList<GenomicElementAndQueryMatch> SpeciesGenes;
						
						//nearby only: merge all into a single set
						if (!CurrentCSD.isNearbyOnly()){
							for (AnnotatedGenome AG : fr.getOS().getSpecies().values()){
								
								//Species Name
								SpeciesKey = AG.getSpecies() + "-1";
								SpeciesGenes = new LinkedList<GenomicElementAndQueryMatch>();
								
								//Contigs
								HashSet<String> Contigs = new HashSet<String>();
								
								for (GenomicElement E : AG.getElements()){
									
									//candidate new values, to add
									if (GenesForCassettes.contains(E.getClusterID())){
										
										//create appropriate GenomicElementAndQueryMatch
										GenomicElementAndQueryMatch GandE = new GenomicElementAndQueryMatch();
										GandE.setQueryMatch(true);
										GandE.setE(E);
										Contigs.add(E.getContig());
										
										//add to list
										SpeciesGenes.add(GandE);
									}
								}
								
								//add, if not empty
								if (!SpeciesGenes.isEmpty()){
									
									//update information
									CassetteContextSetList.put(SpeciesKey,SpeciesGenes);
									CassetteSourceNames.put(SpeciesKey, AG.getSpecies());
									CassetteContigNames.put(SpeciesKey, Contigs);
									
									CassetteCounter++;
								}
							}
							
						} else {
							
							//iterate through all old sets, and adjust
							for (String s : ContextSetList.keySet()){
								
								//implement counter
								CassetteCounter++;
								
								//Retrieve Original
								LinkedList<GenomicElementAndQueryMatch> LL  = ContextSetList.get(s);
								
								//new list to merge
								LinkedList<GenomicElementAndQueryMatch> Additions = new LinkedList<GenomicElementAndQueryMatch>();
								
								//Retrieve appropriate organisms
								String OrgName[]  = s.split("-");
								String WholeName = "";
								for (int i = 0; i < OrgName.length-1; i++){
									WholeName = WholeName + OrgName[i] + "-";
								}
								WholeName = (String) WholeName.subSequence(0, WholeName.length()-1);
								AnnotatedGenome AG = fr.getOS().getSpecies().get(WholeName);
								
								//debugging
								//System.out.println(WholeName);
								//System.out.println(AG.getSpecies());
								
								//iterate through elements, find elements to add.
								for (GenomicElement E : AG.getElements()){
									
									//gene has appropriate ID
									if (GenesForCassettes.contains(E.getClusterID())){
										
										boolean AddThisGene = false;
										
										for (GenomicElementAndQueryMatch GandE : LL){
											GenomicElement E2 = GandE.getE();
											
//											//debugging
//											if (E.getStart() == 382735 && E.getStop() == 383745){
//												System.out.println(CurrentCSD.getNearbyLimit());
//												System.out.println(Math.abs(E2.getStart() - E.getStop()));
//												System.out.println(Math.abs(E.getStart() - E2.getStop()));
//											}
											
											//Add a gene, or not
											if (E.getContig().equals(E2.getContig())){
												if  (Math.abs(E2.getStart() - E.getStop()) <= CurrentCSD.getNearbyLimit() ||
													Math.abs(E.getStart() - E2.getStop()) <= CurrentCSD.getNearbyLimit()){
													AddThisGene = true;
													break;
												}

											}
										}
										
										//add the new gene
										if (AddThisGene){
											GenomicElementAndQueryMatch GandE_n = new GenomicElementAndQueryMatch();
											GandE_n.setE(E);
											GandE_n.setQueryMatch(false);
											Additions.add(GandE_n);
										}
									}
									
								}
								
								//debugging
								//System.out.println(s + ": " + Additions.size());
								
								//Add the list, and sort it.
								LL.addAll(Additions);
								Collections.sort(LL, new AnnotatedGenome.SortGandEByElements());
								
								//write this list into the new hash map
								CassetteContextSetList.put(s,LL);
							}
							
							//update variables that didn't change
							CassetteSourceNames = SourceNames;
							CassetteContigNames = ContigNames;
							
						}
						
						//When complete, add completed structures
						EC.setContexts(CassetteContextSetList);
						EC.setNumberOfEntries(CassetteCounter);
						
						//add source info
						EC.setSourceSpeciesNames(CassetteSourceNames);
						EC.setSourceContigNames(CassetteContigNames);
						
					} else {
						
						//add hash map to extended CRON
						EC.setContexts(ContextSetList);
						EC.setNumberOfEntries(Counter);
						
						//add source info
						EC.setSourceSpeciesNames(SourceNames);
						EC.setSourceContigNames(ContigNames);
					}
					
					//Update the query data with all changes.
					CSDisplayData CSD = new CSDisplayData();
					CSD.setECandInitializeTreeLeaves(EC);
					WorkerQD.setCSD(CSD);

				}

				return null;
			}
			
			//generalized search - to do?
			protected Void SearchOrganisms(){
				
				return null;
			}
			
			//both
 			protected HashSet<LinkedList<GenomicElementAndQueryMatch>> Amalgamate(boolean Amalgamate, HashSet<LinkedList<GenomicElementAndQueryMatch>> Matches){
				
				//option: condense into single list + update matches
				if (Amalgamate){
					
					//Initialize holding cell
					LinkedList<GenomicElementAndQueryMatch> CondensedList = new LinkedList<GenomicElementAndQueryMatch>();
					
					//create an iterator
					Iterator<LinkedList<GenomicElementAndQueryMatch>> itp = Matches.iterator();
					
					//march through entries, condense
					while (itp.hasNext()){
						CondensedList.addAll(itp.next());
					}
					
					//Initialize new output + write condensed
					HashSet<LinkedList<GenomicElementAndQueryMatch>> UpdatedMatches =
							new HashSet<LinkedList<GenomicElementAndQueryMatch>>();
					UpdatedMatches.add(CondensedList);
					
					//reset
					return UpdatedMatches;
					
				} else {
					
					//just return original value
					return Matches;
				}
			}
			
			//Optional Operations
			
			//(1) Create a tree panel of search results
			//============================================//
			protected Void CreateSearchPanel(){
				
				//update search results frame
				SearchResultsFrame = new FrmSearchResults(fr,WorkerQD.getCSD());
				WorkerQD.setCSD(SearchResultsFrame.getCSD());
				WorkerQD.setSRF(SearchResultsFrame);
				return null;
			}
			
			//(2) Compute Dendrogram, render tree
			//============================================//
			protected Void ComputeDendrogram(){

				//Create DE
				try {
					
					//retrieve EC, + modify
					ExtendedCRON EC = this.WorkerQD.getCSD().getEC();
					EC.setCustomDissimilarities(fr.getOS().getCustomDissimilarities());
					EC.computePairwiseDistances(DissimilarityMethod);
					EC.exportDistancesToField();

					//create a DE out of the deal
					this.ProcessedDE = new DadesExternes(EC);
					de = this.ProcessedDE;
					
					//replace EC
					this.WorkerQD.getCSD().setEC(EC);
					this.WorkerQD.setDe(de);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				//get distances, compute dendrogram
				multiDendro = ProcessedDE.getMatriuDistancies();
				minBase = Double.MAX_VALUE;
				nbElements = multiDendro.getCardinalitat();
				
				Reagrupa rg;
				MatriuDistancies mdNew;
				double b;
				int progress;

				while (multiDendro.getCardinalitat() > 1) {
					
					//check for cancellations
					if (Thread.currentThread().isInterrupted()){
						break;
					}
					
					try {
						
						//CLUSTERING FROM DISTANCES DATA
						rg = new Reagrupa(multiDendro, typeData, method, precision);
						
						mdNew = rg.Recalcula();
						
						//SET THE CURRENT MULTIDENDROGRAM TO THE RESULT FROM RG.RECALCULA()
						multiDendro = mdNew;
						this.WorkerQD.setMultiDendro(mdNew);
						
						b = multiDendro.getArrel().getBase();
						if ((b < minBase) && (b != 0)) {
							minBase = b;
						}
						
						//if (DisplayOutput){ 
							
							progress = 50 + 50
									* (nbElements - multiDendro.getCardinalitat())
									/ (nbElements - 1);
							setProgress(progress);

						//}

					} catch (final Exception e) {
						//showError(e.getMessage());
						if (!fr.isSearchWorkerCancelled()){
							showError("problems in calculating dendrogram.");
						} else {
							
							//re-set cursor, progress bar
							fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							setProgress(0);
							progressBar.setString("");
							progressBar.setBorderPainted(false);
						}
					}
				}
				//check for cancellations
				if (!Thread.currentThread().isInterrupted()){

					//set field
					this.RootCluster = multiDendro.getArrel();
					
				}

				
				//debugging
				//System.out.println("Root cluster determined to be " + this.RootCluster);
				
				return null;
			}
			
			//(3) Compute Context Graph
			//============================================//
			protected Void ComputeContextGraph(){
				return null;
			}
			
			//(4) Render Phylogeny
			//============================================//
			protected Void RenderPhylogeny(){
				return null;
			}
			
			//following search + dendrogram computation
			public void done(){
							
			//System.out.println("done fr.isSearchWorkerCancelled(): " + fr.isSearchWorkerCancelled());

			//re-set cursor, progress bar
			fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			setProgress(0);
			progressBar.setString("");
			progressBar.setBorderPainted(false);
				
//			//only do these things if the searchworker is not recognized as cancelled.
//			if (!fr.isSearchWorkerCancelled()){
//			//if (!Thread.currentThread().isInterrupted()){
//				
//				//proceed if exception thrown
//				if (!ExceptionThrown){
//					
//					//process completed!
//					ProcessCompleted = true;
//					fr.setTmpCluster(RootCluster);
//					
//					if (DisplayOutput){
//						
//						//try to update values
//						try {
//							//update values for display
//							if (AnalysesList.isOptionComputeDendrogram()){
//								multiDendro.getArrel().setBase(minBase);
//							}
//							showCalls(action, this.WorkerQD); //pass on the QD + display options
//						} catch (Exception ex) {
//							
//						}
//
//					}
//				}
//				
//			} else {
//				
//				System.out.println("Search process cancelled (done)");
//				
//				//default: search worker is not cancelled.
//				fr.setSearchWorkerCancelled(false);
//			}

			//no search worker is running, any more
			//fr.setSearchWorkerRunning(false);

			}
				
		}
		
		public Jpan_btn_NEW(final FrmPrincipalDesk fr) {
			super();
			this.fr = fr;
			this.jb = this;
			this.getPanel();
			this.searchFieldSize = searchField.getPreferredSize();
			this.setVisible(true);
			
			//DATA SOURCE - initialize
			fitx = new FitxerDades();	
			fitx.setNom("");
			fitx.setPath("");

		}

		private void getPanel() {
			//initialize panel
			this.setLayout(new GridBagLayout());
			this.setBorder(BorderFactory.createTitledBorder("Gene Context Search")); // File
			final GridBagConstraints c = new GridBagConstraints();
			int gridy = 0;
			
			//initial GridBagLayout parameters
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 1;
			c.insets = new Insets(1,1,1,1);
			
			//Total grid width: 4
			//Total grid height: 9
			
			//Search genomes section heading
			c.gridx = 0;
			c.gridy = gridy;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 4;
			SearchGenomes = new JLabel(" SEARCH GENOMES");
			SearchGenomes.setBackground(Color.GRAY);
			SearchGenomes.setOpaque(true);
			SearchGenomes.setFont(fontStandard);
			add(SearchGenomes,c);
			gridy++;
			
			//search type button group definition
			strAnnsearch = "Annotation Search";
			strClusearch = "Cluster Number";
			annotationSearch = new JRadioButton(strAnnsearch);
			clusterSearch = new JRadioButton(strClusearch);
			annotationSearch.setFont(fontStandard);
			clusterSearch.setFont(fontStandard);
			searchType = new ButtonGroup();
			searchType.add(annotationSearch);
			searchType.add(clusterSearch);
			
			//set default state
			searchType.setSelected(annotationSearch.getModel(), true);
//			if (fr.getOS().isGeneClustersLoaded() == true){
//				searchType.setSelected(clusterSearch.getModel(), true);
//			} else {
//				searchType.setSelected(annotationSearch.getModel(), true);
//			}
			
			// display on panel
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(annotationSearch, c);
			c.gridx = 2;
			c.gridwidth = 2;
			c.gridy = gridy;
			add(clusterSearch, c);
			gridy++;
			
			
			// Searchable text
			c.ipady = 5;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			searchField = new JTextField();
			searchField.setText(""); // Enter search bar
			searchField.addActionListener(this);
			searchField.setEditable(true);
			searchField.setColumns(20); // this value may wind up changing, depending on the system.
			add(searchField, c);
			gridy++;

			//Submit search
			c.ipady = 0;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			strSubmit = "Submit Search";
			btnSubmit = new JButton(strSubmit);
			btnSubmit.addActionListener(this);
			btnSubmit.setFont(fontStandard);
			add(btnSubmit, c);
			
			//cancel button
			c.ipady = 0;
			c.gridx = 2;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			btnCancel = new JButton(strCancel);
			btnCancel.addActionListener(this);
			btnCancel.setFont(fontStandard);
			add(btnCancel, c);
			gridy++;
			
			// progress bar
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			progressBar = new JProgressBar(0, 100);
			progressBar.setBorderPainted(false);
			progressBar.setStringPainted(false);
			progressBar.setFont(fontStandard);
			progressBar.setForeground(Color.BLUE);
			progressBar.setValue(0);
			add(progressBar, c);
			gridy++;			
			
			//Genome section heading
			c.gridx = 0;
			c.gridy = gridy;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 4;
			ContextSetSelect = new JLabel(" SELECT CONTEXT SET");
			ContextSetSelect.setBackground(Color.GRAY);
			ContextSetSelect.setOpaque(true);
			ContextSetSelect.setFont(fontStandard);
			add(ContextSetSelect,c);
			gridy++;
			
			// Context Set Text label
			c.ipady = 5;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			//c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(1, 1, 1, 1);
			contextSetHeader = new JTextField();
			contextSetHeader.setText("Context Set:"); // context set currently loaded
			contextSetHeader.addActionListener(this);
			contextSetHeader.setEditable(false);
			contextSetHeader.setFont(fontStandard);
			add(contextSetHeader, c);
			
			// drop-down menu for Context Sets
			c.ipady = 0;
			c.gridx = 1;
			c.gridy = gridy;
			c.gridwidth = 3;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			strUpdate = Language.getLabel(110); // Update
			String[] ContextArray;
			
			//String[] 
			if (fr.getOS() == null){
				ContextArray = new String[1];
				ContextArray[0] = "<none>";
			} else {
				ContextArray = convertContextSets(fr.getOS().getCSDs());
			}

			contextSetMenu = new JComboBox<String>(ContextArray);
			contextSetMenu.addActionListener(this);
			contextSetMenu.setEnabled(true);
			contextSetMenu.setFont(fontStandard);
			add(contextSetMenu, c);
			gridy++;

			//manage context sets button
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(1, 1, 1, 1);
			btnManage = new JButton(strManageCS);
			btnManage.addActionListener(this);
			btnManage.setEnabled(true);
			btnManage.setFont(fontStandard);
			add(btnManage, c);
			gridy++;
			
//			// btn update
//			c.gridx = 2;
//			c.gridy = gridy;
//			c.gridwidth = 1;
//			c.gridheight = 1;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.insets = new Insets(1, 1, 1, 1);
//			//strUpdate = Language.getLabel(110); // Update
//			strUpdate = "Update Settings";
//			btnUpdate = new JButton(strUpdate);
//			btnUpdate.addActionListener(this);
//			btnUpdate.setEnabled(true);
//			btnUpdate.setFont(fontStandard);
//			add(btnUpdate, c);
//			gridy++;
			
//			//New elements, version 2.0
//			// Load sequence Motif label
//			c.gridx = 0;
//			c.gridy = gridy;
//			c.gridheight = 1;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.gridwidth = 4;
//			AddRegMotif = new JLabel(" LOAD REGULATORY MOTIF(S)");
//			AddRegMotif.setBackground(Color.GRAY);
//			AddRegMotif.setOpaque(true);
//			AddRegMotif.setFont(fontStandard);
//			add(AddRegMotif,c);
//			gridy++;			
			
			// empty space
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			JLabel spacer = new JLabel(" ");
			spacer.setFont(fontStandard);
			add(spacer, c);
			gridy++;
			
			// btn update
			c.ipady = 10;
			gridy++;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			//strUpdate = Language.getLabel(110); // Update
			//strUpdate = "Update Display Settings";
			btnUpdate = new JButton(strUpdate);
			btnUpdate.addActionListener(this);
			btnUpdate.setEnabled(false);
			//btnUpdate.setFont(fontStandard);
			btnUpdate.setFont(bigFont);
			add(btnUpdate, c);
			gridy++;
			
//			progressBar.setStringPainted(true);
//			progressBar.setValue(100);
//			progressBar.setBackground(Color.BLUE);

		}

		public static void enableUpdate() {
			if (precisionCorrect && axisMinCorrect && axisMaxCorrect
					&& axisSeparationCorrect && axisEveryCorrect
					&& axisDecimalsCorrect) {
				btnUpdate.setEnabled(true);
			} else {
				btnUpdate.setEnabled(false);
			}
		}

		public static String getFileNameNoExt() {
			String name = "";
			if (fitx != null) {
				name = fitx.getNomNoExt();
			}
			return name;
		}

		public static void setFileName(String name) {
			txtFileName.setText(name);
		}

		public MatriuDistancies getMatriu() {

			return de.getMatriuDistancies();

		}

		// BUTTONS PUSHED -> LOAD FILE OR UPDATE TREE
		@Override
		public void actionPerformed(final ActionEvent evt) {
			
			if (fr.getOS() != null){
				
				//cancel button - version 1.1
				if (evt.getSource().equals(btnCancel)){
					
					 //cancellations!
					 CancelBtn();

				}
				
				String action = null;
				FitxerDades fitxTmp;
				boolean ambDades = false;
				InternalFrameData ifd;
				double minBase;
				MDComputation mdComputation;
				String query;

				/*
				 * Available actions:
				 * (1) Load				 [Create a new Context Tree]
				 * (2) Reload			 [Update Context Tree - new matrix]
				 * (3) Redraw			 [Update Context Tree - old matrix]
				 * (4) cluster search    [take place of Reload]
				 */
				
				//initialize a new 'querydata' object whenever an action is taken - 
				//represents current search parameter space
				QueryData QD = new QueryData();
				QD.setOSName(fr.getOS().getName()); 	//Important later for context viewing.
				if (!evt.getSource().equals(contextSetMenu)){
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						QD.setAnnotationSearch(true);
					} else {
						QD.setAnnotationSearch(false);
					}
					QD.setContextSetName(contextSetMenu.getSelectedItem().toString());
					QD.setDissimilarityType(fr.getPanMenu().getCbDissimilarity().getSelectedItem().toString());
					QD.setAnalysesList(new PostSearchAnalyses(
							fr.getPanMenuTab().getJpo().getDrawSearchResults().isSelected(), //search results
							fr.getPanMenuTab().getJpo().getDrawContextTree().isSelected(), //draw context tree
							fr.getPanMenuTab().getJpo().getDrawContextGraph().isSelected(), //draw context graph
							fr.getPanMenuTab().getJpo().getDrawPhylogeneticTree().isSelected() //phylogeny
							));
				}
				
				//turn on search results, if nothing else turned on.
				if (evt.getSource().equals(searchField) || 
						evt.getSource().equals(btnSubmit) ||
						evt.getSource().equals(btnUpdate)){
					
					//check: if none selected, show search results only.
					if (!fr.getPanMenuTab().getJpo().getDrawSearchResults().isSelected() &&
							!fr.getPanMenuTab().getJpo().getDrawContextTree().isSelected() &&
							!fr.getPanMenuTab().getJpo().getDrawContextGraph().isSelected() &&
							!fr.getPanMenuTab().getJpo().getDrawPhylogeneticTree().isSelected()){
						System.out.println("No analyses were specified. Switching 'Print Search Results' on.");
						QD.getAnalysesList().setOptionDisplaySearches(true);
						fr.getPanMenuTab().getJpo().getDrawSearchResults().setSelected(true);
					}
					
				}


				//Search Query
				if (evt.getSource().equals(searchField) || evt.getSource().equals(btnSubmit)){
					
					//reset bad search flag
					boolean BadSearch = false;
					
					//all semicolon case
					String txt = searchField.getText().trim();
					for (int i = 0; i < txt.length(); i++){
						if (txt.charAt(i) == ';'){
							BadSearch = true;
						} else {
							BadSearch = false;
							break;
						}
					}
					
					//retrieve semicolons
					String[] L = searchField.getText().trim().split(";");
					for (String s : L){
						if (s.trim().equals("")){
							BadSearch = true;
							break;
						}
					}

					//Proceed with Query, if appropriate.
					if (!BadSearch) {
						
						//System.out.println("Search field invoked with query:" + searchField.getText());
						if (searchType.getSelection().equals(annotationSearch.getModel())){
							currentQuery = "Search Query: " + searchField.getText().trim();
						} else {
							currentQuery ="Search Query: Cluster(s) " + searchField.getText().trim();
						}
					
						action = "Load";
						buttonClicked = true;
						ambDades = true;
					
					} else {
						//showError("Please enter a query in the search bar.");
						JOptionPane.showMessageDialog(null, "One or more queries are empty string searches.\nPlease remove all empty string searches.",
								"Empty Search",JOptionPane.ERROR_MESSAGE);
					}
					
				} else if (evt.getSource().equals(btnUpdate)) {
					
					//set wait cursor
					fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					// UPDATE
					buttonClicked = true;
					ifd = currentInternalFrame.getInternalFrameData();

					if ((Jpan_Menu.getTypeData() == ifd.getTypeData())
							&& (Jpan_Menu.getMethod() == ifd.getMethod())
							&& (Jpan_Menu.getPrecision() == ifd.getPrecision())
							&& (QD.getDissimilarityType().equals(ifd.getQD().getDissimilarityType()))
							&& (QD.getContextSetName().equals(ifd.getQD().getContextSetName()))
							
							/* TODO:
							 * Currently set so that any tab change will cause for re-computation.  This should be changed, later,
							 * to account for re-computations of different kinds, for different analyses. (1-14-2013)
							 */
							&& (QD.getAnalysesList().isOptionDisplaySearches() == ifd.getQD().getAnalysesList().isOptionDisplaySearches())
							&& (QD.getAnalysesList().isOptionComputeDendrogram() == ifd.getQD().getAnalysesList().isOptionComputeDendrogram())
							&& (QD.getAnalysesList().isOptionComputeContextGraph() == ifd.getQD().getAnalysesList().isOptionComputeContextGraph())
							&& (QD.getAnalysesList().isOptionRenderPhylogeny() == ifd.getQD().getAnalysesList().isOptionRenderPhylogeny())){
							//&& (QD.getAnalysesList().equals(ifd.getQD().getAnalysesList()))){
					
						action = "Redraw"; // no new matrix required
						//System.out.println("Action = Redraw");
					} else {
						action = "Reload"; //a new matrix is required
						//System.out.println("Action = Reloaded");
//						System.out.println("Method:" + (Jpan_Menu.getMethod() == ifd.getMethod()));
//						System.out.println("Precision: " + (Jpan_Menu.getPrecision() == ifd.getPrecision()));
//						System.out.println("Dissimilarity Type: " + (QD.getDissimilarityType().equals(ifd.getQD().getDissimilarityType())));
//						System.out.println("Context Set Name: " + (QD.getContextSetName().equals(ifd.getQD().getContextSetName())));
						
						//the problem!
						//System.out.println("Analyses List: " + (QD.getAnalysesList().equals(ifd.getQD().getAnalysesList())));
//						
//						System.out.println("QD List:" + QD.getAnalysesList().isOptionDisplaySearches() 
//								+ " " + QD.getAnalysesList().isOptionComputeDendrogram()
//								+ " " + QD.getAnalysesList().isOptionComputeContextGraph()
//								+ " " + QD.getAnalysesList().isOptionRenderPhylogeny());
//						
//						System.out.println("ifd:" + ifd.getQD().getAnalysesList().isOptionDisplaySearches() 
//								+ " " + ifd.getQD().getAnalysesList().isOptionComputeDendrogram()
//								+ " " + ifd.getQD().getAnalysesList().isOptionComputeContextGraph()
//								+ " " + ifd.getQD().getAnalysesList().isOptionRenderPhylogeny());
					}
					ambDades = true;
					
				} else if (evt.getSource().equals(btnManage)){
					action = "manage contexts";
					buttonClicked = true;
					//new manageContextSets(this.fr, fr.getOS().getCSDs(), this);
					new manageContextSetsv2(this.fr, this);
				}
				
				//CARRY OUT ACTION
				if (ambDades && (action.equals("Load"))) {
					String TheName = searchField.getText() + " [" + contextSetMenu.getSelectedItem() + "]";
					QD.setName(TheName);
					try {
						
						//parse into candidates
						String[] Queries = searchField.getText().trim().split(";");
						minBase = Double.MAX_VALUE;
						
						if (searchType.getSelection().equals(annotationSearch.getModel())){
							
							//before carrying out search, ask user about their search.
							String Hypo = "hypothetical protein";
							String Unk = "Unknown function";
							
							if ((Hypo.contains(searchField.getText()) || Unk.contains(searchField.getText()) ||
									searchField.getText().length() <= 3) && QD.getAnalysesList().isOptionComputeDendrogram()
									&& fr.getPanDisplayOptions().getDrawContextTree().isSelected()){
								
								String SureYouWantToSearch = "You have entered a search query that may return a large number of results." + "\n"
										+ "Proceeding may cause this program to crash." + "\n"
										+ "Are you sure you would like to proceed?" + "\n";
								
								//ask question, and maybe proceed with search
								int SearchCheck = JOptionPane.showConfirmDialog(null,SureYouWantToSearch,
										"Proceed with search", JOptionPane.YES_NO_CANCEL_OPTION);
								
								if (SearchCheck == JOptionPane.YES_OPTION){
									this.ProceedWithSearch = true;
								} else {
									this.ProceedWithSearch = false;
									de = null; //this will effectively fast-forward to the catch statement
								}
							} else {
								this.ProceedWithSearch = true;
							}
							
							if (this.ProceedWithSearch == true){
							
								QD.setQueries(Queries);
								
								//single, interruptable Swing Worker
								CurrentSearch = new SearchWorker(QD,action,
										Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
										Jpan_Menu.getPrecision(), true);
								CurrentSearch.addPropertyChangeListener(this);
								
								
								//default: the search worker is not cancelled.
								fr.setSearchWorkerCancelled(false);
								
								CurrentSearch.execute();

							}

						} else {
							LinkedList<Integer> NumQueriesList = new LinkedList<Integer>();
							for (int i = 0; i < Queries.length; i++){
								try {
									if (Queries[i].contains("-")){
										String Rng[] = Queries[i].split("-");
										int Start = Integer.parseInt(Rng[0].trim());
										int Stop = Integer.parseInt(Rng[1].trim());
										for (int j = Start; j <= Stop; j++){
											NumQueriesList.add(j);
										}
									}
									NumQueriesList.add(Integer.parseInt(Queries[i].trim()));
								} catch (Exception ex){}
							}
							
							int[] NumQueries = new int[NumQueriesList.size()];
							for (int i = 0; i < NumQueriesList.size(); i++){
								NumQueries[i] = NumQueriesList.get(i);
							}
							
							//store 
							QD.setClusters(NumQueries);
							
							//try: unified swingworker approach
							CurrentSearch = new SearchWorker(QD,action,
									Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
									Jpan_Menu.getPrecision(), true);//phylogeny
							CurrentSearch.addPropertyChangeListener(this);
							
							//default: the search worker is not cancelled.
							fr.setSearchWorkerCancelled(false);
							
							CurrentSearch.execute();
						}


					} catch (Exception e1) {
						
						e1.printStackTrace();
						
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						
						buttonClicked = false;
						//showError(e1.getMessage());
						if (searchType.getSelection().equals(annotationSearch.getModel())){
							if (this.ProceedWithSearch == true){
								showError("There were no matches to the query (or queries).");
							}
						} else {
							//String LastCluster = Integer.toString(OS.LargestCluster);
							String errMsg = "There were no matches to the query (or queries).";
							showError(errMsg);
						}
					}
				} else if (ambDades && action.equals("Reload")) {
					
					//retrieve information from selected frame
					QueryData SelectedFrame = currentInternalFrame.getInternalFrameData().getQD();
					
					//update internal frame data
					SelectedFrame.setDissimilarityType(QD.getDissimilarityType());
					SelectedFrame.setContextSetName(QD.getContextSetName());
					SelectedFrame.setAnalysesList(QD.getAnalysesList());
					
					if (SelectedFrame.isAnnotationSearch()){
						CurrentSearch = new SearchWorker(SelectedFrame,action,
								Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
								Jpan_Menu.getPrecision(), true);//phylogeny
						CurrentSearch.addPropertyChangeListener(this);
						
						//default: the search worker is not cancelled.
						fr.setSearchWorkerCancelled(false);
						
						CurrentSearch.execute();
					} else {
						CurrentSearch = new SearchWorker(SelectedFrame,action,
								Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
								Jpan_Menu.getPrecision(), true);//phylogeny
						CurrentSearch.addPropertyChangeListener(this);
						
						//default: the search worker is not cancelled.
						fr.setSearchWorkerCancelled(false);
						
						CurrentSearch.execute();
					}
					
				// in this case, no need to modify what's already in the internal frame.
				} else if (ambDades && action.equals("Redraw")) {
					//only GUI updates - no recomputations
					showCalls(action, currentInternalFrame.getInternalFrameData().getQD());//phylogeny
					fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				} else {
					buttonClicked = false;
				}
				

//			} else if (fr.getCurrentLPW() != null){
//				System.out.println("Meeerh!");
//				if (evt.getSource().equals(btnCancel)){
//					if (fr.getCurrentLPW() != null){
//						fr.getCurrentLPW().cancel(true);
//						fr.setCurrentLPW(null);
//						progressBar.setIndeterminate(false);
//						progressBar.setValue(0);
//						fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//					}
//				}
			} else {
				if(!evt.getSource().equals(btnCancel)){
					fr.NoOS();
				}
			}
		}
		
		//cancel button
		public void CancelBtn(){
			
			//Kill search worker
			if (CurrentSearch != null){
				CurrentSearch.cancel(true);
				CurrentSearch = null;
				de = null;
			}
			
			//kill popular set retrieval worker
			if (fr.getCurrentLPW() != null){
				fr.getCurrentLPW().SelectedItem.setSelected(false);
				fr.getCurrentLPW().cancel(true);
				fr.setCurrentLPW(null);
			}
			
			//kill export sequences worker
			if (fr.getCurrentESW() != null) {
				fr.getCurrentESW().cancel(true);
				fr.setCurrentESW(null);
			}
			
			//kill display sequences worker
			if (fr.getCurrentRGW() != null){
				fr.getCurrentRGW().cancel(true);
				fr.setCurrentRGW(null);
			}
			
			//After cancellation, need to modify some things in the main thread.
			//Try to cut out ASAP - but there may be a better way.
			
			//Output-associated resets
			fr.setRenderGenomesWorkerCancelled(true);	//Rendered Genome Worker
			fr.setSearchWorkerCancelled(true); 			//Search Worker
			
			//GUI-related resets
			
			//progress bar back to defaults
			fr.getPanBtn().getProgressBar().setValue(0);
			fr.getPanBtn().getProgressBar().setIndeterminate(false);
			
			//switch cursor back to normal
			Component glassPane = fr.getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			//try to release the current CPU thread, so that 
			//the subordinate thread can be cancelled.
			try {
				Thread.sleep(1);
			} catch (Exception ex){
				
			}
			
			//message to console
			System.out.println("The process has been cancelled.");
			
		}
		
		public void showCalls(String action, QueryData qD) {

			try {
				fr.setCfgPhylo(null);	//for re-drawing.
				if (action.equals("Reload") || action.equals("Redraw")) {
					currentInternalFrame.doDefaultCloseAction();
				}

				/*
				 * GHETTO FIX: artificial opening/closing
				 */
				
				//CHANGE: commented out these two lines needed for mass-selection.
				//CHANGEBACK: this seems to help.
				show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision(), qD);
				currentInternalFrame.doDefaultCloseAction();
								
				/*
				 * GHETTO FIX: artificial opening/closing
				 */
				
				//actual showing (somehow, settings have been appropriately updated)
				show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision(), qD);
				btnUpdate.setEnabled(true);
				buttonClicked = false;
			} catch (Exception ex) {
				//showError("ShowCalls");
				ex.printStackTrace();
			}
		}

		public void show(String action, final metodo method, final int precision, QueryData qD) {

			boolean isUpdate;
			FrmInternalFrame pizarra;
			Config cfg;					//Configuration information for multidendrogram only
			Config cfgp = null;				//Configuration information for phylogenetic tree
			InternalFrameData ifd;
			Fig_Pizarra figPizarra;
			Fig_Pizarra figPhylo;

			//INTERNAL PANELS
			FrmSearchResults fSearch = null;
			FrmPiz fPiz = null;
			FrmGraph fGraph = null;
			FrmPhylo fPhylo = null;
			JScrollPane fPizSP = null;
			JScrollPane fSearchSP = null;
			JScrollPane fGraphSP = null;
			JScrollPane fPhyloSP = null;

			//update or not
			isUpdate = !action.equals("Load");

			try {

				//CREATE INTERNAL FRAME + ADD DATA
				pizarra = fr.createInternalFrame(isUpdate, method.name());
				pizarra.setTitle(qD.getName());
				
				//PREPARE INTERNAL FRAME DATA
				CSDisplayData CSD = qD.getCSD();

				//UPDATE CONFIGURATION INFORMATION
				cfg = fr.getConfig();
				cfg.setPizarra(pizarra);
				
				//OPTION: SEARCHES
				if (qD.getAnalysesList().isOptionDisplaySearches()){
					
					//create scroll panel from search results pane
					fSearch = SearchResultsFrame;
					fSearchSP = new JScrollPane(SearchResultsFrame);
					
					//update CSD with tree nodes
					CSD = fSearch.getCSD();
				}
				
				//OPTION: DENDROGRAM
				if (qD.getAnalysesList().isOptionComputeDendrogram()){

					//update configuration menu panel
					cfg.setFitxerDades(fitx);
					cfg.setMatriu(multiDendro);
					cfg.setHtNoms(de.getTaulaNoms()); //table names
					if (!cfg.isTipusDistancia()) {
						if (cfg.getOrientacioDendo().equals(Orientation.NORTH)) {
							cfg.setOrientacioDendo(Orientation.SOUTH);
						} else if (cfg.getOrientacioDendo().equals(Orientation.SOUTH)) {
							cfg.setOrientacioDendo(Orientation.NORTH);
						} else if (cfg.getOrientacioDendo().equals(Orientation.EAST)) {
							cfg.setOrientacioDendo(Orientation.WEST);
						} else if (cfg.getOrientacioDendo().equals(Orientation.WEST)) {
							cfg.setOrientacioDendo(Orientation.EAST);
						}
					}
					
					//create a new context tree panel
					fPiz = new FrmPiz(fr, CSD);
					
					// Set sizes
					fPiz.setSize(pizarra.getSize());
					fPiz.setPreferredSize(pizarra.getSize());
					
					//determine size of tree rendering based on number of elements
					setVerticalScrollValue(de.getTaulaNoms().size());
					Dimension d = new Dimension(pizarra.getWidth()-
							HorizontalScrollBuffer, VerticalScrollValue);
					fPiz.setPreferredSize(d);
					
					// Call Jpan_Menu -> internalFrameActivated()
					//pizarra.setVisible(true);
					if (action.equals("Load") || action.equals("Reload")) {
						Jpan_Menu.ajustaValors(cfg);
					}
//					
					//System.out.println("Breakpoint!");
					
					//These did not work. keep this in mind for later!
//					cfg.getConfigMenu().setIncrement(0.1);
//					cfg.getConfigMenu().setAxisDecimals(2);
					
					// Convert tree into figures
					figPizarra = new Fig_Pizarra(multiDendro.getArrel(), cfg);
					
					// Pass figures to the window
					fPiz.setFigures(figPizarra.getFigures());
					fPiz.setConfig(cfg);
					
					//scroll panel, with sizes
					fPizSP = new JScrollPane(fPiz);
					fPizSP.setSize(pizarra.getSize());
					fPizSP.setPreferredSize(pizarra.getSize());
					fPizSP.getVerticalScrollBar().setUnitIncrement(ScrollInc);
					
					//update CSD with context tree rectangles
					CSD = fPiz.getCSD();

					//update cfg information
					fr.setCfg(cfg);

				}
				
				//OPTION: GRAPH
				if (qD.getAnalysesList().isOptionComputeContextGraph()){
					fGraph = new FrmGraph(fr, CSD);
					fGraphSP = new JScrollPane(fGraph);
					
					//update CSD with context graph rectangles
					CSD = fGraph.getCSD();
				}

				//OPTION: PHYLOGENY
				if (qD.getAnalysesList().isOptionRenderPhylogeny()){
					
					//tree must be loaded 
					if (fr.getPanPhyTreeMenu().getCurrentParsedTree() != null){
												
						//initialize panel
						fPhylo = new FrmPhylo(fr, CSD);
						
						//update configuration information to be appropriate for the phylogenetic tree
						if (fr.getCfgPhylo() != null){
							cfgp = fr.getCfgPhylo();
						} else {
							cfgp = new Config(cfg.getConfigMenu());
						}

//						System.out.println("cfgp.getValorMaxim(): " + cfgp.getValorMaxim());
//						System.out.println("cfgp.getConfigMenu().isEscalaVisible(): " + cfgp.getConfigMenu().isEscalaVisible());
//						System.out.println("cfgp.getConfigMenu().isEtiquetaEscalaVisible(): " + cfgp.getConfigMenu().isEtiquetaEscalaVisible());
//						System.out.println("cfgp.getConfigMenu().isFranjaVisible(): " + cfgp.getConfigMenu().isFranjaVisible());
//						System.out.println("cfgp.getConfigMenu().isNomsVisibles(): "+ cfgp.getConfigMenu().isNomsVisibles());
//						
						//determine size of tree rendering based on number of elements
						Dimension d = new Dimension(pizarra.getWidth()-
								HorizontalScrollBuffer,CalculateVerticalScrollValue(PhyloTreeLeaves));
						fPhylo.setPreferredSize(d);
						
						
						//cfgp.getConfigMenu().setValMin(0.0);
						//adjust values, if necessary.
//						if (!qD.getAnalysesList().isOptionComputeDendrogram()) {
//							Jpan_Menu.ajustaValors(cfgp);
//						}
						
						// Convert tree into figures
						figPhylo = new Fig_Pizarra(fr.getPanPhyTreeMenu().getCurrentParsedTree(), cfgp);
						
						//debugging
//						LinkedList[] FigList = figPhylo.getFigures();
//						LinkedList<Marge> Marges = FigList[2];
//						for (Marge m : Marges){
//							System.out.println("m, Jpan_btn_NEW: " + m.getPhyloWeight());
//						}
						
						//Update configuration information
						CfgPanelMenu PhyloCfgPanel = cfgp.getConfigMenu();
						PhyloCfgPanel.setTipusDades(tipusDades.DISTANCIA);
						cfgp.setConfigMenu(PhyloCfgPanel);
						cfgp.setHtNoms(figPhylo.getHtNoms());
						
						//TEST - try changing this - it works
						//PhyloCfgPanel.setValMax(figPhylo.getLongestBranch());
						
						PhyloCfgPanel.setValMin(0);
						cfgp.setConfigMenu(PhyloCfgPanel);
						
						//add fields
						this.setPhyloTreeLength(figPhylo.getLongestBranch());
						this.setPhyloTreeLeaves(figPhylo.getHtNoms().size());
						
						//create a dummy 'MatriuDistancies' for this phylo tree.
						MatriuDistancies md = new MatriuDistancies(0);
						Cluster c = new Cluster();
						c.setFills(PhyloTreeLeaves);
						md.setArrel(c);
						cfgp.setMatriu(md);

						//adjust menu, if no context tree
						if (!qD.getAnalysesList().isOptionComputeDendrogram()) {
							Jpan_Menu.adjustValuesPhylo(cfgp);
						}

						//add figures + configuration information to frame
						fPhylo.setFigures(figPhylo.getFigures());
						fPhylo.setConfig(cfgp);
						
						//scroll panel
						fPhyloSP = new JScrollPane(fPhylo);
						fPhyloSP.setSize(pizarra.getSize());
						fPhyloSP.setPreferredSize(pizarra.getSize());
						fPhyloSP.getVerticalScrollBar().setUnitIncrement(ScrollInc);
						
						//update CSD with phylogenetic tree rectangles
						CSD = fPhylo.getCSD();
						
						//update config panel
						fr.setCfgPhylo(cfgp);
						
					} else {
						qD.getAnalysesList().setOptionRenderPhylogeny(false);
					}

				}
				
				//INTERNAL FRAME DATA
				qD.setCSD(CSD);
				//ifd = new InternalFrameData(de, multiDendro);
				ifd = new InternalFrameData(qD.getDe(), qD.getMultiDendro());
				ifd.setQD(qD);
				ifd.setContextGraphPanel(fGraph);
				ifd.setContextTreePanel(fPiz);
				ifd.setSearchResultsFrame(fSearch);
				ifd.setPhyloTreePanel(fPhylo);
				ifd.setCfg(cfg);
				ifd.setCfgp(cfgp);
				
				pizarra.setInternalFrameData(ifd);
				
				//CREATE FINAL FRAME
				JPanel TabbedWrapper = new JPanel();
				TabbedWrapper.setLayout(new BorderLayout());

				//tabbed frame for internal frame
				FrmTabbed AnalysisResults = new FrmTabbed(fPizSP,fGraphSP,fSearchSP,fPhyloSP,
						qD.getAnalysesList(), fr);
				
				TabbedWrapper.add(AnalysisResults, BorderLayout.CENTER);
				fr.getPanGenome().setCSD(CSD);
				
				//ADD TABBED PANEL TO FRAME
				pizarra.add(TabbedWrapper);	//Tabbed menu component with panel
				
				//CONTAINER OWNERSHIP
				pizarra.setInternalPanel(fPiz);
				pizarra.setVisible(true);
				
				//Mod Oct 22
				if (action.equals("Load") || action.equals("Reload")) {
					Jpan_Menu.ajustaValors(cfg);
				}
				
				//both Jpan_btn_NEW and fr
				this.currentInternalFrame = pizarra;
				fr.setCurrentFrame(pizarra);
		
//				//debugging
//				System.out.println("Breakpoint!");

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		private FitxerDades getFitxerDades() {
			return this.getFitxerDades(System.getProperty("user.dir"));
		}

		//DATA FILE
		private FitxerDades getFitxerDades(final String sPath) {
			
			//use pre-existing 'FileDialog' GUI window to retrieve file
			final FileDialog fd = new FileDialog(fr, Language.getLabel(9),
					FileDialog.LOAD);
			FitxerDades fitx; //Data file type is just a bunch of relevant strings

			fitx = new FitxerDades();
			fd.setDirectory(sPath);
			fd.setVisible(true);
			if (fd.getFile() == null) {
				fitx = null;
			} else {
				fitx.setNom(fd.getFile());
				fitx.setPath(fd.getDirectory());
			}
			return fitx; //A bunch of strings relating to the file information.
		}

		private void showError(final String msg) {
			JOptionPane.showMessageDialog(null, msg, Language.getLabel(7),
					JOptionPane.ERROR_MESSAGE);
		}

		//Interal Frame - related methods
		@Override
		public void internalFrameActivated(InternalFrameEvent e) {
			InternalFrameData ifd;

			currentInternalFrame = (FrmInternalFrame) e.getSource();
			btnUpdate.setEnabled(true);
			
			//this is necessary owing to confusion in what activates an internal frame.
			if (!buttonClicked) {
				fr.setCurrentFrame(currentInternalFrame);
				ifd = currentInternalFrame.getInternalFrameData();
				de = ifd.getDadesExternes();
				//fitx = de.getFitxerDades();
				multiDendro = ifd.getMultiDendrogram();
				SearchResultsFrame = ifd.getSearchResultsFrame();
				//this is not likely the point of contention.
				//System.out.println("Jpan_btn_New.internalFrameActivated(): " + ifd.getValMax());
				Jpan_Menu.setConfigPanel(ifd);
				
				//also set current panel (if it exists)
				//fr.setCurrentFpizpanel(currentInternalFrame.getInternalPanel());
			}
			
		}

		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			FrmInternalFrame.decreaseOpenFrameCount();
			btnUpdate.setEnabled(false);
//			if (!buttonClicked) {
//				Jpan_Menu.clearConfigPanel();
//			}
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameOpened(InternalFrameEvent e) {
			//currentInternalFrame = (FrmInternalFrame) e.getSource();
		}

		@Override
		public void internalFrameIconified(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameDeiconified(InternalFrameEvent e) {
//			InternalFrameData ifd;
//			
//			currentInternalFrame = (FrmInternalFrame) e.getSource();
//			btnUpdate.setEnabled(true);
//			if (!buttonClicked) {
//				fr.setCurrentFrame(currentInternalFrame);
//				ifd = currentInternalFrame.getInternalFrameData();
//				de = ifd.getDadesExternes();
//				//fitx = de.getFitxerDades();
//				multiDendro = ifd.getMultiDendrogram();
//				Jpan_Menu.setConfigPanel(ifd);
//			}
		}

		@Override
		public void internalFrameDeactivated(InternalFrameEvent e) {
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName() == "progress") {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			}
		}

	// generated methods - automatically detect scroll value
		
		public int getVerticalScrollValue() {
			return VerticalScrollValue;
		}

		public void setVerticalScrollValue(int numberOfEntries) {
			//VerticalScrollValue = 15*numberOfEntries + 250;
			VerticalScrollValue = CalculateVerticalScrollValue(numberOfEntries);
		}
		
		public int CalculateVerticalScrollValue(int numberOfEntries){
			int CalculatedScrollValue = 15*numberOfEntries + 250;
			return CalculatedScrollValue;
		}

		public boolean isInteger( String input ) {
		    try {
		        Integer.parseInt( input );
		        return true;
		    }
		    catch( Exception e ) {
		        return false;
		    }
		}		

		public String[] convertContextSets(LinkedList<ContextSetDescription> ListOfContextSets){
			
			//initialize output array
			String[] ArrayOfContextSets = new String[ListOfContextSets.size()];
			
			//iterate through array
			for (int i = 0; i < ListOfContextSets.size(); i++){
				ArrayOfContextSets[i] = ListOfContextSets.get(i).getName();
			}
			
			return ArrayOfContextSets;
		}

		public JComboBox getContextSetMenu() {
			return contextSetMenu;
		}

		public void setContextSetMenu(JComboBox contextSetMenu) {
			this.contextSetMenu = contextSetMenu;
		}

		public double getPhyloTreeLength() {
			return PhyloTreeLength;
		}

		public void setPhyloTreeLength(double phyloTreeLength) {
			PhyloTreeLength = phyloTreeLength;
		}

		public int getPhyloTreeLeaves() {
			return PhyloTreeLeaves;
		}

		public void setPhyloTreeLeaves(int phyloTreeLeaves) {
			PhyloTreeLeaves = phyloTreeLeaves;
		}

		public JProgressBar getProgressBar() {
			return progressBar;
		}

		public void setProgressBar(JProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		public JRadioButton getAnnotationSearch() {
			return annotationSearch;
		}

		public void setAnnotationSearch(JRadioButton annotationSearch) {
			this.annotationSearch = annotationSearch;
		}

		public JRadioButton getClusterSearch() {
			return clusterSearch;
		}

		public void setClusterSearch(JRadioButton clusterSearch) {
			this.clusterSearch = clusterSearch;
		}

		public FrmSearchResults getSearchResultsFrame() {
			return SearchResultsFrame;
		}

		public void setSearchResultsFrame(FrmSearchResults searchResultsFrame) {
			SearchResultsFrame = searchResultsFrame;
		}

		public MatriuDistancies getMultiDendro() {
			return multiDendro;
		}

		public void setMultiDendro(MatriuDistancies multiDendro) {
			this.multiDendro = multiDendro;
		}

		public DadesExternes getDe() {
			return de;
		}

		public void setDe(DadesExternes de) {
			this.de = de;
		}


	}
