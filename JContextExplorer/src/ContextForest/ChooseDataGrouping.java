package ContextForest;

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

import moduls.frm.FrmPrincipalDesk;

public class ChooseDataGrouping extends JDialog implements ActionListener, PropertyChangeListener{

	//Fields
	//baseline
	private FrmPrincipalDesk f;
	
	//Data results
	private int NumMismatches;
	private double PenaltyPerMismatch;
	private double SegmentationValue;
	
	//GUI
	private JPanel jp, jp2, jpEnclosing;
	private JLabel LblDGSettings, LblRunSettings, LblSubmit;
	private String strLblDGSettings = " SELECT QUERY SET AND DATA GROUPING";
	private String strLblRunSettings = " DATA GROUPING CORRELATION SETTINGS";
	private String strLblSubmit = " EXECUTE DATA GROUPING CORRELATION";
	private JTextField LblQuerySet, LblSelectDG, LblSelectDGType, 
		LblAdjustmentPenalty, LblFreeMisMatches, TxtFreeMisMatches,
		LblPenaltyperMM, TxtPenaltyperMM, LblSegmentationValue, 
		LblSegValueInner, TxtSegmentationValue;

	private String strLblQuerySet = "Query Set:";
	private String strLblSelectDG = "Data Grouping:";
	private String strLblSelectDGType = "Data Grouping Type:";
	private JComboBox<String> DGMenu, QSMenu;
	private String[] LoadedDGs;
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
	private JCheckBox cbAllowMM;
	private String strcbAllowMM = "Permit some number of mismatches without penalty";
	private String strLblFreeMisMatches = "Number of free mismatches: ";
	private String strTxtFreeMisMatches = "2";
	private String strLblPenaltyperMM = "Penalty per mismatch:";
	private String strTxtPenaltyperMM = ".05";
	private String strLblSegmentationValue = "Context Tree Segmentation Point";
	private String strLblSegValueInner = "Value:";
	private String strTxtSegmentationValue = "0.5";
	private String strrbDice = "Dice's Coefficient";
	private String strrbJaccard = "Jaccard Index";
	private JButton btnOK;
	private String strbtnOK = "Execute Scan";
	private JProgressBar progressBar;
	
	//Insets
	private Insets lblIns = new Insets(1,1,1,1);
	private Insets Ind1Ins = new Insets(3,20,3,3);
	private Insets Ind2Ins = new Insets(3,40,3,3);
	private Insets basIns = new Insets(1,1,1,1);
	private Insets downIns = new Insets(5,5,20,1);
	
	//CONSTRUCTOR
	public ChooseDataGrouping(FrmPrincipalDesk f){
		
		//Initialization-type steps
		this.f = f;
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
	public class DataGroupingWorker extends SwingWorker<Void, Void>{

		//Fields
		
		//constructor
		public DataGroupingWorker(){
			
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			
			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			return null;
		}
		
		//post-processing
		public void done(){
			
			//switch cursor to normal
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			//re-set progress bar
			progressBar.setValue(0);

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
		LblSelectDG = new JTextField(strLblSelectDG);
		LblSelectDG.setEditable(false);
		jp.add(LblSelectDG, c);
		
		//DG drop-down menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = basIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		DGMenu = new JComboBox<String>(LoadedDGs);
		jp.add(DGMenu, c);
		gridy++;
		
		//Label - select Data Grouping Type
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblSelectDGType = new JTextField(strLblSelectDGType);
		LblSelectDGType.setEditable(false);
		jp.add(LblSelectDGType, c);
		
		//Initialize Radio buttons
		rbSpecies = new JRadioButton(strrbSpecies);
		rbGene = new JRadioButton(strrbGene);
		BG = new ButtonGroup();
		BG.add(rbSpecies);
		BG.add(rbGene);
		
		//rb species option
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		rbSpecies.setSelected(true);
		jp.add(rbSpecies, c);
		
		//rb gene option
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		rbGene.setSelected(false);
		jp.add(rbGene, c);
		gridy++;
		
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
		
		//Mismatch radio button group
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets =Ind1Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		rbMisMatch.setSelected(true);
		rbMisMatch.addActionListener(this);
		jp.add(rbMisMatch, c);
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
		
		//Lbl - mismatch penalty
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind2Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblPenaltyperMM = new JTextField(strLblPenaltyperMM);
		LblPenaltyperMM.setEditable(false);
		MisMatchGroup.add(LblPenaltyperMM);
		jp.add(LblPenaltyperMM, c);
		
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
		
		/*
		 * SCALE FACTOR GROUP
		 */
		
		//Radio buttons
		BGDiceJaccard = new ButtonGroup();
		rbDice = new JRadioButton(strrbDice);
		rbJaccard = new JRadioButton(strrbJaccard);
		BGDiceJaccard.add(rbDice);
		BGDiceJaccard.add(rbJaccard);
		
		//Scale Factor group
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind1Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		rbScaleFactor.setSelected(false);
		rbScaleFactor.addActionListener(this);
		jp.add(rbScaleFactor, c);
		gridy++;
		
		//Dice radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = Ind2Ins;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		rbDice.setSelected(true);
		ScaleFactorGroup.add(rbDice);
		jp.add(rbDice, c);
		
		//Jaccard radio button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = lblIns;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		rbJaccard.setSelected(false);
		ScaleFactorGroup.add(rbJaccard);
		jp.add(rbJaccard, c);
		gridy++;
		
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
		this.setSize(820,530);
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
		
		//build DG menu
		Set<String> DGKeys  = f.getOS().getDataGroups().keySet();
		LoadedDGs = new String[DGKeys.size()];
		int Counter = 0;
		for (String s : DGKeys){
			LoadedDGs[Counter] = s;
			Counter++;
		}
	}

	// Action methods

	//action performed
	@Override
	public void actionPerformed(ActionEvent e) {
		
		/*
		 * ENABLING / DISABLING COMPONENTS
		 */
		
		//mismatch penalty radio button
		if (e.getSource().equals(rbMisMatch) || e.getSource().equals(rbScaleFactor)){
			EnableComponents(MisMatchGroup,rbMisMatch.getModel().isSelected());
			EnableComponents(NoMMPenaltySubGroup,cbAllowMM.isSelected());
			EnableComponents(ScaleFactorGroup,rbScaleFactor.getModel().isSelected());
			if (rbScaleFactor.getModel().isSelected()){
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
				if (rbMisMatch.getModel().isSelected()){
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
				DataGroupingWorker DGW = new DataGroupingWorker();
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
