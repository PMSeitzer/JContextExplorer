package haloGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import moduls.frm.FrmPrincipalDesk;

public class NCBIChecker extends JFrame implements ActionListener{

	//Fields
	private FrmPrincipalDesk f;
	
	//GUI
	private JPanel jp;
	private String strTextField = "";
	private int SrchCol = 12;
	private int RmvIndent = 11;
	private int FrameWidth = 700;
	private int FrameWidthBuffer = 30;
	
	//Introductory info
	private JTextPane Instructions;
	private Dimension InstructionDimensions = new Dimension(300,150);
	private JScrollPane InstructionPane;
	
	//Query filtering
	private LinkedList<String> IncludeTypes;
	private DefaultListModel<String> IncludeListModel;
	private JList<String> IncludeList;
	private JScrollPane IncludePane;
	private JCheckBox cbFilterbyName;
	private String strcbFilterbyName = "Query match must contain the following keywords from list";
	private JPanel IncludeInternalPanel;
	private String strIncludeInternalPanel = "Filter NCBI Queries";
	private JTextField IncludeTextField;
	private JButton IncludeAddButton;
	private JButton IncludeRemoveButton;
	
	//other settings
	private String strOtherSettingsBorder = "Other Settings";
	private JPanel OtherSettingsInternalPanel;
	private JTextField LblReturnResults, TxtReturnResults;
	private String strLblReturnResults = "Maximum number of results:";
	
	//submit / return
	private JButton btnSubmit;
	private String strbtnSubmit = "Ok";
	
	//Constructor
	public NCBIChecker(FrmPrincipalDesk f){
		this.f = f;
		this.getInstructions();
		
		//Build GUI
		this.getPanel();
		this.getFrame();
		this.pack();
		
		//make visible
		this.setVisible(true);
	}
	
	//Create Panel
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
		
        /*
         * NCBI Queries to retain
         */
		
		//Initialize list + list elements
		IncludeListModel = new DefaultListModel<String>();
		
		//Add data
		for (String s : f.getNCBIFields().Filters){
			IncludeListModel.addElement(s);
		}

		//GUI list settings
		IncludeList = new JList<String>(IncludeListModel);
		IncludeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);      
		IncludeList.setDragEnabled(true);
		IncludeList.setDropMode(DropMode.INSERT);
		IncludeList.setPreferredSize(new Dimension((FrameWidth-FrameWidthBuffer),100));
        		
		//Internal pane - initialize
        IncludeInternalPanel = new JPanel(new BorderLayout());
        IncludeInternalPanel.setBorder(BorderFactory.createTitledBorder(strIncludeInternalPanel));
        IncludeInternalPanel.setLayout(new GridBagLayout());
        final GridBagConstraints c1 = new GridBagConstraints();
        
        //Internal pane - components
        //scrollpane
        c1.insets = new Insets(1,1,1,10);
        c1.anchor = GridBagConstraints.FIRST_LINE_START;
        c1.weightx = 1;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 3;
        cbFilterbyName = new JCheckBox(strcbFilterbyName);
        cbFilterbyName.setSelected(f.getNCBIFields().ScreenResults);
        IncludeInternalPanel.add(cbFilterbyName, c1);
        
		IncludePane = new JScrollPane(IncludeList);
		//IncludePane.setPreferredSize(new Dimension((FrameWidth-FrameWidthBuffer),100));
		//IncludePane.setPreferredSize(InstructionDimensions);
		c1.gridy = 1;
        IncludeInternalPanel.add(IncludePane, c1);
        
        //search bar
        c1.gridx = 0;
        c1.gridy = 2;
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(1,1,1,1);
        c1.ipady = 7;
        IncludeTextField = new JTextField(strTextField);
        IncludeTextField.setColumns(SrchCol);
        IncludeTextField.setEditable(true);
        IncludeTextField.addActionListener(this);
        IncludeInternalPanel.add(IncludeTextField, c1);
        
        //Add button
        c1.gridx = 1;
        c1.gridy = 2;
        c1.gridwidth = 1;
        c1.ipady = 0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(1,1,1,1);
        IncludeAddButton = new JButton("Add");
        IncludeAddButton.addActionListener(this);
        IncludeInternalPanel.add(IncludeAddButton, c1);
        
        //Remove button
        c1.gridx = 2;
        c1.gridy = 2;
        c1.gridwidth = 1;
        c1.ipady = 0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(1,1,1,RmvIndent);
        IncludeRemoveButton = new JButton("Remove");
        IncludeRemoveButton.addActionListener(this);
        IncludeInternalPanel.add(IncludeRemoveButton, c1);

        //internal panel -> frame panel
        c.gridx = gridx;
        c.gridy = gridy;
        c.insets = new Insets(0,0,0,0);
        jp.add(IncludeInternalPanel,c);
        gridy++;
        
        /*
         * Other Settings
         */
        
		//Internal pane - initialize
        OtherSettingsInternalPanel = new JPanel(new BorderLayout());
        OtherSettingsInternalPanel.setBorder(BorderFactory.createTitledBorder(strOtherSettingsBorder));
        OtherSettingsInternalPanel.setLayout(new GridBagLayout());
        final GridBagConstraints c2 = new GridBagConstraints();
        
        c2.insets = new Insets(1,1,1,10);
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.weightx = 1;
        
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(1,1,1,1);
    	LblReturnResults = new JTextField(strLblReturnResults);
    	LblReturnResults.setEditable(false);
    	OtherSettingsInternalPanel.add(LblReturnResults, c2);
    	
        c2.gridx = 1;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(1,1,1,1);
    	TxtReturnResults = new JTextField(String.valueOf(f.getNCBIFields().RetMax));
    	TxtReturnResults.setEditable(true);
    	OtherSettingsInternalPanel.add(TxtReturnResults, c2);
    	
    	c.gridy = gridy;
        jp.add(OtherSettingsInternalPanel, c);
        
        gridy++;
        
        /*
         * Submit/Return
         */
        
        c.gridx = 0;
        c.gridy = gridy;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.insets = new Insets(10,100,10,100);
        btnSubmit = new JButton(strbtnSubmit);
        btnSubmit.addActionListener(this);
        jp.add(btnSubmit, c);
        
        //add panel to frame
        this.add(jp, BorderLayout.NORTH);
		
	}
	
	//Create frame
	public void getFrame(){
		this.setSize(FrameWidth,620);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("NCBI Database Search Settings");
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
			doc.insertString(doc.getLength(), "Genomes may be imported from NCBI into the current genome set or output to genbank files\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "by selecting Genomes->Import Genomes into current Genome Set->Directly from NCBI Databases.\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "This feature queries NCBI's nucleotide database, using NCBI's Entrez E-utilities features.\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Matches to a search query are returned and printed to a window, with a provisional organism name\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "and identification number.  Matches are determined based on text identity with the organism name,\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "genus, isolation date, annotation, and other informative fields.  This may often result in a large\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "number of matches, so additional filters in the organism name may be specified below to reduce the\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "total number of matches.  It is also possible to modify the total number of search results returned.\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "All NCBI queries and result filters are case-insensitive.\n\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "For additional help and examples, please consult the JContextExplorer manual.", doc.getStyle("regular"));

        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	//Actions
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Include List
		//Adding
		if (e.getSource().equals(IncludeAddButton) || e.getSource().equals(IncludeTextField)){
			
			//ignore empty fields.
			if (IncludeTextField.getText().contentEquals("")==false){
				
				//check to see if adding to list is appropriate or not
				boolean AddToList = true;
				for (int i = 0; i < IncludeListModel.getSize(); i++){
		        	if (IncludeListModel.get(i).toString().contentEquals(IncludeTextField.getText().trim())){
		        		AddToList = false;
		        	}
				}
				
				//add to list, if appropriate
				if (AddToList){
					IncludeListModel.addElement(IncludeTextField.getText().trim().replace(" ", "_"));
				}
				
				//reset text field.
				IncludeTextField.setText("");
			}

		}
		
		//Removing
		if (e.getSource().equals(IncludeRemoveButton)){
			try {
				for (int i = 0; i < IncludeListModel.getSize(); i++){
		        	if (IncludeListModel.get(i).toString().contentEquals(IncludeList.getSelectedValue())){
		        		IncludeListModel.remove(i);
		        	}
				}
			} catch (Exception ex){}
		}
		
		//Submit / proceed
		if (e.getSource().equals(btnSubmit)){
			
			//record included + display only types in linked list form.
			IncludeTypes = new LinkedList<String>();
			for (int i = 0; i < IncludeListModel.getSize(); i++){
				IncludeTypes.add(IncludeListModel.get(i).toString());
			}

			//Update filters
			f.getNCBIFields().Filters = IncludeTypes;
			f.getNCBIFields().ScreenResults = cbFilterbyName.isSelected();
			
			try {
				int NewNum = Integer.parseInt(TxtReturnResults.getText().trim());
				if (NewNum < 100000){
					f.getNCBIFields().RetMax = NewNum;
				} else {
					f.getNCBIFields().RetMax = 100000;
				}

			} catch (Exception ex) {
				//ex.printStackTrace();
			}
			
			//dispose the window.
			this.dispose();
		}
		
	}

}
