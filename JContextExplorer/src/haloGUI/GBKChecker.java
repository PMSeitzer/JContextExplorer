package haloGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import moduls.frm.FrmPrincipalDesk;

public class GBKChecker extends JFrame implements ActionListener{

	//Fields
	//universal
	private static final long serialVersionUID = 1L;
	private JPanel jp;
	private LoadGenomesPanelv2 lgp;
	private FrmPrincipalDesk f;
	
	//Introductory info
	private JTextPane Instructions;
	private Dimension InstructionDimensions = new Dimension(300,150);
	private JScrollPane InstructionPane;
	
	//Fields
	//Annotation
	private JTextField LblAnnotationTag, TxtAnnotationTag;
	private String strAnnotationLbl = "Annotation:";
	
	//Locus Tag
	private JTextField LblGeneIDTag, TxtGeneIDTag;
	private String strGeneIDTag = "Gene ID:";
	
	//Homology clusters
	private LinkedList<Component> GrpHomologyClusters;
	private JCheckBox cbParseHomologyClusters;
	private String strcbParseHomologyClusters = "Add homology clusters based on COG number";
	private JTextField LblHomologyClusterTag, TxtHomologyClusterTag;
	private String strHomologyClusterLbl = "Parse Homology Cluster ID From:";
	
	//Translation
	private JCheckBox cbTranslations;
	private String strcbTranslations = "Retain protein translations (may be memory-intensive)";
	
	//default settings
	private JButton btnDefault;
	private String strDefault = "Revert to Default Settings";
	
	//submit / return
	private JButton btnSubmit;
	private String strbtnSubmit = "Ok";
	

	//alternative constructor
	public GBKChecker(FrmPrincipalDesk f){
		
		//map to main frame
		this.f = f;
		
		//retrieve + build data
		this.getInstructions();
		this.RetrieveMapping();
		
		//create GUI object
		this.getFrame();
		this.getPanel();
		//this.pack();
		
		//enable appropriate data
		this.HomologyClustercb();
		
		//make frame visible
		this.setVisible(true);
	}

	//Build instructional message
	public void getInstructions(){
		
		// create a JTextPane + add settings
		Instructions = new JTextPane();
		Instructions.setEditable(false);
					
		//retrieve document, and add styles
		StyledDocument doc = Instructions.getStyledDocument();	        
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
        Style s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        
        //text into document
        try {
			doc.insertString(doc.getLength(), "Instructions:\n\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "Features in a genbank file are designating according to a series of tags, symbolized by a forward slash and unique text identifier: /text_identifier\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Depending on the source of the genbank file, there may be some variability in the text identifiers used, especially for gene annotation and gene ID.  In the fields below, you may customize which tags should be mapped to gene annotation and gene ID.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Homology Clusters\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "Occasionally, Genbank files may have homology clusters designated, in the form of COG groupings, or an alternative standard homology cluster ID designation.  It is possible to attempt to assign homology cluster IDs from a specified tag.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Translations\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "Genbank files contain the protein translation information for all protein-coding genes.  You may retain this information if you check the appropriate box.  However, be warned that this may be very memory-intensive, especially if your genomic set contains a large number of genomes.\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "For additional help and examples, please consult the JContextExplorer manual.", doc.getStyle("regular"));

        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	//Retrieve Mapping
	public void RetrieveMapping(){
		
		//retrieve data
		GBKFieldMapping GFM = f.getGBKFields();
		
		//annotation
		TxtAnnotationTag = new JTextField(GFM.Annotation);
		TxtAnnotationTag.setEditable(true);
	
		//gene ID
		TxtGeneIDTag = new JTextField(GFM.GeneID);
		TxtGeneIDTag.setEditable(true);
		
		//homology clusters - check box
		cbParseHomologyClusters = new JCheckBox(strcbParseHomologyClusters);
		cbParseHomologyClusters.setSelected(GFM.GetCluster);
		
		//homology clusters - value
		TxtHomologyClusterTag = new JTextField(GFM.GetClusterTag);
		TxtHomologyClusterTag.setEditable(true);
		
		//retain protein translations
		cbTranslations = new JCheckBox(strcbTranslations);
		cbTranslations.setSelected(GFM.GetTranslation);
		
	}
	
	//Create frame
	public void getFrame(){
		this.setSize(700,420);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("GBK File Import Settings");
	}
	
	//Create panel	
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		int gridx = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
				
        /*
         * Instructions / Explanations
         */

		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		InstructionPane= new JScrollPane(Instructions);
		InstructionPane.setPreferredSize(InstructionDimensions);
		Instructions.setCaretPosition(0);
		jp.add(InstructionPane, c);
		
		gridy++;
		gridx = 0;
        
        /*
         * Field Mapping
         */
		
		//Annotation Tag - label
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		LblAnnotationTag = new JTextField(strAnnotationLbl);
		LblAnnotationTag.setEditable(false);
		jp.add(LblAnnotationTag, c);
		gridx++;
		
		//Annotation Tag - text
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		jp.add(TxtAnnotationTag, c);
		gridx = 0;
		gridy++;
		
		//Locus Tag - label
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		LblGeneIDTag = new JTextField(strGeneIDTag);
		LblGeneIDTag.setEditable(false);
		jp.add(LblGeneIDTag, c);
		gridx++;
		
		//Locus Tag - text
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		jp.add(TxtGeneIDTag, c);
		gridx = 0;
		gridy++;
		
		//homology cluster ID
		GrpHomologyClusters = new LinkedList<Component>();
		
		//homology cluster - check box
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		cbParseHomologyClusters.addActionListener(this);
		jp.add(cbParseHomologyClusters, c);
		gridx = 0;
		gridy++;

		//Homology Cluster Text Fields
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		LblHomologyClusterTag = new JTextField(strHomologyClusterLbl);
		LblHomologyClusterTag.setEditable(false);
		jp.add(LblHomologyClusterTag, c);
		GrpHomologyClusters.add(LblHomologyClusterTag);
		gridx++;
		
		//homology cluster text 
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		jp.add(TxtHomologyClusterTag, c);
		GrpHomologyClusters.add(TxtHomologyClusterTag);
		gridx = 0;
		gridy++;

		//Retain protein translation option
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);

		jp.add(cbTranslations, c);
		gridx = 0;
		gridy++;
		
		/*
		 * Default settings
		 */
		
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		btnDefault = new JButton(strDefault);
		btnDefault.addActionListener(this);
		jp.add(btnDefault, c);
		gridx = 0;
		gridy++;
		
        /*
         * Submit/Return
         */
        
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.insets = new Insets(10,100,10,100);
        btnSubmit = new JButton(strbtnSubmit);
        btnSubmit.addActionListener(this);
        btnSubmit.addActionListener(lgp);
        jp.add(btnSubmit, c);
        
        //add panel to frame
        this.add(jp, BorderLayout.NORTH);
		
	}
	
	//Actions!
	@Override
	public void actionPerformed(ActionEvent e) {
				
		//Attempt to parse out homology clusters
		if (e.getSource().equals(cbParseHomologyClusters)){
			HomologyClustercb();
		}
		
		//revert to default settings
		if (e.getSource().equals(btnDefault)){
			GBKFieldMapping GFM = new GBKFieldMapping();
			f.setGBKFields(GFM);
			this.TxtAnnotationTag.setText(GFM.Annotation);
			this.TxtGeneIDTag.setText(GFM.GeneID);
			this.TxtHomologyClusterTag.setText(GFM.GetClusterTag);
			this.cbParseHomologyClusters.setSelected(false);
			this.cbTranslations.setSelected(false);
			this.HomologyClustercb();
		}
		
		//Submit / proceed
		if (e.getSource().equals(btnSubmit)){
					
			//update information
			f.getGBKFields().Annotation = this.TxtAnnotationTag.getText();
			f.getGBKFields().GeneID = this.TxtGeneIDTag.getText();
			f.getGBKFields().GetClusterTag = this.TxtHomologyClusterTag.getText();
			f.getGBKFields().GetCluster = this.cbParseHomologyClusters.isSelected();
			f.getGBKFields().GetTranslation = this.cbTranslations.isSelected();
			
			//dispose the window.
			this.dispose();
		}
		
	}
	
	//enable check boxes, or not
	public void HomologyClustercb(){
		for (Component c : this.GrpHomologyClusters){
			c.setEnabled(this.cbParseHomologyClusters.isSelected());
		}
	}

}
