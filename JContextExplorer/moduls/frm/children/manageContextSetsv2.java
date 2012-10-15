package moduls.frm.children;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescription;
import genomeObjects.ContextSetDescriptions;
import genomeObjects.OrganismSet;
import haloGUI.StartFrame;
import inicial.Language;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.Panels.Jpan_btn_NEW;

public class manageContextSetsv2 extends JDialog implements ActionListener, PropertyChangeListener{

	//fields
	//serial ID
	private static final long serialVersionUID = 1L;

	//parent information
	private FrmPrincipalDesk fr;
	private Jpan_btn_NEW jb;
	
	//Loaded operon file + related booleans
	private String strNoOperons = "Gene groupings could not be loaded or computed successfully.";
	private String OperonStringToDisplay = strNoOperons;
	private boolean OperonsbyComputation;
	private boolean ReadyToAdd = false;
	private boolean AcceptableName = true;
	private ContextSetDescription ToAdd;
	private boolean ComputedGrouping = false;
	private boolean LoadedGrouping = false;
	private String ComputedString;
	private String LoadedString;
	
	//components
	//panels
	/*
	 * 
	 */
	private JPanel jp;
	
	//dummy components (for spacing)
	private JLabel d1, d2, d3, d4, d5;
	
	//general use
	private String strLoad = "Load";
	private JProgressBar progressBar;
	
	//radio button components + names + button group
	private ButtonGroup CSType;
	private LinkedHashMap<ButtonModel, LinkedList<Component>> RadioButtonComponents
		= new LinkedHashMap<ButtonModel, LinkedList<Component>>();

	//CSType (1) - Intergenic Distance
	private LinkedList<Component> CSIntergenicDist_group;
	private JRadioButton CSIntergenicDist;
	private String strCSIntergenicDist = "Group genes based on intergenic distance";
	private JTextField intergenicTolerance;
	private JButton computeIntergenic; 
	private String strcomputeIntergenic = "Compute";
	private JCheckBox cbStrandOption;
	private String strcbStrandOption = "Genes must be on same strand";
	
	//CSType (2) - CSRange
	private LinkedList<Component> CSRange_group;
	private JRadioButton CSRange;
	private String strCSRange = "Group genes based on nucleotide range";
	private JTextField ntBefore;
	private JTextField ntBeforeLabel;
	private String strntBeforeLabel = "nt Before:";
	private JTextField ntAfter;
	private JTextField ntAfterLabel;
	private String strntAfterLabel = "nt After:";
	
	//CSType (3) - CSGenesAround
	private LinkedList<Component> CSGenesAround_group;
	private JRadioButton CSGenesAround;
	private String strCSGenesAround = "Group genes based on number of nearby genes";
	private JTextField GenesBefore;
	private JTextField GenesBeforeLabel;
	private String strGenesBeforeLabel = "Genes Before:";
	private JTextField GenesAfter;
	private JTextField GenesAfterLabel;
	private String strGenesAfterLabel = "Genes After:";
	
	//CSType (4) - CSGenesBetween
	private LinkedList<Component> CSGenesBetween_group;
	private JRadioButton CSGenesBetween;
	private String strCSGenesBetween = "Group all genes between two queries together";
	
	//CSType (5) - CSMultipleQuery
	private LinkedList<Component> CSMultipleQuery_group;
	private JRadioButton CSMultipleQuery;
	private String strCSMultipleQuery = "Group multiple independent queries together";
	
	//CSType (6) CSLoaded
	private LinkedList<Component> CSLoaded_group;
	private JRadioButton CSLoaded;
	private String strCSLoaded = "Load gene groupings from file";
	private JButton btnLoadCS;
	
	//CSType (7) CS
	private LinkedList<Component> CSCombination_group;
	private JRadioButton CSCombination;
	private String strCSCombination = "Create a new context set by combining existing context sets";
	private JButton btnLaunchCombiner;
	private String strLaunchCombiner = "Launch Context Set Combiner Tool";
	
	//Add panel
	private JButton btnAddCS;
	private JLabel Add;
	private String strAddCS = "Add";
	private JTextField CSName, CSNameLabel;
	private String strCSNameLabel = "Enter Name: ";
	private JTextField contextSetHeader;
	private JTextField LoadedFileName;
	
	//Remove CS
	private JButton btnRemoveCS;
	private JLabel Remove;
	private String strRemoveCS = "Remove";
	private JComboBox contextSetMenu;
	
	//okay - close panel
	private JButton btnOK;
	private String strbtnOK = "OK";

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	//constructor
	public manageContextSetsv2(FrmPrincipalDesk f, Jpan_btn_NEW jbn){
		super();
		
		//imported information
		this.fr = f;
		this.jb = jbn;
		//this.ContextList = currentList;
		
		//frame settings
		//this.setSize(new Dimension(400, 350));
		this.setSize(700,620);
		
		this.setTitle("Add or Remove Context Sets");
		this.setLocationRelativeTo(null);
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		//add panel components
		//attempt - add nested panel structure for inherent organization
		this.getPanel();
		this.DisableComponents();
		this.setContentPane(jp);
		//this.pack(); //to pack or not to pack?
		
		//modality settings
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	//SwingWorker
	class btnLoadCS extends SwingWorker<Void, Void>{
		
		//constructor
		public btnLoadCS(String fileName){
			if (fileName == null){
				OperonsbyComputation = true;
				ComputedGrouping = true;
			} else {
				OperonsbyComputation = false;
				OperonStringToDisplay = fileName;
				LoadedGrouping = true;
			}
		}
		
		//do in background method
		@Override
		protected Void doInBackground() throws Exception {

			if (OperonsbyComputation == true){
				
			int TotalOrganisms = fr.getOS().getSpeciesNames().size();	
			progressBar.setVisible(true);
			LoadedFileName.setVisible(false);
			progressBar.setValue(0);
			int OrganismsCompleted = 0;
			int ComputeProgress = 0;
			progressBar.setStringPainted(true);

			for (Entry<String, AnnotatedGenome> entry: fr.getOS().getSpecies().entrySet()){
				
				entry.getValue().ComputeContextSet(CSName.getText(), Integer.parseInt(intergenicTolerance.getText()), cbStrandOption.isSelected());
				OrganismsCompleted++;
				ComputeProgress = (int) Math.round(100*((double)OrganismsCompleted/(double)TotalOrganisms));
				this.setProgress(ComputeProgress);
				
			}
			
			//update the context set candidate for loading
			ToAdd = new ContextSetDescription();
			ToAdd.setName(CSName.getText());
			ToAdd.setPreprocessed(true);
			ToAdd.setType("IntergenicDist");
			
			//update progress bar
			progressBar.setValue(100);
			
			//update operon file name, and make visible
			if (cbStrandOption.isSelected()){
				OperonStringToDisplay = "Gene groupings computed with an intergenic distance of " + intergenicTolerance.getText() + ", same strand only";
			} else {
				OperonStringToDisplay = "Gene groupings computed with an intergenic distance of " + intergenicTolerance.getText() + ", either strand";
			}
			
			ComputedString = OperonStringToDisplay;
			
			LoadedFileName.setText(OperonStringToDisplay);
			progressBar.setVisible(false);
			LoadedFileName.setVisible(true);
			
			ReadyToAdd = true;
			
			} else {
				
				progressBar.setVisible(true);
				LoadedFileName.setVisible(false);
				progressBar.setValue(0);
				int OrganismsCompleted = 0;
				int OperonCounter = 0;
				int operonLoadProgress = 0;
				progressBar.setStringPainted(true);

				try {
					//initialize a context set
					ContextSet CS = new ContextSet();
					
					//import buffered reader
					BufferedReader br_count = new BufferedReader(new FileReader(OperonStringToDisplay));
					BufferedReader br = new BufferedReader(new FileReader(OperonStringToDisplay));
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
						AnnotatedGenome AG = fr.getOS().getSpecies().get(ImportedLine[0]);
						
						//import from file
						AG.ImportContextSet(CSName.getText(), ImportedLine[1]);

						//report to SwingWorker
						LineCounter++;
						
						operonLoadProgress= (int) Math.round(100*((double)LineCounter/(double)TotalLines));
						setProgress(operonLoadProgress);
						
					}

					ToAdd = new ContextSetDescription();
					ToAdd.setName(CSName.getText());
					ToAdd.setPreprocessed(true);
					ToAdd.setType("Loaded");
					
					progressBar.setVisible(false);
					LoadedFileName.setVisible(true);
					LoadedFileName.setText("File Loaded: " + OperonStringToDisplay);
					LoadedString = OperonStringToDisplay;
					//System.out.println("operons loaded successfully.");
					
					ReadyToAdd = true;
					
				} catch(Exception ex) {
					
					progressBar.setStringPainted(false);
					
					JOptionPane.showMessageDialog(null, "The file could not be loaded or was improperly formatted.",
							"Improper File Format",JOptionPane.ERROR_MESSAGE);

					LoadedFileName.setText(strNoOperons);
					LoadedString = strNoOperons;
					//System.out.println("operons not loaded successfully.");
				}
			}
			
			return null;
		}
		
		//after all completed
		public void done(){

		}
		
	}
	
	//add panel components
	private void getPanel(){

		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		//hidden grid!
		//dummy labels, to artificially normalize column widths
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d1 = new JLabel(" ");
		d1.setBackground(Color.LIGHT_GRAY);
		d1.setOpaque(false);
		jp.add(d1, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d2 = new JLabel(" ");
		d2.setBackground(Color.LIGHT_GRAY);
		d2.setOpaque(false);
		jp.add(d2, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d3 = new JLabel(" ");
		d3.setBackground(Color.LIGHT_GRAY);
		d3.setOpaque(false);
		jp.add(d3, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d4 = new JLabel(" ");
		d4.setBackground(Color.LIGHT_GRAY);
		d4.setOpaque(false);
		jp.add(d4, c);
		
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d5 = new JLabel(" ");
		d5.setBackground(Color.LIGHT_GRAY);
		d5.setOpaque(false);
		jp.add(d5, c);

		//create radio buttons
		//search type button group definition
		CSIntergenicDist = new JRadioButton(strCSIntergenicDist);
		CSRange = new JRadioButton(strCSRange);
		CSGenesAround = new JRadioButton(strCSGenesAround);
		CSGenesBetween = new JRadioButton(strCSGenesBetween);
		CSMultipleQuery = new JRadioButton(strCSMultipleQuery);
		CSLoaded = new JRadioButton(strCSLoaded);
		CSCombination = new JRadioButton(strCSCombination);
		
		//define button group
		CSType = new ButtonGroup(); CSType.add(CSIntergenicDist); CSType.add(CSRange); CSType.add(CSGenesAround);
		CSType.add(CSGenesBetween); CSType.add(CSMultipleQuery); CSType.add(CSLoaded); CSType.add(CSCombination);
		//CSType.setSelected(CSIntergenicDist.getModel(), true);
		/*
		 * ADD COMPONENTS TO PANEL!!!
		 */
		
		/*
		 * ADD CS
		 */
		
		//Add CS Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		Add = new JLabel(" ADD A CONTEXT SET");
		Add.setBackground(Color.GRAY);
		Add.setOpaque(true);
		jp.add(Add,c);
		gridy++;
		
		//Name Label
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		CSNameLabel = new JTextField(strCSNameLabel);
		CSNameLabel.setEditable(false);
		jp.add(CSNameLabel,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.insets = new Insets(3,2,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		CSName = new JTextField("");
		CSName.setEditable(true);
		CSName.addActionListener(this);
		//CSName.setColumns(200);
		jp.add(CSName, c);
		gridy++;

		//(1) CSINTERGENICDIST
		
		CSIntergenicDist_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.ipady = 0;
		c.insets = new Insets(5,1,1,1);
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		jp.add(CSIntergenicDist, c);
		CSIntergenicDist.addActionListener(this);
		gridy++;
		
		//intergenic distance text field
		c.gridx = 0;
		c.gridy = gridy;
		c.ipady = 7;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(1,20,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		intergenicTolerance = new JTextField("20");
		intergenicTolerance.setEditable(true);
		intergenicTolerance.addActionListener(this);
		jp.add(intergenicTolerance, c);
		CSIntergenicDist_group.add(intergenicTolerance);
		
		//Compute gene groupings button
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.ipady = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		computeIntergenic = new JButton(strcomputeIntergenic);
		computeIntergenic.addActionListener(this);
		jp.add(computeIntergenic, c);
		CSIntergenicDist_group.add(computeIntergenic);
		
		//check box
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 2;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbStrandOption = new JCheckBox(strcbStrandOption);
		cbStrandOption.setSelected(true);
		jp.add(cbStrandOption, c);
		CSIntergenicDist_group.add(cbStrandOption);
		gridy++;		
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSIntergenicDist.getModel(), CSIntergenicDist_group);
		
		//(2) CSRANGE
		
		//grouping
		CSRange_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(10,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		jp.add(CSRange, c);
		CSRange.addActionListener(this);
		gridy++;
		
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(1,20,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		ntBeforeLabel = new JTextField(strntBeforeLabel);
		ntBeforeLabel.setEditable(false);
		CSRange_group.add(ntBeforeLabel);
		jp.add(ntBeforeLabel, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		ntBefore = new JTextField("1000");
		ntBefore.setEditable(true);
		CSRange_group.add(ntBefore);
		jp.add(ntBefore, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		ntAfterLabel = new JTextField(strntAfterLabel);
		ntAfterLabel.setEditable(false);
		CSRange_group.add(ntAfterLabel);
		jp.add(ntAfterLabel, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		ntAfter = new JTextField("1000");
		ntAfter.setEditable(true);
		ntAfter.addActionListener(this);
		CSRange_group.add(ntAfter);
		jp.add(ntAfter, c);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSRange.getModel(), CSRange_group);
		
		//(3) CSGENESAROUND
		
		//grouping
		CSGenesAround_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,1,1,1);
		c.gridwidth = 5;
		jp.add(CSGenesAround, c);
		CSGenesAround.addActionListener(this);
		gridy++;
		
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(1,20,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		GenesBeforeLabel = new JTextField(strGenesBeforeLabel);
		GenesBeforeLabel.setEditable(false);
		CSGenesAround_group.add(GenesBeforeLabel);
		jp.add(GenesBeforeLabel, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		GenesBefore = new JTextField("2");
		GenesBefore.setEditable(true);
		CSGenesAround_group.add(GenesBefore);
		jp.add(GenesBefore, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		GenesAfterLabel = new JTextField(strGenesAfterLabel);
		GenesAfterLabel.setEditable(false);
		CSGenesAround_group.add(GenesAfterLabel);
		jp.add(GenesAfterLabel, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		GenesAfter = new JTextField("2");
		GenesAfter.addActionListener(this);
		GenesAfter.setEditable(true);
		CSGenesAround_group.add(GenesAfter);
		jp.add(GenesAfter, c);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSGenesAround.getModel(), CSGenesAround_group);
		
		//(4) CSGENESBETWEEN
		
		//grouping
		CSGenesBetween_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(CSGenesBetween, c);
		CSGenesBetween.addActionListener(this);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSGenesBetween.getModel(), CSGenesBetween_group);
		
		//(5) CSMULTIPLEQUERY
		
		//grouping
		CSMultipleQuery_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(CSMultipleQuery, c);
		CSMultipleQuery.addActionListener(this);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSMultipleQuery.getModel(), CSMultipleQuery_group);
		
		//(6) CSLOADED
		
		//grouping
		CSLoaded_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(CSLoaded, c);
		CSLoaded.addActionListener(this);
		gridy++;
		
		// load gene groupings from file
		c.ipadx = 0;
		c.insets = new Insets(1,20,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		btnLoadCS = new JButton(strLoad);
		btnLoadCS.addActionListener(this);
		jp.add(btnLoadCS, c);
		CSLoaded_group.add(btnLoadCS);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(CSLoaded.getModel(), CSLoaded_group);
		
		//(7) CSCOMBINATION
		
		//grouping
		CSCombination_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(CSCombination, c);
		CSCombination.addActionListener(this);
		gridy++;

		//launch button tool
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(1,20,1,1);
		btnLaunchCombiner = new JButton(strLaunchCombiner);
		btnLaunchCombiner.addActionListener(this);
		jp.add(btnLaunchCombiner, c);
		CSCombination_group.add(btnLaunchCombiner);
		gridy++;

		//add this mapping to hash map.
		RadioButtonComponents.put(CSCombination.getModel(), CSCombination_group);

		// ADD CONTEXT SET
		
		// progress bar
		c.insets = new Insets(10,1,1,1);
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 0;
		c.gridwidth = 4;
		c.ipady = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(false);
		progressBar.setBorderPainted(false);
		progressBar.setValue(0);
		progressBar.setForeground(Color.BLUE);
		progressBar.setVisible(false);
		jp.add(progressBar, c);
		
		// Operon File Name
		c.insets = new Insets(10,1,1,1);
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		LoadedFileName = new JTextField();
		LoadedFileName.setText(""); // No file loaded
		LoadedFileName.addActionListener(this);
		LoadedFileName.setEditable(false);
		jp.add(LoadedFileName, c);
		
		//add context set 
		c.gridx = 4;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipady = 0;
		c.insets = new Insets(10,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		btnAddCS = new JButton(strAddCS);
		btnAddCS.addActionListener(this);
		btnAddCS.setEnabled(true);
		jp.add(btnAddCS, c);
		gridy++;
		
		/*
		 * REMOVE CS
		 */
		
		//Remove CS Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(1,1,1,1);
		Remove = new JLabel(" REMOVE A CONTEXT SET");
		Remove.setBackground(Color.GRAY);
		Remove.setOpaque(true);
		jp.add(Remove,c);
		gridy++;
		
		// Context Set Text label
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		contextSetHeader = new JTextField();
		contextSetHeader.setText("Context Set:"); // context set currently loaded
		contextSetHeader.addActionListener(this);
		contextSetHeader.setEditable(false);
		jp.add(contextSetHeader, c);
		
		// drop-down menu for Context Sets
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		String[] CurrentContextLists = this.convertContextSets(fr.getOS().getCSDs());
		contextSetMenu = new JComboBox(CurrentContextLists);
		contextSetMenu.addActionListener(this);
		contextSetMenu.setEnabled(true);
		jp.add(contextSetMenu, c);
		
		//remove button
		c.gridx = 3;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnRemoveCS = new JButton(strRemoveCS);
		btnRemoveCS.addActionListener(this);
		btnRemoveCS.setEnabled(true);
		jp.add(btnRemoveCS, c);
		gridy++;
		gridy++;

		//submit button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,1,1,1);
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp.add(btnOK, c);
		
		this.getContentPane().add(jp, BorderLayout.NORTH);
	}
	
	//disable all components
	private void DisableComponents(){
		for (LinkedList<Component> LLC : RadioButtonComponents.values()){
			for (Component c : LLC){
				c.setEnabled(false);
			}
		}
	}
	
	//ACTIONS
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//check boxes: enable + disable appropriate components
		EnableComponents(CSType.getSelection());
		
		//update appropriate message box
		UpdateMessageBox();
		
		//CSType (1) - Intergenic Distance
		if (evt.getSource().equals(computeIntergenic)){
			
			//check if name is acceptable
			CheckName();
			
			if (AcceptableName == true){
				//create a new swing worker
				btnLoadCS lo = new btnLoadCS(null);
				lo.addPropertyChangeListener(this);
				lo.execute();
			} 

			
		} 
		
		//CSType (6) CSLoaded
		if (evt.getSource().equals(btnLoadCS)){
			
			//check if name is acceptable
			CheckName();
			
			//only try to read in file if the name is acceptable.
			if (AcceptableName == true){
			
				String fileName = getMappingFile();
			
				if (fileName != null){
				
					if (!fileName.equals(OperonStringToDisplay)){
				
						LoadedFileName.setText(OperonStringToDisplay);
						OperonStringToDisplay = fileName;
						btnLoadCS lo = new btnLoadCS(fileName);
						lo.addPropertyChangeListener(this);
						lo.execute();
				
						}
					}
			
				}
		} 

		//CSType (7) CSCombination
		if (evt.getSource().equals(btnLaunchCombiner)){
			
			//check if name is acceptable
			CheckName();
			
			//ensure name is acceptable.
			if (AcceptableName == true){
			
				JOptionPane.showMessageDialog(null,"Functionality not implemented yet!",
						"Not Yet Created",JOptionPane.INFORMATION_MESSAGE);
			}
			
		} 
		
		//ADD BUTTON
		if (evt.getSource().equals(btnAddCS) || evt.getSource().equals(GenesAfter) || evt.getSource().equals(ntAfter)){
			
			//check if name is acceptable
			CheckName();
			
			if (AcceptableName == true){
			
				//rule out pre-processed cases, if necessary
				if (CSType.isSelected(CSIntergenicDist.getModel()) && ReadyToAdd == false) {
					JOptionPane.showMessageDialog(null, 
							"Select the compute button to compute genomic groupings before adding", "Gene Groupings not computed",
							JOptionPane.ERROR_MESSAGE);
				} else if (CSType.isSelected(CSLoaded.getModel()) && ReadyToAdd == false){
					JOptionPane.showMessageDialog(null, 
							"Select the load button to load genomic groupings from file before adding", "Gene Groupings not loaded",
							JOptionPane.ERROR_MESSAGE);
				
				//CSDs to compute on the fly
				} else {
				
					try {
					
					//initialize context set description
					ToAdd = new ContextSetDescription();
					ToAdd.setPreprocessed(true);
					
					//add appropriate type, w necessary information
					if (CSType.isSelected(CSRange.getModel())){ 				//CSType (2) - CSRange
						ToAdd.setType("Range");	ToAdd.setPreprocessed(false);
						ToAdd.setNtRangeBefore(Integer.parseInt(ntBefore.getText()));
						ToAdd.setNtRangeAfter(Integer.parseInt(ntAfter.getText()));
						
					} else if (CSType.isSelected(CSGenesAround.getModel())){    //CSType (3) - CSGenesAround
						ToAdd.setType("GenesAround");	ToAdd.setPreprocessed(false);
						ToAdd.setGenesBefore(Integer.parseInt(GenesBefore.getText()));
						ToAdd.setGenesAfter(Integer.parseInt(GenesAfter.getText()));
						
					} else if (CSType.isSelected(CSGenesBetween.getModel())) {  //CSType (4) - CSGenesBetween
						ToAdd.setType("GenesBetween");	ToAdd.setPreprocessed(false);
						
					} else if (CSType.isSelected(CSMultipleQuery.getModel())){  //CSType (5) - CSMultipleQuery
						ToAdd.setType("MultipleQuery");	ToAdd.setPreprocessed(false);
					}

					//add description to the OS
					ToAdd.setName(CSName.getText());
					fr.getOS().getCSDs().add(ToAdd);
				
					//insert item into the menu
					contextSetMenu.insertItemAt(CSName.getText(), 0);
					
					//pre-processed sets are reset
					ComputedGrouping = false;
					LoadedGrouping = false;
					LoadedFileName.setText("Context Set \"" + ToAdd.getName() + "\" Successfully Added!");
					
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Field values must be integers",
								"Integer",JOptionPane.ERROR_MESSAGE);
					}
				}
			} 
		}
		
		//REMOVE BUTTON
		if (evt.getSource().equals(btnRemoveCS)){
			if (fr.getOS().getCSDs().size() > 1){
				
				//update available context sets
				
				//OS level
				for (int i = 0; i < fr.getOS().getCSDs().size(); i++){
					if (fr.getOS().getCSDs().get(i).getName().equals(contextSetMenu.getSelectedItem())){
						
						//remove from all annotated genomes, if necessary
						if (fr.getOS().getCSDs().get(i).isPreprocessed()){
							for (AnnotatedGenome AG : fr.getOS().getSpecies().values()){
								for (ContextSet CS : AG.getGroupings()){
									if (CS.getName().equals(fr.getOS().getCSDs().get(i).getName())){
										AG.getGroupings().remove(CS);
									}
								}
							}
						}
						
						//remove the organism set-wide context set description
						fr.getOS().getCSDs().remove(i);
					}
				}
				
				//remove from JComboBox
				contextSetMenu.removeItem(contextSetMenu.getSelectedItem());
				
			} else {
				
				//remove the one and only context set
				 JOptionPane.showMessageDialog(null, 
						 "Unable to remove - please retain at least one context set at all times.",
						 "Retain One or More Context Sets",JOptionPane.ERROR_MESSAGE);
				
			}
		} else if (evt.getSource().equals(btnOK)){
			
			//set the main frame menu to the existing menu.
			//remove all items, then add all items back.
			this.fr.getPanBtn().getContextSetMenu().removeAllItems();
			for (int i = 0; i < fr.getOS().getCSDs().size(); i++){
				this.fr.getPanBtn().getContextSetMenu().addItem(fr.getOS().getCSDs().get(i).getName());
			}
			
			//close this window.
			this.dispose();

		}
		
		}

	//enable all components within a single button group
	public void EnableComponents(ButtonModel selectedGroup){

		//enable appropriate  components
		for (ButtonModel bm : RadioButtonComponents.keySet()){
			LinkedList<Component> LL = RadioButtonComponents.get(bm);
			if (bm.equals(selectedGroup)){
				for (Component c : LL){
					c.setEnabled(true);
				}
			} else {
				for (Component c : LL){
					c.setEnabled(false);
				}
			}
		}
		
		//change message
		if (CSName.getText().contentEquals("Between") || CSName.getText().contentEquals("MultipleQuery")){
			CSName.setText("");
		}
	}

	//enanble message box
	public void UpdateMessageBox(){

		//update the message to the user based on what they're doing
		if (CSType.isSelected(CSIntergenicDist.getModel())){
			if (ComputedGrouping == true){
				LoadedFileName.setText(ComputedString);
			} else {
				LoadedFileName.setText("No gene groupings currently computed.");
			}
		} else if (CSType.isSelected(CSLoaded.getModel())){
			if (LoadedGrouping == true){
				LoadedFileName.setText(LoadedString);
			} else {
				LoadedFileName.setText("No gene groupings are currently loaded.");
			}
		} else if (CSType.isSelected(CSRange.getModel())){
			LoadedFileName.setText("All genes within a defined range of a single gene query are grouped together.");
		} else if (CSType.isSelected(CSGenesBetween.getModel())){
			LoadedFileName.setText("All genes between two independent queries are grouped together.");
			if (CSName.getText().contentEquals("") || CSName.getText().contentEquals("MultipleQuery")){
				CSName.setText("Between");
			}
		} else if (CSType.isSelected(CSGenesAround.getModel())){
			LoadedFileName.setText("A number of genes both before and after a single gene query are grouped together");
		} else if (CSType.isSelected(CSMultipleQuery.getModel())) {
			LoadedFileName.setText("Multiple gene query matches within a single organism are grouped together.");
			if (CSName.getText().contentEquals("")  || CSName.getText().contentEquals("Between")){
				CSName.setText("MultipleQuery");
			}
		} else if (CSType.isSelected(CSCombination.getModel())){
			LoadedFileName.setText("Combine existing gene groupings to create a more complex gene grouping.");
		}

	}
	
	//check if name is acceptable
	public void CheckName(){
		
		//set name as acceptable.
		this.AcceptableName = true;
		
		//check for unique names
		LinkedList<String> CurrentContextSets = new LinkedList<String>();
		for (int i = 0 ; i< fr.getOS().getCSDs().size(); i++){
			CurrentContextSets.add(fr.getOS().getCSDs().get(i).getName());
		}
		
		//name is unacceptable if non-unique
		for (int i = 0; i <CurrentContextSets.size(); i++){
			if (CurrentContextSets.get(i).equals(CSName.getText())){
				AcceptableName = false;
			}
		}
		
		//name is also unacceptable if field is empty
		if (CSName.getText().equals("")){
			AcceptableName = false;
		}

		//show error message if appropriate
		if (AcceptableName == false){
			JOptionPane.showMessageDialog(null, "Please give the context set a unique name.",
					"Name Missing",JOptionPane.ERROR_MESSAGE);
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

	//retrieve a data file
	private String getMappingFile() {
		
		//use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(this, "English",
				FileDialog.LOAD);

		fd.setVisible(true);
		String MappingFile = fd.getDirectory() + fd.getFile();
		//String MappingFile =  fd.getFile();
		if (fd.getFile() == null) {
			MappingFile = null;
		} 
		return MappingFile; //file name
	}

	//update progress bar
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}

	}
}
