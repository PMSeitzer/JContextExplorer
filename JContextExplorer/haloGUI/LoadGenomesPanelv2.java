package haloGUI;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescription;
import genomeObjects.ContextSetDescriptions;
import genomeObjects.OrganismSet;
//import importExport.FitxerDades;
import inicial.Dendrograma;
//import inicial.Language;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

//import methods.Reagrupa;
//import moduls.frm.FrmPrincipalDesk;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Iterator;
import java.awt.Window;

@SuppressWarnings("serial")
public class LoadGenomesPanelv2 extends JPanel 
	implements ActionListener, PropertyChangeListener{
	
	//parent
	private StartFrame sf;
	
	//GUI components
	private JLabel Genomes, GeneClusters;
	private JProgressBar progressBar, progressBarClusters;
	private JButton btnLoad, btnClusterLoad, btnSubmit;
	private JTextField GenomeWorkingSetFileName, ClusterFileName;
	private String strGWS = " GENOMIC WORKING SET (REQUIRED)";
	private String strHC = " HOMOLOGOUS GENE CLUSTERS (OPTIONAL)";
	private String strLoad = "Load";
	private String clusterLoad = "Load";
	private String strNoFileLoaded = "No file currently loaded.";
	//private String strCancelled = "The operation was cancelled.";
	private String strCancelled = strNoFileLoaded;
	
	//Switches to determine operations able to be performed
	private boolean LoadingGenomeFiles = false;
	private boolean LoadingGeneClusters = false;
	private boolean GenomeWorkingSetLoaded = false;
	private boolean GeneClustersLoaded = false;
	private boolean ReadyToSubmit = false;
	
	//read in files or directories
	private boolean GenomesAsSingleFile = false;
	
	//improperly-loaded switches
	private boolean ClusterFileImproperlyLoaded = false;
	private boolean GenomeWorkingSetFileImproperlyLoaded = false;

	//Loaded Organism Set + corresponding information
	private OrganismSet OS;
	private int TotalOrganisms;	
	
	//loaded file names, with path
	private String GenomeWorkingSetFile;
	private String ClustersFile;
	
	//loaded file names no path
	private String GenomeWorkingSetFile_NoPath;
	private String ClustersFile_NoPath;
	
	//dummy labels for spacing columns
	private JLabel d1, d2, d3, d4, d5;

	private File[] GenomeFiles;
	
	//constructor
	public LoadGenomesPanelv2 (StartFrame startframe) {
		this.getPanel();
		this.setVisible(true);
		this.sf = startframe;
	}

	//panel components
	public void getPanel(){
		
		//Define GridBagLayout
		this.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(6,3,3,3);
		
		//dummy labels, to artificially normalize column widths
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d1 = new JLabel(" ");
		d1.setBackground(Color.LIGHT_GRAY);
		d1.setOpaque(false);
		add(d1, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d2 = new JLabel(" ");
		d2.setBackground(Color.LIGHT_GRAY);
		d2.setOpaque(false);
		add(d2, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d3 = new JLabel(" ");
		d3.setBackground(Color.LIGHT_GRAY);
		d3.setOpaque(false);
		add(d3, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d4 = new JLabel(" ");
		d4.setBackground(Color.LIGHT_GRAY);
		d4.setOpaque(false);
		add(d4, c);
		
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d5 = new JLabel(" ");
		d5.setBackground(Color.LIGHT_GRAY);
		d5.setOpaque(false);
		add(d5, c);

		//Genome section heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		Genomes = new JLabel(strGWS);
		Genomes.setBackground(Color.LIGHT_GRAY);
		Genomes.setOpaque(true);
		add(Genomes, c);
		gridy++;
	
		//Load File button
		c.insets = new Insets(3,3,3,3);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnLoad = new JButton(strLoad);
		btnLoad.addActionListener(this);
		add(btnLoad, c);
		
		//Genomic Working Set File Name
		c.insets = new Insets(3,3,3,3);
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		GenomeWorkingSetFileName = new JTextField();
		GenomeWorkingSetFileName.setText(strNoFileLoaded); // No file loaded
		GenomeWorkingSetFileName.addActionListener(this);
		GenomeWorkingSetFileName.setEditable(false);
		add(GenomeWorkingSetFileName, c);
//		gridy++;		
		
		//loading genomes progress bar
		c.insets = new Insets(3,3,3,3);
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(false);
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		progressBar.setForeground(Color.BLUE);
		progressBar.setVisible(false);
		add(progressBar, c);
		gridy++;
		
		//Gene clusters section heading
		c.insets = new Insets(10,3,3,3);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		GeneClusters = new JLabel(strHC);
		GeneClusters.setBackground(Color.LIGHT_GRAY);
		GeneClusters.setOpaque(true);
		add(GeneClusters,c);
		gridy++;
		
		//Load Cluster File button
		c.insets = new Insets(3,3,3,3);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnClusterLoad = new JButton(clusterLoad);
		btnClusterLoad.addActionListener(this);
		add(btnClusterLoad, c);

		// gene clusters progress bar
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBarClusters = new JProgressBar(0, 100);
		progressBarClusters.setStringPainted(false);
		progressBarClusters.setBorderPainted(false);
		progressBarClusters.setValue(0);
		progressBarClusters.setForeground(Color.BLUE);
		progressBarClusters.setVisible(false);
		add(progressBarClusters, c);
//		gridy++;
		
		// clusters file name
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		ClusterFileName = new JTextField();
		ClusterFileName.setText(strNoFileLoaded); // No file loaded
		ClusterFileName.setEditable(false);
		add(ClusterFileName, c);
		gridy++;
		
		//Submit button
		c.insets = new Insets(10,3,3,3);
		c.gridx = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipady = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.NONE;
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		add(btnSubmit, c);
		
	}
	
	//All Actions
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//load genome files
		if (evt.getSource().equals(btnLoad)) {
			//set switches to appropriate state
			LoadingGenomeFiles = true;
			GenomeWorkingSetLoaded = false;
			LoadingGeneClusters = false;
			ReadyToSubmit = false;
			GeneClustersLoaded = false;
			
			//reset clusters
			progressBarClusters.setValue(0);
			progressBarClusters.setStringPainted(false);			
			
			String fileName = getGenomeWorkingSetFile();
			//String fileName = this.getGenomes();
			
			//System.out.println("fileName: " + fileName);
			if (fileName != null){
				
				if (!fileName.equals(GenomeWorkingSetFile)){

				ClusterFileName.setText(strNoFileLoaded);
				
				LoadGenomesWorker lg = new LoadGenomesWorker(fileName);
				lg.addPropertyChangeListener(this);
				lg.execute();

				} else {
					GenomeWorkingSetLoaded = true;
					ReadyToSubmit = true;
				} 
				
			} else {

				//set everything back to zero
				progressBar.setValue(0);
				progressBar.setStringPainted(false);
				GenomeWorkingSetFileName.setText(strCancelled);
				ClusterFileName.setText(strCancelled);
				
				//turn everything off - back to square one
				LoadingGenomeFiles = false;
				GenomeWorkingSetLoaded = false;
				LoadingGeneClusters = false;
				ReadyToSubmit = false;
				GeneClustersLoaded = false;
				
				GenomeWorkingSetFile = null;
				ClustersFile = null;

			}

		} 

		
		if (evt.getSource().equals(btnClusterLoad)){
			LoadingGenomeFiles = false;
			LoadingGeneClusters = true;
			
			String clusterfileName = null;
			if (GenomeWorkingSetLoaded == true){
				clusterfileName = getClustersFile();
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set before loading pre-computed gene clusters.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
			
			if (clusterfileName != null){
				
				if (!clusterfileName.equals(ClustersFile)){
				
				LoadClustersWorker lc = new LoadClustersWorker(clusterfileName);
				lc.addPropertyChangeListener(this);
				lc.execute();

				}

			} else {
				progressBarClusters.setValue(0);
				progressBarClusters.setStringPainted(false);
				LoadingGenomeFiles = false;
				if (OS != null){
					OS.setGeneClustersLoaded(false);
				}
				ClusterFileName.setText(strCancelled);
				ClustersFile = null;
				GeneClustersLoaded = false;
			}

		}
		
		if (evt.getSource().equals(btnSubmit)){
		//if (evt.getSource().equals(this.getBtnSubmit())){
			if (ReadyToSubmit == true) {
				
				//close this window
				sf.dispose();
				
				//open the dendrogram window
				invokeDendrograma();
				
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set before proceeding to the main window.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	
	}

	//retrieve a data file
	private String getGenomeWorkingSetFile() {
		
		//use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(sf, "English",
				FileDialog.LOAD);

		fd.setVisible(true);
		String GenomeWorkingSetFile = fd.getDirectory() + fd.getFile();
		this.GenomeWorkingSetFile_NoPath = fd.getFile();
		//String GenomeWorkingSetFile =  fd.getFile();
		if (fd.getFile() == null) {
			GenomeWorkingSetFile = null;
		} 
		GenomesAsSingleFile = true;
		return GenomeWorkingSetFile; //file name
	}	
	
	//retrieve either directory or data file - TODO: finish/implement
	private String getGenomes(){
		
		//initialize output
		JFileChooser GetGenomes = new JFileChooser();
		GetGenomes.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		GetGenomes.getCurrentDirectory();
		GetGenomes.setDialogTitle("Select Annotated Genomes Directory or Genome Working Set File");
		GetGenomes.showOpenDialog(GetGenomes);
		
		//retrieve a directory
		//File[] AllFiles = GetGenomes.getSelectedFiles();
		File DirectoryOrGWSFile = GetGenomes.getSelectedFile();
		this.GenomeWorkingSetFile_NoPath = DirectoryOrGWSFile.getName();
		
		//check if file could be received
		if (DirectoryOrGWSFile != null){
		
			//determine if file or directory loaded
			if (DirectoryOrGWSFile.isDirectory()){
		
				//retrieving info as a directory.
				this.GenomesAsSingleFile = false;
			
				//retrieve directory
				this.GenomeFiles = DirectoryOrGWSFile.listFiles();
		
			} else {
			
				//all information stored in a single genome working set file.
				this.GenomesAsSingleFile = true;

			}
		}
		
		//return the information.
		return DirectoryOrGWSFile.getAbsolutePath();
	}
	
	//retrieve clusters file
	private String getClustersFile() {
		
		//use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(sf, "English",
				FileDialog.LOAD);

		fd.setVisible(true);
		String ClustersFile = fd.getDirectory() + fd.getFile();
		this.ClustersFile_NoPath = fd.getFile();
		//String GenomeWorkingSetFile =  fd.getFile();
		if (fd.getFile() == null) {
			GenomeWorkingSetFile = null;
		} 
		return ClustersFile; //file name
	}	
	
	//Perform File Loading + Operon computation tasks
	class LoadGenomesWorker extends SwingWorker<Void, Void>{
		
		public LoadGenomesWorker(String filename){
			GenomeWorkingSetFile = filename;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//disable all buttons
			btnLoad.setEnabled(false);
			btnClusterLoad.setEnabled(false);
			btnSubmit.setEnabled(false);
			
			//LOAD GENOME SET
			GenomeWorkingSetFileName.setVisible(false);
			progressBar.setVisible(true);
			int progress = 0;
			setProgress(progress);
			progressBar.setStringPainted(true);
			
			//import	
			OS = new OrganismSet();
			TotalOrganisms = OS.determineNumberOfSpecies(GenomeWorkingSetFile);
			int OrganismsCompleted = 0;

			//define a new linked list, for each annotated genome
			LinkedHashMap<String, AnnotatedGenome> Species = new LinkedHashMap<String, AnnotatedGenome>();
			
			//define a new list, for each species name
			LinkedList<String> SpeciesNames = new LinkedList<String>();
			
			//import a single genomic working set file
			if (GenomesAsSingleFile){
				
				try{
					//import buffered reader
					BufferedReader br = new BufferedReader(new FileReader(GenomeWorkingSetFile));
					String Line = null;
					
					while((Line = br.readLine()) != null){
						
							String[] ImportedLine = Line.split("\t");
									
							//create a new AnnotatedGenome
							AnnotatedGenome AG = new AnnotatedGenome();
							
							//middle line is the sequence line
							if (ImportedLine.length == 3){
							
								//Annotation information
								AG.importElements(ImportedLine[0]);
							
								//reference to genome file
								AG.setGenomeFile(new File(ImportedLine[1]));
							
								//Species name
								AG.setSpecies(ImportedLine[2]);
								//System.out.println("Species " + ImportedLine[2] + " Completed.");
							
							} else{ //first = annotation file, last = species name
								AG.importElements(ImportedLine[0]);
								AG.setSpecies(ImportedLine[(ImportedLine.length-1)]);
								AG.setGenomeFile(new File(""));
							}
							
							//Genus name
							String SpeciesAndGenus[] = ImportedLine[(ImportedLine.length-1)].split("_");
							AG.setGenus(SpeciesAndGenus[0]);
							
							//add Context set
							AG.MakeSingleGeneContextSet("SingleGene");
							
							//add to hash map
							Species.put(ImportedLine[(ImportedLine.length-1)], AG);
							
							//add name to array of species
							SpeciesNames.add(ImportedLine[(ImportedLine.length-1)]);
							
							//update progress bar
							OrganismsCompleted++;
							progress= (int) Math.round(100*((double)OrganismsCompleted/(double)TotalOrganisms));
							setProgress(progress);
							//progressBar.setValue(progress);
					}
					br.close();		
					
					//save results to OS structure.
					//imported data
					OS.setSpecies(Species);
					OS.setSpeciesNames(SpeciesNames);
					
					//context set information descriptions in OS
					LinkedList<ContextSetDescription> CSD = new LinkedList<ContextSetDescription>();
					ContextSetDescription Initial = new ContextSetDescription();
					Initial.setName("SingleGene");
					Initial.setPreprocessed(true);
					Initial.setType("IntergenicDist");
					CSD.add(Initial);
					OS.setCSDs(CSD);
					
					progressBar.setValue(100);					
					progressBar.setVisible(false);
					
					GenomeWorkingSetFileName.setVisible(true);
					GenomeWorkingSetFileName.setText(GenomeWorkingSetFile_NoPath);

				}catch(Exception ex){
					progressBar.setStringPainted(false);
					progressBar.setValue(0);
					GenomeWorkingSetFileImproperlyLoaded = true;
					JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
							"Invalid File Format", JOptionPane.ERROR_MESSAGE);
					GenomeWorkingSetFileName.setText(strCancelled);

				}

			} else {
				
				try {
					
				//retrieve all files
				for (File f: GenomeFiles){
					if (f.getName().contains(".gff")){
						//new annotated genome
						AnnotatedGenome AG = new AnnotatedGenome();
						
						//Annotation information
						AG.importElements(f.getAbsolutePath());
					
						//reference to genome file
						AG.setGenomeFile(f);
						
						//Species Name + genus
						String[] SpeciesName = f.getName().split(".gff");
						String TheName = SpeciesName[0];
						AG.setSpecies(TheName);
						
						String[] Genus = SpeciesName[0].split("_");
						String TheGenus = Genus[0];
						AG.setGenus(TheGenus);

						//add Context set
						AG.MakeSingleGeneContextSet("SingleGene");
						
						//add to hash map
						Species.put(TheName, AG);
						
						//add name to array of species
						SpeciesNames.add(TheName);
						
						//update progress bar
						OrganismsCompleted++;
						progress= (int) Math.round(100*((double)OrganismsCompleted/(double)TotalOrganisms));
						setProgress(progress);
					}
				}
				
				//save results to OS structure.
				//imported data
				OS.setSpecies(Species);
				OS.setSpeciesNames(SpeciesNames);
				
				//context set information descriptions in OS
				LinkedList<ContextSetDescription> CSD = new LinkedList<ContextSetDescription>();
				ContextSetDescription Initial = new ContextSetDescription();
				Initial.setName("SingleGene");
				Initial.setPreprocessed(true);
				Initial.setType("IntergenicDist");
				CSD.add(Initial);
				OS.setCSDs(CSD);
				
				progressBar.setValue(100);					
				progressBar.setVisible(false);
				
				GenomeWorkingSetFileName.setVisible(true);
				GenomeWorkingSetFileName.setText(GenomeWorkingSetFile_NoPath);

				} catch (Exception ex){
					progressBar.setStringPainted(false);
					progressBar.setValue(0);
					GenomeWorkingSetFileImproperlyLoaded = true;
					JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
							"Invalid File Format", JOptionPane.ERROR_MESSAGE);
					GenomeWorkingSetFileName.setText(strCancelled);
				}

			}
			return null;
		}
		
		public void done(){
			
			//adjust switches
			LoadingGenomeFiles = false;
			GenomeWorkingSetLoaded = true;
			LoadingGeneClusters = false;
			GeneClustersLoaded = false;
			ReadyToSubmit = true;

			//adjust buttons
			btnLoad.setEnabled(true);
			btnClusterLoad.setEnabled(true);
			btnSubmit.setEnabled(true);
			
			if (ClusterFileImproperlyLoaded == true){
				ClusterFileName.setVisible(true);
				ClusterFileName.setText(strNoFileLoaded);
			}
			ClusterFileImproperlyLoaded = false;
			if (GenomeWorkingSetFileImproperlyLoaded == true){
				GenomeWorkingSetFileName.setVisible(true);
				GenomeWorkingSetFileName.setText(strNoFileLoaded);
			}
			GenomeWorkingSetFileImproperlyLoaded = false;
			
			//all progress bars are made invisible
			progressBar.setVisible(false);
			progressBarClusters.setVisible(false);
		
		}
		
	}

	//Load homology clusters
	class LoadClustersWorker extends SwingWorker<Void, Void>{

		public LoadClustersWorker(String filename){
			ClustersFile = filename;
		}
		
		@Override
		protected Void doInBackground() throws Exception {

			//disable all buttons
			btnLoad.setEnabled(false);
			btnClusterLoad.setEnabled(false);
			btnSubmit.setEnabled(false);
			
			if (GenomeWorkingSetLoaded == true){
				
					ClusterFileName.setVisible(false);
					progressBarClusters.setVisible(true);
					progressBarClusters.setStringPainted(true);
					progressBarClusters.setValue(0);
					
					int LineCounter = 0;
					int clusterProgress = 0;
					setProgress(clusterProgress);

					try {
						
						//First: count lines in the file
						//import buffered reader
						BufferedReader br_count = new BufferedReader( new FileReader(ClustersFile));
						int TotalLines = 0;
						
						//count lines
						while (br_count.readLine() != null){
							TotalLines++;
						}
						
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
							if (OS.LargestCluster < GeneClusterNum){
								OS.LargestCluster = GeneClusterNum;
							}
							
							//add cluster number 
							OS.getSpecies().get(ImportedLine[0])
								.addClusterNumber(ImportedLine[1], GeneStart, GeneStop, GeneClusterNum);
							
							//report to SwingWorker
							LineCounter++;
							
							clusterProgress= (int) Math.round(100*((double)LineCounter/(double)TotalLines));
							setProgress(clusterProgress);
							
						}
						
						//set status of 'gene clusters loaded' to true
						OS.setGeneClustersLoaded(true);
						ClusterFileImproperlyLoaded = false;
						progressBarClusters.setVisible(false);
						ClusterFileName.setVisible(true);
						ClusterFileName.setText(ClustersFile_NoPath);
						
					} catch(Exception ex) {
						
						progressBarClusters.setStringPainted(false);
						
						JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
								"Invalid File Format",JOptionPane.ERROR_MESSAGE);

						ClusterFileImproperlyLoaded = true;
						LoadingGeneClusters = false;
						OS.setGeneClustersLoaded(false);
						ClusterFileName.setText(strCancelled);
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "You must load a genomic working set before loading homologous gene clusters.",
							"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
				}
			
			return null;
		}
		
		public void done(){
			
			//adjust switches
			LoadingGenomeFiles = false;
			GenomeWorkingSetLoaded = true;
			LoadingGeneClusters = false;
			GeneClustersLoaded = true;
			ReadyToSubmit = true;
			
			//adjust buttons
			btnLoad.setEnabled(true);
			btnClusterLoad.setEnabled(true);
			btnSubmit.setEnabled(true);
			
			//all progress bars are made invisible, all files visible
			progressBar.setVisible(false);
			progressBarClusters.setVisible(false);
			ClusterFileName.setVisible(true);
		}
	}
	
	//scroll bar signaling
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress")
			
			//adjust either loading genomes progress bar or loading clusters progress bar
			if (GenomeWorkingSetLoaded == false){
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			} else {
				int progress = (Integer) evt.getNewValue();
				progressBarClusters.setValue(progress);
			}
		}

	//getters + setters
	public JButton getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(JButton btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	//create a new dendrogram window, with the loaded OS
	public void invokeDendrograma(){
		
//		System.out.println("Breakpoint!");
		
		new Dendrograma(OS);
		
//		//optional print statements (working)
//		System.out.println(OS.getSpeciesNames());
//		System.out.println(OS.getSpecies().get("Haloarcula_amylolytica").getGroupings().get(0).getName());
	}
}