package ContextForest;

import genomeObjects.CSDisplayData;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.PostSearchAnalyses;
import moduls.frm.QueryData;

public class ManageQuerySets extends JDialog implements ActionListener{

	//FIELDS
	
	//master
	private FrmPrincipalDesk f;
	
	//GUI
	private JPanel jp;
	
	//Add
	private JLabel LblAdd, LblRemove;
	private String strLblAdd = " ADD A QUERY SET";
	private JTextField QSName, QSNameLabel;
	private String strQSNameLabel = "Enter Name: ";
	private JButton btnLoadFromFile, btnAdd, btnRemoveQS, btnOK;
	private String strLoad = "Load from file";
	private String strbtnAdd = "Add Query Set";
	private JTextArea txtQueries;
	private String strTextAreaTxt = "";
	private String strLblRemove = " REMOVE A QUERY SET";
	private JTextField QSToRemove;
	private String strQSToRemove = "Query Set:";
	private JComboBox<String> QSMenu;
	private String[] QuerySetMenuItems;
	private String strbtnRemoveQS = "Remove Selected";
	private String strbtnOK = "OK";
	
	//Insets
	private Insets lblIns = new Insets(3,3,3,3);
	private Insets rbIns = new Insets(1,5,1,1);
	private Insets indIns = new Insets(1,20,1,1);
	private Insets basIns = new Insets(2,2,2,2);
	
	//CONSTRUCTOR
	public ManageQuerySets(FrmPrincipalDesk f){
		
		//Initializations
		this.f = f;
		RetrieveQuerySet();
		this.setModal(false);

		//create components
		this.getPanel();
		this.getFrame();
		
		this.setVisible(true);
	}

	// ======= GUI Methods ====== //
	
	//panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		c.weightx = 1;
		c.insets = basIns;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		
		//Add CS Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblAdd = new JLabel(strLblAdd);
		LblAdd.setBackground(Color.GRAY);
		LblAdd.setOpaque(true);
		jp.add(LblAdd,c);
		gridy++;
		
		//Name Label
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		QSNameLabel = new JTextField(strQSNameLabel);
		QSNameLabel.setEditable(false);
		jp.add(QSNameLabel,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		QSName = new JTextField("");
		QSName.setEditable(true);
		QSName.addActionListener(this);
		jp.add(QSName, c);
		gridy++;
		
		//Form to write queries
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = basIns;
		txtQueries = new JTextArea("");
		txtQueries.setEditable(true);
		JScrollPane ptsscroll = new JScrollPane(txtQueries);
		ptsscroll.setPreferredSize(new Dimension(100, 200));
		jp.add(ptsscroll, c);
		gridy++;
		
		//load queries from file
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = basIns;
		btnLoadFromFile = new JButton(strLoad);
		btnLoadFromFile.addActionListener(this);
		jp.add(btnLoadFromFile, c);
		
		//add button
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = basIns;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		btnAdd = new JButton(strbtnAdd);
		btnAdd.addActionListener(this);
		jp.add(btnAdd, c);
		
		gridy++;
		
		//Remove Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridwidth = 3;
		LblRemove = new JLabel(strLblRemove);
		LblRemove.setBackground(Color.GRAY);
		LblRemove.setOpaque(true);
		jp.add(LblRemove,c);
		gridy++;
		
		//Remove
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		QSToRemove = new JTextField(strQSToRemove);
		QSToRemove.setEditable(false);
		jp.add(QSToRemove,c);
		
		
		// drop-down menu for Query Sets
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		QSMenu = new JComboBox<String>(QuerySetMenuItems);
		QSMenu.setEnabled(true);
		jp.add(QSMenu, c);
		
		//remove button
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnRemoveQS = new JButton(strbtnRemoveQS);
		btnRemoveQS.addActionListener(this);
		btnRemoveQS.setEnabled(true);
		jp.add(btnRemoveQS, c);
		gridy++;
		gridy++;

		//submit button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.insets = new Insets(10,1,1,1);
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp.add(btnOK, c);
		
		//Last step: add to panel
		this.add(jp);
		
	}
	
	//frame
	public void getFrame(){
		this.setSize(600,450);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Manage Query Sets");
		this.setResizable(true);
	}
	
	// ======= Actions ====== //

	//Actions Headquarters
	@Override
	public void actionPerformed(ActionEvent evt) {

		//load from file
		if (evt.getSource().equals(btnLoadFromFile)){
			ImportFileList();
		}
		
		//Add a Query Set to the current collection
		if (evt.getSource().equals(btnAdd) || evt.getSource().equals(QSName)){
			if (CheckNameAndQueries()){
				
				//Create query set
				QuerySet QS = new QuerySet();
				QS.setName(QSName.getText());
				
				//Parse queries, build preliminary query set
				QS.setContextTrees(BuildQueries());
				
				//Finally, store query set + Update menu
				f.getOS().getQuerySets().add(QS);
				
				//determine item to remove, if any.
				boolean RemoveAnItem = false;
				int Item2Remove = -1;
				for (int i = 0; i < QSMenu.getItemCount(); i++){
					if (QSMenu.getItemAt(i).equals("<none>")){
						Item2Remove = i;
						RemoveAnItem = true;
					}
				}
				
				//remove an item, if appropriate
				if (RemoveAnItem){
					QSMenu.removeItemAt(Item2Remove);
				}
				
				//add new item.
				QSMenu.addItem(QS.getName());
			}
		}
		
		//Remove a Query Set from the current collection
		if (evt.getSource().equals(btnRemoveQS)){
			QuerySet QS2Remove = null;
			if (!QSMenu.getSelectedItem().equals("<none>")){
				
				//Determine QS 2 Remove
				for (int i = 0; i < f.getOS().getQuerySets().size(); i++){
					if (f.getOS().getQuerySets().get(i).getName().equals(QSMenu.getSelectedItem())){
						QS2Remove = f.getOS().getQuerySets().get(i);
					}
				}
				
				//Remove this QS
				if (QS2Remove != null){
					f.getOS().getQuerySets().remove(QS2Remove);	//from list
					QSMenu.removeItem(QS2Remove.getName()); 	//from menu
				}
				
				//add placeholder item, if necessary
				if (QSMenu.getItemCount() == 0){
					QSMenu.addItem("<none>");
				}
			
			}
		}
		
		//close window
		if (evt.getSource().equals(btnOK)){
			
			//close window, move on with life.
			this.dispose();
		}
	}

	//Check Name for duplicates + empty name field
	public boolean CheckNameAndQueries(){
		boolean Proceed = false;
		if (!QSName.getText().equals("")){
			
			//Default: proceed
			Proceed = true;
			
			//check all by name, don't proceed if any duplicates
			for (QuerySet QS: f.getOS().getQuerySets()){
				if (QS.getName().equals(QSName.getText())){
					Proceed = false;
				}
			}
			
			//show message if not proceeding.
			if (!Proceed) {
				JOptionPane.showMessageDialog(null, "A Query Set of this name already exists.\nPlease choose a different name.",
						"Name already in use",JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, 
					"Please Enter a Name for this Query Set in the Name Field.",
					"No Name Provided",JOptionPane.ERROR_MESSAGE);
		}
		return Proceed;
	}
	
	//Launch file chooser
	public void ImportFileList(){
		JFileChooser GetQueryList = new JFileChooser();
		
		GetQueryList.setFileSelectionMode(JFileChooser.FILES_ONLY);
		GetQueryList
				.setDialogTitle("Select A File Containing a List of Queries");

		//retrieve directory
		if (f.getFileChooserSource() != null) {
			GetQueryList.setCurrentDirectory(f.getFileChooserSource());
		} else {
			GetQueryList.setCurrentDirectory(new File("."));
		}
		
		GetQueryList.showOpenDialog(GetQueryList);
		
		// note current directory for next time
		if (GetQueryList.getCurrentDirectory() != null) {
			f.setFileChooserSource(GetQueryList.getCurrentDirectory());
		}
		
		//import IDs
		if (GetQueryList.getSelectedFile() != null){
			AddFiles2QueryList(GetQueryList.getSelectedFile());
		}

	}
	
	//Retrieve Query set
	public void RetrieveQuerySet(){
		QuerySetMenuItems = new String[f.getOS().getQuerySets().size()];
		if (QuerySetMenuItems.length > 0){
			for (int i = 0; i < QuerySetMenuItems.length; i++){
				QuerySetMenuItems[i] = f.getOS().getQuerySets().get(i).getName();
			}
		} else {
			QuerySetMenuItems = new String[1];
			QuerySetMenuItems[0] = "<none>";
		}
	}
	
	//add contents of file to text area
	public void AddFiles2QueryList(File f){
		
		//Retrieve current text
		strTextAreaTxt =  txtQueries.getText();
		
		//Adjust, if necessary
		if (!strTextAreaTxt.trim().equals("") && !strTextAreaTxt.endsWith("\n")){
			strTextAreaTxt = strTextAreaTxt + "\n";
		}
		
//		if (!AccessedTextArea){
//			strTextAreaTxt = "";
//		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String Line = null;
			String OrgName = "";
			String IDNum = "";
			int Counter = 0;
			boolean AddToList = false;
			while ((Line = br.readLine()) != null){
				
				//avoid comments
				if (!Line.startsWith("#")){
					
					//Just add the lines to the current text.
					strTextAreaTxt = strTextAreaTxt + Line + "\n";
					
				}
			}
			
//			//Update JTextArea
//			AccessedTextArea = true;
//			GenbankInfo.setForeground(Color.BLACK);
			txtQueries.setText(strTextAreaTxt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	//Transfer text in window to queries
	public LinkedList<QueryData> BuildQueries(){
		
		//Initialize output
		LinkedList<QueryData> QueryList = new LinkedList<QueryData>();
		
		//Fix entries
		strTextAreaTxt = txtQueries.getText();
		
		//Parameters for each Query Set
		boolean AnnotationSearch;
		String[] Queries = null;
		int[] Clusters = null;
		String Name;
		
		//Parameters from FrmPrincipalDesk
		String ContextSetName = (String) f.getPanBtn().getContextSetMenu().getSelectedItem();
		String DissimilarityType = (String) f.getPan_Menu().getCbDissimilarity().getSelectedItem();
		String ClusteringType = (String) f.getPan_Menu().getCbMethod().getSelectedItem();
		PostSearchAnalyses P = new PostSearchAnalyses(false, false, false, false);
		CSDisplayData CSD = f.getCSD();
		String OSName = f.getOS().getName();

		//Red flag parameter
		boolean IgnoreClusterSet = false;
		
		//Set query type
		if (f.getPanBtn().getAnnotationSearch().isSelected()){
			AnnotationSearch = true;
		} else {
			AnnotationSearch = false;
		}
		
		//Queries are split up by lines.
		String[] StartingSet = strTextAreaTxt.split("\n");
		int Counter = 1;
		
		//Each line is a query, add appropriately to set!
		for (String s : StartingSet){

			//update name
			Name = "Query " + String.valueOf(Counter);
			Counter++;
			
			//Split each query by delimiter (semicolon)
			String SplitList[] = s.split(";");
			
			//build search points
			if (AnnotationSearch){
				Queries = SplitList;
			} else {
				
				//Linked list, for variable size
				LinkedList<Integer> NumQueriesList = new LinkedList<Integer>();
				for (int i = 0; i < SplitList.length; i++){
					try {
						NumQueriesList.add(Integer.parseInt(SplitList[i].trim()));
					} catch (Exception ex){}
				}
				
				//Final - array
				Clusters = new int[NumQueriesList.size()];
				for (int i = 0; i < NumQueriesList.size(); i++){
					Clusters[i] = NumQueriesList.get(i);
				}
				
				//No need to retain cases where no valid clusters were found.
				if (Clusters.length == 0){
					IgnoreClusterSet = true;
				} else {
					IgnoreClusterSet = false;
				}
			}
			
			//If there are clusters to be querying
			if (!IgnoreClusterSet){
				
				//Initialize new query
				QueryData QD = new QueryData();
				
				//Add Parameters
				QD.setAnnotationSearch(AnnotationSearch);
				QD.setQueries(Queries);
				QD.setClusters(Clusters);
				QD.setName(Name);
				QD.setContextSetName(ContextSetName);
				QD.setDissimilarityType(DissimilarityType);
				QD.setClusteringType(ClusteringType);
				QD.setAnalysesList(P);
				QD.setCSD(CSD);
				QD.setOSName(OSName);
				
				//Add to output
				QueryList.add(QD);
				
			}

		}
		
		//return determined query hash
		return QueryList;
	}
	
	// ---- GETTERS AND SETTERS -----//
	
	public String[] getQuerySetMenuItems() {
		return QuerySetMenuItems;
	}

	public void setQuerySetMenuItems(String[] querySetMenuItems) {
		QuerySetMenuItems = querySetMenuItems;
	}
	
}
