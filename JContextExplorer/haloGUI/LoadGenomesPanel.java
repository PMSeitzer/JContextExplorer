package haloGUI;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescriptions;
import genomeObjects.OrganismSet;
//import importExport.FitxerDades;
import inicial.Dendrograma;
//import inicial.Language;

import javax.swing.*;

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
public class LoadGenomesPanel extends JPanel 
	implements ActionListener, PropertyChangeListener{
	
	//GUI components
	private JLabel Genomes, Operons, GeneClusters;
	private JProgressBar progressBar, progressBarOps, progressBarClusters;
	private String operonComp = "Compute";
	private String strLoad = "Load";
	private String clusterLoad = "Load";
	private String distanceOption = "compute based on intergenic distance";
	private String stroperonName = "Enter Name: ";
	private String strNoFileLoaded = "No file currently loaded.";
	//private String distanceOption = "intergenic distance";
	private String loadOption = "load from file";
	private JButton btnLoad, computeOperons, loadOperons, btnClusterLoad, btnSubmit;
	private JTextField MappingFileName, operonTolerance, ClusterFileName, OperonFileName, operonName, contextSetName;
	private StartFrame sf;
	private ButtonGroup operonType;
	private JRadioButton OperonsbyDistance, OperonsbyFile;
	private JCheckBox cbStrandOption;
	private String strcbStrandOption = "Genes must be on same strand";
	
	//Switches to determine operations able to be performed
	private boolean LoadingGenomeFiles = false;
	private boolean AbletoComputeOperons = false;
	private boolean ComputingOperons = false;
	private boolean LoadingGeneClusters = false;
	private boolean LoadingOperons = false;
	private boolean ReadyToSubmit = false;
	private boolean ClusterFileImproperlyLoaded = false;
	private boolean MappingFileImproperlyLoaded = false;
	
	//Loaded Organism Set + corresponding information
	private OrganismSet OS;
	private int TotalOrganisms;	
	
	//fields for display
	private String MappingFile;
	private String ClustersFile;
	
	//actual file name
	private String GenomeContextFile = "";
	private String GenomeClustersFile = "";
	private String GenomeOperonsFile = "";
	
	//constructor
	public LoadGenomesPanel (StartFrame startframe) {
		this.getPanel();
		this.setVisible(true);
		this.sf = startframe;
	}

	//panel components
	public void getPanel(){
		
		//initialize panel
		//this.setBorder(BorderFactory.createTitledBorder("Initializations"));
		this.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(5,5,5,5);
		
		//Genome section heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		Genomes = new JLabel(" GENOMIC WORKING SET");
		Genomes.setBackground(Color.GRAY);
		Genomes.setOpaque(true);
		add(Genomes,c);
		gridy++;
	
		//Load File button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnLoad = new JButton(strLoad);
		btnLoad.addActionListener(this);
		add(btnLoad, c);
		
		// Mapping File Name
		c.ipady = 5;
		c.gridx = 2;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		MappingFileName = new JTextField();
		MappingFileName.setText(strNoFileLoaded); // No file loaded
		MappingFileName.addActionListener(this);
		MappingFileName.setEditable(false);
		add(MappingFileName, c);
//		gridy++;		
		
		//loading genomes progress bar
		c.ipady = 5;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(false);
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		progressBar.setForeground(Color.BLUE);
		progressBar.setVisible(false);
		add(progressBar, c);
		gridy++;

		//Operon computation section heading
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		Operons = new JLabel(" INITIAL GENOMIC CONTEXT SET");
		Operons.setBackground(Color.GRAY);
		Operons.setOpaque(true);
		add(Operons,c);
		gridy++;
		
		//Name: field
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		operonName = new JTextField(stroperonName);
		operonName.setEditable(false);
		add(operonName,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		contextSetName = new JTextField("Operons");
		contextSetName.setEditable(true);
		contextSetName.addActionListener(this);
		//contextSetName.setColumns(200);
		add(contextSetName, c);
		gridy++;
		
		//radio buttons
		//search type button group definition
		OperonsbyDistance = new JRadioButton(distanceOption);
		OperonsbyFile = new JRadioButton(loadOption);
		operonType = new ButtonGroup();
		operonType.add(OperonsbyDistance);
		operonType.add(OperonsbyFile);
		operonType.setSelected(OperonsbyDistance.getModel(),true);
		
		// display on panel
		c.insets = new Insets(1,1,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(OperonsbyDistance, c);
		c.gridx = 3;
		c.gridwidth = 2;
		c.gridy = gridy;
		add(OperonsbyFile, c);
		gridy++;
		
		//intergenic distance text field
		c.insets = new Insets(5,5,1,5);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.ipady = 7;
		operonTolerance = new JTextField("20");
		operonTolerance.setEditable(true);
		operonTolerance.addActionListener(this);
		add(operonTolerance, c);
			
		//Compute operons button
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(5,5,1,5);
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.NONE;
		computeOperons = new JButton(operonComp);
		computeOperons.addActionListener(this);
		add(computeOperons, c);

		// load operons file
		c.ipadx = 0;
		c.insets = new Insets(5,5,1,5);
		c.gridx = 4;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		loadOperons = new JButton(strLoad);
		loadOperons.addActionListener(this);
		add(loadOperons, c);
		gridy++;
		
		//check box
		c.insets = new Insets(1,5,5,5);
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 0;
		c.gridwidth = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbStrandOption = new JCheckBox(strcbStrandOption);
		cbStrandOption.setSelected(true);
		add(cbStrandOption, c);
		gridy++;

		// operon loading progress bar
		c.insets = new Insets(5,5,5,5);
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 0;
		c.gridwidth = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBarOps = new JProgressBar(0, 100);
		progressBarOps.setStringPainted(false);
		progressBarOps.setBorderPainted(false);
		progressBarOps.setValue(0);
		progressBarOps.setForeground(Color.BLUE);
		progressBarOps.setVisible(false);
		add(progressBarOps, c);
		
		// Operon File Name
		c.insets = new Insets(5,5,5,5);
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		OperonFileName = new JTextField();
		OperonFileName.setText(strNoFileLoaded); // No file loaded
		OperonFileName.addActionListener(this);
		OperonFileName.setEditable(false);
		add(OperonFileName, c);
		gridy++;
		
		//Gene clusters section heading
		c.insets = new Insets(5,5,5,5);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		GeneClusters = new JLabel(" PRE-COMPUTED GENE CLUSTERS (OPTIONAL)");
		GeneClusters.setBackground(Color.GRAY);
		GeneClusters.setOpaque(true);
		add(GeneClusters,c);
		gridy++;
		
		//Load Cluster File button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnClusterLoad = new JButton(clusterLoad);
		btnClusterLoad.addActionListener(this);
		add(btnClusterLoad, c);

		// gene clusters progress bar
		c.ipady = 5;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 3;
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
		c.gridx = 2;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		ClusterFileName = new JTextField();
		ClusterFileName.setText(strNoFileLoaded); // No file loaded
		ClusterFileName.setEditable(false);
		add(ClusterFileName, c);
		gridy++;
		
		//Submit button
		c.gridx = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipady = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		add(btnSubmit, c);
		
	}
	
	//All Actions
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//load genome files
		if (evt.getSource().equals(btnLoad)) {
			LoadingGenomeFiles = true;
			ComputingOperons = false;
			LoadingGeneClusters = false;
			LoadingOperons = false;
			ReadyToSubmit = false;
			
			//reset operons, clusters
			progressBarClusters.setValue(0);
			progressBarClusters.setStringPainted(false);			
			progressBarOps.setValue(0);
			progressBarOps.setStringPainted(false);
			
			String fileName = getMappingFile();
			//System.out.println("fileName: " + fileName);
			if (fileName != null){
				
				if (!fileName.equals(GenomeContextFile)){
				
				OperonFileName.setText(strNoFileLoaded);
				ClusterFileName.setText(strNoFileLoaded);
				
				GenomeContextFile = fileName;
				LoadGenomesWorker lg = new LoadGenomesWorker(fileName);
				lg.addPropertyChangeListener(this);
				lg.execute();

				}
				
			} else {
				fileName = "The operation was cancelled.";
				progressBar.setValue(0);
				progressBar.setStringPainted(false);
				GenomeClustersFile = fileName;
						
				AbletoComputeOperons = false;
				LoadingGenomeFiles = false;
				ComputingOperons = false;
				LoadingOperons = false;
				MappingFileName.setText(fileName);
				OperonFileName.setText(strNoFileLoaded);
			}

		} 
		
		if (evt.getSource().equals(computeOperons) && operonType.getSelection().equals(OperonsbyDistance.getModel())){

			LoadingGenomeFiles = false;
			ComputingOperons = true;
			LoadingGeneClusters = false;
			ReadyToSubmit = false;
			LoadingOperons = false;
			
			//create a new swing worker
			LoadGenomesWorker lg = new LoadGenomesWorker(null);
			lg.addPropertyChangeListener(this);
			lg.execute();
		
		} else if (evt.getSource().equals(btnClusterLoad)){
			LoadingGenomeFiles = false;
			ComputingOperons = false;
			LoadingGeneClusters = true;
			LoadingOperons = false;
			
			String clusterfileName = null;
			if (AbletoComputeOperons == true){
				clusterfileName = getMappingFile();
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set before loading pre-computed gene clusters.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
			
			if (clusterfileName != null){
				
				if (!clusterfileName.equals(GenomeClustersFile)){
				
				GenomeClustersFile = clusterfileName;
				LoadGenomesWorker lg = new LoadGenomesWorker(clusterfileName);
				lg.addPropertyChangeListener(this);
				lg.execute();
				
				}

			} else {
				clusterfileName = "The operation was cancelled.";
				progressBarClusters.setValue(0);
				progressBarClusters.setStringPainted(false);
				LoadingGenomeFiles = false;
				ComputingOperons = false;
				if (OS != null){
					OS.setGeneClustersLoaded(false);
				}
				ClusterFileName.setText(clusterfileName);
				GenomeClustersFile = clusterfileName;
			}
			

		}
		
		if (evt.getSource().equals(loadOperons) && operonType.getSelection().equals(OperonsbyFile.getModel())) {
			LoadingGenomeFiles = false;
			ComputingOperons = false;
			LoadingGeneClusters = false;
			LoadingOperons = true;
			
			String operonsfileName = null;
			if (AbletoComputeOperons == true){

				operonsfileName = getMappingFile();
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set and compute or load a context set before continuing.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
			
			if (operonsfileName != null){
				
				if (!operonsfileName.equals(GenomeOperonsFile)){
				
				//System.out.println("before swing worker");
				OperonFileName.setText(operonsfileName);
				GenomeOperonsFile = operonsfileName;
				LoadGenomesWorker lg = new LoadGenomesWorker(operonsfileName);
				lg.addPropertyChangeListener(this);
				lg.execute();

				}

			} else {
				operonsfileName = "The operation was cancelled.";;
				progressBarOps.setValue(0);
				progressBarOps.setStringPainted(false);
				progressBarOps.setVisible(false);
				LoadingGenomeFiles = false;
				ComputingOperons = false;
				OperonFileName.setText(operonsfileName);
				GenomeOperonsFile = operonsfileName;
			}
			
			
		}
		
		if (evt.getSource().equals(btnSubmit)){
		//if (evt.getSource().equals(this.getBtnSubmit())){
			if (ReadyToSubmit == true) {
				
				//close this window
				sf.dispose();
				
				//open the dendrogram window
				invokeDendrograma();
				
			} else if (AbletoComputeOperons == true){
				JOptionPane.showMessageDialog(null, "Please compute or load a context set before continuing.",
						"No Valid Context Set Loaded", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set and compute or load a context set before continuing.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	
	}

	//retrieve a data file
	private String getMappingFile() {
		
		//use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(sf, "English",
				FileDialog.LOAD);

		fd.setVisible(true);
		String MappingFile = fd.getDirectory() + fd.getFile();
		//String MappingFile =  fd.getFile();
		if (fd.getFile() == null) {
			MappingFile = null;
		} 
		return MappingFile; //file name
	}	
	
	//Perform File Loading + Operon computation tasks
	class LoadGenomesWorker extends SwingWorker<Void, Void>{
		
		public LoadGenomesWorker(String filename){
			if (filename != null){
				if (LoadingGenomeFiles == true){
					MappingFile = filename;
					GenomeContextFile = filename;
				} else if(LoadingGeneClusters == true) {
					ClustersFile = filename;
					GenomeClustersFile = filename;
				} else if (ComputingOperons == true){
					GenomeOperonsFile = filename;
				}
			}
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//disable all buttons, then re-enable all buttons
			btnLoad.setEnabled(false);
			computeOperons.setEnabled(false);
			btnClusterLoad.setEnabled(false);
			btnSubmit.setEnabled(false);
			loadOperons.setEnabled(false);
			
			//LOAD GENOME SET
			if (LoadingGenomeFiles == true){
			
			MappingFileName.setVisible(false);
			progressBar.setVisible(true);
			int progress = 0;
			setProgress(progress);
			progressBar.setStringPainted(true);
			
			//import	
			OS = new OrganismSet();
			TotalOrganisms = OS.determineNumberOfSpecies(MappingFile);
			int OrganismsCompleted = 0;

			//define a new linked list, for each annotated genome
			LinkedHashMap<String, AnnotatedGenome> Species = new LinkedHashMap<String, AnnotatedGenome>();
			
			//define a new list, for each species name
			LinkedList<String> SpeciesNames = new LinkedList<String>();
						
				try{
					//import buffered reader
					BufferedReader br = new BufferedReader(new FileReader(MappingFile));
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
					OS.setSpecies(Species);
					OS.setSpeciesNames(SpeciesNames);
					progressBar.setValue(100);
					AbletoComputeOperons = true;
					
					progressBar.setVisible(false);
					MappingFileName.setVisible(true);
					MappingFileName.setText(GenomeContextFile);

				}catch(Exception ex){
					progressBar.setStringPainted(false);
					progressBar.setValue(0);
					progressBarOps.setStringPainted(false);
					progressBarOps.setValue(0);
					AbletoComputeOperons = false;
					MappingFileImproperlyLoaded = true;
					JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
							"Invalid File Format", JOptionPane.ERROR_MESSAGE);

				}

			LoadingGenomeFiles = false;
			
		} else if (ComputingOperons == true || LoadingOperons == true){
			
			if (AbletoComputeOperons == true){
			
				if (ComputingOperons == true){
				
				progressBarOps.setVisible(true);
				OperonFileName.setVisible(false);
				progressBarOps.setValue(0);
				int OrganismsCompleted = 0;
				int ComputeProgress = 0;
				progressBarOps.setStringPainted(true);

				for (Entry<String, AnnotatedGenome> entry: OS.getSpecies().entrySet()){
					
					//"true" is for initialization
					entry.getValue().setGroupings(null);
					entry.getValue().ComputeContextSet(contextSetName.getText(), Integer.parseInt(operonTolerance.getText()), cbStrandOption.isSelected());
					OrganismsCompleted++;
					ComputeProgress = (int) Math.round(100*((double)OrganismsCompleted/(double)TotalOrganisms));
					setProgress(ComputeProgress);
					
					//optional print statement
					//System.out.println(ComputeProgress);
					//System.out.println(Counter + "/" + OS.getSpecies().entrySet().size() + " Completed.");
				}
				
				//update progress bar
				progressBarOps.setValue(100);
				
				//add descriptions to the OS
				LinkedList<String> CSD = new LinkedList<String>();
				CSD.add(contextSetName.getText());
				OS.setCSDs(CSD);
				//System.out.println("LoadGenomePanels: " + OS.getCSDs());
				
				//change tasks descriptions
				ComputingOperons = false;
				ReadyToSubmit = true;
				
				//update operon file name, and make visible
				if (cbStrandOption.isSelected()){
					//GenomeOperonsFile = "Computed with an intergenic distance of " + operonTolerance.getText() + ", same strand only";
					GenomeOperonsFile = "Genes on the same strand within " + operonTolerance.getText() + " bp of each other are in the same set.";
				} else {
					//GenomeOperonsFile = "Computed with an intergenic distance of " + operonTolerance.getText() + ", either strand";
					GenomeOperonsFile = "Genes on either strand within " + operonTolerance.getText() + " bp of each other are in the same set.";
				}
				OperonFileName.setText(GenomeOperonsFile);
				progressBarOps.setVisible(false);
				OperonFileName.setVisible(true);
				
				
				} else if (LoadingOperons = true) {
					
					//make operons able to be computed
					OS.setContinueImportingOperons(true);
					
					progressBarOps.setVisible(true);
					OperonFileName.setVisible(false);
					progressBarOps.setValue(0);
					int OrganismsCompleted = 0;
					int OperonCounter = 0;
					int operonLoadProgress = 0;
					progressBarOps.setStringPainted(true);

					try {
						//initialize a context set
						ContextSet CS = new ContextSet();
						
						//import buffered reader
						BufferedReader br_count = new BufferedReader(new FileReader(GenomeOperonsFile));
						BufferedReader br = new BufferedReader(new FileReader(GenomeOperonsFile));
						String Line = null;
						int TotalLines = 0;
						
						//count lines
						while (br_count.readLine() != null){
							TotalLines++;
						}
						
						int LineCounter = 0;
						while ((Line = br.readLine()) != null){
							
							//import each line
							String[] ImportedLine = Line.split("\t");
						
							//retrieve species
							AnnotatedGenome AG = OS.getSpecies().get(ImportedLine[0]);
							AG.setGroupings(null);
							
							//import from file
							if (OS.isContinueImportingOperons() == true){
								AG.ImportContextSet(contextSetName.getText(), ImportedLine[1]);
								
								//report to SwingWorker
								LineCounter++;
								
								operonLoadProgress= (int) Math.round(100*((double)LineCounter/(double)TotalLines));
								setProgress(operonLoadProgress);
							}

							if (AG.isTryToComputeOperons() == false){
								OS.setContinueImportingOperons(false);
								break;
							}

						}
						
						//set the first context set
						LinkedList<String> CSD = new LinkedList<String>();
						CSD.add(contextSetName.getText());
						OS.setCSDs(CSD);
						
						progressBarOps.setVisible(false);
						OperonFileName.setVisible(true);
						OperonFileName.setText(GenomeOperonsFile);
						ReadyToSubmit = true;
						//System.out.println("operons loaded successfully.");
						
					} catch(Exception ex) {
						
						progressBarOps.setStringPainted(false);

						JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
								"Unable to Load Context Set File",JOptionPane.ERROR_MESSAGE);

						LoadingOperons = false;
						OperonFileName.setText(strNoFileLoaded);
						//System.out.println("operons not loaded successfully.");
					}
					
					if (OS.isContinueImportingOperons() == false){

						progressBarOps.setStringPainted(false);
						
						JOptionPane.showMessageDialog(null, "One or more of the component files could not be loaded or were improperly formatted.",
								"Unable to Load Context Set File",JOptionPane.ERROR_MESSAGE);

						LoadingOperons = false;
						OperonFileName.setText(strNoFileLoaded);
					}
					
					
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set before loading or computing a context set.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}	
		
		if (LoadingGeneClusters == true){
			
			if (AbletoComputeOperons == true){	
				
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
					ClusterFileName.setText(GenomeClustersFile);
					
					
				} catch(Exception ex) {
					
					progressBarClusters.setStringPainted(false);
					
					JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
							"Invalid File Format",JOptionPane.ERROR_MESSAGE);

					ClusterFileImproperlyLoaded = true;
					LoadingGeneClusters = false;
					OS.setGeneClustersLoaded(false);
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "You must load a genomic working set before loading or computing a context set.",
						"No Valid Genomic Working Set Loaded", JOptionPane.ERROR_MESSAGE);
			}
				
		}

			return null;
		}
		
		public void done(){
			btnLoad.setEnabled(true);
			computeOperons.setEnabled(true);
			btnClusterLoad.setEnabled(true);
			btnSubmit.setEnabled(true);
			loadOperons.setEnabled(true);
			if (ClusterFileImproperlyLoaded == true){
				ClusterFileName.setVisible(true);
				ClusterFileName.setText(strNoFileLoaded);
			}
			ClusterFileImproperlyLoaded = false;
			if (MappingFileImproperlyLoaded == true){
				MappingFileName.setVisible(true);
				MappingFileName.setText(strNoFileLoaded);
			}
			MappingFileImproperlyLoaded = false;
			
			//all progress bars should be invisible!
			progressBar.setVisible(false);
			progressBarOps.setVisible(false);
			progressBarClusters.setVisible(false);
		}
		
	}

	//scroll bar signaling
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress")
			
			//Loading genome files or operons
			if (LoadingGenomeFiles == true){
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			} else if (ComputingOperons == true){
				int progress = (Integer) evt.getNewValue();
				progressBarOps.setValue(progress);
			} else if (LoadingGeneClusters == true){
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
		new Dendrograma(OS);
		
//		//optional print statements (working)
//		System.out.println(OS.getSpeciesNames());
//		System.out.println(OS.getSpecies().get("Haloarcula_amylolytica").getGroupings().get(0).getName());
	}
}