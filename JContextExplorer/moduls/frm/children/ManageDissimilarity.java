package moduls.frm.children;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import moduls.frm.FrmPrincipalDesk;

public class ManageDissimilarity extends JDialog implements ActionListener{

	//Fields
	private FrmPrincipalDesk f;
	private JPanel jp;
	
	//GUI fields
	//Name/intro
	private JLabel Add;
	private JTextField DMNameLabel, DMName;
	private String strDMNameLabel = "Enter Name: ";
	
	//Options
	private JLabel LblFactor, LblWeight, LblImportance, LblAmalgamation;
	private String strLblFactor = " FACTOR:";
	private String strLblWeight = " WEIGHT";
	private String strLblImportance = " IMPORTANCE";
	private String strAmalgamation = " AMALGAMATION TYPE:";
	private JRadioButton radLinear;
	private JRadioButton radScaleHierarchy;
	private String strradLinear = "Linear";
	private String strradScaleHierarchy = "Scale Hierarchy";
	private ButtonGroup AmalgamationType;
	
	//checkboxes
	private JCheckBox chkCommonGenes;
	private JCheckBox chkCommonMotifs;
	private JCheckBox chkGeneOrder;
	private JCheckBox chkGeneGaps;
	private JCheckBox chkStrandedness;
	private String strCommonGenes = "Presence / absence of common genes";
	private String strCommonMotifs = "Presence / absence of common motifs";
	private String strGeneOrder = "Changes in gene order";
	private String strGeneGaps = "Changes in intragenic gap size";
	private String strStrandedness = "Changes in strandedness";
	private LinkedList<Component> grpCommonGenes = new LinkedList<Component>();
	private LinkedList<Component> grpCommonMotifs = new LinkedList<Component>();
	private LinkedList<Component> grpGeneOrder = new LinkedList<Component>();
	private LinkedList<Component> grpGeneGaps = new LinkedList<Component>();
	private LinkedList<Component> grpStrandedness = new LinkedList<Component>();
	
	//amalgamation technique - related
	private LinkedList<Component> grpLinear = new LinkedList<Component>();
	private LinkedList<Component> grpScaleHierarchy = new LinkedList<Component>();
	private String TxtLinear = "Weight:"; 
	private String TxtScale = "Importance:";
	
	//(1) COMMON GENES
	private JTextField LblcgWeight, TxtcgWeight, LblcgScale, TxtcgScale;
	private String strTxtcgWeight = "0.3";
	private String strTxtcgScale = "1";
	private JCheckBox chkTreatDuplicatesAsUnique;
	private String strTreatDuplicatesAsUnique = "Treat duplicate genes as unique";
	private JRadioButton radDice, radJaccard;
	private String strDice = "Dice's Coefficient";
	private String strJaccard = "Jaccard Index";
	private ButtonGroup DiceOrJaccard;
	
	//(2) COMMON MOTIFS
	private JTextField LblcmWeight, TxtcmWeight, LblcmScale, TxtcmScale;
	private String strTxtcmWeight = "0.25";
	private String strTxtcmScale = "2";
	private JTextField LblSelectMotifs,LblComparisonScheme;
	private String strSelectMotifs = " Select Motifs:";
	private String strComparisonScheme = " Comparison Scheme:";
	private JPanel AvailableMotifsPanel;
	private CheckCombo AvailableMotifsBox;
	private JRadioButton radDiceMotif, radJaccardMotif;
	private ButtonGroup DiceOrJaccardMotif;
	private JCheckBox chkTreatDuplicatesAsUniqueMotif;
	private String strTreatDuplicatesAsUniqueMotif = "Treat duplicate motifs as unique";
	
	//(3) GENE ORDER
	private JTextField LblgoWeight, TxtgoWeight, LblgoScale, TxtgoScale;
	private String strTxtgoWeight = "0.2";
	private String strTxtgoScale = "3";
	private JRadioButton radCollinear, radSingle;
	private ButtonGroup CollinearOrSingle;
	private String strCollinear = "Collinear group gene reordering";
	private String strSingle = "Single gene reordering";
	
	// (4) GENE GAPS
	private JTextField LblggWeight, TxtggWeight, LblggScale, TxtggScale;
	private String strTxtggWeight = "0.15";
	private String strTxtggScale = "4";
	//TODO graph for function between gap + dissimilarity
	
	// (5) STRANDEDNESS
	private JTextField LblssWeight, TxtssWeight, LblssScale, TxtssScale;
	private String strTxtssWeight = "0.10";
	private String strTxtssScale = "5";
	private JCheckBox chkIndStrand, chkGrpStrand;
	private String strIndStrand = "Change in strandedness of individual genes";
	private String strGrpStrand = "Change in strandedness of entire group";
	private JTextField LblwtInd, LblwtGrp, TxtwtInd, TxtwtGrp;
	private String strLblwt = " Relative Weight:";
	private String strTxtwtInd = "0.5";
	private String strTxtwtGrp = "0.5";
	private int StrColNum = 10;
	
	//constructor
	public ManageDissimilarity(FrmPrincipalDesk f){
		super();
		this.f = f;
		
		this.setSize(700,700);
		this.setTitle("Manage Dissimilarity Measures");
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setModal(true);
		
		//build + initialize panel
		this.getPanel();
		DeactivateAllComponents();
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	//build panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		//Add Dissimilarity Measure Heading
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 6;
		Add = new JLabel(" ADD A CUSTOM DISSIMILARITY METRIC");
		Add.setBackground(Color.GRAY);
		Add.setOpaque(true);
		jp.add(Add,c);
		gridy++;
		
		//Name Label
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		DMNameLabel = new JTextField(strDMNameLabel);
		DMNameLabel.setEditable(false);
		jp.add(DMNameLabel,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 2;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		DMName = new JTextField("");
		DMName.setEditable(true);
		DMName.addActionListener(this);
		jp.add(DMName, c);
		gridy++;
		
		//AMALGAMATION
		//Heading
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 6;
		LblAmalgamation = new JLabel(strAmalgamation);
		LblAmalgamation.setBackground(Color.GRAY);
		LblAmalgamation.setOpaque(true);
		jp.add(LblAmalgamation,c);
		gridy++;
		
		//radio buttons - initialization
		AmalgamationType = new ButtonGroup();
		radLinear = new JRadioButton(strradLinear);
		radScaleHierarchy = new JRadioButton(strradScaleHierarchy);
		AmalgamationType.add(radLinear);
		AmalgamationType.add(radScaleHierarchy);
		radLinear.setSelected(true);
		
		//radio buttons
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		radLinear.addActionListener(this);
		jp.add(radLinear, c);
		
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.NONE;
		radScaleHierarchy.addActionListener(this);
		jp.add(radScaleHierarchy, c);
		gridy++;

		//Add Factor Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblFactor = new JLabel(strLblFactor);
		LblFactor.setBackground(Color.GRAY);
		LblFactor.setOpaque(true);
		jp.add(LblFactor,c);
		
		//Add Weight Heading
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblWeight = new JLabel(strLblWeight);
		LblWeight.setBackground(Color.GRAY);
		LblWeight.setOpaque(true);
		jp.add(LblWeight,c);
		
		//Add Importance Heading
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblImportance = new JLabel(strLblImportance);
		LblImportance.setBackground(Color.GRAY);
		LblImportance.setOpaque(true);
		gridy++;
		jp.add(LblImportance,c);
		gridy++;
		
		//checkboxes
		chkCommonGenes = new JCheckBox(strCommonGenes);
		chkCommonMotifs = new JCheckBox(strCommonMotifs);
		chkGeneOrder = new JCheckBox(strGeneOrder);
		chkGeneGaps = new JCheckBox(strGeneGaps);
		chkStrandedness = new JCheckBox(strStrandedness);
	
		//(1) COMMON GENES
		//checkbox
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		chkCommonGenes.addActionListener(this);
		jp.add(chkCommonGenes, c);
		
		//label - linear
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblcgWeight = new JTextField(TxtLinear);
		LblcgWeight.setEditable(false);
		grpCommonGenes.add(LblcgWeight);
		grpLinear.add(LblcgWeight);
		jp.add(LblcgWeight, c);

		//value - linear
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtcgWeight = new JTextField(strTxtcgWeight);
		TxtcgWeight.setEditable(true);
		grpCommonGenes.add(TxtcgWeight);
		grpLinear.add(TxtcgWeight);
		jp.add(TxtcgWeight, c);
		
		//label - scale hierarchy
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblcgScale = new JTextField(TxtScale);
		LblcgScale.setEditable(false);
		grpCommonGenes.add(LblcgScale);
		grpScaleHierarchy.add(LblcgScale);
		jp.add(LblcgScale, c);
		
		//value - scale hierarchy
		c.gridx = 5;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtcgScale = new JTextField(strTxtcgScale);
		TxtcgScale.setEditable(true);
		grpCommonGenes.add(TxtcgScale);
		grpScaleHierarchy.add(TxtcgScale);
		jp.add(TxtcgScale, c);
		gridy++;
		
		c.ipady = 0;
		
		//Dice/Jaccard radio buttons
		radDice = new JRadioButton(strDice);
		radJaccard = new JRadioButton(strJaccard);
		DiceOrJaccard = new ButtonGroup();
		DiceOrJaccard.add(radDice);
		DiceOrJaccard.add(radJaccard);
		
		//Dice option
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,20,1,1);
		radDice.setSelected(true);
		grpCommonGenes.add(radDice);
		jp.add(radDice, c);
		
		//Jaccard Option
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		radJaccard.setSelected(false);
		grpCommonGenes.add(radJaccard);
		jp.add(radJaccard, c);
		gridy++;
		
		//check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,20,1,1);
		chkTreatDuplicatesAsUnique = new JCheckBox(strTreatDuplicatesAsUnique);
		chkTreatDuplicatesAsUnique.setSelected(false);
		grpCommonGenes.add(chkTreatDuplicatesAsUnique);
		jp.add(chkTreatDuplicatesAsUnique, c);
		gridy++;
		
		//(2) COMMON MOTIFS
		c.ipady = 7;
		//checkbox
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		chkCommonMotifs.addActionListener(this);
		jp.add(chkCommonMotifs, c);
		
		//label - linear
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblcmWeight = new JTextField(TxtLinear);
		LblcmWeight.setEditable(false);
		grpCommonMotifs.add(LblcmWeight);
		grpLinear.add(LblcmWeight);
		jp.add(LblcmWeight, c);

		//value - linear
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtcmWeight = new JTextField(strTxtcmWeight);
		TxtcmWeight.setEditable(true);
		grpCommonMotifs.add(TxtcmWeight);
		grpLinear.add(TxtcmWeight);
		jp.add(TxtcmWeight, c);
		
		//label - scale hierarchy
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblcmScale = new JTextField(TxtScale);
		LblcmScale.setEditable(false);
		grpCommonMotifs.add(LblcmScale);
		grpScaleHierarchy.add(LblcmScale);
		jp.add(LblcmScale, c);
		
		//value - scale hierarchy
		c.gridx = 5;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtcmScale = new JTextField(strTxtcmScale);
		TxtcmScale.setEditable(true);
		grpCommonMotifs.add(TxtcmScale);
		grpScaleHierarchy.add(TxtcmScale);
		jp.add(TxtcmScale, c);
		
		gridy++;
		c.ipady = 0;
		
		//Label: Select motifs
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.insets = new Insets(1,20,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		LblSelectMotifs = new JTextField(strSelectMotifs);
		LblSelectMotifs.setEditable(false);
		LblSelectMotifs.setBorder(null);
		grpCommonMotifs.add(LblSelectMotifs);
		jp.add(LblSelectMotifs, c);

		//Drop-down check box menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.NONE;
		AvailableMotifsBox = new CheckCombo(this.f.getPanMotifOptions().getLoadedMotifs());
		AvailableMotifsPanel = AvailableMotifsBox.getContent();
		grpCommonMotifs.add(AvailableMotifsPanel);
		grpCommonMotifs.add(AvailableMotifsBox);
		jp.add(AvailableMotifsPanel, c);
		gridy++;
		
		//Label: comparison scheme
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(1,20,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		LblComparisonScheme = new JTextField(strComparisonScheme);
		LblComparisonScheme.setEditable(false);
		LblComparisonScheme.setBorder(null);
		grpCommonMotifs.add(LblComparisonScheme);
		jp.add(LblComparisonScheme, c);
		gridy++;

		//Dice/Jaccard radio buttons
		radDiceMotif = new JRadioButton(strDice);
		radJaccardMotif = new JRadioButton(strJaccard);
		DiceOrJaccardMotif = new ButtonGroup();
		DiceOrJaccardMotif.add(radDiceMotif);
		DiceOrJaccardMotif.add(radJaccardMotif);
		
		//Dice option
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,20,1,1);
		radDiceMotif.setSelected(true);
		grpCommonMotifs.add(radDiceMotif);
		jp.add(radDiceMotif, c);
		
		//Jaccard Option
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		radJaccardMotif.setSelected(false);
		grpCommonMotifs.add(radJaccardMotif);
		jp.add(radJaccardMotif, c);
		gridy++;
		
		//check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,20,1,1);
		chkTreatDuplicatesAsUniqueMotif = new JCheckBox(strTreatDuplicatesAsUniqueMotif);
		chkTreatDuplicatesAsUniqueMotif.setSelected(false);
		grpCommonMotifs.add(chkTreatDuplicatesAsUniqueMotif);
		jp.add(chkTreatDuplicatesAsUniqueMotif, c);
		gridy++;

		//(3) GENE ORDER
		c.ipady = 7;
		
		//checkbox
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		chkGeneOrder.addActionListener(this);
		jp.add(chkGeneOrder, c);
		
		//label - linear
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblgoWeight = new JTextField(TxtLinear);
		LblgoWeight.setEditable(false);
		grpGeneOrder.add(LblgoWeight);
		grpLinear.add(LblgoWeight);
		jp.add(LblgoWeight, c);

		//value - linear
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtgoWeight = new JTextField(strTxtgoWeight);
		TxtgoWeight.setEditable(true);
		grpGeneOrder.add(TxtgoWeight);
		grpLinear.add(TxtgoWeight);
		jp.add(TxtgoWeight, c);
		
		//label - scale hierarchy
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblgoScale = new JTextField(TxtScale);
		LblgoScale.setEditable(false);
		grpGeneOrder.add(LblgoScale);
		grpScaleHierarchy.add(LblgoScale);
		jp.add(LblgoScale, c);
		
		//value - scale hierarchy
		c.gridx = 5;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtgoScale = new JTextField(strTxtgoScale);
		TxtgoScale.setEditable(true);
		grpGeneOrder.add(TxtgoScale);
		grpScaleHierarchy.add(TxtgoScale);
		jp.add(TxtgoScale, c);
		gridy++;
		
		c.ipady = 0;
		
		//button group for single/collinear insertion type
		radCollinear = new JRadioButton(strCollinear);
		radSingle = new JRadioButton(strSingle);
		CollinearOrSingle = new ButtonGroup();
		CollinearOrSingle.add(radCollinear);
		CollinearOrSingle.add(radSingle);

		//add collinear group option to panel
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,20,1,1);
		radCollinear.setSelected(true);
		grpGeneOrder.add(radCollinear);
		jp.add(radCollinear, c);
		gridy++;
		
		//add single gene insertion option to panel
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,20,1,1);
		radSingle.setEnabled(true);
		grpGeneOrder.add(radSingle);
		jp.add(radSingle, c);
		gridy++;

		//(4) GENE GAPS
		//checkbox
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		chkGeneGaps.addActionListener(this);
		jp.add(chkGeneGaps, c);
		
		//label - linear
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblggWeight = new JTextField(TxtLinear);
		LblggWeight.setEditable(false);
		grpGeneGaps.add(LblggWeight);
		grpLinear.add(LblggWeight);
		jp.add(LblggWeight, c);

		//value - linear
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtggWeight = new JTextField(strTxtggWeight);
		TxtggWeight.setEditable(true);
		grpGeneGaps.add(TxtggWeight);
		grpLinear.add(TxtggWeight);
		jp.add(TxtggWeight, c);
		
		//label - scale hierarchy
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblggScale = new JTextField(TxtScale);
		LblggScale.setEditable(false);
		grpGeneGaps.add(LblggScale);
		grpScaleHierarchy.add(LblggScale);
		jp.add(LblggScale, c);
		
		//value - scale hierarchy
		c.gridx = 5;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtggScale = new JTextField(strTxtggScale);
		TxtggScale.setEditable(true);
		grpGeneGaps.add(TxtggScale);
		grpScaleHierarchy.add(TxtggScale);
		jp.add(TxtggScale, c);
		gridy++;
		
		//(5) STRANDEDNESS
		//checkbox
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,1,1,1);
		c.fill = GridBagConstraints.NONE;
		chkStrandedness.addActionListener(this);
		jp.add(chkStrandedness, c);
		
		//label - linear
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblssWeight = new JTextField(TxtLinear);
		LblssWeight.setEditable(false);
		grpStrandedness.add(LblssWeight);
		grpLinear.add(LblssWeight);
		jp.add(LblssWeight, c);

		//value - linear
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtssWeight = new JTextField(strTxtssWeight);
		TxtssWeight.setEditable(true);
		grpStrandedness.add(TxtssWeight);
		grpLinear.add(TxtssWeight);
		jp.add(TxtssWeight, c);
		
		//label - scale hierarchy
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		LblssScale = new JTextField(TxtScale);
		LblssScale.setEditable(false);
		grpStrandedness.add(LblssScale);
		grpScaleHierarchy.add(LblssScale);
		jp.add(LblssScale, c);
		
		//value - scale hierarchy
		c.gridx = 5;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		TxtssScale = new JTextField(strTxtssScale);
		TxtssScale.setEditable(true);
		grpStrandedness.add(TxtssScale);
		grpScaleHierarchy.add(TxtssScale);
		jp.add(TxtssScale, c);
		
		gridy++;
		c.ipady = 0;
		
		//individual element strandedness option
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(1,20,1,1);
		chkIndStrand = new JCheckBox(strIndStrand);
		grpStrandedness.add(chkIndStrand);
		jp.add(chkIndStrand, c);
		gridy++;
		
		//relative weights
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(1,40,1,1);
		LblwtInd = new JTextField(strLblwt);
		LblwtInd.setBorder(null);
		LblwtInd.setEditable(false);
		grpStrandedness.add(LblwtInd);
		jp.add(LblwtInd, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.NONE;
		TxtwtInd = new JTextField(strTxtwtInd);
		TxtwtInd.setEditable(true);
		TxtwtInd.setColumns(StrColNum);
		grpStrandedness.add(TxtwtInd);
		jp.add(TxtwtInd, c);
		gridy++;
		
		//whole group strandedness option
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(1,20,1,1);
		chkGrpStrand = new JCheckBox(strGrpStrand);
		grpStrandedness.add(chkGrpStrand);
		jp.add(chkGrpStrand, c);
		gridy++;
		
		//relative weights
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(1,40,1,1);
		LblwtGrp = new JTextField(strLblwt);
		LblwtGrp.setBorder(null);
		LblwtGrp.setEditable(false);
		grpStrandedness.add(LblwtGrp);
		jp.add(LblwtGrp, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.NONE;
		TxtwtGrp = new JTextField(strTxtwtGrp);
		TxtwtGrp.setEditable(true);
		TxtwtGrp.setColumns(StrColNum);
		grpStrandedness.add(TxtwtGrp);
		jp.add(TxtwtGrp, c);
		gridy++;
		
		this.add(jp);
	}

	
	
	// ----- Component State Switching -------------------//
	public void SwitchStateComponents(LinkedList<Component> list, boolean SwitchState){
		//adjust component states
		if (SwitchState){
			for (Component C : list){
				//Components associated with amalgamation options
				if (AmalgamationType.isSelected(radLinear.getModel())){
					if (grpLinear.contains(C)){
						C.setEnabled(true);
					}
				} else if (AmalgamationType.isSelected(radScaleHierarchy.getModel())){
					if (grpScaleHierarchy.contains(C)){
						C.setEnabled(true);
					}
				}
				
				//all others
				if (!grpLinear.contains(C) && !grpScaleHierarchy.contains(C)){
					C.setEnabled(true);
				}
			}
		} else {
			for (Component C : list){
				C.setEnabled(false);
			}
		}
	}

	public void ActivateIfEnabled(LinkedList<Component> list){
		for (Component c : list){
			if (grpCommonGenes.contains(c)){
				if (chkCommonGenes.isSelected()){
					c.setEnabled(true);
				}
			} else if (grpCommonMotifs.contains(c)){
				if (chkCommonMotifs.isSelected()){
					c.setEnabled(true);
				}
			} else if (grpGeneOrder.contains(c)){
				if (chkGeneOrder.isSelected()){
					c.setEnabled(true);
				}
			} else if (grpGeneGaps.contains(c)){
				if (chkGeneGaps.isSelected()){
					c.setEnabled(true);
				}
			} else if (grpStrandedness.contains(c)){
				if (chkStrandedness.isSelected()){
					c.setEnabled(true);
				}
			}
		}
	}
	
	public void DeactivateAllComponents(){
		for (Component C : this.grpCommonGenes){
			C.setEnabled(false);
		}
		for (Component C: this.grpCommonMotifs){
			C.setEnabled(false);
		}
		for (Component C: this.grpGeneGaps){
			C.setEnabled(false);
		}
		for (Component C: this.grpGeneOrder){
			C.setEnabled(false);
		}
		for (Component C : this.grpStrandedness){
			C.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//activating/deactivating states
		if (evt.getSource().equals(chkCommonGenes)){
			if (chkCommonGenes.isSelected()){					//common genes
				SwitchStateComponents(grpCommonGenes, true);
			} else {
				SwitchStateComponents(grpCommonGenes, false);
			}
		} else if (evt.getSource().equals(chkCommonMotifs)){	//common motifs
			if (chkCommonMotifs.isSelected()){
				SwitchStateComponents(grpCommonMotifs, true);
			} else {
				SwitchStateComponents(grpCommonMotifs, false);
			}
		} else if (evt.getSource().equals(chkGeneOrder)){		//gene order
			if (chkGeneOrder.isSelected()){
				SwitchStateComponents(grpGeneOrder, true);
			} else{
				SwitchStateComponents(grpGeneOrder, false);
			}
		} else if (evt.getSource().equals(chkGeneGaps)){		//gene gaps
			if (chkGeneGaps.isSelected()){
				SwitchStateComponents(grpGeneGaps, true);
			} else {
				SwitchStateComponents(grpGeneGaps, false);
			}
		} else if (evt.getSource().equals(chkStrandedness)){	//strandedness
			if (chkStrandedness.isSelected()){
				SwitchStateComponents(grpStrandedness, true);
			} else{
				SwitchStateComponents(grpStrandedness, false);
			}
		} else if (evt.getSource().equals(radLinear)){
				ActivateIfEnabled(grpLinear);
				SwitchStateComponents(grpScaleHierarchy, false);
		} else if (evt.getSource().equals(radScaleHierarchy)){
				ActivateIfEnabled(grpScaleHierarchy);
				SwitchStateComponents(grpLinear, false);
		}
		
		
		
	}
}
	
