package ContextForest;

import genomeObjects.CSDisplayData;
import genomeObjects.ExtendedCRON;

import importExport.DadesExternes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import parser.Fig_Pizarra;

import newickTreeParsing.Tree;

import definicions.Cluster;
import definicions.Config;
import definicions.MatriuDistancies;

import methods.Reagrupa;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.PostSearchAnalyses;
import moduls.frm.QueryData;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_btn_NEW;
import moduls.frm.Panels.Jpan_btn_NEW.SearchWorker;

public class ChooseCompareTree extends JDialog implements ActionListener, PropertyChangeListener{
	
	//FIELDS
	
	//master
	private FrmPrincipalDesk f;
	private ChooseCompareTree CCT;
	
	//GUI
	private JPanel jp, jp2, jpEnclosing;
	private JLabel LblTree, LblParameters, LblRun;
	private String strLblTree = " REFERENCE TREE";
	private String strLblParameters = " SCANNING PARAMETERS";
	private String strLblRun = " EXECUTE SCAN";
	private JRadioButton rbLoadedTree, rbQueryTree;
	private String strLoadedTree = "Loaded Phylogenetic Tree:";
	private String strQueryTree = "Context Tree, generated by Query:";
	private ButtonGroup bg;
	private JComboBox<String> PhyloMenu, QSMenu, ComparisonMenu;
	private String[] LoadedPhyloItems, QuerySetMenuItems, ComparisonApproaches;
	private JTextField txtQueryField, LblQuerySet, LblComparisonApproach;
	private String strQuerySet = "Query Set:";
	private String strComparisonApproach = "Comparison Approach:";
	private JButton btnExecuteScan;
	private String strScan = "Excecute Scan";
	private JProgressBar progressbar;
	private JTextField LblSegValue, TxtSegValue;
	private String strLblSegValue = "Segmentation Point:";
	private String strTxtSegValue = "0.5";
		
	//Insets
	private Insets lblIns = new Insets(3,3,3,3);
	private Insets downIns = new Insets(5,5,20,1);
	private Insets basIns = new Insets(1,1,1,1);
	
	//CONSTRUCTOR
	public ChooseCompareTree(FrmPrincipalDesk f){
		
		//Initializations
		this.f = f;
		BuildMenus();
		
		//create components
		this.getPanel();
		this.getFrame();
		
		this.CCT = this;
		
		//Last step: make window visible
		this.setVisible(true);
	}
	
	// ======= Classes ===========//
	public class TreeCompareWorker extends SwingWorker<Void, Void>{

		//Fields
		public QuerySet TQ = null;
		public double segvalue;
		
		//constructor
 		public TreeCompareWorker(double segValue){
			segvalue = segValue;
		}
		
		//Central processing
		@Override
		protected Void doInBackground() throws Exception {
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			//Initialize output
			LinkedList<TreeCompareReport> Reports = new LinkedList<TreeCompareReport>();
			
			//Retrieve appropriate Query Set
			for (QuerySet QS : f.getOS().getQuerySets()){
				if (QS.getName().equals((String) QSMenu.getSelectedItem())){
					TQ = QS;
					break;
				}
			}
			
			//Retrieve tree and cluster
			String TreeName = "";
			Cluster cm = null;
			boolean ReferenceTreeReady = true;
			QueryData Q = null;
			
			if (rbLoadedTree.isSelected()){
				
				//retrieve phy tree
				TreeName = (String) PhyloMenu.getSelectedItem();
				Tree TheTree = null;
				
				for (Tree t : f.getOS().getParsedPhyTrees()){
					if (t.getName().equals(TreeName)){
						TheTree = t;
						break;
					}
				}
				
				//retrieve tree as cluster
				Config cfgp;
				if (f.getCfgPhylo() != null){
					cfgp = f.getCfgPhylo();
				} else {
					cfgp = f.getConfig();
				}
				Fig_Pizarra fig_p = new Fig_Pizarra(TheTree, cfgp);
				cm = fig_p.ComputedRootCluster;

			} else {
				
				//invent name
				TreeName = "Query: " + txtQueryField.getText();
				
				//Assemble appropriate query data set
				Q = new QueryData();
				
				//Parameters for each Query Set
				boolean AnnotationSearch;
				String[] Queries = null;
				int[] Clusters = null;
				
				//Parameters from FrmPrincipalDesk
				String ContextSetName = (String) f.getPanBtn().getContextSetMenu().getSelectedItem();
				String DissimilarityType = (String) f.getPan_Menu().getCbDissimilarity().getSelectedItem();
				String ClusteringType = (String) f.getPan_Menu().getCbMethod().getSelectedItem();
				PostSearchAnalyses P = new PostSearchAnalyses(false, true, false, false);
				CSDisplayData CSD = f.getCSD();
				String OSName = f.getOS().getName();
				
				//Set query type
				if (f.getPanBtn().getAnnotationSearch().isSelected()){
					AnnotationSearch = true;
				} else {
					AnnotationSearch = false;
				}
				
				//Split each query by delimiter (semicolon)
				String SplitList[] = txtQueryField.getText().split(";");
				
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
						ReferenceTreeReady = false;
					}
				}

				//proceed
				if (ReferenceTreeReady){
					
					//Add Parameters
					Q.setAnnotationSearch(AnnotationSearch);
					Q.setQueries(Queries);
					Q.setClusters(Clusters);
					Q.setName(TreeName);
					Q.setContextSetName(ContextSetName);
					Q.setDissimilarityType(DissimilarityType);
					Q.setClusteringType(ClusteringType);
					Q.setAnalysesList(P);
					Q.setCSD(CSD);
					Q.setOSName(OSName);
					
					//convert to cluster
					cm = GenerateClusterFromQuery(Q,true);
					
				}
			
			}
			
			//proceed
			if (ReferenceTreeReady){
				
				//Initialize counter, prepare progress bar
				int Counter = 0;
				setProgress(0);
				
				//Scan each individual query 
				for (QueryData QD : TQ.getContextTrees()){
					
					//Initialize output
					TreeCompareReport TCR = null;
					
					//generate cluster from every test query
					Cluster cq = GenerateClusterFromQuery(QD,false);
					
					//Generate report for every cluster
					String Method = (String) ComparisonMenu.getSelectedItem();
					if (Method.equals("Adjusted Fowlkes-Mallows")){
						TCR = FowlkesMallows(cm, cq);
					} else if (Method.equals("Robinson-Foulds")) {
						TCR = RobinsonFoulds(cm, cq);
					}
					
					//Add to list
					Reports.add(TCR);
					
					//Increment counter + update progress bar
					Counter++;
					int progress = (int) (100.0 *((double) Counter )/((double) TQ.getContextTrees().size())); 
					setProgress(progress);

				}
				
				//Finally, store this set of reports appropriately
				TQ.getTreeComparisons().put(TreeName, Reports);
				
			} else {
				JOptionPane.showMessageDialog(null, "Unable to Create or Load Reference Tree.",
						"Reference Tree Format Error.",JOptionPane.ERROR_MESSAGE);
			}
		
			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			return null;
		}
		
		//post processing
		public void done(){
			
			//switch cursor to normal
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			//re-set progress bar
			progressbar.setValue(0);
			
			//launch new window
			new TreeSimilarityScanWindow(f, TQ);
			
			//close window
			dispose();
		}
		
		// ======= Supplemental Methods ====== //
		
		//Generate cluster from query
		protected Cluster GenerateClusterFromQuery(QueryData QD, boolean AddListener){

			//Initialize output
			SearchWorker SW = f.getPanBtn().new SearchWorker(QD,
					"Load", Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
					Jpan_Menu.getPrecision(), false);
			if (AddListener){
				SW.addPropertyChangeListener(CCT);
			}

			SW.execute();

			//empty while loop - implicit waiting
			while(!SW.isDone()){}
			
			return SW.RootCluster;
			
		}
		
		//Segregate a cluster into smaller clusters based on segmentation value.
		protected LinkedList<Cluster> SegregateCluster(Cluster c){
			
			//Initialize output
			LinkedList<Cluster> CutSet = new LinkedList<Cluster>();
			
			//Initialize seed, to begin analysis.
			LinkedList<Cluster> Seed = new LinkedList<Cluster>();
			Seed.add(c);
			
			//Define initial children set - children of root
			ClusterGroup CG = SegmentCluster(Seed);
			CutSet.addAll(CG.getRetainGroup());
			LinkedList<Cluster> Children = CG.getSegGroup();
			
			while (Children.size() != 0){
				CG = SegmentCluster(Children);
				Children = CG.getSegGroup();
				CutSet.addAll(CG.getRetainGroup());
			}

			return CutSet;
		}
		
		//Translate sets into string
		protected LinkedList<LinkedList<String>> SegregatedLeaves(LinkedList<Cluster> C){
			
			//Initialize output
			LinkedList<LinkedList<String>> LeafList = new LinkedList<LinkedList<String>>();
			
			//Process
			for (Cluster c : C){
				
				//Retrieve data
				LinkedList<String> Leaves = c.getLeafNames();
				LinkedList<String> NewList = new LinkedList<String>();
				
				//remove tags
				for (String s : Leaves){

					String[] L = s.split("-");
					String strRebuild = null;
					boolean First = true;
					for (int i = 0; i < L.length-1; i++){
						if (First){
							strRebuild = L[i];
							First = false;
						} else {
							strRebuild = strRebuild + "-" + L[i];
						}
					}
					NewList.add(strRebuild);
				}
				
				//add List
				LeafList.add(NewList);
			}
			
			//output
			return LeafList;
		}
		
		//Return which children need to be further processed.
		protected ClusterGroup SegmentCluster(LinkedList<Cluster> ParentCluster){

			//Initialize
			ClusterGroup CG = new ClusterGroup();
			LinkedList<Cluster> SegChildren = new LinkedList<Cluster>();
			LinkedList<Cluster> RetainChildren = new LinkedList<Cluster>();
			
			//build based on segmentation point
			for (Cluster c : ParentCluster){
				if (c.getAlcada() > segvalue){
					SegChildren.addAll(c.getLst());
				} else {
					RetainChildren.add(c);
				}
			}
			
			//Add lists to output data structure
			CG.setRetainGroup(RetainChildren);
			CG.setSegGroup(SegChildren);
			
			return CG;
		}
		
		//TODO Tree comparison method: Fowlkes-Mallows
		protected TreeCompareReport FowlkesMallows(Cluster Master, Cluster Query){
			
			//Initialize output
			TreeCompareReport TCR = new TreeCompareReport();
			
			//Generate lists
			LinkedList<LinkedList<String>> MasterList = SegregatedLeaves(SegregateCluster(Master));
			LinkedList<LinkedList<String>> QueryList = SegregatedLeaves(SegregateCluster(Query));
			
			//create FowlkesMallow object + compute
			FowlkesMallows FM = new FowlkesMallows(MasterList, QueryList);
			TCR.setDissimilarity(FM.Compute());
			
			System.out.println(TCR.getDissimilarity());
			
//			//DEBUGGING
//			for (LinkedList<String> L : QueryList){
//				System.out.println("----Cluster-----");
//				for (String s : L){
//					System.out.println(s);
//				}
//			}
			
			//return
			return TCR;
		}
		
		//TODO Tree comparison method: Robinson Foulds
		protected TreeCompareReport RobinsonFoulds(Cluster Master, Cluster Query){
			
			//Initialize output
			TreeCompareReport TCR = new TreeCompareReport();
			
			//return
			return TCR;
		}
		

		
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
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		
		/*
		 * COMPARISON TREE
		 */
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblTree = new JLabel(strLblTree);
		LblTree.setBackground(Color.GRAY);
		LblTree.setOpaque(true);
		jp.add(LblTree,c);
		gridy++;
		
		//create button group
		bg = new ButtonGroup();
		rbLoadedTree = new JRadioButton(strLoadedTree);
		rbQueryTree = new JRadioButton(strQueryTree);
		bg.add(rbLoadedTree);
		bg.add(rbQueryTree);
		
		//Loaded Tree RB
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		jp.add(rbLoadedTree, c);
		
		//Select from drop-down menu
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		PhyloMenu = new JComboBox<String>(LoadedPhyloItems);
		if (f.getOS().getParsedPhyTrees().size() > 0){
			PhyloMenu.setEnabled(true);
			rbLoadedTree.setSelected(true);
		} else {
			PhyloMenu.setEnabled(false);
			rbLoadedTree.setEnabled(false);
			rbQueryTree.setSelected(true);
		}

		jp.add(PhyloMenu, c);
		gridy++;
		
		//Generated Tree RB
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		jp.add(rbQueryTree, c);
		
		//Enter Query box
		c.ipady = 7;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		txtQueryField = new JTextField("");
		txtQueryField.setEditable(true);
		txtQueryField.addActionListener(this);
		jp.add(txtQueryField, c);
		c.ipady = 0;
		gridy++;
		
		/*
		 * SCANNING PARAMETERS
		 */
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblParameters = new JLabel(strLblParameters);
		LblParameters.setBackground(Color.GRAY);
		LblParameters.setOpaque(true);
		jp.add(LblParameters,c);
		gridy++;
		
		//Select Query Set
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		LblQuerySet = new JTextField(strQuerySet);
		LblQuerySet.setHorizontalAlignment(JTextField.LEFT);
		LblQuerySet.setEditable(false);
		jp.add(LblQuerySet, c);
		
		//Query Set Drop-down menu
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		QSMenu = new JComboBox<String>(QuerySetMenuItems);
		jp.add(QSMenu, c);
		gridy++;
		
		//Select comparison approach
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		LblComparisonApproach = new JTextField(strComparisonApproach);
		LblComparisonApproach.setHorizontalAlignment(JTextField.LEFT);
		LblComparisonApproach.setEditable(false);
		jp.add(LblComparisonApproach, c);
		
		//Comparison Approach drop-down menu
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		ComparisonMenu = new JComboBox<String>(ComparisonApproaches);
		jp.add(ComparisonMenu, c);
		gridy++;
		
		//Segmentation label + field
		c.gridx = 1; 
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		LblSegValue = new JTextField(strLblSegValue);
		LblSegValue.setEditable(false);
		jp.add(LblSegValue, c);
		
		//segmentation value
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtSegValue = new JTextField(strTxtSegValue);
		TxtSegValue.setEditable(true);
		jp.add(TxtSegValue, c);
		gridy++;
		
		/*
		 * EXECUTE
		 */
		
		jp2 = new JPanel();
		jp2.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.CENTER;
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblRun = new JLabel(strLblRun);
		LblRun.setBackground(Color.GRAY);
		LblRun.setOpaque(true);
		jp2.add(LblRun,c);
		gridy++;
		
		//button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnExecuteScan = new JButton(strScan);
		btnExecuteScan.addActionListener(this);
		jp2.add(btnExecuteScan, c);
		gridy++;
		
		//progressbar
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.insets = downIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressbar = new JProgressBar();
		progressbar.setValue(0);
		jp2.add(progressbar, c);
		
		//add to frame
		jpEnclosing = new JPanel();
		jpEnclosing.setLayout(new BorderLayout());
		jpEnclosing.add(jp, BorderLayout.NORTH);
		jpEnclosing.add(jp2, BorderLayout.SOUTH);
		this.add(jpEnclosing);
		
	}
	
	//frame
	public void getFrame(){
		this.setSize(600,350);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Select Tree and Query Set");
		this.setResizable(true);
	}
	
	// ======= Listeners + Action Methods ====== //

	//Action headquarters
	@Override
	public void actionPerformed(ActionEvent e) {

		//Error catching
		if ((rbQueryTree.isSelected() && !txtQueryField.getText().equals("")) || rbLoadedTree.isSelected()) {
			
			//determine segregation point
			try {
				
				//seg value parsing
				double segvalue = Double.parseDouble(TxtSegValue.getText());
				if (segvalue < 0.0 || segvalue > 1.0){
					throw new Exception();
				}
				
				//launch listener
				TreeCompareWorker TCW = new TreeCompareWorker(segvalue);
				TCW.addPropertyChangeListener(this);
				TCW.execute();
				
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "The Segmentation Point value must be numerical value from 0 to 1.",
						"No Reference Query Defined",JOptionPane.ERROR_MESSAGE);
			}

			
		} else {
			JOptionPane.showMessageDialog(null, "Please Enter a Query to generate the reference Context Tree.",
					"No Reference Query Defined",JOptionPane.ERROR_MESSAGE);
		}

		
		//finally, close window
		//this.dispose();
	}
	

	//Build Menus
	public void BuildMenus(){
		
		//Phylo Trees
		LoadedPhyloItems = f.getPanPhyTreeMenu().getLoadedPhyTrees();
		
		//Query Sets
		QuerySetMenuItems = new String[f.getOS().getQuerySets().size()];
		if (QuerySetMenuItems.length > 0){
			for (int i = 0; i < QuerySetMenuItems.length; i++){
				QuerySetMenuItems[i] = f.getOS().getQuerySets().get(i).getName();
			}
		} else {
			QuerySetMenuItems = new String[1];
			QuerySetMenuItems[0] = "<none>";
		}
		
		//Comparison Approaches
		ComparisonApproaches = new String[2];
		ComparisonApproaches[0] = "Adjusted Fowlkes-Mallows";
		ComparisonApproaches[1] = "Robinson-Foulds";
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressbar.setValue(progress);
		}
	}
}
