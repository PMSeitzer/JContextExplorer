package moduls.frm.children;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
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
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.Panels.Jpan_btn_NEW;

public class manageContextSets extends JDialog implements ActionListener, PropertyChangeListener{

	//fields
	private static final long serialVersionUID = 1L;
	
	//Biological + parent information
	private FrmPrincipalDesk fr;
	//private LinkedList<String> ContextList;
	private Jpan_btn_NEW jb;
	
	//Loaded operon file + realted booleans
	private String strNoOperons = "no operons computed or loaded.";
	private String OperonStringToDisplay = strNoOperons;
	private boolean OperonsbyComputation;
	private boolean ReadyToAdd = false;
	private boolean AcceptableName = true;
	
	//components
	private JPanel jp;
	private JPanel jp2;
	private JLabel Add, Remove;
	private JTextField operonTolerance, contextSetName, operonName;
	private JButton computeOperons, btnAddCS, btnRemoveCS, btnOK;
	private String operonComp = "Compute";
	private String stroperonName = "Enter Name: ";
	private String strAddCS = "Add";
	private String strRemoveCS = "Remove";
	private String strbtnOK = "OK";
	private JRadioButton OperonsbyDistance;
	private JRadioButton OperonsbyFile;
	private ButtonGroup operonType;
	private String distanceOption = "compute based on intragenic distance";
	private String loadOption = "load from file";
	private JButton loadOperons;
	private String strLoad = "Load";
	private JCheckBox cbStrandOption;
	private String strcbStrandOption = "Genes must be on same strand";
	private JProgressBar progressBarOps;
	private JTextField OperonFileName;
	private JComboBox<String> contextSetMenu;
	private JTextField contextSetHeader;


	//constructor
	public manageContextSets(FrmPrincipalDesk f, LinkedList<String> currentList, Jpan_btn_NEW jbn){
		super();
		
		//imported information
		this.fr = f;
		this.jb = jbn;
		//this.ContextList = currentList;
		
		//frame settings
		//this.setSize(new Dimension(400, 350));
		this.setSize(520,420);
		
		this.setTitle("Add or Remove Context Sets");
		this.setLocationRelativeTo(null);
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		//add panel components
		//attempt - add nested panel structure for inherent organization
		this.getPanel();
		this.setContentPane(jp);
		//this.pack();
		
		//modality settings
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	//SwingWorker
	class LoadOperons extends SwingWorker<Void, Void>{

//		boolean OperonsbyComputation;
//		String LocalSwingFileName;
//		
		//constructor
		public LoadOperons(String fileName){
			if (fileName == null){
				OperonsbyComputation = true;
			} else {
				OperonsbyComputation = false;
				OperonStringToDisplay = fileName;
			}
		}
		
		//do in background method
		@Override
		protected Void doInBackground() throws Exception {

			if (OperonsbyComputation == true){
				
			int TotalOrganisms = fr.getOS().getSpeciesNames().size();	
			progressBarOps.setVisible(true);
			OperonFileName.setVisible(false);
			progressBarOps.setValue(0);
			int OrganismsCompleted = 0;
			int ComputeProgress = 0;
			progressBarOps.setStringPainted(true);

			for (Entry<String, AnnotatedGenome> entry: fr.getOS().getSpecies().entrySet()){
				
				//"true" is for initialization
				
				entry.getValue().ComputeContextSet(contextSetName.getText(), Integer.parseInt(operonTolerance.getText()), cbStrandOption.isSelected());
				OrganismsCompleted++;
				ComputeProgress = (int) Math.round(100*((double)OrganismsCompleted/(double)TotalOrganisms));
				this.setProgress(ComputeProgress);
				
				//optional print statement
				//System.out.println(ComputeProgress);
				//System.out.println(Counter + "/" + OS.getSpecies().entrySet().size() + " Completed.");
			}
			
			//update progress bar
			progressBarOps.setValue(100);

			//update operon file name, and make visible
			if (cbStrandOption.isSelected()){
				//GenomeOperonsFile = "Computed with an intergenic distance of " + operonTolerance.getText() + ", same strand only";
				OperonStringToDisplay = "Genes on the same strand within " + operonTolerance.getText() + " bp of each other are in the same set.";
			} else {
				//GenomeOperonsFile = "Computed with an intergenic distance of " + operonTolerance.getText() + ", either strand";
				OperonStringToDisplay = "Genes on either strand within " + operonTolerance.getText() + " bp of each other are in the same set.";
			}
			
			OperonFileName.setText(OperonStringToDisplay);
			progressBarOps.setVisible(false);
			OperonFileName.setVisible(true);
			
			ReadyToAdd = true;
			
			} else {
				
//				JOptionPane noLoadFile = new JOptionPane();
//				noLoadFile.showMessageDialog(null, "Functionality is not available at this time.");
//				noLoadFile.setValue(true);
				
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
						AG.ImportContextSet(contextSetName.getText(), ImportedLine[1]);

						//report to SwingWorker
						LineCounter++;
						
						operonLoadProgress= (int) Math.round(100*((double)LineCounter/(double)TotalLines));
						setProgress(operonLoadProgress);
						
					}
					
					//set the first context set
					LinkedList<String> CSD = new LinkedList<String>();
					CSD.add(contextSetName.getText());
					//OS.setCSDs(CSD);
					
					progressBarOps.setVisible(false);
					OperonFileName.setVisible(true);
					OperonFileName.setText(OperonStringToDisplay);
					//System.out.println("operons loaded successfully.");
					
					ReadyToAdd = true;
					
				} catch(Exception ex) {
					
					progressBarOps.setStringPainted(false);
					
					JOptionPane noLoadFile = new JOptionPane();
					noLoadFile.showMessageDialog(null, "The file could not be loaded or was improperly formatted.");
					noLoadFile.setValue(true);

					OperonFileName.setText(strNoOperons);
					//System.out.println("operons not loaded successfully.");
				}
			}
			
			return null;
		}
		
		//after all completed
		public void done(){
			//update operon file name, and make visible
			if (cbStrandOption.isSelected()){
				OperonStringToDisplay = "Computed with an intergenic distance of " + operonTolerance.getText() + ", same Strand only";
			} else {
				OperonStringToDisplay = "Computed with an intergenic distance of " + operonTolerance.getText() + ", either strand";
			}
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
		c.insets = new Insets(5,5,5,5);
		
		//Add CS Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		Add = new JLabel(" ADD A CONTEXT SET");
		Add.setBackground(Color.GRAY);
		Add.setOpaque(true);
		jp.add(Add,c);
		gridy++;
		
		//Name: field
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		operonName = new JTextField(stroperonName);
		operonName.setEditable(false);
		jp.add(operonName,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		contextSetName = new JTextField("");
		contextSetName.setEditable(true);
		contextSetName.addActionListener(this);
		//contextSetName.setColumns(200);
		jp.add(contextSetName, c);
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
		c.insets = new Insets(5,5,5,5);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		jp.add(OperonsbyDistance, c);
		c.gridx = 3;
		c.gridwidth = 2;
		c.gridy = gridy;
		jp.add(OperonsbyFile, c);
		gridy++;
		
		//intergenic distance text field
		c.insets = new Insets(5,5,5,5);
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
		jp.add(operonTolerance, c);
			
		//Compute operons button
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(5,5,5,5);
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.NONE;
		computeOperons = new JButton(operonComp);
		computeOperons.addActionListener(this);
		jp.add(computeOperons, c);

		// load operons file
		c.ipadx = 0;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 4;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		loadOperons = new JButton(strLoad);
		loadOperons.addActionListener(this);
		jp.add(loadOperons, c);
		gridy++;
		
		//check box
		c.insets = new Insets(5,5,5,5);
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 0;
		c.gridwidth = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbStrandOption = new JCheckBox(strcbStrandOption);
		cbStrandOption.setSelected(true);
		jp.add(cbStrandOption, c);
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
		jp.add(progressBarOps, c);
		
		// Operon File Name
		c.insets = new Insets(5,5,5,5);
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		OperonFileName = new JTextField();
		OperonFileName.setText("no file currently loaded"); // No file loaded
		OperonFileName.addActionListener(this);
		OperonFileName.setEditable(false);
		jp.add(OperonFileName, c);
		gridy++;
		
		//remove button
		c.gridx = 3;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnAddCS = new JButton(strAddCS);
		btnAddCS.addActionListener(this);
		btnAddCS.setEnabled(true);
		jp.add(btnAddCS, c);
		gridy++;
		
		//Remove CS Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
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
		c.insets = new Insets(5,5,5,5);
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
		String[] CurrentContextLists = convertContextSets(fr.getOS().getCSDs());
		contextSetMenu = new JComboBox<String>(CurrentContextLists);
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
		
		//add 
		jp2 = new JPanel();
		jp2.add(jp, BorderLayout.NORTH);
		
		gridy++;
		gridy++;
		
		//submit button
		//remove button
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp.add(btnOK, c);
		
		//this.getContentPane().add(jp, BorderLayout.NORTH);
	}
	
	//ACTIONS
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(loadOperons) && operonType.getSelection().equals(OperonsbyFile.getModel())){
			
			//set name as acceptable.
			this.AcceptableName = true;
			
			//check for unique names
			LinkedList<String> CurrentContextSets = fr.getOS().getCSDs();
			
			//name is unacceptable if non-unique
			for (int i = 0; i <CurrentContextSets.size(); i++){
				if (CurrentContextSets.get(i).equals(contextSetName.getText())){
					AcceptableName = false;
				}
			}
			
			//name is also unacceptable if field is empty
			if (contextSetName.getText().equals("")){
				AcceptableName = false;
			}
			
			//only try to read in file if the name is acceptable.
			if (AcceptableName == true){
			
				String fileName = getMappingFile();
			
				if (fileName != null){
				
					if (!fileName.equals(OperonStringToDisplay)){
				
						OperonFileName.setText(OperonStringToDisplay);
						OperonStringToDisplay = fileName;
						LoadOperons lo = new LoadOperons(fileName);
						lo.addPropertyChangeListener(this);
						lo.execute();
				
						}
					}
			
				}
			
			
		} else if (evt.getSource().equals(computeOperons) && operonType.getSelection().equals(OperonsbyDistance.getModel())){
		
			//set accetable name
			this.AcceptableName = true;
			
			//System.out.println("manageContextSets: " + fr.getOS().getCSDs());
			
			//check for unique names
			LinkedList<String> CurrentContextSets = fr.getOS().getCSDs();
			
			//name is unacceptable if non-unique
			for (int i = 0; i <CurrentContextSets.size(); i++){
				if (CurrentContextSets.get(i).equals(contextSetName.getText())){
					AcceptableName = false;
				}
			}
			
			//name is also unacceptable if field is empty
			if (contextSetName.getText().equals("")){
				AcceptableName = false;
			}
			
			if (AcceptableName == true){
				//create a new swing worker, if name acceptable
				LoadOperons lo = new LoadOperons(null);
				lo.addPropertyChangeListener(this);
				lo.execute();
			} else {
				JOptionPane UnAcceptableName = new JOptionPane();
				UnAcceptableName.showMessageDialog(null, "Please give the context set a non-unique name.");
				UnAcceptableName.setValue(true);
			}

			
		} else if (evt.getSource().equals(btnAddCS)){
			
			if (ReadyToAdd == false){
				
				//try to add a context set too hastily
				JOptionPane noSetToAdd = new JOptionPane();
				noSetToAdd.showMessageDialog(null, "You must compute or load a new context set before adding it to the list.");
				noSetToAdd.setValue(true);
				
			} else {
				
				//update available context sets
				//OS level
				
				//add descriptions to the OS
				LinkedList<String> CSD = fr.getOS().getCSDs();
				CSD.add(contextSetName.getText());
				fr.getOS().setCSDs(CSD);
				
				//internal linked list
				//ContextList.add(contextSetName.getText());

				//add to external, available choices
				contextSetMenu.insertItemAt(contextSetName.getText(), 0);
			}
			
		} else if (evt.getSource().equals(btnRemoveCS)){
			if (fr.getOS().getCSDs().size() > 1){
				
				//update available context sets
				//OS level
				for (int i = 0; i < fr.getOS().getCSDs().size(); i++){
					if (fr.getOS().getCSDs().get(i).equals(contextSetMenu.getSelectedItem())){
						fr.getOS().getCSDs().remove(i);
					}
				}
				
				//internal linked list
				//ContextList.remove(contextSetMenu.getSelectedItem());

				//remove from JComboBox
				contextSetMenu.removeItem(contextSetMenu.getSelectedItem());
				
			} else {
				
				//remove the one and only context set
				JOptionPane noSetToAdd = new JOptionPane();
				noSetToAdd.showMessageDialog(null, "Unable to remove - please retain at least one context set at all times.");
				noSetToAdd.setValue(true);
				
			}
		} else if (evt.getSource().equals(btnOK)){
			
			//set the main frame menu to the existing menu.
			//remove all items, then add all items back.
			this.fr.getPanBtn().getContextSetMenu().removeAllItems();
			for (int i = 0; i < fr.getOS().getCSDs().size(); i++){
				this.fr.getPanBtn().getContextSetMenu().addItem(fr.getOS().getCSDs().get(i));
			}
			
			//close this window.
			this.dispose();

		}
		
		}

	
	public String[] convertContextSets(LinkedList<String> ListOfContextSets){
		
		//initialize output array
		String[] ArrayOfContextSets = new String[ListOfContextSets.size()];
		
		//iterate through rray
		for (int i = 0; i < ListOfContextSets.size(); i++){
			ArrayOfContextSets[i] = ListOfContextSets.get(i);
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

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBarOps.setValue(progress);
		}

	}
}
