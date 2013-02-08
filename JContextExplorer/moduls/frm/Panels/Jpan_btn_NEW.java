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
	import parser.Fig_Pizarra;
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
		private static JButton btnManage, btnSubmit, btnUpdate;
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
		
		private int HorizontalScrollBuffer = 30;
		private int VerticalScrollValue = 1500;
		
		//organism set
		private OrganismSet OS;

		private String currentQuery;

	// ----- New Fields (1.1) --------------------------------------------//			
		
		//searches are handled by a single Swing Worker
		private SearchWorker CurrentSearch = null;
		private String strUpdate = "Update";
		
		//display search results
		private FrmSearchResults SearchResultsFrame = null;
		
		//parameters associated with phylo tree
		private double PhyloTreeLength;
		private int PhyloTreeLeaves;
				
	// ----- Methods -----------------------------------------------//	
		
		// ---- Thread Workers for Searches ------------------------//

		//Unified SwingWorker for searches + multidendrogram computations
		class SearchWorker extends SwingWorker<Void, Void>{

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
			
			//constructor
			public SearchWorker(final QueryData QD, final String action,
					final tipusDades typeData, final metodo method, final int precision){

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

			}
			
			@Override
			protected Void doInBackground() throws Exception {

			try {	
				
				//visualization-related things
				fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				progressBar.setBorderPainted(true);
				progressBar.setString(null);
				multiDendro = null;
				de = null;
				
				//Search organisms
				if (AnnotationSearch){
					SearchOrganismsbyAnnotation();
				} else {
					SearchOrganismsbyCluster();
				}
				if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() == 1){
					System.out.println("Search Completed. " + this.WorkerQD.getCSD().getEC().getNumberOfEntries() + " Gene Grouping Recovered.");					
				} else {
					System.out.println("Search Completed. " + this.WorkerQD.getCSD().getEC().getNumberOfEntries() + " Gene Groupings Recovered.");					
				}

				//adjust analyses options based on number of matches.
				if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() < 2){
					this.WorkerQD.getAnalysesList().setOptionComputeDendrogram(false);
					this.WorkerQD.getAnalysesList().setOptionComputeContextGraph(false);
					if (this.WorkerQD.getCSD().getEC().getNumberOfEntries() == 0){
						String errMsg = "There were no matches to the query (or queries).";
						showError(errMsg);
					}
				}
				
				//Analyses options
				//=================================//
				
				//(1) Construct search pane
				if (AnalysesList.isOptionDisplaySearches()){
					CreateSearchPanel();
					
				}

				//(2) Compute dendrogram
				if (AnalysesList.isOptionComputeDendrogram()){
					ComputeDendrogram();
				}
				
			} catch (Exception ex) {
				showError("There were no matches to the query (or queries).");
				//ex.printStackTrace();
			}
			
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
				for (ContextSetDescription csd : OS.getCSDs()){
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
					
					//recover the context set description of the cassette
					for (ContextSetDescription csd : OS.getCSDs()){
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
				LinkedHashMap<String, String> ContigNames = 
						new LinkedHashMap<String, String>();
				
				//initialize a counter variable
				int Counter = 0;
				int SpeciesCounter = 0;
				
				//Initialize a hash map to use for the case of cassette contexts.
				HashSet<String> GenesForCassettes = new HashSet<String>();

				//iterate through species.
				for (Entry<String, AnnotatedGenome> entry : OS.getSpecies().entrySet()) {

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
					
					//Cassette cases: add genes
					if (isCassette){
						for (LinkedList<GenomicElementAndQueryMatch> MatchList : Matches){
							for (GenomicElementAndQueryMatch GandE : MatchList){
								GenesForCassettes.add(GandE.getE().getAnnotation());
							}
						}
					}
					
					SpeciesCounter++;
					progress = (int) (50*((double)SpeciesCounter/(double)OS.getSpecies().size()));
					//update progress
					setProgress(progress);
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
					LinkedHashMap<String, String> CassetteContigNames = 
							new LinkedHashMap<String, String>();
					
					//parameters for each
					String SpeciesKey;
					LinkedList<GenomicElementAndQueryMatch> SpeciesGenes;
					
					for (AnnotatedGenome AG : OS.getSpecies().values()){
						
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
							
							//compress contigs to a single string
							String AllContigs = "";
							int ContigCounter = 0;
							for (String s : Contigs){
								ContigCounter++;
								AllContigs = AllContigs + "[Contig_" + ContigCounter + "]=" + s + ";";
								
								//displayable case (single contig)
								if (Contigs.size() == 1){
									AllContigs = s;
								}
							}
							CassetteContigNames.put(SpeciesKey, AllContigs);
							
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

				//Update the query data with all changes.
				CSDisplayData CSD = new CSDisplayData();
				CSD.setECandInitializeTreeLeaves(EC);
				WorkerQD.setCSD(CSD);
				
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
				for (ContextSetDescription csd : OS.getCSDs()){
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
					
					//recover the context set description of the cassette
					for (ContextSetDescription csd : OS.getCSDs()){
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
				EC.setSearchType("cluster");
				EC.setClusterNumbers(this.ClusterNumber);
				
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
				int SpeciesCounter = 0;
				
				//Initialize a hash map to use for the case of cassette contexts.
				HashSet<String> GenesForCassettes = new HashSet<String>();
				
				//iterate through species.
				for (Entry<String, AnnotatedGenome> entry : OS.getSpecies().entrySet()) {

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
					
					
					//Cassette cases: add genes
					if (isCassette){
						for (LinkedList<GenomicElementAndQueryMatch> MatchList : Matches){
							for (GenomicElementAndQueryMatch GandE : MatchList){
								GenesForCassettes.add(GandE.getE().getAnnotation());
							}
						}
					}
					
					SpeciesCounter++;
					progress = (int) (50*((double)SpeciesCounter/(double)OS.getSpecies().size()));
					//update progress
					setProgress(progress);
					
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
					LinkedHashMap<String, String> CassetteContigNames = 
							new LinkedHashMap<String, String>();
					
					//parameters for each
					String SpeciesKey;
					LinkedList<GenomicElementAndQueryMatch> SpeciesGenes;
					
					for (AnnotatedGenome AG : OS.getSpecies().values()){
						
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
							
							//compress contigs to a single string
							String AllContigs = "";
							int ContigCounter = 0;
							for (String s : Contigs){
								ContigCounter++;
								AllContigs = AllContigs + "[Contig_" + ContigCounter + "]=" + s + ";";
								
								//displayable case (single contig)
								if (Contigs.size() == 1){
									AllContigs = s;
								}
							}
							CassetteContigNames.put(SpeciesKey, AllContigs);
							
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

				//Update the query data with all changes.
				CSDisplayData CSD = new CSDisplayData();
				CSD.setECandInitializeTreeLeaves(EC);
				WorkerQD.setCSD(CSD);
				
				return null;
			}
			
			//Optional Operations
			
			//(1) Create a tree panel of search results
			//============================================//
			protected Void CreateSearchPanel(){
				
				//update search results frame
				SearchResultsFrame = new FrmSearchResults(fr,WorkerQD.getCSD());
				WorkerQD.setCSD(SearchResultsFrame.getCSD());

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
					try {
						
						//CLUSTERING FROM DISTANCES DATA
						rg = new Reagrupa(multiDendro, typeData, method, precision);
						
						mdNew = rg.Recalcula();
						
						//SET THE CURRENT MULTIDENDROGRAM TO THE RESULT FROM RG.RECALCULA()
						multiDendro = mdNew;
						
						b = multiDendro.getArrel().getBase();
						if ((b < minBase) && (b != 0)) {
							minBase = b;
						}
						progress = 50 + 50
								* (nbElements - multiDendro.getCardinalitat())
								/ (nbElements - 1);
						setProgress(progress);
					} catch (final Exception e) {
						//showError(e.getMessage());
						showError("problems in calculating dendrogram.");
					}
				}
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
				
				//re-set cursor, progress bar
				fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				progressBar.setString("");
				progressBar.setBorderPainted(false);
				progressBar.setValue(0);

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
				
		}
		
		public Jpan_btn_NEW(final FrmPrincipalDesk fr) {
			super();
			this.fr = fr;
			this.jb = this;
			this.getPanel();
			this.searchFieldSize = searchField.getPreferredSize();
			this.setVisible(true);
			this.OS = fr.getOS();
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
			if (fr.getOS().isGeneClustersLoaded() == true){
				searchType.setSelected(clusterSearch.getModel(), true);
			} else {
				searchType.setSelected(annotationSearch.getModel(), true);
			}
			
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
			String[] ContextArray = convertContextSets(fr.getOS().getCSDs());
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
			//System.out.println(de);
			//System.out.println("tried to return");

			return de.getMatriuDistancies();


		}

		// BUTTONS PUSHED -> LOAD FILE OR UPDATE TREE
		@Override
		public void actionPerformed(final ActionEvent evt) {
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
			if (!evt.getSource().equals(contextSetMenu)){
				if (searchType.getSelection().equals(annotationSearch.getModel())){
					QD.setAnnotationSearch(true);
				} else {
					QD.setAnnotationSearch(false);
				}
				QD.setContextSetName(contextSetMenu.getSelectedItem().toString());
				QD.setDissimilarityType(Jpan_Menu.getCbDissimilarity().getSelectedItem().toString());
				QD.setAnalysesList(new PostSearchAnalyses(
						fr.getPanMenuTab().getJpo().getDrawSearchResults().isSelected(), //search results
						fr.getPanMenuTab().getJpo().getDrawContextTree().isSelected(), //draw context tree
						fr.getPanMenuTab().getJpo().getDrawContextGraph().isSelected(), //draw context graph
						fr.getPanMenuTab().getJpo().getDrawPhylogeneticTree().isSelected() //phylogeny
						));
			}
			
			//check: if none selected, show search results only.
			if (!fr.getPanMenuTab().getJpo().getDrawSearchResults().isSelected() &&
					!fr.getPanMenuTab().getJpo().getDrawContextTree().isSelected() &&
					!fr.getPanMenuTab().getJpo().getDrawContextGraph().isSelected() &&
					!fr.getPanMenuTab().getJpo().getDrawPhylogeneticTree().isSelected()){
				System.out.println("No analyses were specified. Switching 'Print Search Results' on.");
				QD.getAnalysesList().setOptionDisplaySearches(true);
				fr.getPanMenuTab().getJpo().getDrawSearchResults().setSelected(true);
			}

			//Search Query
			if (evt.getSource().equals(searchField) || evt.getSource().equals(btnSubmit)){
				
				if (!searchField.getText().equals("")) {
					
					//System.out.println("Search field invoked with query:" + searchField.getText());
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						currentQuery = "Search Query: " + searchField.getText();
					} else {
						currentQuery ="Search Query: Cluster(s) " + searchField.getText();
					}
				
					action = "Load";
					buttonClicked = true;
					ambDades = true;
				
				} else {
					showError("Please enter a query in the search bar.");
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
//					System.out.println("Method:" + (Jpan_Menu.getMethod() == ifd.getMethod()));
//					System.out.println("Precision: " + (Jpan_Menu.getPrecision() == ifd.getPrecision()));
//					System.out.println("Dissimilarity Type: " + (QD.getDissimilarityType().equals(ifd.getQD().getDissimilarityType())));
//					System.out.println("Context Set Name: " + (QD.getContextSetName().equals(ifd.getQD().getContextSetName())));
					
					//the problem!
					//System.out.println("Analyses List: " + (QD.getAnalysesList().equals(ifd.getQD().getAnalysesList())));
//					
//					System.out.println("QD List:" + QD.getAnalysesList().isOptionDisplaySearches() 
//							+ " " + QD.getAnalysesList().isOptionComputeDendrogram()
//							+ " " + QD.getAnalysesList().isOptionComputeContextGraph()
//							+ " " + QD.getAnalysesList().isOptionRenderPhylogeny());
//					
//					System.out.println("ifd:" + ifd.getQD().getAnalysesList().isOptionDisplaySearches() 
//							+ " " + ifd.getQD().getAnalysesList().isOptionComputeDendrogram()
//							+ " " + ifd.getQD().getAnalysesList().isOptionComputeContextGraph()
//							+ " " + ifd.getQD().getAnalysesList().isOptionRenderPhylogeny());
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
					
					//DATA SOURCE
					fitx = new FitxerDades();	
					fitx.setNom("");
					fitx.setPath("");
					
					//parse into candidates
					String[] Queries = searchField.getText().split(";");
					minBase = Double.MAX_VALUE;
					
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						
						//before carrying out search, ask user about their search.
						String Hypo = "hypothetical protein";
						String Unk = "Unknown function";
						
						if (Hypo.contains(searchField.getText()) || Unk.contains(searchField.getText()) ||
								searchField.getText().length() <= 3){
							
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
									Jpan_Menu.getPrecision());
							CurrentSearch.addPropertyChangeListener(this);
							CurrentSearch.execute();

						}

					} else {
						LinkedList<Integer> NumQueriesList = new LinkedList<Integer>();
						for (int i = 0; i < Queries.length; i++){
							try {
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
								Jpan_Menu.getPrecision());//phylogeny
						CurrentSearch.addPropertyChangeListener(this);
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
							Jpan_Menu.getPrecision());//phylogeny
					CurrentSearch.addPropertyChangeListener(this);
					CurrentSearch.execute();
				} else {
					CurrentSearch = new SearchWorker(SelectedFrame,action,
							Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
							Jpan_Menu.getPrecision());//phylogeny
					CurrentSearch.addPropertyChangeListener(this);
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
			
			//cancel button - version 1.1
			if (evt.getSource().equals(btnCancel)){
				
				//Kill swing worker
				if (CurrentSearch != null){
					CurrentSearch.cancel(true);
					CurrentSearch = null;
					de = null;
				}

				//message to console
//				System.out.println("Search successfully cancelled.");
			}
		} 

		private void showCalls(final String action, QueryData qD) {
			try {
				fr.setCfgPhylo(null);	//for re-drawing.
				if (action.equals("Reload") || action.equals("Redraw")) {
					currentInternalFrame.doDefaultCloseAction();
				}
				show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision(), qD);
				//currentInternalFrame.setVisible(true);
				//System.out.println(currentInternalFrame);
				currentInternalFrame.doDefaultCloseAction();
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
					// Convert tree into figures
					figPizarra = new Fig_Pizarra(multiDendro.getArrel(), cfg);
					
					// Pass figures to the window
					fPiz.setFigures(figPizarra.getFigures());
					fPiz.setConfig(cfg);
					
					//scroll panel, with sizes
					fPizSP = new JScrollPane(fPiz);
					fPizSP.setSize(pizarra.getSize());
					fPizSP.setPreferredSize(pizarra.getSize());
					
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

						//Update configuration information
						CfgPanelMenu PhyloCfgPanel = cfgp.getConfigMenu();
						PhyloCfgPanel.setTipusDades(tipusDades.DISTANCIA);
						cfgp.setConfigMenu(PhyloCfgPanel);
						cfgp.setHtNoms(figPhylo.getHtNoms());
						PhyloCfgPanel.setValMax(figPhylo.getLongestBranch());
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
						
						//update CSD with phylogenetic tree rectangles
						CSD = fPhylo.getCSD();
						
						//update config panel
						fr.setCfgPhylo(cfgp);
						
					} 

				}
				
				//INTERNAL FRAME DATA
				qD.setCSD(CSD);
				ifd = new InternalFrameData(de, multiDendro);
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
				
				//AnalysisResults.setSelectedIndex(SelectedTabbedPane);
				//System.out.println("Jpan_btn: " + AnalysisResults.getSelectedIndex());
				
				TabbedWrapper.add(AnalysisResults, BorderLayout.CENTER);
				fr.getPanGenome().setCSD(CSD);
				
				//set Jpan_genome
				//if (!isUpdate){
					
				//} 
				
				//ADD TABBED PANEL TO FRAME
				pizarra.add(TabbedWrapper);	//Tabbed menu component with panel
				
				//CONTAINER OWNERSHIP
				pizarra.setInternalPanel(fPiz);
				pizarra.setVisible(true);
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


	}
