package moduls.frm.children;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescription;
import genomeObjects.MotifGroup;
import genomeObjects.MotifGroupDescription;
import genomeObjects.SequenceMotif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.children.manageContextSetsv2.btnLoadCS;

public class ManageMotifs extends JDialog implements ActionListener, PropertyChangeListener{

	//Fields
	//Management
	private FrmPrincipalDesk f;
	private String[] SequenceMotifsAsArray;
	private LinkedList<String> SequenceMotifsAsList;
	private ButtonGroup MSType;
	private LinkedHashMap<ButtonModel, LinkedList<Component>> RadioButtonComponents
		= new LinkedHashMap<ButtonModel, LinkedList<Component>>();
	private boolean AcceptableName = true;
	private boolean FimoLoaded = false;
	private boolean CustomLoaded = false;
	private MotifGroupDescription ToAdd;
	
	//File import
	private File ReferenceDirectory = null;
	private File[] FimoFiles;
	
	//GUI
	//general use
	private JPanel jp;
	private JProgressBar progressBar;

	//(1) MSFimo
	private LinkedList<Component> MSFimo_group;
	private JRadioButton MSFimo;
	private String strMSFimo = "Load sequence motif(s) from a set of FIMO output files";
	private JButton btnMSFimo;
	private String strbtnFimo = "Load";
	
	//(2) MSCustom
	private LinkedList<Component> MSCustom_group;
	private JRadioButton MSCustom;
	private String strMSCustom = "Load sequence motif(s) from a customized file (or files)";
	private JButton btnMSCustom;
	private String strbtnMSCustom = "Load";
	
	//Add Motifs
	private JLabel Add;
	private JButton btnAddMS;
	private String strAddMS = "Add";
	private JTextField MSName, MSNameLabel;
	private String strMSNameLabel = "Enter Name: ";
	private JTextField LoadedFileName;
	
	//Remove Motifs
	private JTextField MotifSequenceHeader;
	
	private JButton btnRemoveMotif;
	private JLabel Remove;
	private String strRemoveMotif = "Remove";
	private JComboBox<String> SequenceMotifsMenu;
	
	//okay button - close panel
	private JButton btnOK;
	private String strbtnOK = "OK";
	
	//Constructor
	public ManageMotifs(FrmPrincipalDesk f){
		super();
		this.f = f ;
		
		//Initialize Sequence Motifs array + list
		SequenceMotifsAsArray = this.f.getPanMotifOptions().getMenuLoadedMotifs();
		SequenceMotifsAsList = new LinkedList<String>();
		for (int i = 0; i <SequenceMotifsAsArray.length; i++){
			SequenceMotifsAsList.add(SequenceMotifsAsArray[i]);
		}
		
		this.setSize(600,400);
		
		this.setTitle("Manage Sequence Motifs");
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
	
	//SwingWorkers
	//load from fimo file
	class btnLoadFimo extends SwingWorker<Void,Void>{

		private int OrganismsMapped;

		//Fields
		@Override
		protected Void doInBackground() throws Exception {
			
			//re-initialize loading
			FimoLoaded = false;
			
			//initialize context set description
			ToAdd = new MotifGroupDescription();
			ToAdd.setName(MSName.getText());
			LinkedList<String> SpeciesNames = new LinkedList<String>();
			
			//Initialize progress bar type things
			MSName.setEditable(false);
			progressBar.setValue(0);
			progressBar.setVisible(true);
			int OrganismsCompleted = 0;
			this.OrganismsMapped = 0;
			int ComputeProgress = 0;
			int TotalFilesForProcess = FimoFiles.length;
			
			// retrieve all files
			for (File fi : FimoFiles) {
				
				//increment counter
				OrganismsCompleted++;
				
				//check if file or directory
				String FimoFile = null;
				String SpeciesName = null;
				
				if (fi.isDirectory()){
					FimoFile = fi.getAbsolutePath() + "/fimo.txt";
					SpeciesName = fi.getName();
				} else {
					FimoFile = fi.getAbsolutePath();
					SpeciesName = fi.getName().replaceFirst("[.][^.]+$", "");
				}
				
				//map this to an orgnanism
				boolean FoundOrganism = false;
				for (String s : f.getOS().getSpeciesNames()){
					if (s.equals(SpeciesName)){
						FoundOrganism = true;
						break;
					}
				}
				
				//if there is an organism to map to, proceed.
				if (FoundOrganism){
					
					//try to read in information from this fimo file + map to organism
					try {
						BufferedReader br = new BufferedReader(new FileReader(FimoFile));
						String Line = null;
						
						//define a new motif group
						MotifGroup MG = new MotifGroup();
						MG.setName(MSName.getText());
						MG.setFileName(fi.getAbsolutePath());
						
						//add all sequences
						while ((Line = br.readLine()) != null){
							
							//ignore commented lines
							if (!Line.startsWith("#")){
								
								//determine imported line
								String ImportedLine[] = Line.split("\t");
								
								//build sequence motif
								SequenceMotif SM = new SequenceMotif();
								SM.setSource("FIMO");
								SM.setMotifName(MSName.getText());
								SM.setSequence(ImportedLine[1]);
								SM.setStart(Integer.parseInt(ImportedLine[2]));
								SM.setStop(Integer.parseInt(ImportedLine[3]));
								SM.setScore(Double.parseDouble(ImportedLine[4]));
								SM.setPvalue(Double.parseDouble(ImportedLine[5]));
								SM.setQvalue(Double.parseDouble(ImportedLine[6]));
								SM.setSequence(ImportedLine[7]);

								//add to list
								MG.getMotifInstances().add(SM);
							}

						}
						
						//add this motif grouping to the species list.
						f.getOS().getSpecies().get(SpeciesName).getMotifs().add(MG);
						
						//increment counter
						OrganismsMapped++;
						SpeciesNames.add(SpeciesName);
						
					} catch (Exception ex) {
						System.out.println("Unable to map file: " + FimoFile + " to an organism in the genomic working set.");
					}
				}

				//update progress bar
				ComputeProgress = (int) Math.round(100*((double)OrganismsCompleted/(double)TotalFilesForProcess));
				this.setProgress(ComputeProgress);
			}
			
			//Update final info
			ToAdd.setSpecies(SpeciesNames);
			ToAdd.setSource("FIMO");
			
			return null;
		}
		
		//conclude all processes
		protected void done(){
			//add this list to the set, for update.
			//SequenceMotifsAsList.add(MSName.getText());
			FimoLoaded = true;
			progressBar.setVisible(false);
			LoadedFileName.setText("Sequence motif \"" + MSName.getText() 
					+ "\" successfully mapped to " + this.OrganismsMapped + " organisms.");
			btnAddMS.setEnabled(true);
			MSName.setEditable(true);
		}
		
	}
	
	private void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		
		/*
		 * ADD MOTIFS
		 */
		
		//Add Motifs Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		Add = new JLabel(" ADD A SEQUENCE MOTIF");
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
		MSNameLabel = new JTextField(strMSNameLabel);
		MSNameLabel.setEditable(false);
		jp.add(MSNameLabel,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.insets = new Insets(3,2,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		MSName = new JTextField("");
		MSName.setEditable(true);
		MSName.addActionListener(this);
		//CSName.setColumns(200);
		jp.add(MSName, c);
		gridy++;
		
		//create radio buttons
		//search type button group definition
		MSFimo = new JRadioButton(strMSFimo);
		MSCustom = new JRadioButton(strMSCustom);

		//define button group
		MSType = new ButtonGroup(); 
			MSType.add(MSFimo);
			MSType.add(MSCustom);
			
		//(1) MSFIMO
		
		//grouping
		MSFimo_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(MSFimo, c);
		MSFimo.addActionListener(this);
		gridy++;
		
		// load motif sequences from fimo files
		c.ipadx = 0;
		c.insets = new Insets(1,20,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		btnMSFimo = new JButton(strbtnFimo);
		btnMSFimo.addActionListener(this);
		jp.add(btnMSFimo, c);
		MSFimo_group.add(btnMSFimo);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(MSFimo.getModel(), MSFimo_group);

		//(2) MSCustom
		
		//grouping
		MSCustom_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(10,1,1,1);
		jp.add(MSCustom, c);
		MSCustom.addActionListener(this);
		gridy++;
		
		// load motif sequences from custom files
		c.ipadx = 0;
		c.insets = new Insets(1,20,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		btnMSCustom = new JButton(strbtnMSCustom);
		btnMSCustom.addActionListener(this);
		jp.add(btnMSCustom, c);
		MSCustom_group.add(btnMSCustom);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(MSCustom.getModel(), MSCustom_group);
		
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
		
		// Loaded File Name
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
		btnAddMS = new JButton(strAddMS);
		btnAddMS.addActionListener(this);
		btnAddMS.setEnabled(true);
		jp.add(btnAddMS, c);
		gridy++;
		
		/*
		 * REMOVE MOTIFS
		 */
		
		//Remove Motifs Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.insets = new Insets(3,3,3,3);
		Remove = new JLabel(" REMOVE A SEQUENCE MOTIF");
		Remove.setBackground(Color.GRAY);
		Remove.setOpaque(true);
		jp.add(Remove,c);
		gridy++;
		
		// Sequence Motif Text label
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		MotifSequenceHeader = new JTextField();
		MotifSequenceHeader.setText("Sequence Motif:"); // context set currently loaded
		MotifSequenceHeader.addActionListener(this);
		MotifSequenceHeader.setEditable(false);
		jp.add(MotifSequenceHeader, c);
		
		// drop-down menu for Context Sets
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		SequenceMotifsMenu = new JComboBox<String>(SequenceMotifsAsArray);
		SequenceMotifsMenu.addActionListener(this);
		SequenceMotifsMenu.setEnabled(true);
		jp.add(SequenceMotifsMenu, c);
		
		//remove button
		c.gridx = 3;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnRemoveMotif = new JButton(strRemoveMotif);
		btnRemoveMotif.addActionListener(this);
		btnRemoveMotif.setEnabled(true);
		jp.add(btnRemoveMotif, c);
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
		
		//Finally, add to panel.
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
	}
	
	//retrieve either directory or data file
	private void getFimoFiles(){
		
		//initialize output
		JFileChooser GetFimoFiles = new JFileChooser();
		try {
			//GetGenomes.setLUIManager.getLookAndFeel()
		} catch (Exception ex){
			
		}
		GetFimoFiles.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		GetFimoFiles.setDialogTitle("Select directory containing FIMO output files (or directories)");

		if (this.ReferenceDirectory != null){
			GetFimoFiles.setCurrentDirectory(ReferenceDirectory);
		} else {
			GetFimoFiles.setCurrentDirectory(new File("."));
		}
		GetFimoFiles.showOpenDialog(GetFimoFiles);
		
		//retrieve directory containing fimo files
		File ParentDirectory = GetFimoFiles.getSelectedFile();
		this.ReferenceDirectory = GetFimoFiles.getCurrentDirectory();
		
		//check if file could be received
		if (ParentDirectory != null){
		
			//retrieve directory
			this.FimoFiles = ParentDirectory.listFiles();

		} else {
			
			//no files are currently loaded.
			this.FimoFiles = null;
		}

	}
	
	
	public String[] convertSequenceMotifs(LinkedList<ContextSetDescription> ListOfContextSets){
		
		//initialize output array
		String[] ArrayOfContextSets = new String[ListOfContextSets.size()];
		
		//iterate through array
		for (int i = 0; i < ListOfContextSets.size(); i++){
			ArrayOfContextSets[i] = ListOfContextSets.get(i).getName();
		}
		
		return ArrayOfContextSets;
	}
	@Override
	public void actionPerformed(ActionEvent evt) {

		//check boxes: enable + disable appropriate components
		EnableComponents(MSType.getSelection());
		
		//update appropriate message box
		UpdateMessageBox();
		
		//CSType (1) - Intergenic Distance
		if (evt.getSource().equals(btnMSFimo)){
			
			//check if name is acceptable
			CheckName();
			
			if (AcceptableName == true){
				
				//retrieve directory
				getFimoFiles();
				
				//create a new swing worker
				btnLoadFimo LF = new btnLoadFimo();
				LF.addPropertyChangeListener(this);
				LF.execute();
			} 

			
		} 
		
		if (evt.getSource().equals(btnAddMS)){
			
			CheckName();

			if (AcceptableName == true){
				
				//check cases that are not working
				if (!FimoLoaded && MSType.isSelected(MSFimo.getModel())) {
					JOptionPane.showMessageDialog(null, 
							"Select the load button to load fimo-output motif files", "Fimo files not loaded",
							JOptionPane.ERROR_MESSAGE);
				} else if (MSType.isSelected(MSCustom.getModel()) && !CustomLoaded){
					JOptionPane.showMessageDialog(null, 
							"Select the load button to load custom-determined motif files", "Custom files not loaded",
							JOptionPane.ERROR_MESSAGE);
				} else {
					
					//add description to the OS
					f.getOS().getMGDescriptions().add(ToAdd);
					SequenceMotifsAsList.add(ToAdd.getName());
					
					//rebuild appropriately
					SequenceMotifsMenu.removeAllItems();
					
					for (String s : SequenceMotifsAsList){
						if (!s.equals("<none>")){
							SequenceMotifsMenu.addItem(s);
						}
					}
					
					//pre-processed sets are reset
					FimoLoaded = false;
					CustomLoaded = false;
					LoadedFileName.setText("Motif sequence \"" + ToAdd.getName() + "\" successfully added to the genomic working set!");
					
				}
				
				
			}

		}
		
		//REMOVE BUTTON
		if (evt.getSource().equals(btnRemoveMotif)){
			
			//update motifs
			//OS level
			for (int i = 0; i < f.getOS().getMGDescriptions().size(); i++){
				if (f.getOS().getMGDescriptions().get(i).getName().equals(SequenceMotifsMenu.getSelectedItem())){
					
					//remove from all annotated genomes if necessary
					for (AnnotatedGenome AG : f.getOS().getSpecies().values()){
						for (MotifGroup MG : AG.getMotifs()){
							if (MG.getName().equals(f.getOS().getCSDs().get(i).getName())){
								AG.getGroupings().remove(MG);
							}
						}
					}

					//remove the organism set-wide context set description
					f.getOS().getMGDescriptions().remove(i);
				}
			}
			
			//remove from list
			SequenceMotifsAsList.remove(SequenceMotifsMenu.getSelectedItem());
			
			//remove from JComboBoxes
			//add/remove menu
			SequenceMotifsMenu.removeItem(SequenceMotifsMenu.getSelectedItem());
			
			//add a new tag, if there are none
			if (SequenceMotifsMenu.getItemCount() == 0){
				SequenceMotifsMenu.addItem("<none>");
			}
		}


		
		
		//close panel, after updating list.
		if (evt.getSource().equals(btnOK)){
			
			//set the main frame menu to the existing menu.
			//remove all items, then add all items back.
			this.f.getPanMotifOptions().getMenuOfMotifs().removeAllItems();
			for (int i = 0; i < f.getOS().getMGDescriptions().size(); i++){
				this.f.getPanMotifOptions().getMenuOfMotifs().addItem(f.getOS().getMGDescriptions().get(i).getName());
			}

			//close this window.
			this.dispose();

		}
	}

	//update message box
	public void UpdateMessageBox(){
		
	}
	
	//check if name is acceptable
	public void CheckName(){
		
		//set name as acceptable.
		this.AcceptableName = true;
		
		//name is unacceptable if non-unique
		for (String s : SequenceMotifsAsArray){
			if (s.equals(MSName.getText())){
				AcceptableName = false;
			}
		}
		
		//name is also unacceptable if field is empty
		if (MSName.getText().equals("")){
			AcceptableName = false;
		}

		//show error message if appropriate
		if (AcceptableName == false){
			JOptionPane.showMessageDialog(null, "Please give the context set a unique name.",
					"Name Missing or non-unique",JOptionPane.ERROR_MESSAGE);
		}
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
