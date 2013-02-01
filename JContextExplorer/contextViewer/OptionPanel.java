package contextViewer;

import genomeObjects.CSDisplayData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionPanel extends JPanel implements ActionListener{

	//serial ID
	private static final long serialVersionUID = 1L;

	//info from parent frame
	private mainFrame mf;
	
	//dimensions of this frame
	private Dimension opdim;
	
	//GUI Components
	private JTextField showClick, showRange, showOptions;
	private JTextField beforeRange, beforeRangeValue, beforent, afterRange, afterRangeValue, afternt;
	private JCheckBox cbStart, cbStop, cbSize, cbType, cbClusterID, cbAnnotation;
	private JCheckBox cbCoordinates, cbShowSurrounding, cbColorSurrounding, cbStrandNormalize;
	private JButton btnUpdateContexts;
	
	//labels
	//headings
	private String strshowClick = " GENE INFORMATION ";
	private String strshowRange = " RANGE AROUND CONTEXT SEGMENT ";
	private String strshowOptions = " GENOME DISPLAY ";
	
	//gene info to show on click
	private String strcbStart = "Start";
	private String strcbStop = "Stop";
	private String strcbSize = "Size";
	private String strcbType = "Type";
	private String strcbClusterID = "Cluster ID";
	private String strcbAnnotation = "Annotation";
	private String strbtnUpdateContexts = "Update Contexts";
	
	//affect genome display settings
	private String strcbCoordinates = "Show Coordinates";
	private String strcbShowSurrounding = "Show Surrounding";
	private String strcbColorSurrounding = "Color Surrounding";
	private String strcbStrandNormalize = "Strand Normalize";
	
	//re-draw genomic segments
	private String strbeforeRange = "Before: ";
	//private String strbeforeRangeValue = "2000";
	private String strbeforeRangeValue = "200";
	private String strafterRange = "After: ";
	//private String strafterRangeValue = "2000";
	private String strafterRangeValue = "200";
	private String strnt = "nt";	
	
	//font
	private Font fontStandard = new Font("Dialog", Font.BOLD, 12);
	
	//biological info
	private CSDisplayData CSD;
	
	OptionPanel(mainFrame mfr){
		super();
		this.mf = mfr;
		//this.dim = d;
		this.CSD = mfr.getCSD();
		getPanel();
		this.opdim = this.getPreferredSize();
	}

	//create components
	private void getPanel() {

		//initialize panel
		this.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(""));
		final GridBagConstraints c = new GridBagConstraints();
		
		//GridBagLayout:
		// 3 rows, 12 columns
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(1,1,1,1);
		
		// Show on click header
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		showClick = new JTextField();
		showClick.setText(strshowClick);
		showClick.setEditable(false);
		showClick.setFont(fontStandard);
		showClick.setBackground(Color.GRAY);
		showClick.setOpaque(true);
		add(showClick, c);
		
		// Display Options header
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		showOptions = new JTextField();
		showOptions.setText(strshowOptions);
		showOptions.setEditable(false);
		showOptions.setFont(fontStandard);
		showOptions.setBackground(Color.GRAY);
		showOptions.setOpaque(true);
		add(showOptions, c);
		
		// Display CS range header
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 7;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		showRange = new JTextField();
		showRange.setText(strshowRange); // Enter search bar
		showRange.setEditable(false);
		showRange.setFont(fontStandard);
		showRange.setBackground(Color.GRAY);
		showRange.setOpaque(true);
		add(showRange, c);
		
		//check box series 1: display gene options
		//check box: start
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbStart = new JCheckBox(strcbStart);
		cbStart.setSelected(false);
		cbStart.addActionListener(this);
		cbStart.setFont(fontStandard);
		add(cbStart, c);
		
		//check box: stop
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbStop = new JCheckBox(strcbStop);
		cbStop.setSelected(false);
		cbStop.addActionListener(this);
		cbStop.setFont(fontStandard);
		add(cbStop, c);
		
		//check box: size
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbSize = new JCheckBox(strcbSize);
		cbSize.setSelected(true);
		cbSize.addActionListener(this);
		cbSize.setFont(fontStandard);
		add(cbSize, c);
		
		//check box: type
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbType = new JCheckBox(strcbType);
		cbType.setSelected(false);
		cbType.addActionListener(this);
		cbType.setFont(fontStandard);
		add(cbType, c);
		
		//check box: clusterID
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbClusterID = new JCheckBox(strcbClusterID);
		cbClusterID.addActionListener(this);
		cbClusterID.setFont(fontStandard);
		cbClusterID.setSelected(true);
		add(cbClusterID, c);
		
		//check box: annotation
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbAnnotation = new JCheckBox(strcbAnnotation);
		cbAnnotation.setSelected(true);
		cbAnnotation.addActionListener(this);
		cbAnnotation.setFont(fontStandard);
		add(cbAnnotation, c);

		//check box series 2: display context range options
		//check box: show coordinates
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbCoordinates = new JCheckBox(strcbCoordinates);
		cbCoordinates.setSelected(false);
		cbCoordinates.addActionListener(this);
		cbCoordinates.setFont(fontStandard);
		add(cbCoordinates, c);
		
		//check box: strand-normalize
		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbStrandNormalize = new JCheckBox(strcbStrandNormalize);
		cbStrandNormalize.setSelected(false);
		cbStrandNormalize.addActionListener(this);
		cbStrandNormalize.setFont(fontStandard);
		add(cbStrandNormalize, c);
		
		//check box: show surrounding regions
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbShowSurrounding = new JCheckBox(strcbShowSurrounding);
		cbShowSurrounding.setSelected(true);
		cbShowSurrounding.addActionListener(this);
		cbShowSurrounding.setFont(fontStandard);
		add(cbShowSurrounding, c);
		
		//check box: color surrounding 
		c.gridx = 4;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		cbColorSurrounding = new JCheckBox(strcbColorSurrounding);
		cbColorSurrounding.setSelected(false);
		cbColorSurrounding.addActionListener(this);
		cbColorSurrounding.setFont(fontStandard);
		add(cbColorSurrounding, c);
		
		//label - Before:
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		beforeRange = new JTextField(strbeforeRange);
		beforeRange.setEditable(false);
		beforeRange.setFont(fontStandard);
		add(beforeRange, c);
		
		//Editable before range value box
		c.ipadx = 20;
		c.gridx = 6;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		beforeRangeValue = new JTextField(strbeforeRangeValue);
		beforeRangeValue.setEditable(true);
		beforeRangeValue.setFont(fontStandard);
		add(beforeRangeValue, c);
		c.ipady = 0;
		
		//label - nt:
		c.gridx = 7;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		beforent = new JTextField(strnt);
		beforent.setEditable(false);
		beforent.setFont(fontStandard);
		add(beforent, c);

		// empty space between text options
		c.gridx = 8;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		JLabel textspacer = new JLabel("");
		textspacer.setFont(fontStandard);
		c.ipadx = 10;
		add(textspacer, c);
		c.ipadx = 0;
		
		//label - After:
		c.gridx = 9;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		afterRange = new JTextField(strafterRange);
		afterRange.setEditable(false);
		afterRange.setFont(fontStandard);
		add(afterRange, c);
		
		//Editable after range value box
		c.ipadx = 20;
		c.gridx = 10;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		afterRangeValue = new JTextField(strafterRangeValue);
		afterRangeValue.setEditable(true);
		afterRangeValue.setFont(fontStandard);
		add(afterRangeValue, c);
		c.ipady = 0;
		
		//label - nt:
		c.gridx = 11;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		afternt = new JTextField(strnt);
		afternt.setEditable(false);
		afternt.setFont(fontStandard);
		add(afternt, c);
		
		//Update contexts button
		c.gridx = 5;
		c.gridy = 2;
		c.gridwidth = 7;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnUpdateContexts = new JButton(strbtnUpdateContexts);
		btnUpdateContexts.addActionListener(this);
		btnUpdateContexts.setFont(fontStandard);
		add(btnUpdateContexts, c);

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//check box: show or unshow surrounding genes
		if (evt.getSource().equals(cbShowSurrounding)){

			if (cbShowSurrounding.isSelected()){
				this.mf.getRgp().setShowSurrounding(true);
				this.mf.getRgp().repaint();
			} else {
				this.mf.getRgp().setShowSurrounding(false);
				this.mf.getRgp().repaint();
			}
		}
		
		//check box: set surrounding genes to color or light gray
		if (evt.getSource().equals(cbColorSurrounding)){

			if (cbColorSurrounding.isSelected()){
				this.mf.getRgp().setColorSurrounding(true);
				this.mf.getRgp().repaint();
			} else {
				this.mf.getRgp().setColorSurrounding(false);
				this.mf.getRgp().repaint();
			}
		}
		
		//check box: set surrounding genes to color or light gray
		if (evt.getSource().equals(cbStrandNormalize)){

			if (cbStrandNormalize.isSelected()){
				this.mf.getRgp().setStrandNormalize(true);
				this.mf.getRgp().repaint();
			} else {
				this.mf.getRgp().setStrandNormalize(false);
				this.mf.getRgp().repaint();
			}
		}
		
		//check box: add coordinate information
		if (evt.getSource().equals(cbCoordinates)){

			if (cbCoordinates.isSelected()){
				this.mf.getRgp().setShowCoordinates(true);
				this.mf.getRgp().repaint();
			} else {
				this.mf.getRgp().setShowCoordinates(false);
				this.mf.getRgp().repaint();
			}
		}
		
		//re-compute all segments
		if (evt.getSource().equals(btnUpdateContexts)){
			
//			RenderedGenomesPanel RGP = new RenderedGenomesPanel(this.mf);
//			this.mf.setRgp(RGP);
//			this.mf.getRgp().repaint();
			this.mf.getRgp().ReComputeWithNewSegments(beforeRangeValue.getText(), 
					afterRangeValue.getText());
			this.mf.getRgp().repaint();
		}
		
	}

	public Dimension getOpdim() {
		return opdim;
	}

	public void setOpdim(Dimension opdim) {
		this.opdim = opdim;
	}

	public String getStrbeforeRangeValue() {
		return strbeforeRangeValue;
	}

	public void setStrbeforeRangeValue(String strbeforeRangeValue) {
		this.strbeforeRangeValue = strbeforeRangeValue;
	}

	public String getStrafterRangeValue() {
		return strafterRangeValue;
	}

	public void setStrafterRangeValue(String strafterRangeValue) {
		this.strafterRangeValue = strafterRangeValue;
	}

	public JCheckBox getCbStart() {
		return cbStart;
	}

	public void setCbStart(JCheckBox cbStart) {
		this.cbStart = cbStart;
	}

	public JCheckBox getCbStop() {
		return cbStop;
	}

	public void setCbStop(JCheckBox cbStop) {
		this.cbStop = cbStop;
	}

	public JCheckBox getCbSize() {
		return cbSize;
	}

	public void setCbSize(JCheckBox cbSize) {
		this.cbSize = cbSize;
	}

	public JCheckBox getCbType() {
		return cbType;
	}

	public void setCbType(JCheckBox cbType) {
		this.cbType = cbType;
	}

	public JCheckBox getCbClusterID() {
		return cbClusterID;
	}

	public void setCbClusterID(JCheckBox cbClusterID) {
		this.cbClusterID = cbClusterID;
	}

	public JCheckBox getCbAnnotation() {
		return cbAnnotation;
	}

	public void setCbAnnotation(JCheckBox cbAnnotation) {
		this.cbAnnotation = cbAnnotation;
	}
}
