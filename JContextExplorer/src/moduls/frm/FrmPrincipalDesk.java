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
import genomeObjects.OSCreationInstructions;
import genomeObjects.OrganismSet;
import haloGUI.GBKChecker;
import haloGUI.GFFChecker;
import inicial.FesLog;
import inicial.Language;
import inicial.Parametres_Inicials;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import operonClustering.CustomDissimilarity;

import GenomicSetHandling.ImportGenbankIDs;
import GenomicSetHandling.NewGS;

import moduls.frm.Panels.Jpan_DisplayOptions;
import moduls.frm.Panels.Jpan_GraphMenu;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_MotifOptions;
import moduls.frm.Panels.Jpan_PhyTreeMenu;
import moduls.frm.Panels.Jpan_TabbedMenu;
import moduls.frm.Panels.Jpan_btn;
import moduls.frm.Panels.Jpan_btnExit;
import moduls.frm.Panels.Jpan_btn_NEW;
import moduls.frm.Panels.Jpan_genome;
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

	private final JPanel pan_West, pan_Exit, pan_South; //Segment space into different groups
	
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

	private final Jpan_DisplayOptions panDisplayOptions;
	
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
	

	// ----- New Fields (1.2) ------------------------------------------//
	
	//Multiple OS
//	private LinkedHashMap<String, OSCreationInstructions> GenomeSets 
//		= new LinkedHashMap<String, OSCreationInstructions>();
	private LinkedHashMap<String, OSCreationInstructions> GenomeSets 
	= new LinkedHashMap<String, OSCreationInstructions>();	
	
	private LinkedHashMap<String, File> GenomeSetFiles = 
			new LinkedHashMap<String, File>();
	private LinkedList<JCheckBoxMenuItem> AvailableOSCheckBoxMenuItems 
		= new LinkedList<JCheckBoxMenuItem>();
//	private ButtonGroup bg = new ButtonGroup();
	
	//private ButtonGroup AvailableOSCheckBoxMenuItems = new ButtonGroup();
	//Import related
	private LinkedList<String> GFFIncludeTypes;
	private LinkedList<String> GFFDisplayTypes;
	private LinkedList<String> GBKIncludeTypes;
	private LinkedList<String> GBKDisplayTypes;
	
	//Menu bar related
	private JMenuBar MB;
	
	//Top-level
	private JMenu M_Genomes;
	private JMenu M_Load;
	private JMenu M_Export;
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
	private JMenu MG_ImportSettings;
	private JMenuItem MG_GS;
	private JMenuItem MG_GFF;
	private JMenuItem MG_Genbank;
	private JMenu MG_PopularSets;
	private JMenuItem MG_Halos;
	private JMenuItem MG_Myxo;
	private JMenuItem MG_Chloroviruses;
	
	//Load components
	private JMenuItem ML_ContextSet;
	private JMenuItem ML_DissMeas;
	private JMenuItem ML_Phylo;
	private JMenuItem ML_Motifs;
	
	// ----- Classes --------------------------------------------------//
	
	//genomes from files filter
	class GenomeFileFilter implements FilenameFilter {

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
		public File SelectedFile;
		
		//constructor
		public LoadGenomesWorker(File SelectedDirectoryOrFile){
			this.SelectedFile = SelectedDirectoryOrFile;
		}
		
		//background
		@Override
		protected Void doInBackground() throws Exception {

			//set Cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			//import genome(s).
			if (SelectedFile.isDirectory()){
				RetrieveFromDirectory();
			} else {
				RetrieveFromFile();
			}
			
			//add a context set description, if appropriate
			boolean MissingSingleGene = true;
			for (ContextSetDescription CSD : OS.getCSDs()){
				if (CSD.getName().equals("SingleGene")){
					MissingSingleGene = false;
				}
			}
			
			
			//Modify this!!
			if (MissingSingleGene){
				
				//add to OS
				ContextSetDescription CSD = new ContextSetDescription();
				CSD.setName("SingleGene");
				CSD.setPreprocessed(true);
				CSD.setType("IntergenicDist");
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
			
		}
		
		//additional methods
		public void RetrieveFromDirectory(){
			
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
					AG.setIncludeTypes(GFFIncludeTypes);
					AG.setDisplayOnlyTypes(GFFDisplayTypes);

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
					AG.MakeSingleGeneContextSet("SingleGene");

					//if (getAvailableMemory() < 100000){
						OS.AdjustAvailableSpecies(TheName);
						System.out.println("Adjustment!");
					//}
					System.out.println("Memory: " + getAvailableMemory());
					
					// add to hash map
					OS.getSpecies().put(TheName, AG);

					// add name to array of species
					OS.getSpeciesNames().add(TheName);
					OS.getAGLoaded().put(TheName, true);

					// update progress bar
					OrgCounter++;
					int progress = (int) Math
							.round(100 * ((double) OrgCounter / (double) GenomeFiles.length));
					setProgress(progress);
					//panBtn.getProgressBar().setValue(progress);
					
				//genbank file import
				} else {
					//TODO: need to genbank on it.
				}
			}

		}
		
		public void RetrieveFromFile(){
			System.out.println("File!");
		}
	}
	
	//reloading an OS from instructions
	public class LoadOSWorker extends SwingWorker<Void, Void>{

		//fields
		OSCreationInstructions OSC;
		
		//constructor
		public LoadOSWorker(OSCreationInstructions OSC){
			this.OSC = OSC;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			return null;
		}
		
		//post-processing
		public void done(){
			
		}
	}
	
	//Constructor
	public FrmPrincipalDesk(final String title, OrganismSet theOrganismSet) {
		
		//INITIALIZATIONS
		super(title);
		this.OS = theOrganismSet;
//		if (OS.getSourceDirectory() != null){
//			this.FileChooserSource = OS.getSourceDirectory();
//		}
		
		//DESKTOP FRAME INFORMATION
		pan_Desk = new JDesktopPane();
		pan_Desk.setBackground(Color.LIGHT_GRAY);
		pan_Desk.setBorder(BorderFactory.createTitledBorder(""));

		//CREATE COMPONENT PANELS
		panMenu = new Jpan_Menu(this); 			//Settings panel (West)
		pan_Exit = new Jpan_btnExit(this); 		//About + Exit (SouthWest)
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
	
		//switch 1
//			JScrollPane scrollPane1 = new JScrollPane(panMenu);	
//		pan_West.add(scrollPane1, BorderLayout.CENTER);
		
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
			NoOSMenuComponents(false);
		}

	}
	
	// =================================================================//
	// ===== Methods ===================================================//		
	// =================================================================//
	
	// ----- Memory Management ---------------------------------------------//	
	
	//Export an existing Organism Set object into a file
	public void ExportSerializedOS(String OSName){
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
	
	//Export a non-focussed organism set into a file.
	public void ExportNonFocusOS(OrganismSet OS_2){
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
	
	//Method to switch between two OS (files already exist)
	@SuppressWarnings("unchecked")
	public void SwitchBetweenOS(String FirstOS, String SecondOS){
		
		//System.out.println("Switch!");
		
		//switch progressbar
		this.getPanBtn().getProgressBar().setIndeterminate(true);
		
		//switch cursor
		Component glassPane = this.getRootPane().getGlassPane();
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		glassPane.setVisible(true);
		
		//Switch in OS
		ExportSerializedOS(FirstOS);
		GenomeSetFiles.put(OS.getName(), new File(OS.getName()));
		this.OS = new OrganismSet();
		ImportSerializedOS(SecondOS);

		//Switch in menu
		for (JCheckBoxMenuItem b : this.AvailableOSCheckBoxMenuItems){
			if (b.getName().equals(SecondOS)){
				b.setSelected(true);
			} else{
				b.setSelected(false);
			}
		}
		
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

		
		//switch cursor
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		glassPane.setVisible(false);
		
		//switch progressbar
		this.getPanBtn().getProgressBar().setIndeterminate(false);
		
	}
	
	//when No OS loaded
	public void NoOS(){
		
		//TODO: indicate menu bar?
		
		String MenuBar = "Please define a genome set before continuing.\n" +
						"This can be accomplished by selecting 'New Genome Set'\n" +
						"From the Genomes drop-down menu, or by typing ";
		String invokeNew;
		if (System.getProperty("os.name").contains("Mac")){
			invokeNew = "command + N";
		} else {
			invokeNew = "ctrl + N";
		}
		
		String msg = MenuBar + invokeNew;
		JOptionPane.showMessageDialog(null, msg,
				"No Genome Set Defined", JOptionPane.ERROR_MESSAGE);
	}
	
	//activate/deactivate
	public void NoOSMenuComponents(boolean SwitchPos){
		M_Load.setEnabled(SwitchPos);
		M_Export.setEnabled(SwitchPos);
	}
	
	//check heap size
	public long getAvailableMemory(){
		return Runtime.getRuntime().freeMemory();
	}
	
	public void LoadOrganismSet(String Name){
		
		//Retrieve organism set
		OSCreationInstructions OSC = GenomeSets.get(Name);
		
		//Send process to another thread
		LoadOSWorker LOW = new LoadOSWorker(OSC);
		LOW.addPropertyChangeListener(panBtn);
		LOW.execute();
		
	}
	
	// ----- Internal Frame Data Management ---------------------------//
	
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
	
	public int getInternalFrameID() {
		return InternalFrameID;
	}

	public void setInternalFrameID(int internalFrameID) {
		InternalFrameID = internalFrameID;
	}

	public void InitializeData(){
		//GFF files
		GFFIncludeTypes = new LinkedList<String>();
		GFFIncludeTypes.add("CDS");
		GFFIncludeTypes.add("tRNA");
		GFFIncludeTypes.add("rRNA");
		
		GFFDisplayTypes = new LinkedList<String>();
		GFFDisplayTypes.add("mobile_element");
		GFFDisplayTypes.add("IS_element");
		
		//Genbank files
		GBKIncludeTypes = new LinkedList<String>();
		GBKIncludeTypes.add("Test!");
		
		GBKDisplayTypes = new LinkedList<String>();
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

	//Are you sure you want to Exit?
	public void toGoOut() {
		final String msg = Language.getLabel(0);
		int opt;
		opt = JOptionPane.showConfirmDialog(null, msg, Language.getLabel(46),
				JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			FesLog.LOG.info("Exit");
			System.exit(0);
		}
	}

	//Save stuff
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

	//Getters and Setters
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

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	
		FrmInternalFrame CurrentFrame = (FrmInternalFrame) e.getSource();
		this.setCurrentFrame(CurrentFrame);
		System.out.println(CurrentFrame.getInternalFrameData().getQD().getName());
		
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
		//TODO: Available
		MG_ManageGS = new JMenuItem("Manage Genome Sets");
		MG_CurrentGS.add(MG_NoGS);
		MG_ManageCurrentGS = new JMenuItem("Current Genome Set");
		
		//Key stroke shortcuts
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_NewGS.setAccelerator(stroke);
		MG_NewGS.addActionListener(this);
		
		//Manage sets
		KeyStroke Mstroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_ManageGS.setAccelerator(Mstroke);
		MG_ManageGS.addActionListener(this);
		
		//Current genome set
		KeyStroke Cstroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_ManageCurrentGS.setAccelerator(Cstroke);
		MG_ManageCurrentGS.addActionListener(this);
		
		MG_ImportGS = new JMenuItem("Import Genome Set from .GS File");
		MG_AddGenomes = new JMenu("Import Genomes into current Genome Set");
		MG_Files = new JMenuItem("From Genbank or .GFF Files");
		MG_AccessionID = new JMenuItem ("From a list of Genbank IDs");
		MG_Ncbi = new JMenuItem("Browse publically available NCBI genomes");
		MG_AddGenomes.add(MG_Files);
		MG_AddGenomes.add(MG_AccessionID);
		MG_AddGenomes.add(MG_Ncbi);

		//Current genome set
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
		
		//Browse NCBI genomes
		KeyStroke Bstroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		MG_Ncbi.setAccelerator(Bstroke);
		MG_Ncbi.addActionListener(this);
		
		//Import settings
		MG_ImportSettings = new JMenu("Import Settings");
		MG_GFF = new JMenuItem(".GFF Files ");
		MG_Genbank = new JMenuItem("Genbank Files");
		MG_GS = new JMenuItem(".GS Files");
		MG_ImportSettings.add(MG_GS);
		MG_ImportSettings.add(MG_GFF);
		MG_ImportSettings.add(MG_Genbank);
	
		MG_GFF.addActionListener(this);
		MG_Genbank.addActionListener(this);
		
		//Popular sets
		MG_PopularSets = new JMenu("Retrieve Popular Genome Set");
		MG_Halos = new JMenuItem("Halophilic Archaea");
		MG_Chloroviruses = new JMenuItem("Chloroviruses");
		MG_Myxo = new JMenuItem("Myxococcux Xanthus");
		MG_PopularSets.add(MG_Halos);
		MG_PopularSets.add(MG_Chloroviruses);
		MG_PopularSets.add(MG_Myxo);
		
		//Genomes menu - add to menu
		M_Genomes.add(MG_NewGS);
		M_Genomes.add(MG_CurrentGS);
		M_Genomes.add(MG_ManageGS);
		M_Genomes.addSeparator();
		M_Genomes.add(MG_ManageCurrentGS);
		M_Genomes.add(MG_ImportGS);
		M_Genomes.add(MG_AddGenomes);
		M_Genomes.add(MG_ImportSettings);
		M_Genomes.addSeparator();
		M_Genomes.add(MG_PopularSets);
			
		// ====== load non-genomes things ===== //
		M_Load = new JMenu("Load");
			
		//Components
		JMenuItem HomologyClusterMenu = new JMenuItem("Homology Clusters");
		JMenuItem GeneIDs = new JMenuItem("Gene IDs");
		ML_ContextSet = new JMenuItem("Context Set");
		ML_DissMeas = new JMenuItem("Dissimilarity Measure");
		ML_Phylo = new JMenuItem("Phylogenetic Tree");
		ML_Motifs = new JMenuItem("Sequence Motifs");
			
		//add to menu
		M_Load.add(HomologyClusterMenu);
		M_Load.add(GeneIDs);
		M_Load.add(ML_ContextSet);
		M_Load.add(ML_DissMeas);
		M_Load.add(ML_Phylo);
		M_Load.add(ML_Motifs);
			
		ML_ContextSet.addActionListener(this);
		ML_DissMeas.addActionListener(this);
		ML_Phylo.addActionListener(this);
		ML_Motifs.addActionListener(this);
		
		//Export menu
		M_Export = new JMenu("Export");
		JMenuItem GWS = new JMenuItem("Genome Set");
		JMenuItem GFFs = new JMenuItem("Genomes as GFF files");
		JMenuItem Genbanks = new JMenuItem("Genomes as Genbank files");
		JMenuItem Clusters = new JMenuItem("Homology Clusters");
			
		M_Export.add(GWS);
		M_Export.add(GFFs);
		M_Export.add(Genbanks);
		M_Export.add(Clusters);
			
		//Help menu
		M_Help = new JMenu("Help");
		JMenuItem Manual = new JMenuItem("User's Manual");
		JMenuItem Video = new JMenuItem("Video Tutorials");
		JMenuItem DataSets = new JMenuItem("Existing Datasets");
			
		M_Help.addSeparator();
		M_Help.add(Manual);
		M_Help.add(Video);
		M_Help.add(DataSets);
			
		//Add sub-menus to top-level
		MB.add(M_Genomes);
		MB.add(M_Load);
		MB.add(M_Export);
		MB.add(M_Help);
			
		this.setJMenuBar(MB);
	}
		
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
		
		//Manage Genome sets
		if (evt.getSource().equals(MG_ManageGS)){
			System.out.println("TODO: Manage Genome Sets");
		}
		
		//Current genome set
		if (evt.getSource().equals(MG_ManageCurrentGS)){
			System.out.println("TODO: Current Genome Set");
		}
		
		//Import from .GS file
		if (evt.getSource().equals(MG_ImportGS)){
			System.out.println("TODO: Import Genome Set from .GS File");
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
			
			if (this.OS == null){
				OS = new OrganismSet();
				OS.setName("Default Genome Set");
				
				//update check box menu
				for (JCheckBoxMenuItem b : getCurrentItems()){
					if (b.equals(getMG_NoGS())){
						getMG_CurrentGS().remove(b);
					} else {
						b.setSelected(false);
					}
				}
				
				//Add check box menu item
				JCheckBoxMenuItem NewOS = new JCheckBoxMenuItem(OS.getName());
				NewOS.setSelected(true);	
				NewOS.addActionListener(this);
				
				//update menu + corresponding list
				getCurrentItems().add(NewOS);
				getMG_CurrentGS().add(NewOS);

			}
			
			// initialize output
			JFileChooser GetGenomes = new JFileChooser();
			
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
			if (GetGenomes.getCurrentDirectory() != null) {
				this.FileChooserSource = GetGenomes.getCurrentDirectory();
			}
			
			//begin import
			LoadGenomesWorker LGW = new LoadGenomesWorker(GetGenomes.getSelectedFile());
			LGW.addPropertyChangeListener(panBtn);
			LGW.execute();
			
		}
		
		//Add genomes from NCBI
		if (evt.getSource().equals(MG_AccessionID)){
			new ImportGenbankIDs();
		}
		
		/*
		 * Switching between genome sets
		 */

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

					this.SwitchBetweenOS(OS.getName(), OSName);
					
				}
				
			} else {
				
				//item remains enabled.
				for (JCheckBoxMenuItem b : AvailableOSCheckBoxMenuItems){
					b.setSelected(true);
				}
				
			}

		}
		
		/*
		 * LOAD
		 */
		
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
		
		if (evt.getSource().equals(MG_Ncbi)){
			try {
								
				if (System.getProperty("os.name").contains("Windows")){
					Runtime.getRuntime().exec("cmd /c start http://www.ncbi.nlm.nih.gov/genome/browse");
				} else if (System.getProperty("os.name").contains("Mac")){
					Runtime.getRuntime().exec("open http://www.ncbi.nlm.nih.gov/genome/browse");
				} else {
					Runtime.getRuntime().exec("firefox http://www.ncbi.nlm.nih.gov/genome/browse");
				}
				
			} catch (Exception ex){
				JOptionPane.showMessageDialog(null, 
						"Unable to connect to internet or locate NCBI website.",
						"NCBI Website Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public LinkedList<String> getGFFIncludeTypes() {
		return GFFIncludeTypes;
	}

	public void setGFFIncludeTypes(LinkedList<String> gFFIncludeTypes) {
		GFFIncludeTypes = gFFIncludeTypes;
	}

	public LinkedList<String> getGFFDisplayTypes() {
		return GFFDisplayTypes;
	}

	public void setGFFDisplayTypes(LinkedList<String> gFFDisplayTypes) {
		GFFDisplayTypes = gFFDisplayTypes;
	}

	public LinkedList<String> getGBKIncludeTypes() {
		return GBKIncludeTypes;
	}

	public void setGBKIncludeTypes(LinkedList<String> gBKIncludeTypes) {
		GBKIncludeTypes = gBKIncludeTypes;
	}

	public LinkedList<String> getGBKDisplayTypes() {
		return GBKDisplayTypes;
	}

	public void setGBKDisplayTypes(LinkedList<String> gBKDisplayTypes) {
		GBKDisplayTypes = gBKDisplayTypes;
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

	public LinkedHashMap<String, OSCreationInstructions> getGenomeSets() {
		return GenomeSets;
	}

	public void setOSSeeds(LinkedHashMap<String, OSCreationInstructions> oSSeeds) {
		GenomeSets = oSSeeds;
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
	
}
