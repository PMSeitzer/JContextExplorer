package moduls.frm.Panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import newickTreeParsing.Tree;
import newickTreeParsing.TreeParser;

import moduls.frm.FrmPrincipalDesk;

public class Jpan_PhyTreeMenu extends JPanel implements ActionListener {

	//fields
	private FrmPrincipalDesk f;
	
	//GUI components
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private JLabel LoadPhyTreeBanner;
	private String strLoadPhyTreeBanner = " LOAD A PHYLOGENETIC TREE";
	private JButton btnPhyTree;
	private String strPhyTree = "Load";
	private JTextField CurrentTree;
	private String strCurrentTree = " Current Tree: ";
	private JLabel CurrentlyEnabledBanner;
	private String strCurrentlyEnabledBanner = " AVAILABLE PHYLOGENETIC TREES";
	private JComboBox<String> menuLoadedPhyTrees;
	private JButton btnRemoveSelectedPhyTree;
	private String strRemoveSelectedPhyTree = "Remove Selected";
	
	private JLabel DisplayOptionsBanner;
	private String strDisplayOptions = " PHYLOGENETIC TREE DISPLAY OPTIONS";
	private ButtonGroup PhyloDisplayOptions;
	private JRadioButton radCladogram, radPhylogram;
	private JCheckBox chkDashed, chkWeights;
	private String strCladogram = "Cladogram";
	private String strPhylogram = "Phylogram";
	private String strchkDashed = "Draw dashed line to label";
	private String strchkWeights = "Display support values";
			
	//Data components
	private File FilePath = null;
	private LinkedList<File> LoadedPhyTrees = new LinkedList<File>();
	private LinkedList<Tree> ParsedPhyTrees = new LinkedList<Tree>();
	private Tree CurrentParsedTree;
	
	//constructor
	public Jpan_PhyTreeMenu(FrmPrincipalDesk fr){
		this.f = fr;
		this.FilePath = fr.getFileChooserSource();
		
		//build components
		this.getPanel();
	}
	
	//build components
	public void getPanel(){
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Phylogenetic Tree Settings")); // title
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		//c.weighty = 1;
		c.insets = new Insets(1,1,1,1);

		//Load Phylogenetic Tree banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		LoadPhyTreeBanner = new JLabel(strLoadPhyTreeBanner);
		LoadPhyTreeBanner.setBackground(Color.GRAY);
		LoadPhyTreeBanner.setOpaque(true);
		LoadPhyTreeBanner.setFont(fontStandard);
		add(LoadPhyTreeBanner,c);
		gridy++;
		
		//Load Phylogenetic Tree button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 4;
		btnPhyTree = new JButton(strPhyTree);
		btnPhyTree.setFont(fontStandard);
		btnPhyTree.addActionListener(this);
		add(btnPhyTree,c);
		gridy++;

		//Currently Loaded banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		CurrentlyEnabledBanner = new JLabel(strCurrentlyEnabledBanner);
		CurrentlyEnabledBanner.setBackground(Color.GRAY);
		CurrentlyEnabledBanner.setOpaque(true);
		CurrentlyEnabledBanner.setFont(fontStandard);
		add(CurrentlyEnabledBanner,c);
		gridy++;

		//Currently Loaded tree tag
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		CurrentTree = new JTextField(strCurrentTree);
		CurrentTree.setFont(fontStandard);
		CurrentTree.setEditable(false);
		add(CurrentTree,c);
		gridy++;
		
		// currently loaded motifs drop-down menu
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		menuLoadedPhyTrees = new JComboBox<String>(getLoadedPhyTrees());
		menuLoadedPhyTrees.addActionListener(this);
		menuLoadedPhyTrees.setEnabled(true);
		menuLoadedPhyTrees.setFont(fontStandard);
		add(menuLoadedPhyTrees, c);
		gridy++;
		
		//Remove phylogenetic tree button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 4;
		btnRemoveSelectedPhyTree = new JButton(strRemoveSelectedPhyTree);
		btnRemoveSelectedPhyTree.setFont(fontStandard);
		btnRemoveSelectedPhyTree.addActionListener(this);
		add(btnRemoveSelectedPhyTree,c);
		gridy++;
		
		//Display Options banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		DisplayOptionsBanner = new JLabel(strDisplayOptions);
		DisplayOptionsBanner.setBackground(Color.GRAY);
		DisplayOptionsBanner.setOpaque(true);
		DisplayOptionsBanner.setFont(fontStandard);
		add(DisplayOptionsBanner,c);
		gridy++;
		

		//define button group
		PhyloDisplayOptions = new ButtonGroup();
		radCladogram = new JRadioButton(strCladogram);
		radPhylogram = new JRadioButton(strPhylogram);
		PhyloDisplayOptions.add(radCladogram);
		PhyloDisplayOptions.add(radPhylogram);
		
		//Display option 1: cladogram
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		radCladogram.setSelected(false);
		radCladogram.addActionListener(this);
		add(radCladogram, c);
		
		//Display option 2: phylogram
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		radPhylogram.setSelected(true);
		radPhylogram.addActionListener(this);
		add(radPhylogram, c);
		gridy++;
		
		//option: draw dashed line to label
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		chkDashed = new JCheckBox(strchkDashed);
		chkDashed.addActionListener(this);
		chkDashed.setSelected(true);
		chkDashed.setEnabled(false);
		this.add(chkDashed, c);
		gridy++;
		
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		chkWeights = new JCheckBox(strchkWeights);
		chkWeights.setSelected(true);
		chkWeights.addActionListener(this);
		this.add(chkWeights, c);
		gridy++;
		
	}
	
	//convert motifs from linked list to integer array
	public String[] getLoadedPhyTrees(){
		String PhyTrees[];
		if (LoadedPhyTrees.size() > 0){
			PhyTrees = new String[LoadedPhyTrees.size()];
			for (int i = 0; i < LoadedPhyTrees.size(); i++){
				PhyTrees[i] = LoadedPhyTrees.get(i).getName();
			}
		} else {
			PhyTrees =  new String[1];
			PhyTrees[0] = "<none>";
		}
		
		return PhyTrees;
	}
	
	//parse a tree
	public Tree ParseNewickTree(File NewickTreeFile){

	  //Initialize output
	  Tree t;
		
	  //attempt to import file
      try {
          BufferedReader r = new BufferedReader(new FileReader(NewickTreeFile));
          TreeParser tp = new TreeParser(r);
          t = tp.tokenize(NewickTreeFile.length(), NewickTreeFile.getName(), null);
      } catch (FileNotFoundException e) {
          JOptionPane.showMessageDialog(null, "The selected file is not appropriately formatted.");
          t = null;
      }
      
      //return tree file
      return t;
	}
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//load button
		if (evt.getSource().equals(btnPhyTree)){

			//create filechooser
			JFileChooser getPhyTree = new JFileChooser();
			getPhyTree.setFileSelectionMode(JFileChooser.FILES_ONLY);
			getPhyTree.setDialogTitle("Select Phylogenetic tree file (Newick format required)");
			
			//open at last open point.
			if (FilePath != null){
				getPhyTree.setCurrentDirectory(FilePath);
			}
			
			getPhyTree.showOpenDialog(getPhyTree);
			
			//retrieve file
			File TreeFile = getPhyTree.getSelectedFile();
			
			//if the file is not null (cancel operation), try to parse
			if (TreeFile != null){
				Tree t = ParseNewickTree(TreeFile);
				setCurrentParsedTree(t);

				//add + update, if the tree is new.
				if (!LoadedPhyTrees.contains(TreeFile) && t != null){
					
					//update catalog
					CurrentParsedTree = t;				//Tree
					ParsedPhyTrees.add(t);				//LinkedList<Tree>
					LoadedPhyTrees.add(0,TreeFile);		//LinkedList<File>
					FilePath = TreeFile;				//Path
							
					//update GUI
					menuLoadedPhyTrees.removeAllItems();
					String[] PhyTrees = getLoadedPhyTrees();
					for (String s : PhyTrees){
						menuLoadedPhyTrees.addItem(s);
					}
				}
			}
		}
		
		//switch menu to active tree
		if (evt.getSource().equals(menuLoadedPhyTrees)){
			//retrieve name of current tree
			String CurrentName = (String) menuLoadedPhyTrees.getSelectedItem();
			
			//find this tree in the list, and set it to the active one.
			for (Tree t : ParsedPhyTrees){
				if (t.getName().equals(CurrentName)){
					setCurrentParsedTree(t);
					break;
				}
			}
		}
		
		//remove an available phylogenetic tree
		if (evt.getSource().equals(btnRemoveSelectedPhyTree)){
			
			//remove from the list of trees
			for (Tree t : ParsedPhyTrees){
				if (t.getName().equals((String) menuLoadedPhyTrees.getSelectedItem())){
					ParsedPhyTrees.remove(t);
					break;
				}
			}
			
			//remove from the list of files
			for (File f: LoadedPhyTrees){
				if (f.getName().equals((String) menuLoadedPhyTrees.getSelectedItem())){
					LoadedPhyTrees.remove(f);
					break;
				}
			}
			
			//remake the menu
			menuLoadedPhyTrees.removeAllItems();
			String[] PhyTrees = getLoadedPhyTrees();
			for (String s : PhyTrees){
				menuLoadedPhyTrees.addItem(s);
			}
		}
		
		//display options
		if (evt.getSource().equals(radPhylogram)){
			this.chkDashed.setEnabled(true);
			
		}
		
		if (evt.getSource().equals(radCladogram)){
			this.chkDashed.setEnabled(false);
		}
		
		//redraw phylogenetic tree
		if (evt.getSource().equals(radPhylogram) || 
				evt.getSource().equals(radCladogram) ||
				evt.getSource().equals(chkDashed) ||
				evt.getSource().equals(chkWeights)){
			
			//update phylogenetic tree, if appropriate.
			if (f.getCurrentFrame() != null){	//frame exists
				
				//phylogenetic tree exists
				if (f.getCurrentFrame().getInternalFrameData().getQD().getAnalysesList().isOptionRenderPhylogeny()){
					
					//repaint!
					f.getCurrentFrame().getInternalFrameData().getPhyloTreePanel().repaint();
					
				}
			}
		}

	}

	//retrieve the current parsed phylogenetic tree
	public Tree getCurrentParsedTree() {
		return CurrentParsedTree;
	}

	public void setCurrentParsedTree(Tree currentParsedTree) {
		CurrentParsedTree = currentParsedTree;
	}

	public JRadioButton getRadCladogram() {
		return radCladogram;
	}

	public JRadioButton getRadPhylogram() {
		return radPhylogram;
	}

	public JCheckBox getChkDashed() {
		return chkDashed;
	}

	public JCheckBox getChkWeights() {
		return chkWeights;
	}

}
