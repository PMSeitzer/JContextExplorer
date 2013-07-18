package ContextForest;

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
import java.util.Set;

import javax.swing.ButtonGroup;
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
import javax.swing.SwingWorker;

import definicions.Cluster;
import definicions.MatriuDistancies;

import methods.Reagrupa;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.QueryData;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_btn_NEW.SearchWorker;

public class ChooseContextForest extends JDialog implements ActionListener, PropertyChangeListener{

	//Fields
	//baseline
	private FrmPrincipalDesk f;
	private ChooseContextForest CCF;
	
	//Data Parameters
	private int NumMismatches = 0;
	private double PenaltyPerMismatch;
	private double SegmentationValue;
	
	//GUI
	private JPanel jp, jp2, jpEnclosing;
	private JLabel LblDGSettings, LblRunSettings, LblSubmit;
	private String strLblDGSettings = " SELECT QUERY SET AND DISSIMILARITY MEASURE";
	private String strLblRunSettings = " CONTEXT FOREST CORRELATION SETTINGS";
	private String strLblSubmit = " BUILD CONTEXT FOREST";
	private JTextField LblQuerySet, LblSelectDG, LblSelectDGType, 
		LblAdjustmentPenalty, LblFreeMisMatches, TxtFreeMisMatches,
		LblPenaltyperMM, TxtPenaltyperMM, LblSegmentationValue, 
		LblSegValueInner, TxtSegmentationValue;

	private String strLblQuerySet = "Query Set:";
	private String strLblSelectDiss = "Dissimilarity Metric:";
	private String strLblSelectDGType = "Data Grouping Type:";
	private JComboBox<String> CFDissimilarities, QSMenu;
	private String[] LoadedDissimilarities;
	private String[] LoadedQSs;
	private ButtonGroup BG, BGAdj, BGDiceJaccard;
	private JRadioButton rbSpecies, rbGene, rbMisMatch, rbScaleFactor, rbDice, rbJaccard;
	private String strrbSpecies = "Species Grouping";
	private String strrbGene = "Gene Grouping";
	private String strLblAdjustmentPenalty = "Non-Identical Dataset Adjustment";
	private String strrbMisMatch = "Summed Mismatch Penalty";
	private String strrbScaleFactor = "Dice or Jaccard Scale Factor Penalty";
	private LinkedList<Component> MisMatchGroup;
	private LinkedList<Component> NoMMPenaltySubGroup;
	private LinkedList<Component> ScaleFactorGroup;
	private JCheckBox cbAllowMM, cbMisMatchPenalty;
	private String strcbAllowMM = "Permit some number of mismatches without penalty";
	private String strcbMisMatchPenalty = "Exact a summed mismatch penalty";
	private String strLblFreeMisMatches = "Number of free mismatches: ";
	private String strTxtFreeMisMatches = "2";
	private String strLblPenaltyperMM = "Penalty per mismatch:";
	private String strTxtPenaltyperMM = "0.01";
	private String strLblSegmentationValue = "Context Tree Segmentation Point";
	private String strLblSegValueInner = "Value:";
	private String strTxtSegmentationValue = "0.05";
	private String strrbDice = "Dice's Coefficient";
	private String strrbJaccard = "Jaccard Index";
	private JButton btnOK;
	private String strbtnOK = "Execute";
	private JProgressBar progressBar;
	
	//Insets
	private Insets lblIns = new Insets(1,1,1,1);
	private Insets Ind1Ins = new Insets(3,20,3,3);
	private Insets Ind2Ins = new Insets(3,40,3,3);
	private Insets basIns = new Insets(1,1,1,1);
	private Insets downIns = new Insets(5,5,20,1);
	
	//CONSTRUCTOR
	public ChooseContextForest(FrmPrincipalDesk f){
		
		//Initialization-type steps
		this.f = f;
		this.CCF = this;
		BuildMenus();
		
		//get panel and frame
		this.getPanel();
		this.getFrame();
		
		//Available Components
		EnableComponents(MisMatchGroup, true);
		EnableComponents(ScaleFactorGroup, false);
		
		//Last step: make window visible
		this.setVisible(true);
	}

	// ======= Classes ===========//
	public class ContextForestWorker extends SwingWorker<Void, Void>{

		//Fields
		public QuerySet TQ = null;
		public double segvalue;
		public String ComparisonName;
		public DadesExternes de;
		
		//constructor
		public ContextForestWorker(double segValue){
			this.segvalue = segValue;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
						
			//Retrieve appropriate Query Set
			for (QuerySet QS : f.getOS().getQuerySets()){
				if (QS.getName().equals((String) QSMenu.getSelectedItem())){
					TQ = QS;
					break;
				}
			}
			
			// =================================// Compute Context Trees
			
			//set counter
			int Counter = 0;
			
			//Generate cluster for each context tree, if not already available.
			for (QueryData QD : TQ.getContextTrees()){

				//query -> cluster
				if (QD.getOutputCluster() == null){
					Cluster Query = GenerateClusterFromQuery(QD, false);
					QD.setOutputCluster(Query);
				}
				
				//Increment counter + update progress bar
				Counter++;
				int progress = (int) (100.0 *((double) Counter )/((double) TQ.getContextTrees().size())); 
				setProgress(progress);
			}
			
			// =================================// Build Dissimilarities
			
			DissimilarityMatrixData DMD = BuildDissimilarities();
			
			// =================================// Build Dendrogram
			
			de = BuildDendrogram(DMD);
			
			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			return null;
		}
		
		// ------ Supplemental methods ------- //
		
		//Reformat array
		protected LinkedList<LinkedList<String>> MasterListReformat(LinkedList<String[]> Input){
			LinkedList<LinkedList<String>> Output = new LinkedList<LinkedList<String>>();
			for (String[] L : Input){
				LinkedList<String> Component = new LinkedList<String>();
				for (String s : L){
					Component.add(s);
				}
				Output.add(Component);
			}
			
			return Output;
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
		
		//Generate cluster from query
		protected Cluster GenerateClusterFromQuery(QueryData QD, boolean AddListener){

			//For null cluster results, these values somehow are set to off
			QD.getAnalysesList().setOptionComputeDendrogram(true);
			QD.getAnalysesList().setOptionDisplaySearches(true);
			
			//Create a new SearchWorker.
			SearchWorker SW = f.getPanBtn().new SearchWorker(QD,
					"Load", Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
					Jpan_Menu.getPrecision(), false);
			if (AddListener){
				SW.addPropertyChangeListener(CCF);
			}
			
			SW.execute();

			//empty while loop - implicit waiting
			while(!SW.isDone()){}
			
			return SW.RootCluster;
			
		}
		
		//Build dissimilarities
		protected DissimilarityMatrixData BuildDissimilarities(){
			
			//reset progress and counter to zero, in preparation for dissimilarities
			setProgress(0);
			int Counter = 0;
			
			//Note dissimilarity matrix generation parameters
			DatasetAdjustmentParameters DAP = new DatasetAdjustmentParameters();
			DAP.setAdjustmentPenalty(cbMisMatchPenalty.isSelected());
			DAP.setFreeMismatches(cbAllowMM.isSelected());
			DAP.setNumberOfFreeMatches(NumMismatches);
			DAP.setPenaltyperMismatch(PenaltyPerMismatch);
			
			//Dissimilarity
			DissimilarityMatrixData DMD = null;
			
			//attempt to retrieve dissimilarities
			for (DatasetAdjustmentParameters DAP2 : TQ.getDissimilarities().keySet()){
				if (DAP2.equals(DAP)){
					DMD = TQ.getDissimilarities().get(DAP2);
				}
			}
			
			int CompareSize = TQ.getContextTrees().size();
			int Total = 0;
			
			//compute matrix from dissimilarity, if none already
			if (DMD == null){
				
				//initialize list of dissimilarities
				DMD = new DissimilarityMatrixData();
				LinkedList<Double> D = new LinkedList<Double>();
				LinkedList<String> FD = new LinkedList<String>();
				LinkedList<String> MFD = new LinkedList<String>();
				DMD.setNumLeaves(CompareSize);
				
				//determine total
				while (CompareSize > 1){
					CompareSize--;
					Total = Total + CompareSize;
				}
				
				//iterate over keys
				for (int i = 0; i <  TQ.getContextTrees().size(); i++){
					
					String str = TQ.getContextTrees().get(i).getName().replaceAll(" ", "_").replaceAll(";", "AND");
					str = str + "\t";
					for (int j = i+1; j < TQ.getContextTrees().size(); j++){
						
						double dist = 1.0;
						
						//Fowlkes-Mallows approach
						if (CFDissimilarities.getSelectedItem().equals("Fowlkes-Mallows")){
							
							//Retrieve Leaves in appropriate format from cluster
							LinkedList<LinkedList<String>> QueryList_I = SegregatedLeaves(SegregateCluster(TQ.getContextTrees().get(i).getOutputCluster()));
							LinkedList<LinkedList<String>> QueryList_J = SegregatedLeaves(SegregateCluster(TQ.getContextTrees().get(j).getOutputCluster()));

							//Create new Fowlkes-Mallows objects
							FowlkesMallows FM = new FowlkesMallows(QueryList_I, QueryList_J);
							
							//Set Adjustment parameters
							FM.setAdjustmentPenalty(DAP.isAdjustmentPenalty());
							FM.setFreeMismatches(DAP.isFreeMismatches());
							FM.setNumberOfFreeMatches(DAP.getNumberOfFreeMatches());
							FM.setPenaltyperMismatch(DAP.getPenaltyperMismatch());
							
							//Compute similarity
							FM.Compute();
							
							//dissimilarity = 1 - similarity
							dist = 1.0 - FM.getB();
														
						}

						if ((j+1) != TQ.getContextTrees().size()){
							str = str + String.valueOf(dist) + "\t";
						} else {
							str = str + String.valueOf(dist);
						}
						
						//Define node names - individual context trees
						String Name1 = TQ.getContextTrees().get(i).getName().replaceAll(" ", "_").replaceAll(";", "AND");
						String Name2 = TQ.getContextTrees().get(j).getName().replaceAll(" ", "_").replaceAll(";", "AND");
						
						//write row
						String Row = Name1 + ";" + Name2 + ";" + String.valueOf(dist);
						
						//add to list
						FD.add(Row);
												
						//add value to linked list
						D.add(dist);
						
						//Increment counter + update progress bar
						Counter++;
						int progress = (int) (100.0 *((double) Counter )/((double) Total)); 
						setProgress(progress);
						
					}
					
					//debugging - view matrix
					//System.out.println(str);
					
					//write row of matrix
					MFD.add(str);
				}
				
//				//debugging: view pairwise relationships
//				for (String s : FD){
//					System.out.println(s);
//				}
				
				//add formatted triangle (alternative input format)
				DMD.setMatrixFormattedDissimilarities(Triangle2Matrix(MFD));

//				//debugging: view pairwise relationships
//				for (String s : DMD.getMatrixFormattedDissimilarities()){
//					System.out.println(s);
//				}
				
				//add info to DMD
				DMD.setDissimilarities(D);
				DMD.setFormattedDissimilarities(FD);
				DMD.setMethodName((String) Jpan_Menu.getCbMethod().getSelectedItem());
				
				//store dissimilarities with parameters
				TQ.getDissimilarities().put(DAP, DMD);

			}
		
			return DMD;
		}
		
		//Build dendrogram
		protected DadesExternes BuildDendrogram(DissimilarityMatrixData DMD){
			
			//display-related
			setProgress(0);
			int Counter = 0;
			
			progressBar.setIndeterminate(true);
			setProgress(100);
			
			//initialize matrix
			DadesExternes de = null;
			MatriuDistancies M = null;
			try {
				
				//attempt to retrieve matrix
				for (DissimilarityMatrixData DAM2 : TQ.getContextForests().keySet()){
					if (DAM2.equals(DMD)){
						M = TQ.getContextForests().get(DMD);
					}
				}
				
				//if the matrix does not already exist, need to calculate it
				if (M == null){
					
					//create a DE out of dissimilarity matrix
					de = new DadesExternes(DMD);
					M = de.getMatriuDistancies();

					//get distances, compute dendrogram
					double minBase = Double.MAX_VALUE;
					int nbElements = M.getCardinalitat();
					
					Reagrupa rg;
					MatriuDistancies mdNew;
					double b;
					int progress = 0;
					int ItCounter = 0;
					
					progressBar.setIndeterminate(false);
					setProgress(0);
					
					long StartTime = System.nanoTime();
					
					while (M.getCardinalitat() > 1) {
						
						long ElapsedTime = (System.nanoTime()-StartTime)/(1000000000);
			
						
						ItCounter++;
//						System.out.println("Iteration: " + ItCounter 
//								+ " Cardinality: " + M.getCardinalitat() 
//								+ " Time: " + ElapsedTime);
						
						//CLUSTERING FROM DISTANCES DATA
						rg = new Reagrupa(M, Jpan_Menu.getTypeData(), 
								Jpan_Menu.getMethod(),
								Jpan_Menu.getPrecision());
						
						mdNew = rg.Recalcula();
						
						//SET THE CURRENT MULTIDENDROGRAM TO THE RESULT FROM RG.RECALCULA()
						M = mdNew;
						de.setMatriuDistancies(M);
						
						b = M.getArrel().getBase();
						if ((b < minBase) && (b != 0)) {
							minBase = b;
						}
					
						progress = 100 * (nbElements - M.getCardinalitat())
								/ (nbElements - 1);
						
						//System.out.println("Progress: " + progress);
						
						setProgress(progress);	

					}
				}

			} catch (Exception ex){
				ex.printStackTrace();
			}
			
			return de;
		}
		
		//Extend Data Matrix
		protected LinkedList<String> Triangle2Matrix(LinkedList<String> Triangle){
			
			//Initialize
			LinkedList<String> Complete = new LinkedList<String>();
			
			String[][] EntryMatrix = new String[Triangle.size()][Triangle.size()+1];
			
			//constant for # non-name columns, # rows
			int L = Triangle.size();
			
			for (int i = 0; i < Triangle.size(); i++){
				String[] Tabs = Triangle.get(i).split("\t");
				
				//add name
				EntryMatrix[i][0] = Tabs[0];
				
				//zero entries along diagonal
				EntryMatrix[i][i+1] = "0.0";
				
				//add entries
				if (Tabs.length > 1){
					for (int j = 1; j < Tabs.length; j++){
						
						EntryMatrix[i][(L-j+1)] = Tabs[j];
						EntryMatrix[L-j][i+1] = Tabs[j];
					}
				}
				
			}

			for (int i = 0; i < Triangle.size(); i++){
				String str = "";
				for (int j = 0; j < Triangle.size()+1; j++){
					if (j == 0){
						str = EntryMatrix[i][j];
					} else {
						str = str + "\t" + EntryMatrix[i][j];
					}
				}
				Complete.add(str);
			}
			
			return Complete;
		}
		
		// ----- post-processing----------- //
		
		//post-processing
		public void done(){
			
			//switch cursor to normal
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			//re-set progress bar
			progressBar.setValue(0);

			//launch new window
			new FrmScanOutputWindow(f, TQ, ComparisonName, true, de);
			
			//close window
			dispose();
			
		}
		
	}
		
	//Panel components
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
		 * SELECT QUERY SET AND DATA GROUPING
		 */
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblDGSettings = new JLabel(strLblDGSettings);
		LblDGSettings.setBackground(Color.GRAY);
		LblDGSettings.setOpaque(true);
		jp.add(LblDGSettings,c);
		gridy++;
		
		//Select QS
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblQuerySet = new JTextField(strLblQuerySet);
		LblQuerySet.setEditable(false);
		jp.add(LblQuerySet, c);
		
		//QS drop-down menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		QSMenu = new JComboBox<String>(LoadedQSs);
		jp.add(QSMenu, c);
		gridy++;
		
		//Select DG
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblSelectDG = new JTextField(strLblSelectDiss);
		LblSelectDG.setEditable(false);
		jp.add(LblSelectDG, c);
		
		//DG drop-down menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		CFDissimilarities = new JComboBox<String>(LoadedDissimilarities);
		jp.add(CFDissimilarities, c);
		gridy++;
		
//		//Label - select Data Grouping Type
//		c.gridx = 0;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.insets = lblIns;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 1;
//		LblSelectDGType = new JTextField(strLblSelectDGType);
//		LblSelectDGType.setEditable(false);
//		jp.add(LblSelectDGType, c);
//		
//		//Initialize Radio buttons
//		rbSpecies = new JRadioButton(strrbSpecies);
//		rbGene = new JRadioButton(strrbGene);
//		BG = new ButtonGroup();
//		BG.add(rbSpecies);
//		BG.add(rbGene);
//		
//		//rb species option
//		c.gridx = 1;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 1;
//		rbSpecies.setSelected(true);
//		jp.add(rbSpecies, c);
//		
//		//rb gene option
//		c.gridx = 2;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 1;
//		rbGene.setSelected(false);
//		jp.add(rbGene, c);
//		gridy++;
		
		/*
		 * RUN PARAMETER SETTINGS
		 */
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblRunSettings = new JLabel(strLblRunSettings);
		LblRunSettings.setBackground(Color.GRAY);
		LblRunSettings.setOpaque(true);
		jp.add(LblRunSettings,c);
		gridy++;
		
		//Initialize radio buttons + button groups
		BGAdj = new ButtonGroup();
		rbMisMatch = new JRadioButton(strrbMisMatch);
		rbScaleFactor = new JRadioButton(strrbScaleFactor);
		BGAdj.add(rbMisMatch);
		BGAdj.add(rbScaleFactor);
		MisMatchGroup = new LinkedList<Component>();
		NoMMPenaltySubGroup = new LinkedList<Component>();
		ScaleFactorGroup = new LinkedList<Component>();
		
		//Penalty step
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblAdjustmentPenalty = new JTextField(strLblAdjustmentPenalty);
		LblAdjustmentPenalty.setEditable(false);
		jp.add(LblAdjustmentPenalty, c);
		gridy++;
		
		/*
		 * MISMATCH GROUP
		 */
		
//		//Mismatch radio button group
//		c.gridx = 0;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.insets =Ind1Ins;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 3;
//		rbMisMatch.setSelected(true);
//		rbMisMatch.addActionListener(this);
//		jp.add(rbMisMatch, c);
//		gridy++;
		
		//Mismatch check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets =Ind1Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		cbMisMatchPenalty = new JCheckBox(strcbMisMatchPenalty);
		cbMisMatchPenalty.setSelected(true);
		cbMisMatchPenalty.addActionListener(this);
		jp.add(cbMisMatchPenalty, c);
		gridy++;
		
		//Lbl - mismatch penalty
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind1Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblPenaltyperMM = new JTextField(strLblPenaltyperMM);
		LblPenaltyperMM.setEditable(false);
		MisMatchGroup.add(LblPenaltyperMM);
		jp.add(LblPenaltyperMM, c);
		
		//Txt - mismatch penalty
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtPenaltyperMM = new JTextField(strTxtPenaltyperMM);
		TxtPenaltyperMM.setEditable(true);
		MisMatchGroup.add(TxtPenaltyperMM);
		jp.add(TxtPenaltyperMM, c);
		gridy++;
		
		//check box - enable free mismatches
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind2Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		cbAllowMM = new JCheckBox(strcbAllowMM);
		cbAllowMM.setSelected(true);
		cbAllowMM.addActionListener(this);
		MisMatchGroup.add(cbAllowMM);
		jp.add(cbAllowMM, c);
		gridy++;
		
		//Lbl - free mismatches
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind2Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblFreeMisMatches = new JTextField(strLblFreeMisMatches);
		LblFreeMisMatches.setEditable(false);
		MisMatchGroup.add(LblFreeMisMatches);
		NoMMPenaltySubGroup.add(LblFreeMisMatches);
		jp.add(LblFreeMisMatches, c);
		
		//value - specify number of free mismatches
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		TxtFreeMisMatches = new JTextField(strTxtFreeMisMatches);
		TxtFreeMisMatches.setEditable(true);
		MisMatchGroup.add(TxtFreeMisMatches);
		NoMMPenaltySubGroup.add(TxtFreeMisMatches);
		jp.add(TxtFreeMisMatches, c);
		gridy++;

		
//		/*
//		 * SCALE FACTOR GROUP
//		 */
//		
//		//Radio buttons
//		BGDiceJaccard = new ButtonGroup();
//		rbDice = new JRadioButton(strrbDice);
//		rbJaccard = new JRadioButton(strrbJaccard);
//		BGDiceJaccard.add(rbDice);
//		BGDiceJaccard.add(rbJaccard);
//		
//		//Scale Factor group
//		c.gridx = 0;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.insets = Ind1Ins;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 3;
//		rbScaleFactor.setSelected(false);
//		rbScaleFactor.addActionListener(this);
//		jp.add(rbScaleFactor, c);
//		gridy++;
//		
//		//Dice radio button
//		c.gridx = 0;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.insets = Ind2Ins;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 1;
//		rbDice.setSelected(true);
//		ScaleFactorGroup.add(rbDice);
//		jp.add(rbDice, c);
//		
//		//Jaccard radio button
//		c.gridx = 1;
//		c.gridy = gridy;
//		c.gridheight = 1;
//		c.insets = lblIns;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridwidth = 1;
//		rbJaccard.setSelected(false);
//		ScaleFactorGroup.add(rbJaccard);
//		jp.add(rbJaccard, c);
//		gridy++;
		
		/*
		 * SEGMENTATION VALUE
		 */
		
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblSegmentationValue = new JTextField(strLblSegmentationValue);
		LblSegmentationValue.setEditable(false);
		jp.add(LblSegmentationValue, c);
		gridy++;
		
		//Value label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind1Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblSegValueInner = new JTextField(strLblSegValueInner);
		LblSegValueInner.setEditable(false);
		jp.add(LblSegValueInner, c);
		
		//value text
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtSegmentationValue = new JTextField(strTxtSegmentationValue);
		TxtSegmentationValue.setEditable(true);
		jp.add(TxtSegmentationValue, c);
		gridy++;
		
		
		/*
		 * SUBMIT
		 */
		
		jp2 = new JPanel();
		jp2.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.CENTER;
		
		//Label
		//Label
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		LblSubmit = new JLabel(strLblSubmit);
		LblSubmit.setBackground(Color.GRAY);
		LblSubmit.setOpaque(true);
		jp2.add(LblSubmit,c);
		gridy++;
		
		//button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp2.add(btnOK, c);
		gridy++;
		
		//progressbar
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.insets = downIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		jp2.add(progressBar, c);
		
		/*
		 * ADD TO FRAME
		 */
		
		//add to frame
		jpEnclosing = new JPanel();
		jpEnclosing.setLayout(new BorderLayout());
		jpEnclosing.add(jp, BorderLayout.NORTH);
		jpEnclosing.add(jp2, BorderLayout.SOUTH);
		this.add(jpEnclosing);
		
	}
	
	//Whole Frame
	public void getFrame(){
		this.setSize(620,450);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Select Data Grouping and Analysis Parameters");
		this.setResizable(true);
	}

	//convert available DGs to menu
	public void BuildMenus(){
		//build QS menu
		LinkedList<QuerySet> QS = f.getOS().getQuerySets();
		LoadedQSs = new String[QS.size()];
		for (int i = 0; i < LoadedQSs.length; i++){
			QuerySet Q = QS.get(i);
			LoadedQSs[i] = Q.getName();
		}
		
		//build Dissimilarities menu
		LoadedDissimilarities = new String[1];
		LoadedDissimilarities[0] = "Fowlkes-Mallows";
	}

	// Action methods

	//action performed
	@Override
	public void actionPerformed(ActionEvent e) {
		
		/*
		 * ENABLING / DISABLING COMPONENTS
		 */
		
		//mismatch penalty radio button
		if (e.getSource().equals(rbMisMatch) || e.getSource().equals(rbScaleFactor) || e.getSource().equals(cbMisMatchPenalty)){
			EnableComponents(MisMatchGroup,rbMisMatch.getModel().isSelected());
			EnableComponents(MisMatchGroup, cbMisMatchPenalty.isSelected());
			EnableComponents(NoMMPenaltySubGroup,cbAllowMM.isSelected());
			//EnableComponents(ScaleFactorGroup,rbScaleFactor.getModel().isSelected());
			if (!cbMisMatchPenalty.isSelected()){
				EnableComponents(NoMMPenaltySubGroup,false);
			}
		}
		
		//penalty component enabling
		if (e.getSource().equals(cbAllowMM)){
			EnableComponents(NoMMPenaltySubGroup,cbAllowMM.isSelected());
		}
		
		/*
		 * EXECUTE SCAN
		 */
		if (e.getSource().equals(btnOK)){
			
			try {
				
				//retrieve parameters
				SegmentationValue = Double.parseDouble(TxtSegmentationValue.getText());
				
				//Optional parameters (when appropriate), w appropriat exceptions
				//if (rbMisMatch.getModel().isSelected()){
				if (cbMisMatchPenalty.isSelected()){
					PenaltyPerMismatch = Double.parseDouble(TxtPenaltyperMM.getText());
					if (PenaltyPerMismatch > 1.0 || PenaltyPerMismatch < 0.0){
						throw new Exception();
					}
					if (cbAllowMM.isSelected()){
						NumMismatches = Integer.parseInt(TxtFreeMisMatches.getText());
						if (NumMismatches < 0) {
							throw new Exception();
						}
					}
				}
				
				//throw exceptions, if necessary
				if (SegmentationValue > 1.0 || SegmentationValue < 0.0){
					throw new Exception();
				}
				
				//new data groupings worker, to compute Adjusted Fowlkes-Mallows index.
				ContextForestWorker DGW = new ContextForestWorker(SegmentationValue);
				DGW.addPropertyChangeListener(this);
				DGW.execute();
						
			} catch (Exception ex){
				JOptionPane.showMessageDialog(null, "Numerical value format or out of bounds error.\n" +
						"Change numerical fields and try again.",
						"Number Format Error",JOptionPane.ERROR_MESSAGE);
			}
			//TODO
			
		}

	}
	
	//component selection
	public void EnableComponents(LinkedList<Component> C, boolean value){
		for (Component c : C){
			c.setEnabled(value);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
	
	// Getters and Setters
	
	public FrmPrincipalDesk getF() {
		return f;
	}

	public void setF(FrmPrincipalDesk f) {
		this.f = f;
	}

}

