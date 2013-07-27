/*
 * Copyright (C) Justo Montiel, David Torres, Sergio Gomez, Alberto Fernandez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>
 */

package moduls.frm;

import genomeObjects.AnnotatedGenome;
import genomeObjects.CSDisplayData;
import genomeObjects.ContextSetDescription;
import genomeObjects.GenomicElement;
import genomeObjects.OrganismSet;
import haloGUI.GBKChecker;
import haloGUI.GBKFieldMapping;
import haloGUI.GFFChecker;
import inicial.FesLog;
import inicial.Language;
import inicial.Parametres_Inicials;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultDesktopManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

//Importing these classes on a non-Mac will cause crash!

//import com.apple.eawt.AboutHandler;
//import com.apple.eawt.AppEvent.AboutEvent;
//import com.apple.eawt.Application;

import operonClustering.CustomDissimilarity;

import ContextForest.ChooseCompareTree;
import ContextForest.ChooseContextForest;
import ContextForest.ChooseDataGrouping;
import ContextForest.ContextForestWindow;
import ContextForest.ManageQuerySets;
import ContextForest.SelectQS;
import GenomicSetHandling.CurrentGenomeSet;
import GenomicSetHandling.GSInfo;
import GenomicSetHandling.ImportGenbankIDs;
import GenomicSetHandling.ManageGenomeSets;
import GenomicSetHandling.NewGS;
import GenomicSetHandling.PopularGenomeSetData;

import moduls.frm.Panels.Jpan_DisplayOptions;
import moduls.frm.Panels.Jpan_GraphMenu;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_MotifOptions;
import moduls.frm.Panels.Jpan_PhyTreeMenu;
import moduls.frm.Panels.Jpan_TabbedMenu;
import moduls.frm.Panels.Jpan_btn;
import moduls.frm.Panels.Jpan_btn_NEW;
import moduls.frm.Panels.Jpan_genome;
import moduls.frm.children.AboutBox;
import moduls.frm.children.AboutJCE;
import moduls.frm.children.CitationInfo;
import moduls.frm.children.DeviationMeasuresBox;
import moduls.frm.children.FrmPiz;
import moduls.frm.children.ManageDissimilarity;
import moduls.frm.children.ManageMotifs;
import moduls.frm.children.manageContextSetsv2;
import parser.ToNewick;
import parser.ToNewick2;
import parser.ToTXT;
import parser.Ultrametric;
import parser.EPS.EPSExporter;
import tipus.tipusDades;
import utils.MiMath;
import definicions.Cluster;
import definicions.Config;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Main frame window
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class FrmPrincipalDesk extends JFrame implements InternalFrameListener, ActionListener, ItemListener{
	
	// ----- Fields ---------------------------------------------------//
	
	private static final long serialVersionUID = 1L;

	private final JPanel pan_West, pan_South; //Segment space into different groups
	
	private final JPanel pan_Center;
										//pan_Exit = About + Exit buttons
	private final JDesktopPane pan_Desk; //Desktop Pane

	//private final Jpan_btn panBtn; //Load and Update buttons
	private Jpan_btn_NEW panBtn;
	
	private final Jpan_Menu panMenu; //Settings panel
	
	//version 2.0
	private final Jpan_TabbedMenu panMenuTab;
	private final Jpan_GraphMenu panGraphMenu;
	private final Jpan_MotifOptions panMotifOptions;
	private final Jpan_PhyTreeMenu panPhyTreeMenu;

	private Jpan_DisplayOptions panDisplayOptions;
	
	private final Jpan_genome panGenome; // genome-viewing frame

	private Config cfg; 	//context trees
	private Config cfgPhylo;	//phylo trees
	
	//private JInternalFrame currentFpiz; //internal frame that contains tree pane
	private FrmInternalFrame currentFpiz;
	
	private FrmPiz currentFpizpanel;
	
	private OrganismSet OS; //currently active Organism Set information
	
	//data necessary to render contexts
	private boolean[] SelectedNodeNumbers;
	private CSDisplayData CSD;

	// ----- New Fields (1.1) ------------------------------------------//
	
	private boolean IncludeMotifs = false;
	private boolean DisplayMotifs = false;
	private String SelectedAnalysisType = "Search Results";
	private File FileChooserSource;
	private int InternalFrameID = 0;	//for debugging
	

	// ----- New Fields (2.0x) ------------------------------------------//
	
	//Multiple OS
	private LinkedHashMap<String, GSInfo> GenomeSets = new LinkedHashMap<String, GSInfo>();	
	private LinkedHashMap<String, File> GenomeSetFiles = new LinkedHashMap<String, File>();
	private LinkedList<JCheckBoxMenuItem> AvailableOSCheckBoxMenuItems = new LinkedList<JCheckBoxMenuItem>();
	
	//Available Query Sets
	private LinkedList<JCheckBoxMenuItem> AvailableQuerySets = new LinkedList<JCheckBoxMenuItem>();

	//Popular genome sets, with data
	private LinkedHashMap<JCheckBoxMenuItem, PopularGenomeSetData> PopularGenomeSets = new LinkedHashMap<JCheckBoxMenuItem, PopularGenomeSetData>();
	
	//System time limit
	private long TimeLimit = 5;
	
	//private ButtonGroup AvailableOSCheckBoxMenuItems = new ButtonGroup();
	//Import related
	private LinkedList<String> FeatureIncludeTypes;
	private LinkedList<String> FeatureDisplayTypes;
	private GBKFieldMapping GBKFields;
	
	private ChooseCompareTree CurrentCCTWindow = null;
	private Cluster TmpCluster = null;
	
	// ===== MENU RELATED ====== //
	
	//Menu bar related
	private JMenuBar MB;
	
	//Top-level
	private JMenu M_Genomes;
	private JMenu M_Load;
	private JMenu M_Export;
	private JMenu M_Process;
	private JMenu M_Help;
	
	//Genomes components
	private JMenuItem MG_NewGS;
	private JMenu MG_CurrentGS;
	private JMenuItem MG_ManageGS;
	private JCheckBoxMenuItem MG_NoGS;
	private JMenuItem MG_ManageCurrentGS;
	private JMenuItem MG_ImportGS;
	private JMenu MG_AddGenomes;
	private JMenuItem MG_Files;
	private JMenuItem MG_AccessionID;
	private JMenuItem MG_Ncbi;
	private JMenuItem MG_NcbiTax;
	private JMenu MG_ImportSettings;
	private JMenuItem MG_NcbiSettings;
	private JMenuItem MG_GFF;
	private JMenuItem MG_Genbank;
	
	//public String strCF = "Whole Genome Set Analysis";
	public String strCF = "Construct a Context Forest";
	private JMenuItem MG_WholeSet;
	
	//popular sets
	private JMenu MG_PopularSets;
	private JCheckBoxMenuItem MG_Halos;
	private JCheckBoxMenuItem MG_Myxo;
	private JCheckBoxMenuItem MG_Chloroviruses;
	private JCheckBoxMenuItem MG_Staph;
	private JCheckBoxMenuItem MG_Salmonella;
	private String strHalos = "Haloarchaea";
	private String strMyxo = "Myxococcus";
	private String strChloroviruses = "Chloroviruses";
	private String strStaph = "Staphylococcus Aureus";
	private String strSalmonella = "Salmonella Enterica";
	
	//Load components
	private JMenuItem ML_ContextSet;
	private JMenuItem ML_DissMeas;
	private JMenuItem ML_Phylo;
	private JMenuItem ML_Motifs;
	private JMenuItem ML_HomologyClusterMenu;
	private JMenuItem ML_GeneIDs;
	private JMenuItem ML_QuerySet;
	private JMenuItem ML_DataGrouping;
	
	//export components
	private JMenuItem ME_gs;
	private JMenuItem ME_GFFs;
	private JMenuItem ME_Genbanks;
	private JMenuItem ME_Clusters;
	
	//process components (version 3)
	private JMenuItem MP_NewQuery;
	private JMenuItem MP_ManageQueries;
	private JMenu MP_QuerySet;
	private JMenuItem MP_ContextForest;
	private JMenuItem MP_Similarity;
	private JMenuItem MP_NewPheno;
	private JMenuItem MP_ManagePheno;
	private JMenu MP_PhenotypeData;
	private JMenuItem MP_TreeDataCorr;
	
	//default sub-menus
	private JCheckBoxMenuItem MP_NoQuerySets;
	private JCheckBoxMenuItem MP_NoPhenotypeData;
	
	//help components
	private JMenuItem MH_Manual;
	private JMenuItem MH_Video; 
	private JMenuItem MH_Citation;
	private JMenuItem MH_Publication;
	
	// ==== SwingWorkers ===== //
	private LoadGenomesWorker CurrentLGW;
	private LoadPopularWorker CurrentLPW;
	private boolean SearchWorkerRunning = false;
	
	// ===== Classes ===== //
	
	//genomes from files filter
	public class GenomeFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".gff")				//GFF file
					|| name.endsWith(".gb")			//Gb file
					|| name.endsWith(".gbk")){
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	//load genomes
	public class LoadGenomesWorker extends SwingWorker<Void, Void>{

		//Fields
		public File[] AllFiles;
		public double TotalNumberOfFiles;
		
		//constructor
		public LoadGenomesWorker(File[] Selected){
			AllFiles = Selected;
		}
		
		//background
		@Override
		protected Void doInBackground() throws Exception {
			
			//set Cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			//determine total number of files
			int TotalFiles = 0;
			
			for (File f : AllFiles){
				if (f.isDirectory()){
					File[] DirFiles = f.listFiles();
					for (File f2 : DirFiles){
						if (f2.getName().endsWith(".gff") || f2.getName().endsWith(".gb") || f2.getName().endsWith(".gbk") || f2.getName().endsWith(".gbf")){
							TotalFiles++;
						}
					}
				} else {
					if (f.getName().endsWith(".gff") || f.getName().endsWith(".gb") || f.getName().endsWith(".gbk") || f.getName().endsWith(".gbf")){
						TotalFiles++;
					}
				}
			}
			TotalNumberOfFiles = TotalFiles;
			
			//initialize counter
			int OrgCounter = 0;

			//Add all files
			for (File SelectedFile : AllFiles) {
				
				//import genome(s).
				if (SelectedFile.isDirectory()){
					//OrgCounter = RetrieveFromDirectory(SelectedFile);
					
					//process all appropriate files in the directory.
					File[] DirFiles = SelectedFile.listFiles();
					
					for (File f2: DirFiles){
						
						//process file, retrieve organism counter
						OrgCounter = RetrieveFromFile(f2, OrgCounter);

						// update progress bar
						int progress = (int) Math
								.round(100 * ((double) OrgCounter / TotalNumberOfFiles));
						setProgress(progress);
						
					}
					
				} else {
					
					//process file, retrieve organism counter
					OrgCounter = RetrieveFromFile(SelectedFile, OrgCounter);
					
					// update progress bar
					int progress = (int) Math
							.round(100 * ((double) OrgCounter / TotalNumberOfFiles));
					setProgress(progress);
					
				}
			}
				
			//add a context set description, if appropriate
			boolean MissingSingleGene = true;
			for (ContextSetDescription CSD : OS.getCSDs()){
				if (CSD.getName().equals("SingleGene")){
					MissingSingleGene = false;
				}
			}
			
			//create default single gene set
			if (MissingSingleGene){
				
				//add to OS
				ContextSetDescription CSD = new ContextSetDescription();
				CSD.setName("SingleGene");
				CSD.setType("SingleGene");
				CSD.setPreprocessed(false);
				OS.getCSDs().add(CSD);
				
				//add to menu
				panBtn.getContextSetMenu().addItem("SingleGene");
				panBtn.getContextSetMenu().removeItem("<none>");

			}

			return null;

		}
		
		//post-import
		public void done(){
			
			//re-set cursor, progress bar
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			//reset progress bar
			panBtn.getProgressBar().setString("");
			panBtn.getProgressBar().setBorderPainted(false);
			panBtn.getProgressBar().setValue(0);
			
			//enable components
			OSMenuComponentsEnabled(true);
			
			//Update description
			CreateAndStoreGSInfo(OS);
			
			//UI update
			SwingUtilities.updateComponentTreeUI(getRootPane());
			
		}
		
		//additional methods
		public void RetrieveFromDirectory(File SelectedFile){
			
			//Start counter
			int OrgCounter = 0;
			
			// Retrieve files
			File[] GenomeFiles = SelectedFile.listFiles(new GenomeFileFilter());

			//TODO: need to think about memory management here
			for (File f : GenomeFiles){
				AnnotatedGenome AG = new AnnotatedGenome();
				
				//GFF file import
				if (f.getName().endsWith(".gff")){
				
					// set appropriate types to import
					AG.setIncludeTypes(FeatureIncludeTypes);
					AG.setDisplayOnlyTypes(FeatureDisplayTypes);

					// Annotation information
					AG.importFromGFFFile(f.getAbsolutePath());
					
					// reference to genome file
					AG.setGenomeFile(f);

					// Species Name + genus
					String[] SpeciesName = f.getName().split(".gff");
					String TheName = SpeciesName[0];
					AG.setSpecies(TheName);

					String[] Genus = SpeciesName[0].split("_");
					String TheGenus = Genus[0];
					AG.setGenus(TheGenus);

					// add Context set
					//AG.MakeSingleGeneContextSet("SingleGene");

					//if (getAvailableMemory() < 100000){
					
						//System.out.println("Adjustment!");
					
					//TODO: this isn't working! Why not??
					//OS.FindAG2Deactivate();

					//}
					
					//System.out.println("Memory: " + getAvailableMemory());
					
					// add to hash map
					OS.getSpecies().put(TheName, AG);

					// add name to array of species
					OS.getSpeciesNames().add(TheName);
					OS.getAGLoaded().put(TheName, true);
					OS.getGenomeDescriptions().put(TheName, AG.getTextDescription());

					// update progress bar
					OrgCounter++;
					int progress = (int) Math
							.round(100 * ((double) OrgCounter / (double) GenomeFiles.length));
					setProgress(progress);
					//panBtn.getProgressBar().setValue(progress);
					
				//genbank file import
				} else if (f.getName().endsWith(".gb") || f.getName().endsWith(".gbk")){
					//TODO!!!
					// set appropriate types to import
					AG.setIncludeTypes(FeatureIncludeTypes);
					AG.setDisplayOnlyTypes(FeatureDisplayTypes);
					
				}
			}

		}
		
		//single file import
		public int RetrieveFromFile(File f, int OrgCounter){
			
			//initialize annotated genome
			AnnotatedGenome AG = new AnnotatedGenome();
			
			//GFF file import
			if (f.getName().endsWith(".gff")){
			
				//increment counter.
				OrgCounter++;
				
				// set appropriate types to import
				AG.setIncludeTypes(FeatureIncludeTypes);
				AG.setDisplayOnlyTypes(FeatureDisplayTypes);

				// Annotation information
				AG.importFromGFFFile(f.getAbsolutePath());

				//update cluster IDs, if appropriate
				if (AG.getLargestCluster() > -1){
					if (OS.LargestCluster < AG.getLargestCluster()){
						OS.LargestCluster = AG.getLargestCluster();
					}
				}
				
				// reference to genome file
				AG.setGenomeFile(f);

				// Species Name + genus
				String[] SpeciesName = f.getName().split(".gff");
				String TheName = SpeciesName[0];
				AG.setSpecies(TheName);

				String[] Genus = SpeciesName[0].split("_");
				String TheGenus = Genus[0];
				AG.setGenus(TheGenus);
				
				//System.out.println(TheName + ":\t" + OS.LargestCluster);
				
				
				// add Context set
				//AG.MakeSingleGeneContextSet("SingleGene");

				//if (getAvailableMemory() < 100000){
				
					//System.out.println("Adjustment!");
				
				//TODO: this isn't working! Why not??
				//OS.FindAG2Deactivate();

				//}
				
				//System.out.println("Memory: " + getAvailableMemory());
				
				// add to hash map
				OS.getSpecies().put(TheName, AG);

				// add name to array of species
				OS.getSpeciesNames().add(TheName);
				OS.getAGLoaded().put(TheName, true);
				OS.getGenomeDescriptions().put(TheName, AG.getTextDescription());
				
			//genbank file import
			} else if (f.getName().endsWith(".gb") || f.getName().endsWith(".gbk") || f.getName().endsWith(".gbf")){

				//increment counter.
				OrgCounter++;
				
				// set appropriate types to import
				AG.setIncludeTypes(FeatureIncludeTypes);
				AG.setDisplayOnlyTypes(FeatureDisplayTypes);
				AG.setGFM(GBKFields);
				
				// Annotation information
				AG.importFromGBKFile(f.getAbsolutePath());

				// reference to genome file
				AG.setGenomeFile(f);

				String[] SpeciesName = null;
				// Species Name + genus
				if (f.getName().endsWith(".gbk")){
					SpeciesName = f.getName().split(".gbk");
				} else if (f.getName().endsWith(".gb")){
					SpeciesName = f.getName().split(".gb");
				} else if (f.getName().endsWith(".gbf")){
					SpeciesName = f.getName().split(".gbf");
				}
				
				String TheName = SpeciesName[0];
				AG.setSpecies(TheName);

				String[] Genus = SpeciesName[0].split("_");
				String TheGenus = Genus[0];
				AG.setGenus(TheGenus);
				
				// add to hash map
				OS.getSpecies().put(TheName, AG);

				// add name to array of species
				OS.getSpeciesNames().add(TheName);
				OS.getAGLoaded().put(TheName, true);
				OS.getGenomeDescriptions().put(TheName, AG.getTextDescription());
			}
			
			return OrgCounter;
		}
	}
	
	//load genome IDs / homology clusters
	public class LoadTagsWorker extends SwingWorker<Void, Void>{

		//Fields
		protected File SourceFile;
		protected boolean isClusters;
		
		//constructor
		public LoadTagsWorker(File f, boolean isClusters){
			this.SourceFile = f;
			this.isClusters = isClusters;	//true = clusters, false = gene IDs
		}
		
		//background
		@Override
		protected Void doInBackground() throws Exception {
			if (isClusters){
				LoadHomologyClusters();
			} else {
				LoadGeneIDs();
			}
		return null;	
		}
		
		//Load homology clusters
		public void LoadHomologyClusters(){
			int LineCounter = 0;
			int clusterProgress = 0;
			setProgress(clusterProgress);

			try {

				// First: count lines in the file
				// import buffered reader
				BufferedReader br_count = new BufferedReader(
						new FileReader(SourceFile));
				int TotalLines = 0;

				// count lines
				while (br_count.readLine() != null) {
					TotalLines++;
				}

				// Second: import/process lines in the file
				// import buffered reader
				BufferedReader br = new BufferedReader(new FileReader(
						SourceFile));
				String Line = null;
				int ClusterNumCounter = 0;

				while ((Line = br.readLine()) != null) {

					// import each line
					String[] ImportedLine = Line.split("\t");

					// increment cluster counter.
					ClusterNumCounter++;

					// try to parse every line
					try {
						// Gene Name
						if (ImportedLine.length == 1) {

							// add cluster number
							for (AnnotatedGenome AG : OS.getSpecies()
									.values()) {
								AG.addClusterNumber(
										ImportedLine[0].replace("_ ", " "),
										ClusterNumCounter);
							}

							// largest cluster designation is always the
							// last
							OS.LargestCluster = TotalLines;

							// Gene Name - Cluster Number
						} else if (ImportedLine.length == 2) {

							// recover bioinfo
							int GeneClusterNum = Integer
									.parseInt(ImportedLine[1]);

							// set largest cluster number
							if (OS.LargestCluster < GeneClusterNum) {
								OS.LargestCluster = GeneClusterNum;
							}

							// add cluster number
							for (AnnotatedGenome AG : OS.getSpecies()
									.values()) {
								AG.addClusterNumber(
										ImportedLine[0].replace("_", " "),
										GeneClusterNum);
							}

							// Organism - Gene Name - Cluster Number
						} else if (ImportedLine.length == 3) {

							// recover bioinfo
							int GeneClusterNum = Integer
									.parseInt(ImportedLine[2]);

							// set largest cluster number
							if (OS.LargestCluster < GeneClusterNum) {
								OS.LargestCluster = GeneClusterNum;
							}

							// add cluster number
							OS.getSpecies()
									.get(ImportedLine[0])
									.addClusterNumber(
											ImportedLine[1].replace("_",
													" "), GeneClusterNum);

							// Organism - Contig - Gene Name - Cluster
							// Number
						} else if (ImportedLine.length == 4) {

							// recover bioinfo
							int GeneClusterNum = Integer
									.parseInt(ImportedLine[3]);

							// set largest cluster number
							if (OS.LargestCluster < GeneClusterNum) {
								OS.LargestCluster = GeneClusterNum;
							}

							// add cluster number
							OS.getSpecies()
									.get(ImportedLine[0])
									.addClusterNumber(
											ImportedLine[1],
											ImportedLine[2].replace("_",
													" "), GeneClusterNum);

							// Organism - Contig - Gene Start - Gene Stop -
							// Cluster Number
						} else if (ImportedLine.length == 5) {

							// recover bioinfo
							int GeneStart = Integer
									.parseInt(ImportedLine[2]);
							int GeneStop = Integer
									.parseInt(ImportedLine[3]);
							int GeneClusterNum = Integer
									.parseInt(ImportedLine[4]);

							// set largest cluster number
							if (OS.LargestCluster < GeneClusterNum) {
								OS.LargestCluster = GeneClusterNum;
							}

							// add cluster number
							OS.getSpecies()
									.get(ImportedLine[0])
									.addClusterNumber(ImportedLine[1],
											GeneStart, GeneStop,
											GeneClusterNum);

						} else {
							throw new Exception();
						}
					} catch (Exception ex) {
					}

					// report to SwingWorker
					LineCounter++;

					// update progress
					clusterProgress = (int) Math
							.round(100 * ((double) LineCounter / (double) TotalLines));
					setProgress(clusterProgress);

				}

			} catch (Exception ex) {

				setProgress(0);

				JOptionPane
						.showMessageDialog(
								null,
								"The file could not be loaded or was improperly formatted.",
								"Invalid File Format",
								JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//Load gene IDs
		
		/* TODO: current implementation is only of the form
		 * species_name		contig		start		stop		geneID
		 * 
		 * need to implement other file formats in the future.
		 */
		public void LoadGeneIDs(){
			
			int LineCounter = 0;
			int clusterProgress = 0;
			setProgress(clusterProgress);

			try {

				// First: count lines in the file
				// import buffered reader
				BufferedReader br_count = new BufferedReader(
						new FileReader(SourceFile));
				int TotalLines = 0;

				// count lines
				while (br_count.readLine() != null) {
					TotalLines++;
				}

				// Second: import/process lines in the file
				// import buffered reader
				BufferedReader br = new BufferedReader(new FileReader(
						SourceFile));
				String Line = null;
				int ClusterNumCounter = 0;

				while ((Line = br.readLine()) != null) {

					// import each line
					String[] ImportedLine = Line.split("\t");

					// increment cluster counter.
					ClusterNumCounter++;

					// try to parse every line
					try {

						if (ImportedLine.length == 5) {

							//System.out.println(Line);
							//recover species into
							
							// recover bioinfo
							int GeneStart = Integer
									.parseInt(ImportedLine[2]);
							int GeneStop = Integer
									.parseInt(ImportedLine[3]);

							// adjust gene ID
							AnnotatedGenome AG = OS.getSpecies().get(ImportedLine[0]);

							//check all
							for (GenomicElement E : AG.getElements()){
								if (E.getContig().equals(ImportedLine[1]) &&
										E.getStart() == GeneStart &&
										E.getStop() == GeneStop){
									E.setGeneID(ImportedLine[4]);
									//System.out.println(Line);
									break;
								}
							}
							
						} 
						
					} catch (Exception ex) {
					}

					// report to SwingWorker
					LineCounter++;

					// update progress
					clusterProgress = (int) Math
							.round(100 * ((double) LineCounter / (double) TotalLines));
					setProgress(clusterProgress);

				}

			} catch (Exception ex) {

				setProgress(0);

				JOptionPane
						.showMessageDialog(
								null,
								"The file could not be loaded or was improperly formatted.",
								"Invalid File Format",
								JOptionPane.ERROR_MESSAGE);
			}

			
		}
		
		//done
		public void done(){
			
			//re-set cursor, progress bar
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			//reset progress bar
			panBtn.getProgressBar().setString("");
			panBtn.getProgressBar().setBorderPainted(false);
			panBtn.getProgressBar().setValue(0);
			
		}
	}
	
	//Export files of a particular type
	public class ExportWorker extends SwingWorker<Void, Void>{

		//Fields
		protected String DirName;
		
		//constructor
		public ExportWorker(String DirName){
			this.DirName = DirName;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//Initialize values
			int clusterProgress = 0;
			setProgress(clusterProgress);
			double LineCounter = 0;
			double TotalSpecies = OS.getSpecies().size();
			
			//iterate through species
			for (String s : OS.getSpecies().keySet()){
				String FileName = DirName + "/" + s + ".gff";
				AnnotatedGenome AG = OS.getSpecies().get(s);
				AG.ExportExtendedGFFFile(FileName);
				LineCounter++;
				
				// update progress
				clusterProgress = (int) Math
						.round(100 * ((double) LineCounter / (double) TotalSpecies));
				setProgress(clusterProgress);
			}
			
			return null;
		}
		
		//done
		public void done(){
			
			//re-set cursor, progress bar
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			//reset progress bar
			panBtn.getProgressBar().setString("");
			panBtn.getProgressBar().setBorderPainted(false);
			panBtn.getProgressBar().setValue(0);
			
		}
		
	}
	
	//Switch between genome sets
	public class SwitchWorker extends SwingWorker<Void, Void>{

		//Fields
		public String FirstOS;
		public String SecondOS;
		
		//Constructor
		public SwitchWorker(String FirstOS, String SecondOS){
			this.FirstOS = FirstOS;
			this.SecondOS = SecondOS;
		}
		
		@Override
		protected Void doInBackground() throws Exception {

			//switch progressbar
			getPanBtn().getProgressBar().setIndeterminate(true);
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			setProgress(100);
			
			//Switch in OS
			ExportSerializedOS(FirstOS);
			GenomeSetFiles.put(OS.getName(), new File(OS.getName()));
			OS = new OrganismSet();
			ImportSerializedOS(SecondOS);

			//Switch in menu
			for (JCheckBoxMenuItem b : AvailableOSCheckBoxMenuItems){
				if (b.getName().equals(SecondOS)){
					b.setSelected(true);
				} else{
					b.setSelected(false);
				}
			}
			
			NewOSUpdateGUI();

			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			return null;
		}
		
		//done
		public void done(){
			
			//re-set cursor, progress bar
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			//reset progress bar
			panBtn.getProgressBar().setString("");
			panBtn.getProgressBar().setBorderPainted(false);
			panBtn.getProgressBar().setValue(0);
			
		}
		
	}
	
	//Load a popular set from the internet
	public class LoadPopularWorker extends SwingWorker<Void, Void>{

		//fields
		public JCheckBoxMenuItem SelectedItem;
		
		public LoadPopularWorker(JCheckBoxMenuItem j) {
			this.SelectedItem = j;
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			//switch progressbar
			getPanBtn().getProgressBar().setIndeterminate(true);
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			setProgress(100);
			
			// ==== Call method ==== //
			
			//note system time
			long StartTime = System.nanoTime();
			
			//pass system time to method
			ImportPopularSet(SelectedItem);
			
			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			return null;
		}
		
		//post-processing
		public void done(){
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			setProgress(0);
		}
		
	}
	
	//Export a genome set (.gs file)
	public class ExportGenomicSetWorker extends SwingWorker<Void, Void>{

		//Fields
		protected File file;
		
		//constructor
		protected ExportGenomicSetWorker(File fi){
			this.file = fi;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//switch progressbar
			getPanBtn().getProgressBar().setIndeterminate(true);
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			setProgress(100);
			
			//Method
			try {
				
		        FileOutputStream fileOut = new FileOutputStream(file);
		        ObjectOutputStream out = new ObjectOutputStream(fileOut);
		        out.writeObject(OS);
		        out.close();
		        fileOut.close();

			} catch (Exception ex){
				JOptionPane.showMessageDialog(null, "Unable to Export Genomic Set.",
						"Export Error",JOptionPane.ERROR_MESSAGE);
			}

			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			return null;
		}
		
		public void done(){
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			setProgress(0);
			
		}
	}
	
	//Import a genomic set (.gs file)
	public class ImportGenomicSetWorker extends SwingWorker<Void, Void>{

		//Fields
		protected File file;
		
		//constructor
		protected ImportGenomicSetWorker(File fi){
			this.file = fi;
		}
		
		@Override
		protected Void doInBackground() throws Exception {

			//switch progressbar
			getPanBtn().getProgressBar().setIndeterminate(true);
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			setProgress(100);
			
			//Method
			try
		      {	

				//Import data
		        FileInputStream fileIn = new FileInputStream(file);
		        ObjectInputStream in = new ObjectInputStream(fileIn);
		        OrganismSet OS_Imported = (OrganismSet) in.readObject();
		        in.close();
		        fileIn.close();
					
		        //store
				GenomeSetFiles.put(OS_Imported.getName(), file);
					
				//Need a new check box
				JCheckBoxMenuItem imp = new JCheckBoxMenuItem();
				imp.setText(OS_Imported.getName());
				imp.setName(OS_Imported.getName());
				imp.setSelected(true);
					
				//turn on additional options
				OSMenuComponentsEnabled(true);
		        
				//update current genome set menu
				if (AvailableOSCheckBoxMenuItems.contains(MG_NoGS)){
					
					//update appropriately
					OS = OS_Imported;

					//remove no GS type + add new type
					AvailableOSCheckBoxMenuItems.remove(MG_NoGS);
					AvailableOSCheckBoxMenuItems.add(imp);
					MG_CurrentGS.remove(MG_NoGS);
					MG_CurrentGS.add(imp);
					
					//Add information
					CreateAndStoreGSInfo(OS);
					
					//Update GUI
					NewOSUpdateGUI();
					
				//Switch out of old genome set
				} else {
					
					//Create a GS
					//Add this menu item to the list.			
					MG_CurrentGS.add(imp);
					
					//create a dummy file for new genome set, store appropriately
					OS_Imported.setName(imp.getName());
					ExportNonFocusOS(OS_Imported);

					//invoke switch worker
					CallSwitchWorker(OS.getName(), OS_Imported.getName());
					
				}
				
				//Activate menu items
				OSMenuComponentsEnabled(true);
		         
		      } catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "Unable to Import Genomic Set.\nPlease check the file format and try again.",
							"File Reading Error",JOptionPane.ERROR_MESSAGE);
		      }
			
			
			return null;
		}
		
		public void done(){
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			getPanBtn().getProgressBar().setIndeterminate(false);
			
			if (OS != null){
				OSMenuComponentsEnabled(true);
			}
			
			setProgress(0);
			
		}
	}
	
	// ==== Constructor ==== //
	public FrmPrincipalDesk(final String title, OrganismSet theOrganismSet) {
		
		//INITIALIZATIONS
		super(title);
		this.OS = theOrganismSet;
		
		//set UI to look natural
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {}
		
		//DESKTOP FRAME INFORMATION
		pan_Desk = new JDesktopPane();
		pan_Desk.setBackground(Color.LIGHT_GRAY);
		pan_Desk.setBorder(BorderFactory.createTitledBorder(""));

		//CREATE COMPONENT PANELS
		panMenu = new Jpan_Menu(this); 			//Settings panel (West)
		panBtn = new Jpan_btn_NEW(this);
		panGenome = new Jpan_genome(this);		//scrollable genome view
		
		//ORIENTATION PANELS
		//CENTER: THIS
		pan_Center = new JPanel();
		pan_Center.setLayout(new BorderLayout());
		pan_Center.add(pan_Desk, BorderLayout.CENTER);
		
		//WEST: Update Settings, Searching
		pan_West = new JPanel();
		pan_West.setLayout(new BorderLayout());
		pan_West.add(panBtn, BorderLayout.NORTH);

		//options/menus
		panGraphMenu = new Jpan_GraphMenu(this);		//Graph menu
		panMotifOptions = new Jpan_MotifOptions(this);	//Motif Options tab
		panPhyTreeMenu = new Jpan_PhyTreeMenu(this);	//Loadable phylogenetic tree
		panDisplayOptions = new Jpan_DisplayOptions(this);//options panel
		panMenuTab = new Jpan_TabbedMenu(panDisplayOptions, panMenu, panGraphMenu,
				panMotifOptions,panPhyTreeMenu);
		pan_West.add(panMenuTab, BorderLayout.CENTER);
		
		//SOUTH: Genome context viewing
		pan_South = new JPanel();
		pan_South.setLayout(new BorderLayout());
		pan_South.add(panGenome);
		
		pan_Center.add(pan_South, BorderLayout.SOUTH);
		
		//set precision value
		Jpan_Menu.setPrecision(18);
		
		//SET PROPERTIES OF DESKTOP FRAME
		//JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame.setDefaultLookAndFeelDecorated(false);
		final int width_win = Parametres_Inicials.getWidth_frmPrincipal();
		final int height_win = Parametres_Inicials.getHeight_frmPrincipal();
		this.setSize(width_win, height_win);
		//this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//ADD ORIENTATION FRAMES TO DESKTOP FRAME
		this.add(pan_West, BorderLayout.WEST);
		this.add(pan_Center, BorderLayout.CENTER);
		
		//finally, create menu bar
		CreateAndAddMenuBar();
		
		//Initialize various data import for default settings.
		InitializeData();
		
		//disable components, if appropriate
		if (OS == null){
			OSMenuComponentsEnabled(false);
		}

	}
	
	// ==== Construction Associated ==== //
	
	//create menu bar method
	public void CreateAndAddMenuBar(){
		
		//Menu bar
		this.MB = new JMenuBar();
		
		/*
		 * GENOMES MENU
		 */
		M_Genomes = new JMenu("Genomes");
	
		//Import genomes options
		MG_NewGS = new JMenuItem("New Genome Set");
		MG_CurrentGS = new JMenu("Genome Sets");
		MG_NoGS = new JCheckBoxMenuItem("None Available");
		MG_NoGS.setSelected(false);
		MG_NoGS.setEnabled(false);
		AvailableOSCheckBoxMenuItems.add(MG_NoGS);
		MG_ManageGS = new JMenuItem("Manage Genome Sets");
		MG_CurrentGS.add(MG_NoGS);
		MG_ManageCurrentGS = new JMenuItem("Current Genome Set");
		
		//New genome set
		KeyStroke Nstroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_NewGS.setAccelerator(Nstroke);
		MG_NewGS.addActionListener(this);
		
		//Manage sets
		KeyStroke Mstroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_ManageGS.setAccelerator(Mstroke);
		MG_ManageGS.addActionListener(this);
		
		//Current genome set
		KeyStroke Gstroke = KeyStroke.getKeyStroke(KeyEvent.VK_G, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_ManageCurrentGS.setAccelerator(Gstroke);
		MG_ManageCurrentGS.addActionListener(this);
		
		MG_ImportGS = new JMenuItem("Import Genome Set from .gs file");
		MG_AddGenomes = new JMenu("Import Genomes into current Genome Set");
		MG_Files = new JMenuItem("From Genbank or .GFF Files");
		MG_AccessionID = new JMenuItem ("Directly from NCBI Databases");
		MG_Ncbi = new JMenuItem("Browse NCBI available genomes by organism name");
		MG_NcbiTax = new JMenuItem("Launch NCBI microbial taxonomy browser");
		MG_AddGenomes.add(MG_Files);
		MG_AddGenomes.add(MG_AccessionID);

		//Import a genome set (.gs)
		KeyStroke Istroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_ImportGS.setAccelerator(Istroke);
		MG_ImportGS.addActionListener(this);
		
		//add genome file
		KeyStroke Fstroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_Files.setAccelerator(Fstroke);
		MG_Files.addActionListener(this);
		
		//add genomes by ID
		KeyStroke Rstroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_AccessionID.setAccelerator(Rstroke);
		MG_AccessionID.addActionListener(this);
		
		//Browse NCBI genomes by organism
		KeyStroke Bstroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_Ncbi.setAccelerator(Bstroke);
		MG_Ncbi.addActionListener(this);
		
		//Browse NCBI genomes by taxonomy
		KeyStroke Tstroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_NcbiTax.setAccelerator(Tstroke);
		MG_NcbiTax.addActionListener(this);
		
		//Import settings
		MG_ImportSettings = new JMenu("Import Settings");
		MG_GFF = new JMenuItem("Feature Type Settings");
		MG_Genbank = new JMenuItem("Genbank File Options");
		MG_NcbiSettings = new JMenuItem("NCBI Database Query Settings");
		MG_ImportSettings.add(MG_GFF);
		MG_ImportSettings.add(MG_Genbank);
		MG_ImportSettings.add(MG_NcbiSettings);
	
		MG_GFF.addActionListener(this);
		MG_Genbank.addActionListener(this);
		
		//Popular sets
		MG_PopularSets = new JMenu("Retrieve Popular Genome Set");
		MG_Halos = new JCheckBoxMenuItem(strHalos);
		MG_Chloroviruses = new JCheckBoxMenuItem(strChloroviruses);
		MG_Myxo = new JCheckBoxMenuItem(strMyxo);
		MG_Staph = new JCheckBoxMenuItem(strStaph);
		MG_Salmonella = new JCheckBoxMenuItem(strSalmonella);
		MG_PopularSets.add(MG_Halos);
		MG_PopularSets.add(MG_Chloroviruses);
		MG_PopularSets.add(MG_Myxo);
		MG_PopularSets.add(MG_Staph);
		MG_PopularSets.add(MG_Salmonella);
		
//		//Whole genome set analysis / context forest
//		MG_WholeSet = new JMenuItem(strCF);
//		
//		//Browse NCBI genomes by taxonomy
//		KeyStroke Wstroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, 
//				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
//		MG_WholeSet.setAccelerator(Wstroke);
//		MG_WholeSet.addActionListener(this);
		
		//Genomes menu - add to menu
		M_Genomes.add(MG_NewGS);
		M_Genomes.add(MG_ImportGS);
		M_Genomes.add(MG_CurrentGS);
		M_Genomes.add(MG_ManageGS);
		M_Genomes.addSeparator();
		M_Genomes.add(MG_ManageCurrentGS);
		M_Genomes.add(MG_AddGenomes);
		M_Genomes.add(MG_ImportSettings);
		M_Genomes.addSeparator();
		M_Genomes.add(MG_Ncbi);
		M_Genomes.add(MG_NcbiTax);
		M_Genomes.addSeparator();
		M_Genomes.add(MG_PopularSets);
		//M_Genomes.addSeparator();
		//M_Genomes.add(MG_WholeSet);
		
		/*
		 * LOAD MENU
		 */
		M_Load = new JMenu("Load");
			
		//Components
		ML_HomologyClusterMenu = new JMenuItem("Homology Clusters");
		ML_GeneIDs = new JMenuItem("Gene IDs");
		ML_ContextSet = new JMenuItem("Context Set");
		ML_DissMeas = new JMenuItem("Dissimilarity Measure");
		ML_Phylo = new JMenuItem("Phylogenetic Tree");
		ML_Motifs = new JMenuItem("Sequence Motifs");
		ML_QuerySet = new JMenuItem("Load Query Set");
		ML_DataGrouping = new JMenuItem("Load Data Grouping");
		
		//Load context Set
		KeyStroke OneStroke = KeyStroke.getKeyStroke(KeyEvent.VK_1,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_ContextSet.setAccelerator(OneStroke);
		ML_ContextSet.addActionListener(this);
		
		//Load dissimilarity measure
		KeyStroke TwoStroke  = KeyStroke.getKeyStroke(KeyEvent.VK_2,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_DissMeas.setAccelerator(TwoStroke);
		ML_DissMeas.addActionListener(this);
		
		//Load homology clusters
		KeyStroke Hstroke = KeyStroke.getKeyStroke(KeyEvent.VK_U, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_HomologyClusterMenu.setAccelerator(Hstroke);
		ML_HomologyClusterMenu.addActionListener(this);
		
		//Load gene IDs
		KeyStroke Dstroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_GeneIDs.setAccelerator(Dstroke);
		ML_GeneIDs.addActionListener(this);
	
		//Load phylogenetic tree
		KeyStroke Pstroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, 
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_Phylo.setAccelerator(Pstroke);
		ML_Phylo.addActionListener(this);
	
		//Load sequence motifs
		KeyStroke Estroke = KeyStroke.getKeyStroke(KeyEvent.VK_E,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_Motifs.setAccelerator(Estroke);
		ML_Motifs.addActionListener(this);
		
		//add to menu
		M_Load.add(ML_HomologyClusterMenu);
		M_Load.add(ML_GeneIDs);
		M_Load.add(ML_ContextSet);
		M_Load.add(ML_DissMeas);
		M_Load.add(ML_Phylo);
		M_Load.add(ML_Motifs);

		/*
		 * EXPORT MENU
		 */
		M_Export = new JMenu("Export");
		ME_gs = new JMenuItem("Genome Set as .gs file");
		ME_GFFs = new JMenuItem("Genomes as Extended GFF files");
		ME_Genbanks = new JMenuItem("Genomes as Genbank files from NCBI");
		ME_Clusters = new JMenuItem("Homology Clusters");
		
		//Export gs file
		KeyStroke Wstroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ME_gs.setAccelerator(Wstroke);
		ME_gs.addActionListener(this);
		
		//Export GFFs
		KeyStroke Xstroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ME_GFFs.setAccelerator(Xstroke);
		ME_GFFs.addActionListener(this);
		
		//Export Genbanks
		KeyStroke Ystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ME_Genbanks.setAccelerator(Ystroke);
		ME_Genbanks.addActionListener(this);
		
		M_Export.add(ME_gs);
		M_Export.add(ME_GFFs);
		M_Export.add(ME_Genbanks);
		//M_Export.add(ME_Clusters);
			
		/*
		 * PROCESS MENU
		 */
		//Top-level
		M_Process = new JMenu("Process");
		
		//Components
		MP_ContextForest = new JMenuItem("Create Context Forest");
		MP_Similarity = new JMenuItem("Tree Similarity Scan");
		MP_TreeDataCorr = new JMenuItem("Data Grouping Correlation");
		
		//Load Query Set
		ML_QuerySet.addActionListener(this);
		KeyStroke Lstroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_QuerySet.setAccelerator(Lstroke);
		
		//Load Supplemental Data Set
		ML_DataGrouping.addActionListener(this);
		KeyStroke Kstroke = KeyStroke.getKeyStroke(KeyEvent.VK_K, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		ML_DataGrouping.setAccelerator(Kstroke);

		//Data grouping correlation
		MP_TreeDataCorr.addActionListener(this);
		KeyStroke Threestroke = KeyStroke.getKeyStroke(KeyEvent.VK_3, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MP_TreeDataCorr.setAccelerator(Threestroke);
		
		//Tree similarity scan
		MP_Similarity.addActionListener(this);
		KeyStroke Fourstroke = KeyStroke.getKeyStroke(KeyEvent.VK_4, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MP_Similarity.setAccelerator(Fourstroke);
		
		//Context Forest
		MP_ContextForest.addActionListener(this);
		KeyStroke Fivestroke = KeyStroke.getKeyStroke(KeyEvent.VK_5, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MP_ContextForest.setAccelerator(Fivestroke);
		
		//Build menu
		M_Process.add(ML_QuerySet);
		M_Process.add(ML_DataGrouping);
		M_Process.addSeparator();
		M_Process.add(MP_TreeDataCorr);
		M_Process.add(MP_Similarity);
		M_Process.add(MP_ContextForest);

		/*
		 * HELP MENU
		 */
		M_Help = new JMenu("Help");
		MH_Manual = new JMenuItem("User's Manual");
		MH_Video = new JMenuItem("Video Tutorials");
		MH_Citation = new JMenuItem("Show Citation");
		MH_Publication = new JMenuItem("View Publication");
			
		MH_Manual.addActionListener(this);
		MH_Video.addActionListener(this);
		MH_Citation.addActionListener(this);
		MH_Publication.addActionListener(this);
		
		M_Help.addSeparator();
		M_Help.add(MH_Manual);
		M_Help.add(MH_Video);
		M_Help.add(MH_Citation);
		M_Help.add(MH_Publication);
			
		/*
		 * SUB-MENUS TO TOP-LEVEL
		 */
		MB.add(M_Genomes);
		MB.add(M_Load);
		MB.add(M_Export);
		MB.add(M_Process);
		MB.add(M_Help);
			
		this.setJMenuBar(MB);
		
		//add "about" information, if appropriate.
		if (System.getProperty("os.name").contains("Mac")){
			this.AppleOSMenuAdjustments();
		}
		
	}

//	//initialize apple-specific menu components
	//Method is currently commented out
	public void AppleOSMenuAdjustments(){
//		
//		try {
//			
////			//retrieve classes
//			Class AboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
//			Class ApplicationClass = Class.forName("com.apple.eawt.Application");
//		
//			//throws an exception, even on apple.
//			//Class AboutEventClass = Class.forName("com.apple.eawt.AppEvent.AboutEvent");
//			
//			com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
//			a.setAboutHandler(new com.apple.eawt.AboutHandler(){
//
//				@Override
//				public void handleAbout(com.apple.eawt.AppEvent.AboutEvent e) {
//					new AboutJCE();
//				}
//			});
//			
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			//e1.printStackTrace();
//		}
//
	}
	
	//Initialize data
	public void InitializeData(){
		
		// ===== Import parameters ======== //
		
		//GFF files
		FeatureIncludeTypes = new LinkedList<String>();
		FeatureIncludeTypes.add("CDS");
		FeatureIncludeTypes.add("tRNA");
		FeatureIncludeTypes.add("rRNA");
		
		FeatureDisplayTypes = new LinkedList<String>();
		FeatureDisplayTypes.add("mobile_element");
		FeatureDisplayTypes.add("IS_element");
		
		//Genbank files
		setGBKFields(new GBKFieldMapping());
		
		// ===== Popular Genome Sets ======== //
		
		//add names
		MG_Halos.setName(strHalos);
		MG_Chloroviruses.setName(strChloroviruses);
		MG_Myxo.setName(strMyxo);
		MG_Staph.setName(strStaph);
		MG_Salmonella.setName(strSalmonella);
		
		//Popular genome set objects (including various options)
		
		//Halophiles
		PopularGenomeSetData PGD_halos = new PopularGenomeSetData();
		PGD_halos.setName(strHalos);
		PGD_halos.setChkBox(MG_Halos);
		PGD_halos.setURL("http://www.bme.ucdavis.edu/facciotti/files/2013/07/Haloarchaea.txt");
		PGD_halos.setPasswordProtected(false);
		PopularGenomeSets.put(MG_Halos, PGD_halos);
		
		//Chloroviruses
		PopularGenomeSetData PGD_chloros = new PopularGenomeSetData();
		PGD_chloros.setName(strChloroviruses);
		PGD_chloros.setChkBox(MG_Chloroviruses);
		PGD_chloros.setURL("http://www.bme.ucdavis.edu/facciotti/files/2013/07/Chloroviruses.txt");
		PGD_chloros.setPasswordProtected(false);
		PopularGenomeSets.put(MG_Chloroviruses, PGD_chloros);
		
		//Myxococcus
		PopularGenomeSetData PGD_myxo = new PopularGenomeSetData();
		PGD_myxo.setName(strMyxo);
		PGD_myxo.setChkBox(MG_Myxo);
		PGD_myxo.setURL("http://www.bme.ucdavis.edu/facciotti/files/2013/07/Myxococcus.txt");
		PGD_myxo.setPasswordProtected(false);
		PopularGenomeSets.put(MG_Myxo, PGD_myxo);
		
		//Staphylococcus
		PopularGenomeSetData PGD_staph = new PopularGenomeSetData();
		PGD_staph.setName(strStaph);
		PGD_staph.setChkBox(MG_Staph);
		PGD_staph.setURL("http://www.bme.ucdavis.edu/facciotti/files/2013/07/Staphylococcus_aureus.txt");
		PGD_staph.setPasswordProtected(true);
		PGD_staph.setPassword("nenegoose");
		PopularGenomeSets.put(MG_Staph, PGD_staph);
		
		//Salmonella
		PopularGenomeSetData PGD_salmonella = new PopularGenomeSetData();
		PGD_salmonella.setName(strSalmonella);
		PGD_salmonella.setChkBox(MG_Salmonella);
		PGD_salmonella.setURL("http://www.bme.ucdavis.edu/facciotti/files/2013/07/Salmonella_Enterica.txt");
		PGD_salmonella.setPasswordProtected(true);
		PGD_salmonella.setPassword("fugufish");
		PopularGenomeSets.put(MG_Salmonella, PGD_salmonella);
		
		//add action listener
		for (JMenuItem j : PopularGenomeSets.keySet()){
			j.addActionListener(this);
		}
		
		
	}
	
	// ======= Action Methods ====== //
	
	//Action Listener - just for JMenuBar stuff
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		/*
		 * GENOMES
		 */

		//Create a new Genome Set
		if (evt.getSource().equals(MG_NewGS)){
			new NewGS(this);
		}
		
		//Load a genome set from a .gs file
		if (evt.getSource().equals(MG_ImportGS)){

			// initialize output
			JFileChooser GetGenomeSet = new JFileChooser();
			
			GetGenomeSet.setMultiSelectionEnabled(false);
			GetGenomeSet.setFileSelectionMode(JFileChooser.FILES_ONLY);
			GetGenomeSet
					.setDialogTitle("Select A Genomic Set (.gs) File");
		
			//retrieve directory
			if (this.FileChooserSource != null) {
				GetGenomeSet.setCurrentDirectory(FileChooserSource);
			} else {
				GetGenomeSet.setCurrentDirectory(new File("."));
			}
			
			GetGenomeSet.showOpenDialog(GetGenomeSet);
			
			// note current directory for next time
			if (GetGenomeSet.getSelectedFile() != null) {
								
				//adjust file
				this.FileChooserSource = GetGenomeSet.getCurrentDirectory();
				
				//retrieve file
				File fi = GetGenomeSet.getSelectedFile();

				//call SwingWorker
				ImportGenomicSetWorker IGSW = new ImportGenomicSetWorker(fi);
				IGSW.addPropertyChangeListener(panBtn);
				IGSW.execute();
				
				//update components
				if (OS != null){
					OSMenuComponentsEnabled(true);
				}
			} 
			
		}
		
		//Manage Genome sets
		if (evt.getSource().equals(MG_ManageGS)){
			if (getOS() != null){
				new ManageGenomeSets(this);
			} else {
				this.NoOS();
			}
		}
		
		//Current genome set
		if (evt.getSource().equals(MG_ManageCurrentGS)){
			if (getOS() != null){
				new CurrentGenomeSet(this);
			} else {
				this.NoOS();
			}
		}
		
		//Edit GFF file type processing settings
		if (evt.getSource().equals(MG_GFF)){
			new GFFChecker(this);
		}
		
		//Edit Genbank file type processing settings
		if (evt.getSource().equals(MG_Genbank)){
			new GBKChecker(this);
		}

		//Add one or more files to an existing genomic working set
		if (evt.getSource().equals(MG_Files)){
			
			// initialize output
			JFileChooser GetGenomes = new JFileChooser();
			
			GetGenomes.setMultiSelectionEnabled(true);
			GetGenomes.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			GetGenomes
					.setDialogTitle("Select An Annotated Genome File or Directory of Annotated Genome Files");
		
			//retrieve directory
			if (this.FileChooserSource != null) {
				GetGenomes.setCurrentDirectory(FileChooserSource);
			} else {
				GetGenomes.setCurrentDirectory(new File("."));
			}
			
			GetGenomes.showOpenDialog(GetGenomes);
			
			// note current directory for next time
			if (GetGenomes.getSelectedFile() != null) {
				
				if (this.OS == null){
					MakeDefaultGenomeSet("Default Genome Set");
				}
				
				this.FileChooserSource = GetGenomes.getCurrentDirectory();
				
				//begin import
				File[] files = GetGenomes.getSelectedFiles();
				LoadGenomesWorker LGW = new LoadGenomesWorker(files);
				CurrentLGW = LGW;
				LGW.addPropertyChangeListener(panBtn);
				LGW.execute();
				CurrentLGW = null;
			} 
		
		}
		
		//Add genomes from NCBI
		if (evt.getSource().equals(MG_AccessionID)){
			new ImportGenbankIDs(this);
		}

		//Switching between genome sets
		if (this.AvailableOSCheckBoxMenuItems.contains(evt.getSource())){
			
			//don't do anything if only one item in the list.
			if (this.AvailableOSCheckBoxMenuItems.size() > 1){
				
				//Initialize: no OS
				String OSName = null;
				
				//selection process
				for (JCheckBoxMenuItem b : AvailableOSCheckBoxMenuItems){
					if (b.equals(evt.getSource())){
						OSName = b.getName();
						b.setSelected(true);
					} else {
						b.setSelected(false);
					}
				}
				
				//If the OS is already loaded, no need for further action.
				if (OSName.equals(OS.getName())){
					OSName = null;
				}

				//If an appropriate name 
				if (OSName != null){

					this.CallSwitchWorker(OS.getName(), OSName);
				}
				
			} else {
				
				//item remains enabled.
				for (JCheckBoxMenuItem b : AvailableOSCheckBoxMenuItems){
					b.setSelected(true);
				}
				
			}

		}
		
		//Popular genome set
		for (JCheckBoxMenuItem j : PopularGenomeSets.keySet()){
			if (j.equals(evt.getSource())){
				if (j.isSelected()){

					//only import if publically available, or correct password input
					boolean ContinueLoading = false;
					PopularGenomeSetData PDG = PopularGenomeSets.get(j);
					
					if (PDG.isPasswordProtected()){
						
						JPanel panel = new JPanel();
						JLabel label = new JLabel("Please enter the password:");
						JPasswordField pass = new JPasswordField(15);
						panel.add(label);
						panel.add(pass);
						
						String[] options = new String[]{"OK","Cancel"};
						int Option = JOptionPane.showOptionDialog(null, panel, "Password Required",
								JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					
						if (JOptionPane.YES_OPTION == Option){
							char[] pchar = pass.getPassword();
							String password = new String(pchar);

							if (password.equals(PDG.getPassword())){
								ContinueLoading = true;
							} else {
								JOptionPane.showMessageDialog(null, "The password entered is not correct.\n" +
										"The data could not be loaded.",
										"Incorrect Password",JOptionPane.ERROR_MESSAGE);
								j.setSelected(false);
							}
						}
						
						if (JOptionPane.NO_OPTION == Option){
							j.setSelected(false);
						}
						
					} else {
						ContinueLoading = true;
					}
					
					if (ContinueLoading){
						LoadPopularWorker LPW = new LoadPopularWorker(j);
						LPW.addPropertyChangeListener(panBtn);
						this.CurrentLPW = LPW;
						LPW.execute();
						
						if (PDG.isPasswordProtected()){
							//update the GUI to enable components.
							OSMenuComponentsEnabled(true);
							SwingUtilities.updateComponentTreeUI(getRootPane());
							this.repaint();
						}

					}
					
					
					break;
				} else {
					j.setSelected(true);
				}
			}
		}
				
		/*
		 * LOAD
		 */
		
		//load homology clusters
		if (evt.getSource().equals(ML_HomologyClusterMenu)){
			
			// initialize output
			JFileChooser GetHC = new JFileChooser();
			
			GetHC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			GetHC
					.setDialogTitle("Select pre-computed Homology Clusters File");

			//retrieve directory
			if (this.FileChooserSource != null) {
				GetHC.setCurrentDirectory(FileChooserSource);
			} else {
				GetHC.setCurrentDirectory(new File("."));
			}
		
			GetHC.showOpenDialog(GetHC);
			
			// note current directory for next time
			if (GetHC.getCurrentDirectory() != null) {
				this.FileChooserSource = GetHC.getCurrentDirectory();
			}
			
			//import homology clusters from file.
			if (GetHC.getSelectedFile() != null){
				
				//begin import
				LoadTagsWorker LTW = new LoadTagsWorker(GetHC.getSelectedFile(), true);
				LTW.addPropertyChangeListener(panBtn);
				LTW.execute();
				
			}
			
		}
		
		//load gene IDs
		if (evt.getSource().equals(ML_GeneIDs)){
			
			// initialize output
			JFileChooser GetHC = new JFileChooser();
			
			GetHC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			GetHC
					.setDialogTitle("Select Gene IDs File");

			//retrieve directory
			if (this.FileChooserSource != null) {
				GetHC.setCurrentDirectory(FileChooserSource);
			} else {
				GetHC.setCurrentDirectory(new File("."));
			}
		
			GetHC.showOpenDialog(GetHC);
			
			// note current directory for next time
			if (GetHC.getCurrentDirectory() != null) {
				this.FileChooserSource = GetHC.getCurrentDirectory();
			}
			
			//import homology clusters from file.
			if (GetHC.getSelectedFile() != null){
				
				//begin import
				LoadTagsWorker LTW = new LoadTagsWorker(GetHC.getSelectedFile(), false);
				LTW.addPropertyChangeListener(panBtn);
				LTW.execute();
				
			}
			
		}
		
		//load context set
		if (evt.getSource().equals(ML_ContextSet)){
			new manageContextSetsv2(this, this.getPanBtn());
		}
		
		//Add a new dissimilarity measure
		if (evt.getSource().equals(ML_DissMeas)){
			new ManageDissimilarity(this);
		}
		
		//Add a phylogenetic tree
		if (evt.getSource().equals(ML_Phylo)){
			panPhyTreeMenu.ImportPhyTree();
		}
		
		//Add motifs
		if (evt.getSource().equals(ML_Motifs)){
			new ManageMotifs(this);
		}

		/*
		 * WEB-RELATED
		 */
		
		//Launch NCBI genome search window
		if (evt.getSource().equals(MG_Ncbi)){
			LaunchWebsite("http://www.ncbi.nlm.nih.gov/genome/browse");
		}
		
		//Launch NCBI taxonomy browser
		if (evt.getSource().equals(MG_NcbiTax)){
			LaunchWebsite("http://www.ncbi.nlm.nih.gov/genomes/MICROBES/microbial_taxtree.html");
		}

		/*
		 * EXPORT
		 */
		
		//Export extended GFFS
		if (evt.getSource().equals(ME_GFFs)){
			
			// initialize output
			JFileChooser ExportGenomes = new JFileChooser();
			
			ExportGenomes.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			ExportGenomes
					.setDialogTitle("Select a Directory for Export");

			//retrieve directory
			if (this.FileChooserSource != null) {
				ExportGenomes.setCurrentDirectory(FileChooserSource);
			} else {
				ExportGenomes.setCurrentDirectory(new File("."));
			}
		
			ExportGenomes.showOpenDialog(ExportGenomes);
			
			//File Path
			String DirName = "";
			
			// note current directory for next time
			if (ExportGenomes.getCurrentDirectory() != null) {
				this.FileChooserSource = ExportGenomes.getCurrentDirectory();
			}
			//DirName = ExportGenomes.getCurrentDirectory().getPath();
			DirName = ExportGenomes.getSelectedFile().getPath();
			
			//begin export
			if (DirName != null){
				ExportWorker EW = new ExportWorker(DirName);
				EW.addPropertyChangeListener(panBtn);
				EW.execute();
			}
			
		}
		
		//Genbank Export from NCBI
		if (evt.getSource().equals(ME_Genbanks)){
			
			//Announcement/instructions
			String msg = "To Retrieve one or more Genbank file(s) from NCBI, in the next window,\n" +
					"under the heading 'Organism and GenbankIDs', on each line type in the name of each genome\n" +
					"followed by the genbank ID in the provided text area and push the 'Export Genome Files' button.\n\n" +
					"Please see the User's manual for more information.";
			JOptionPane.showMessageDialog(null, msg,"",JOptionPane.INFORMATION_MESSAGE);
			
			//launch frame
			new ImportGenbankIDs(this);
		}
		
		//Export genomic working set (GS)
		if (evt.getSource().equals(ME_gs)){
			
			//Initialize file dialog
			FileDialog fd = new FileDialog(this, "Export Genome Set", FileDialog.SAVE);
			String str = OS.getName() + ".gs";
			fd.setFile(str);
			fd.setVisible(true);
			
			//Retrieve file, export to file
			if (fd.getFile() != null) {
				
				//file name
				String sPath = fd.getDirectory() + fd.getFile();
				File f = new File(sPath);
			
				//call worker
				ExportGenomicSetWorker EGSW = new ExportGenomicSetWorker(f);
				EGSW.addPropertyChangeListener(getPanBtn());
				EGSW.execute();
				
			}
			
		}
		
		/*
		 * PROCESS
		 */
		
		//Add Query Set
		if (evt.getSource().equals(ML_QuerySet)){
			new ManageQuerySets(this);
		}
		
		//Add Data Grouping
		if (evt.getSource().equals(ML_DataGrouping)){
			NewDataGrouping();
		}
		
		//Data Grouping comparison
		if (evt.getSource().equals(MP_TreeDataCorr)){
			if (getOS() != null){
				if (OS.getQuerySets().size() > 0){
					if (OS.getDataGroups().size() > 0){
						new ChooseDataGrouping(this);
					} else{
						this.NoDG();
					}
				} else {
					this.NoQS();
				}
				
			} else {
				this.NoOS();
			}
		}
		
		//Tree Similarity Scan
		if (evt.getSource().equals(MP_Similarity)){
			if (getOS() != null){
				if (OS.getQuerySets().size() > 0){
					new ChooseCompareTree(this);
				} else {
					this.NoQS();
				}
			} else {
				this.NoOS();
			}
		}
		
		//Context Forest
		if (evt.getSource().equals(MP_ContextForest)){
			if (getOS() != null){
				if (OS.getQuerySets().size() > 0){
					new ChooseContextForest(this);
				} else {
					this.NoQS();
				}
				
			} else {
				this.NoOS();
			}
		}
		
		/*
		 * HELP
		 */
		
		//Launch User'sManual
		if (evt.getSource().equals(MH_Manual)){
			LaunchWebsite("http://www.bme.ucdavis.edu/facciotti/files/2012/11/UsersManual.pdf");
		}
		
		//Youtube page of video tutorials
		if (evt.getSource().equals(MH_Video)){
			LaunchWebsite("http://www.youtube.com/user/JContextExplorer");
		}
		
		//Show Citation information
		if (evt.getSource().equals(MH_Citation)){
			//TODO;
			new CitationInfo();
		}
		
		//Bring up publication
		if (evt.getSource().equals(MH_Publication)){
			LaunchWebsite("http://www.biomedcentral.com/1471-2105/14/18");
		}
		
	}

	//Launch a website, using default browser.
	public void LaunchWebsite(String URL){
		try {
			String cmd = "";
			if (System.getProperty("os.name").contains("Windows")){
				cmd = "cmd /c start " + URL;
				Runtime.getRuntime().exec(cmd);
			} else if (System.getProperty("os.name").contains("Mac")){
				cmd = "open " + URL;
				Runtime.getRuntime().exec(cmd);
			} else {
				cmd = "firefox " + URL;
				Runtime.getRuntime().exec(cmd);
			}
			
		} catch (Exception ex){
			JOptionPane.showMessageDialog(null, 
					"Unable to connect to internet or locate website.",
					"Internet Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//create a default genome set, if no genome set exists.
	public void MakeDefaultGenomeSet(String OSName){
		OS = new OrganismSet();
		OS.setName(OSName);
		
		//update check box menu
		for (JCheckBoxMenuItem b : getCurrentItems()){
			if (b.equals(getMG_NoGS())){
				getMG_CurrentGS().remove(b);
			} else {
				b.setSelected(false);
			}
		}
		
		//Add info to set of GS
		CreateAndStoreGSInfo(OS);
		
		//Add check box menu item
		JCheckBoxMenuItem NewOS = new JCheckBoxMenuItem(OS.getName());
		NewOS.setName(OSName);
		NewOS.setSelected(true);	
		NewOS.addActionListener(this);
		
		//update menu + corresponding list
		AvailableOSCheckBoxMenuItems.add(NewOS);
		MG_CurrentGS.add(NewOS);

	}
	
	//centralized select node update source
	public void UpdateSelectedNodes() {
		
		//search results frame
		if (this.getCurrentFrame().getInternalFrameData().getSearchResultsFrame() != null){
			this.getCurrentFrame().getInternalFrameData().getSearchResultsFrame().UpdateNodes();
		}
		
		//context tree update
		if (this.getCurrentFrame().getInternalFrameData().getContextTreePanel() != null){
			this.getCurrentFrame().getInternalFrameData().getContextTreePanel().UpdateNodes();
		}
		
		//phylo tree update
		if (this.getCurrentFrame().getInternalFrameData().getPhyloTreePanel() != null){
			this.getCurrentFrame().getInternalFrameData().getPhyloTreePanel().UpdateNodes();
		}
		
	}
	
	//invoke swing worker, for progress bar stuff
	public void CallSwitchWorker(String FirstOS, String SecondOS){
		
		SwitchWorker SW = new SwitchWorker(FirstOS, SecondOS);
		SW.addPropertyChangeListener(panBtn);
		SW.execute();

	}
	
	//Create a new data grou
	public void NewDataGrouping(){
		
		// initialize output
		JFileChooser GetGrouping = new JFileChooser();
		
		GetGrouping.setFileSelectionMode(JFileChooser.FILES_ONLY);
		GetGrouping
				.setDialogTitle("Select A File Containing Data Groupings");

		//retrieve directory
		if (this.FileChooserSource != null) {
			GetGrouping.setCurrentDirectory(FileChooserSource);
		} else {
			GetGrouping.setCurrentDirectory(new File("."));
		}
	
		GetGrouping.showOpenDialog(GetGrouping);
		
		// note current directory for next time
		if (GetGrouping.getSelectedFile() != null) {
			
			//re-set file chooser source
			this.FileChooserSource = GetGrouping.getCurrentDirectory();
			
			RetrieveDataGrouping(GetGrouping.getSelectedFile());
		}
		
	}
	
	//Retrieve data grouping file
	public void RetrieveDataGrouping(File f){
		try {
			//retrieve from file
			BufferedReader br = new BufferedReader(new FileReader(f));
			String Line = null;
			LinkedList<String[]> Groupings = new LinkedList<String[]>();
			while ((Line = br.readLine()) != null){
				String[] L = Line.split("\t");
				Groupings.add(L);
			}
			
			//create name
			String DGName = f.getName().replaceFirst("[.][^.]+$", "");
			
			//key= file name, no extension, value = groupings
			OS.getDataGroups().put(DGName, Groupings);
		
			//message
			String msg = "The Data Grouping \"" + DGName + "\" was successfully imported.";
			JOptionPane.showMessageDialog(null, msg,
					"Successful Data Grouping Import", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			ImproperFormat();
		}
	}
	
	//When No OS (Organism Sets) loaded
	public void NoOS(){
		
		//TODO: indicate menu bar?
		
		String MenuBar = "Please define a genome set before continuing.\n" +
						"This can be accomplished by selecting 'New Genome Set'\n" +
						"From the Genomes drop-down menu, or by typing ";
		String invokeNew;
		if (System.getProperty("os.name").contains("Mac")){
			invokeNew = "command + N.\n";
		} else {
			invokeNew = "ctrl + N.\n";
		}
		
		String OrPopular = "\nAlternatively, you may select a popular genome set\n"+
				"from the Genomes drop-down menu by selecting one from the\n" +
				"'Retrieve Popular Genome Set' sub-menu.";
		
		String msg = MenuBar + invokeNew + OrPopular;
		
		JOptionPane.showMessageDialog(null, msg,
				"No Genome Set Defined", JOptionPane.ERROR_MESSAGE);
	}

	//When no QS (Query Sets) loaded
	public void NoQS(){
	
		String str = "Please create one or more Query Sets before continuing.\n"
				+ "Query Sets can be created by selecting 'Load Query Set' from \nthe Process drop-down menu, " +
				"or by typing ";
		String invokeNew;
		if (System.getProperty("os.name").contains("Mac")){
			invokeNew = "command + L.\n";
		} else {
			invokeNew = "ctrl + L.\n";
		}
		str = str + invokeNew;
		
		JOptionPane.showMessageDialog(null, str,
				"No Query Sets", JOptionPane.ERROR_MESSAGE);
		
	}
	
	//When no DG (Data Groupings) loaded
	public void NoDG(){
		String msg = "No Data Groupings are loaded.\n" +
				"To load a Data Grouping, select 'Load Data Grouping'\n" +
				"from the Process drop-down menu, or type ";
		
		String invokeNew;
		if (System.getProperty("os.name").contains("Mac")){
			invokeNew = "command + K.\n";
		} else {
			invokeNew = "ctrl + K.\n";
		}
		msg = msg + invokeNew;
		
		JOptionPane.showMessageDialog(null, msg,
				"No Data Groupings Loaded",JOptionPane.ERROR_MESSAGE);
	}
	
	//when File improperly formatted
	public void ImproperFormat(){
		JOptionPane.showMessageDialog(null, "File Not Correctly Formatted.",
				"File Format Error",JOptionPane.ERROR_MESSAGE);
	}
	
	//activate/deactivate
	public void OSMenuComponentsEnabled(boolean SwitchPos){

		M_Load.setEnabled(SwitchPos);
		M_Export.setEnabled(SwitchPos);
		M_Process.setEnabled(SwitchPos);

	}
	
	// ==== Memory + OS Import/Export Management ====== //	
	
	//Export an existing Organism Set object into a file
	public void ExportSerializedOS(String OSName){
		CreateAndStoreGSInfo(OS);
		try {
			File f = new File(OSName);
			//System.out.println("Export: " + f.getAbsolutePath());
			GenomeSetFiles.put(OS.getName(), f);
	        FileOutputStream fileOut = new FileOutputStream(f);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(OS);
	        out.close();
	        fileOut.close();

		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//Export a non-focused organism set into a file.
	public void ExportNonFocusOS(OrganismSet OS_2){
		
		//store information
        CreateAndStoreGSInfo(OS_2);
        
        //read in data
		try {
			File f = new File(OS_2.getName());
			//System.out.println("Export: " + f.getAbsolutePath());
			GenomeSetFiles.put(OS_2.getName(), f);
	        FileOutputStream fileOut = new FileOutputStream(f);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(OS_2);
	        out.close();
	        fileOut.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//Import an Organism Set object into memory
	public void ImportSerializedOS(String OSName){

		try
	      {	
		     File f = GenomeSetFiles.get(OSName);
		     //System.out.println("Import: " + f.getAbsolutePath());
	         FileInputStream fileIn = new FileInputStream(f);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         OS = (OrganismSet) in.readObject();
	         in.close();
	         fileIn.close();
	         
	      }catch(Exception ex) {
	         ex.printStackTrace();  
	      }

	}
	
	//Import organism set info from web.
	public void ImportPopularSet(JCheckBoxMenuItem m){
		
		//Retrieve info		
		File f = new File(m.getName());
		String strURL = PopularGenomeSets.get(m).getURL();
		GenomeSetFiles.put(m.getName(), f);
		
		//Import!
		try {

			URL inputURL = new URL(strURL);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			ObjectInputStream in = new ObjectInputStream(c.getInputStream());
				
			//import data, update appropriately.
			OrganismSet OSPopular = (OrganismSet) in.readObject();
			
			//Initialize a file for the organism set, even if we don't use it.
			File fx = new File(OSPopular.getName());
			GenomeSetFiles.put(OSPopular.getName(), fx);
			
			//Need a new check box
			JCheckBoxMenuItem pop = new JCheckBoxMenuItem();
			pop.setText(m.getText());
			pop.setName(m.getName());
			
			//turn on additional options
			OSMenuComponentsEnabled(true);

			//update current genome set menu
			if (AvailableOSCheckBoxMenuItems.contains(MG_NoGS)){
				
				//make a new, default genome set.
				MakeDefaultGenomeSet(OSPopular.getName());
				
				//update appropriately
				OS = OSPopular;

				//remove no GS type.
				AvailableOSCheckBoxMenuItems.remove(MG_NoGS);
				
				//Add information
				CreateAndStoreGSInfo(OS);
				
				//Update GUI
				NewOSUpdateGUI();
				
			//Switch out of old genome set
			} else {
				
				//Create a GS
				//Add this menu item to the list.			
				AvailableOSCheckBoxMenuItems.add(pop);
				MG_CurrentGS.add(pop);
				
				//create a dummy file for new genome set, store appropriately
				OSPopular.setName(pop.getName());
				this.ExportNonFocusOS(OSPopular);

				//invoke switch worker
				this.CallSwitchWorker(OS.getName(), OSPopular.getName());
				
			}

		} catch (Exception e) {
			
			//item not selected
			m.setSelected(false);
			
			//error message
			JOptionPane.showMessageDialog(null, "There was a problem reading data from the internet.\nCheck your internet connection and try again later.",
					"Data Import Error", JOptionPane.ERROR_MESSAGE);
		}


	}

	//Method to switch between two OS (files already exist)
	@SuppressWarnings("unchecked")
	public void SwitchBetweenOS(String FirstOS, String SecondOS){
		
		//System.out.println("Switch!");
		
		//switch progressbar
		this.getPanBtn().getProgressBar().setValue(100);
		this.getPanBtn().getProgressBar().setIndeterminate(true);
		this.getPanBtn().repaint();
		
		//switch cursor
		Component glassPane = this.getRootPane().getGlassPane();
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		glassPane.setVisible(true);
		
		//UI update
		SwingUtilities.updateComponentTreeUI(getRootPane());
		
		//Switch in OS - note helpful messages
		ExportSerializedOS(FirstOS);
		System.out.println("Finished Exporting Genome Set " + FirstOS);
		GenomeSetFiles.put(OS.getName(), new File(OS.getName()));
		this.OS = new OrganismSet();
		ImportSerializedOS(SecondOS);
		System.out.println("Finished Importing Genome Set " + SecondOS);
		
		//Switch in menu
		for (JCheckBoxMenuItem b : this.AvailableOSCheckBoxMenuItems){
			if (b.getName().equals(SecondOS)){
				b.setSelected(true);
			} else{
				b.setSelected(false);
			}
		}
		
//		// ====== Context Set Menu ======//
//		
//		this.getPanBtn().getContextSetMenu().removeAllItems();
//		
//		if (OS.getCSDs().size() > 0){
//			for (ContextSetDescription CSD : OS.getCSDs()){
//				this.getPanBtn().getContextSetMenu().addItem(CSD.getName());
//			}
//		} else {
//			this.getPanBtn().getContextSetMenu().addItem("<none>");
//		}
//
//		// ====== Custom Dissimilarities ======//
//		
//		//Switch dissimilarities
//		this.getPan_Menu().getCbDissimilarity().removeAllItems();
//		
//		//add all custom dissimilarities
//		if (OS.getCustomDissimilarities().size() > 0){
//			for (CustomDissimilarity CD : OS.getCustomDissimilarities()){
//				this.getPan_Menu().getCbDissimilarity().addItem(CD.getName());
//			}
//		}
//		
//		//add fundamental dissimilarities
//		this.getPan_Menu().getCbDissimilarity().addItem("Common Genes - Dice");
//		this.getPan_Menu().getCbDissimilarity().addItem("Common Genes - Jaccard");
//		this.getPan_Menu().getCbDissimilarity().addItem("Moving Distances");
//		this.getPan_Menu().getCbDissimilarity().addItem("Total Length");
//		
//		// ====== Phylogenetic Trees ======//
//		
//		this.getPanPhyTreeMenu().setParsedPhyTrees(OS.getParsedPhyTrees());
//		this.getPanPhyTreeMenu().setLoadedPhyTrees(OS.getLoadedPhyTrees());	
//		if (OS.getLoadedPhyTrees().size() > 0){
//			this.getPanPhyTreeMenu().setFilePath(OS.getLoadedPhyTrees().get(0));
//		}
//		
//		//update GUI
//		this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().removeAllItems();
//		String[] PhyTrees = this.getPanPhyTreeMenu().getLoadedPhyTrees();
//		if (PhyTrees.length > 0){
//			for (String s : PhyTrees){
//				this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().addItem(s);
//			}
//		} else {
//			this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().addItem("<none>");
//		}
//
//		// ====== Motif Menu ======//
//		
//		//Most motifs are in the actual organism sets, this simply adjusts the menu.
//		this.getPanMotifOptions().getMenuOfMotifs().removeAllItems();
//		if (OS.getMotifNames().size() > 0){
//			for (String s : OS.getMotifNames()){
//				this.getPanMotifOptions().getMenuOfMotifs().addItem(s);
//			}
//		} else{
//			this.getPanMotifOptions().getMenuOfMotifs().addItem("<none>");
//		}

		NewOSUpdateGUI();
		
		//switch cursor
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		glassPane.setVisible(false);
		
		//switch progressbar
		this.getPanBtn().getProgressBar().setValue(0);
		this.getPanBtn().getProgressBar().setIndeterminate(false);
		
		//UI update
		SwingUtilities.updateComponentTreeUI(getRootPane());
		
	}

	//Update GUI components when a new OS is loaded.
	public void NewOSUpdateGUI(){
		
		// ====== Context Set Menu ======//
		
		this.getPanBtn().getContextSetMenu().removeAllItems();
				
		if (OS.getCSDs().size() > 0){
			for (ContextSetDescription CSD : OS.getCSDs()){
				this.getPanBtn().getContextSetMenu().addItem(CSD.getName());
			}
		} else {
			this.getPanBtn().getContextSetMenu().addItem("<none>");
		}

		// ====== Custom Dissimilarities ======//
		
		//Switch dissimilarities
		this.getPan_Menu().getCbDissimilarity().removeAllItems();
		
		//add all custom dissimilarities
		if (OS.getCustomDissimilarities().size() > 0){
			for (CustomDissimilarity CD : OS.getCustomDissimilarities()){
				this.getPan_Menu().getCbDissimilarity().addItem(CD.getName());
			}
		}
		
		//add fundamental dissimilarities
		this.getPan_Menu().getCbDissimilarity().addItem("Common Genes - Dice");
		this.getPan_Menu().getCbDissimilarity().addItem("Common Genes - Jaccard");
		this.getPan_Menu().getCbDissimilarity().addItem("Moving Distances");
		this.getPan_Menu().getCbDissimilarity().addItem("Total Length");
		
		// ====== Phylogenetic Trees ======//
		
		this.getPanPhyTreeMenu().setParsedPhyTrees(OS.getParsedPhyTrees());
		this.getPanPhyTreeMenu().setLoadedPhyTrees(OS.getLoadedPhyTrees());	
		if (OS.getLoadedPhyTrees().size() > 0){
			this.getPanPhyTreeMenu().setFilePath(OS.getLoadedPhyTrees().get(0));
		}
		
		//update GUI
		this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().removeAllItems();
		String[] PhyTrees = this.getPanPhyTreeMenu().getLoadedPhyTrees();
		if (PhyTrees.length > 0){
			for (String s : PhyTrees){
				this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().addItem(s);
			}
		} else {
			this.getPanPhyTreeMenu().getMenuLoadedPhyTrees().addItem("<none>");
		}

		// ====== Motif Menu ======//
		
		//Most motifs are in the actual organism sets, this simply adjusts the menu.
		this.getPanMotifOptions().getMenuOfMotifs().removeAllItems();
		if (OS.getMotifNames().size() > 0){
			for (String s : OS.getMotifNames()){
				this.getPanMotifOptions().getMenuOfMotifs().addItem(s);
			}
		} else{
			this.getPanMotifOptions().getMenuOfMotifs().addItem("<none>");
		}		
		
		// ====== UI update ====== //
		//SwingUtilities.updateComponentTreeUI(getRootPane());
		
	}
	
	//Create and store GS Info
	public void CreateAndStoreGSInfo(OrganismSet OS){
		
		//create Genome Set Info
		GSInfo  GI = new GSInfo();
		GI.setGSName(OS.getName());
		GI.setGSNotes(OS.getNotes());
		GI.setGSGenomeDescriptions(OS.getGenomeDescriptions());
		
		//store
		GenomeSets.put(GI.getGSName(), GI);
	}
	
	//check heap size
	public long getAvailableMemory(){
		return Runtime.getRuntime().freeMemory();
	}
	
	// ==== Save/Export Items (Original Multidendrograms) ==== //
	
	public void savePicture(final BufferedImage buff, final String tipus)
			throws Exception {
		String sPath;
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		final FileDialog fd = new FileDialog(this, Language.getLabel(75) + " "
				+ tipus.toUpperCase(), FileDialog.SAVE);
		fd.setFile(sNameNoExt + "." + tipus);
		fd.setVisible(true);

		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			final File fil = new File(sPath);
			try {
				ImageIO.write(buff, tipus, fil);
//				FesLog.LOG.info("Imatge Emmagatzemada amb exit");
			} catch (final IOException e) {
				String msg_err = Language.getLabel(76);
//				FesLog.LOG
//						.throwing(
//								"FrmPrincipalDesk",
//								"savePicture(final BufferedImage buff, final String tipus)",
//								e);
				throw new Exception(msg_err);
			} catch (Exception e) {
				String msg_err = Language.getLabel(77);
//				FesLog.LOG
//						.throwing(
//								"FrmPrincipalDesk",
//								"savePicture(final BufferedImage buff, final String tipus)",
//								e);
				throw new Exception(msg_err);
			}
		}
	}

	public void savePostSript(FrmPiz frmpiz) throws Exception {
		String sPath;
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		final FileDialog fd = new FileDialog(this, Language.getLabel(75)
				+ " EPS", FileDialog.SAVE);
		fd.setFile(sNameNoExt + ".eps");
		fd.setVisible(true);

		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			try {
				new EPSExporter(cfg, frmpiz, sPath);

//				FesLog.LOG.info("Imatge EPS emmagatzemada amb exit");
			} catch (Exception e) {
				System.out.println("Exception 291 FrmPrincipalDesk");
				String msg_err = Language.getLabel(77);
//				FesLog.LOG.throwing("FrmPrincipalDesk",
//						"savePostScript(final BufferedImage buff)", e);
				throw new Exception(msg_err);
			}
		}
	}

	public void saveTXT(Cluster arrel, int precisio, tipusDades tip)
			throws Exception {
		String sPath, msg_box = Language.getLabel(80) + " TXT";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		FileDialog fd = new FileDialog(this, msg_box, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-tree.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			ToTXT saveTXT = new ToTXT(arrel, precisio, tip);
			saveTXT.saveAsTXT(sPath);
		}
	}

	//save Newick tree format
	public void saveNewick(Cluster root, int precision, tipusDades typeData)
			throws Exception {
		String msgBox, sPath;
		FileDialog fd;
		double heightBottom, heightMin, heightMax, extraSpace;
		//ToNewick toNewick;

		msgBox = Language.getLabel(80) + " Newick";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		fd = new FileDialog(this, msgBox, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-Newick.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			if (cfg.getTipusMatriu().equals(tipusDades.DISTANCIA)) {
				heightBottom = 0.0;
			} else {
				heightMin = cfg.getBaseDendograma();
				heightMax = cfg.getCimDendograma();
				extraSpace = (heightMax - heightMin)
						* (0.05 * MiMath.Arodoneix((heightMax - heightMin),
								precision));
				extraSpace = MiMath.Arodoneix(extraSpace, precision);
				heightBottom = heightMax + extraSpace;
			}
			//old version
//			toNewick = new ToNewick(root, precision, typeData, heightBottom);
//			toNewick.saveAsNewick(sPath);
			
			//new version
			ToNewick2 toNewick2 = new ToNewick2(root, precision, typeData, heightBottom);
			toNewick2.saveAsNewick(sPath);

		}
	}

	public void saveUltrametricTXT() throws Exception {
		String sPath, msg_box = Language.getLabel(80) + " TXT";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		FileDialog fd = new FileDialog(this, msg_box, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-ultrametric.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			Ultrametric um = new Ultrametric();
			um.saveAsTXT(sPath, cfg.getPrecision());
		}
	}

	public void showUltrametricErrors() {
		DeviationMeasuresBox box = new DeviationMeasuresBox(this);
		box.setVisible(true);
	}
	
	// ======= Internal Frame Stuff ========//
	
	//Create an internal frame
	public FrmInternalFrame createInternalFrame(boolean isUpdate,
			String methodName) {
		int x, y, width, height;
		this.InternalFrameID = InternalFrameID + 1;
		FrmInternalFrame pizarra;
		
		x = 0;
		y = 0;
		width = Parametres_Inicials.getWidth_frmDesk();
		height = Parametres_Inicials.getHeight_frmDesk();
		
		//pizarra translates to "slate" - internal tree frame
		pizarra = new FrmInternalFrame(methodName, isUpdate, x, y, this);
		pizarra.setSize(width, height);
		pizarra.setBackground(Color.BLUE);
		pizarra.setLayout(new BorderLayout());
		pizarra.addInternalFrameListener(panBtn);
		pizarra.addInternalFrameListener(panGenome);
		pan_Desk.add(pizarra, BorderLayout.CENTER);
				
		//desktop manager maximizes frame.
		DefaultDesktopManager ddm = new DefaultDesktopManager();
		ddm.maximizeFrame(pizarra);

		return pizarra;
	}
	
	//internal frame listener methods
	
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	
		FrmInternalFrame CurrentFrame = (FrmInternalFrame) e.getSource();
		this.setCurrentFrame(CurrentFrame);
		//System.out.println(CurrentFrame.getInternalFrameData().getQD().getName());
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// ==== Setters And Getters ===== //
	
	public LinkedList<String> getGFFIncludeTypes() {
		return FeatureIncludeTypes;
	}

	public void setGFFIncludeTypes(LinkedList<String> gFFIncludeTypes) {
		FeatureIncludeTypes = gFFIncludeTypes;
	}

	public LinkedList<String> getGFFDisplayTypes() {
		return FeatureDisplayTypes;
	}

	public void setGFFDisplayTypes(LinkedList<String> gFFDisplayTypes) {
		FeatureDisplayTypes = gFFDisplayTypes;
	}

	public JMenu getMG_CurrentGS() {
		return MG_CurrentGS;
	}

	public void setMG_CurrentGS(JMenu mG_CurrentGS) {
		MG_CurrentGS = mG_CurrentGS;
	}

	public JMenuItem getMG_NoGS() {
		return MG_NoGS;
	}

	public void setMG_NoGS(JCheckBoxMenuItem mG_NoGS) {
		MG_NoGS = mG_NoGS;
	}

	public LinkedList<JCheckBoxMenuItem> getCurrentItems() {
		return AvailableOSCheckBoxMenuItems;
	}

	public void setCurrentItems(LinkedList<JCheckBoxMenuItem> currentItems) {
		AvailableOSCheckBoxMenuItems = currentItems;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		//change position, if appropriate

//		for (JCheckBoxMenuItem b : AvailableOSCheckBoxMenuItems){
//			if (b.equals(e.getItemSelectable())){
//				if (!b.isSelected()){
//					b.setSelected(true);
//				}
//			} else {
//				b.setSelected(false);
//			}
//		}

	}

	public LinkedHashMap<String, File> getGenomeSetFiles() {
		return GenomeSetFiles;
	}

	public void setGenomeSetFiles(LinkedHashMap<String, File> genomeSetFiles) {
		GenomeSetFiles = genomeSetFiles;
	}

	public GBKFieldMapping getGBKFields() {
		return GBKFields;
	}

	public void setGBKFields(GBKFieldMapping gBKFields) {
		GBKFields = gBKFields;
	}
	
	public Jpan_genome getPanGenome() {
		return panGenome;
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
//		System.out.println("Selected Nodes:" );
//		if (CSD.getSelectedNodes() != null){
//			for (int i = 0; i<CSD.getSelectedNodes().length; i++){
//				System.out.println(i + ": " + CSD.getSelectedNodes()[i]);
//			}
//		} else {
//			System.out.println("none");
//		}
	}

	public Jpan_Menu getPanMenu() {
		return panMenu;
	}

	public boolean isIncludeMotifs() {
		return IncludeMotifs;
	}

	public void setIncludeMotifs(boolean includeMotifs) {
		IncludeMotifs = includeMotifs;
	}

	public Jpan_TabbedMenu getPanMenuTab() {
		return panMenuTab;
	}

	public Jpan_PhyTreeMenu getPanPhyTreeMenu() {
		return panPhyTreeMenu;
	}

	//try to return a phylo config, if possible
	public Config getCfgPhylo() {
		try {
			return cfgPhylo;
		} catch (Exception ex){
			cfgPhylo = new Config(Jpan_Menu.getCfgPanel());
			//System.out.println("made a config");
			
			//problem 11-
			try {
				cfgPhylo.setMatriu(panBtn.getMatriu());
			} catch (Exception ex2) {}

			//System.out.println("set matrix");
			if (cfgPhylo.getValorMaxim() == 0) {
				cfgPhylo.getConfigMenu().setValMax(cfgPhylo.getCimDendograma());
				//System.out.println("set valmax");
			}
			
			return cfgPhylo;
		}
		

	}

	public void setCfgPhylo(Config cfgPhylo) {
		this.cfgPhylo = cfgPhylo;
	}

	public Config getCfg() {
		return cfg;
	}

	public void setCfg(Config cfg) {
		this.cfg = cfg;
	}

	public String getSelectedAnalysisType() {
		return SelectedAnalysisType;
	}

	public void setSelectedAnalysisType(String selectedAnalysisType) {
		SelectedAnalysisType = selectedAnalysisType;
	}

	public boolean isDisplayMotifs() {
		return DisplayMotifs;
	}

	public void setDisplayMotifs(boolean displayMotifs) {
		DisplayMotifs = displayMotifs;
	}

	public File getFileChooserSource() {
		return FileChooserSource;
	}

	public void setFileChooserSource(File fileChooserSource) {
		FileChooserSource = fileChooserSource;
	}
	
	public int getInternalFrameID() {
		return InternalFrameID;
	}

	public void setInternalFrameID(int internalFrameID) {
		InternalFrameID = internalFrameID;
	}

	public Jpan_MotifOptions getPanMotifOptions() {
		return panMotifOptions;
	}

	public Jpan_btn_NEW getPanBtn() {
		return panBtn;
	}

	public Config getConfig() {
		//System.out.println("enter getconfig");
		cfg = new Config(Jpan_Menu.getCfgPanel());
		//System.out.println("made a config");
		
		//problem 11-
		try {
			cfg.setMatriu(panBtn.getMatriu());
		} catch (Exception ex) {}

		//System.out.println("set matrix");
		if (cfg.getValorMaxim() == 0) {
			cfg.getConfigMenu().setValMax(cfg.getCimDendograma());
			//System.out.println("set valmax");
		}
		//System.out.println("return");
		return cfg;
	}

	//Original Getters and Setters
	
	public Jpan_Menu getPan_Menu() {
		return this.panMenu;
	}

	public JDesktopPane getPan_Desk() {
		return this.pan_Desk;
	}

	public void setCurrentFrame(FrmInternalFrame internalFrame) {
		this.currentFpiz = internalFrame;
	}
	
	public FrmInternalFrame getCurrentFrame(){
		return currentFpiz;
	}
	
	public OrganismSet getOS() {
		return OS;
	}

	public void setOS(OrganismSet oS) {
		OS = oS;
	}

	public boolean[] getSelectedNodeNumbers() {
		return SelectedNodeNumbers;
	}

	public void setSelectedNodeNumbers(boolean[] selectedNodeNumbers) {
		SelectedNodeNumbers = selectedNodeNumbers;
	}

	public LinkedHashMap<String, GSInfo> getGenomeSets() {
		return GenomeSets;
	}

	public void setGenomeSets(LinkedHashMap<String, GSInfo> genomeSets) {
		GenomeSets = genomeSets;
	}

	public LoadGenomesWorker getCurrentLGW() {
		return CurrentLGW;
	}

	public void setCurrentLGW(LoadGenomesWorker currentLGW) {
		CurrentLGW = currentLGW;
	}

	public LoadPopularWorker getCurrentLPW() {
		return CurrentLPW;
	}

	public void setCurrentLPW(LoadPopularWorker currentLPW) {
		CurrentLPW = currentLPW;
	}

	public LinkedList<JCheckBoxMenuItem> getAvailableQuerySets() {
		return AvailableQuerySets;
	}

	public void setAvailableQuerySets(LinkedList<JCheckBoxMenuItem> availableQuerySets) {
		AvailableQuerySets = availableQuerySets;
	}

	public ChooseCompareTree getCurrentCCTWindow() {
		return CurrentCCTWindow;
	}

	public void setCurrentCCTWindow(ChooseCompareTree currentCCTWindow) {
		CurrentCCTWindow = currentCCTWindow;
	}

	public Cluster getTmpCluster() {
		return TmpCluster;
	}

	public void setTmpCluster(Cluster tmpCluster) {
		TmpCluster = tmpCluster;
	}

	public Jpan_DisplayOptions getPanDisplayOptions() {
		return panDisplayOptions;
	}

	public void setPanDisplayOptions(Jpan_DisplayOptions panDisplayOptions) {
		this.panDisplayOptions = panDisplayOptions;
	}

	public boolean isSearchWorkerRunning() {
		return SearchWorkerRunning;
	}

	public void setSearchWorkerRunning(boolean searchWorkerRunning) {
		SearchWorkerRunning = searchWorkerRunning;
	}

}
