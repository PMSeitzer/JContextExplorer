package haloGUI;

import java.awt.BorderLayout;
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

public class GFFChecker extends JFrame implements ActionListener{

	//Fields
	//universal
	private static final long serialVersionUID = 1L;
	private JPanel jp;
	private LoadGenomesPanelv2 lgp;
	private FrmPrincipalDesk f;
	private TransferHandler lh;
	private String strTextField = "";
	private int SrchCol = 12;
	private int RmvIndent = 11;
	
	//Introductory info
	private JTextPane Instructions;
	private Dimension InstructionDimensions = new Dimension(300,150);
	private JScrollPane InstructionPane;

	//include list
	private LinkedList<String> IncludeTypes;
	private DefaultListModel<String> IncludeListModel;
	private JList<String> IncludeList;
	private JScrollPane IncludePane;
	private JPanel IncludeInternalPanel;
	private String strIncludeInternalPanel = "Types to Include in Genomic Groupings";
	private JTextField IncludeTextField;
	private JButton IncludeAddButton;
	private JButton IncludeRemoveButton;
	
	//display list
	private LinkedList<String> DisplayOnlyTypes;
	private DefaultListModel<String> DisplayOnlyListModel;
	private JList<String> DisplayOnlyList;
	private JScrollPane DisplayOnlyPane;
	private JPanel DisplayOnlyInternalPanel;
	private String strDisplayOnlyInternalPanel = "Types to Include for Display only";
	private JTextField DisplayOnlyTextField;
	private JButton DisplayOnlyAddButton;
	private JButton DisplayOnlyRemoveButton;
	
	//submit / return
	private JButton btnSubmit;
	//private String strbtnSubmit = "Proceed to GFF import with these type-processing settings.";
	private String strbtnSubmit = "Ok";
	
	//Constructor
	public GFFChecker(LoadGenomesPanelv2 lgp){
		//parent bioinfo
		this.lgp = lgp;
		
		//initializations
		lh = new ListTransferHandler();
		
		//create GUI object
		this.getInstructions();
		this.getFrame();
		this.getPanel();
		this.pack();
		
		//make frame visible
		this.setVisible(true);
	}
	
	//alternative constructor
	public GFFChecker(FrmPrincipalDesk f){
		
		this.f = f;
		
		//initializations
		lh = new ListTransferHandler();
		
		//create GUI object
		this.getInstructions();
		this.getFrame();
		this.getPanel();
		this.pack();
		
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
			doc.insertString(doc.getLength(), "The third column of a .GFF file, or the first column of a GenBank file, describes each annotated feature's biological \"type\".", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "  For example, coding regions", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), " often have a type designation of \"CDS\" or \"gene\", and transfer RNA often have a type designation of \"tRNA\".\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "This tool allows you to specify how to handle different types of annotated features.\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "In general, among all possible feature types, you may specify\n",doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "(1) The types that should be retained for both genomic grouping computation and display,\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(2) The types that should be excluded from genomic grouping computation, but retained for display, and \n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(3) The types that should be excluded altogether.\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Types in the list ", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Types to Include in Genomic Groupings ",doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "(left) will be retained for both genomic grouping computation and display.  Types in the list ", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Types to Include for Display only ",doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "(right) will be retained for display only when viewing genomic segments.  ",doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "All other types will be ignored (excluded altogether).\n\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "To add types to a list, type in the type in the text field below the list and push the \"Add\" button. \n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "To remove types from a list, select the type with your mouse, and push the \"Remove\" button. \n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "To transfer types from one list to another, select the type with your mouse, and drag the type to the other list.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "WARNING!\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "Features in a GFF or GenBank file may not overlap in the genomic coordinates they span.  In the case that they do overlap, JContextExplorer will exhibit unpredictable behavior ", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "and likely fail.\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Please ensure that no annotated features overlap prior to loading GFF or GenBank files.", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "\n\nFor additional help and examples, please consult the JContextExplorer manual.", doc.getStyle("regular"));

        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	//Create frame
	public void getFrame(){
		this.setSize(700,620);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Feature Type Import Settings");
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
         * Include List
         */
		//Initialize list + list elements
		IncludeListModel = new DefaultListModel<String>();
		
		if (f != null){
			for (String s : f.getGFFIncludeTypes()){
				IncludeListModel.addElement(s);
			}
		} else {
			IncludeListModel.addElement("CDS");
			IncludeListModel.addElement("tRNA");
			IncludeListModel.addElement("rRNA");
		}

		//GUI list settings
		IncludeList = new JList<String>(IncludeListModel);
		IncludeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);      
		IncludeList.setDragEnabled(true);
		IncludeList.setDropMode(DropMode.INSERT);
		IncludeList.setTransferHandler(lh);
        setMappings(IncludeList);
        		
		//Internal pane - initialize
        IncludeInternalPanel = new JPanel(new BorderLayout());
        IncludeInternalPanel.setBorder(BorderFactory.createTitledBorder(strIncludeInternalPanel));
        IncludeInternalPanel.setLayout(new GridBagLayout());
        final GridBagConstraints c1 = new GridBagConstraints();
        
        //Internal pane - components
        //scrollpane
		IncludePane = new JScrollPane(IncludeList);
		IncludePane.setPreferredSize(new Dimension(300,100));
        c1.insets = new Insets(1,1,1,10);
        c1.anchor = GridBagConstraints.FIRST_LINE_START;
        c1.weightx = 1;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 3;
        IncludeInternalPanel.add(IncludePane, c1);
        
        //search bar
        c1.gridx = 0;
        c1.gridy = 1;
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
        c1.gridy = 1;
        c1.gridwidth = 1;
        c1.ipady = 0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(1,1,1,1);
        IncludeAddButton = new JButton("Add");
        IncludeAddButton.addActionListener(this);
        IncludeInternalPanel.add(IncludeAddButton, c1);
        
        //Remove button
        c1.gridx = 2;
        c1.gridy = 1;
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
        
        //GridBagLayout transitions
        gridx++;
        
        /*
         * Display List
         */
        
        //Initialize list + list elements
        DisplayOnlyListModel = new DefaultListModel<String>();
        
        if (f != null){
			for (String s : f.getGFFDisplayTypes()){
				DisplayOnlyListModel.addElement(s);
			}
        } else {
            DisplayOnlyListModel.addElement("mobile_element");
            DisplayOnlyListModel.addElement("IS_element");
        }

     
        //GUI list settings
        DisplayOnlyList = new JList<String>(DisplayOnlyListModel);
        DisplayOnlyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);      
        DisplayOnlyList.setDragEnabled(true);
		DisplayOnlyList.setDropMode(DropMode.INSERT);    
		DisplayOnlyList.setTransferHandler(lh);
        setMappings(DisplayOnlyList);
        
        //internal pane - initialize
        DisplayOnlyInternalPanel = new JPanel(new BorderLayout());
        DisplayOnlyInternalPanel.setBorder(BorderFactory.createTitledBorder(strDisplayOnlyInternalPanel));
        DisplayOnlyInternalPanel.setLayout(new GridBagLayout());
        final GridBagConstraints c2 = new GridBagConstraints();
        
        //Internal pane - components
        //scrollpane
        c2.insets = new Insets(1,1,1,10);
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.weightx = 1;
        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 3;
		DisplayOnlyPane = new JScrollPane(DisplayOnlyList);
		DisplayOnlyPane.setPreferredSize(new Dimension(300,100));
		DisplayOnlyInternalPanel.add(DisplayOnlyPane, c2);
        
        //search bar
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(1,1,1,1);
        c2.ipady = 7;
        DisplayOnlyTextField = new JTextField(strTextField);
        DisplayOnlyTextField.setEditable(true);
        DisplayOnlyTextField.setColumns(SrchCol);
        DisplayOnlyTextField.addActionListener(this);
        DisplayOnlyInternalPanel.add(DisplayOnlyTextField, c2);
        
        //add button
        c2.gridx = 1;
        c2.gridy = 1;
        c2.gridwidth = 1;
        c2.ipady = 0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(1,1,1,1);
        DisplayOnlyAddButton = new JButton("Add");
        DisplayOnlyAddButton.addActionListener(this);
        DisplayOnlyInternalPanel.add(DisplayOnlyAddButton, c2);
        
        //Remove button
        c1.gridx = 2;
        c1.gridy = 1;
        c1.gridwidth = 1;
        c1.ipady = 0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(1,1,1,RmvIndent);
        DisplayOnlyRemoveButton = new JButton("Remove");
        DisplayOnlyRemoveButton.addActionListener(this);
        DisplayOnlyInternalPanel.add(DisplayOnlyRemoveButton, c1);
        
        //internal panel -> frame panel
        c.gridx = gridx;
        c.gridy = gridy;
        c.insets = new Insets(0,40,0,0);
        jp.add(DisplayOnlyInternalPanel,c);
       
        //GridBagLayout transitions
        gridx = 0;
        gridy++;
        
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
	
	//List-associated mappings
	private void setMappings(JList list) {
        ActionMap map = list.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

    }
	
	//Actions!
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Include List
		//Adding
		if (e.getSource().equals(IncludeAddButton) || e.getSource().equals(IncludeTextField)){
			
			//ignore empty fields.
			if (IncludeTextField.getText().contentEquals("")==false){
				//check to see if adding to list is appropriate or not
				boolean AddToList = true;
				boolean RemoveFromOtherList = false;
				for (int i = 0; i < IncludeListModel.getSize(); i++){
		        	if (IncludeListModel.get(i).toString().contentEquals(IncludeTextField.getText().trim())){
		        		AddToList = false;
		        	}
				}
				
				//add to list, if appropriate
				if (AddToList){
					IncludeListModel.addElement(IncludeTextField.getText().trim());
				}
				
				//remove from other list, if appropriate.
				for (int i = 0; i < DisplayOnlyListModel.getSize(); i++){
		        	if (DisplayOnlyListModel.get(i).toString().contentEquals(IncludeTextField.getText().trim())){
		        		DisplayOnlyListModel.remove(i);
		        	}
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
		
		//DisplayOnly List
		//Adding
		if (e.getSource().equals(DisplayOnlyAddButton) || e.getSource().equals(DisplayOnlyTextField)){
			
			if (DisplayOnlyTextField.getText().contentEquals("")==false){
				
				//check to see if adding to list is appropriate or not
				boolean AddToList = true;
				boolean RemoveFromOtherList = false;
				for (int i = 0; i < DisplayOnlyListModel.getSize(); i++){
		        	if (DisplayOnlyListModel.get(i).toString().contentEquals(DisplayOnlyTextField.getText().trim())){
		        		AddToList = false;
		        	}
				}
				
				//add to list, if appropriate
				if (AddToList){
					DisplayOnlyListModel.addElement(DisplayOnlyTextField.getText().trim());
				}
				
				//remove from other list, if appropriate.
				for (int i = 0; i < IncludeListModel.getSize(); i++){
		        	if (IncludeListModel.get(i).toString().contentEquals(DisplayOnlyTextField.getText().trim())){
		        		IncludeListModel.remove(i);
		        	}
				}

				//reset text field.
				DisplayOnlyTextField.setText("");
			}
		}
		
		//Removing
		if (e.getSource().equals(DisplayOnlyRemoveButton)){
			try {
				for (int i = 0; i < DisplayOnlyListModel.getSize(); i++){
		        	if (DisplayOnlyListModel.get(i).toString().contentEquals(DisplayOnlyList.getSelectedValue())){
		        		DisplayOnlyListModel.remove(i);
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
			
			DisplayOnlyTypes = new LinkedList<String>();
			for (int i = 0; i < DisplayOnlyListModel.getSize(); i++){
				DisplayOnlyTypes.add(DisplayOnlyListModel.get(i).toString());
			}
			
			//when lgp exists, remember type settings
			if (lgp != null) {
				
				//write these types to the output structure.
				lgp.setIncludeTypes(IncludeTypes);
				lgp.setDisplayOnlyTypes(DisplayOnlyTypes);
			}

			//when frm principal desk exists, remember type settings.
			if (f != null){
				
				//write these types to output structure.
				f.setGFFIncludeTypes(IncludeTypes);
				f.setGFFDisplayTypes(DisplayOnlyTypes);
			}
			
			
			//dispose the window.
			this.dispose();
		}
		
	}
	
	//---GETTERS + SETTERS-----------
	public JButton getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(JButton btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	//transfer handler class
	class ListTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		/**
	     * Perform the actual data import.
	     */
	    public boolean importData(TransferHandler.TransferSupport info) {
	        String data = null;

	        //If we can't handle the import, bail now.
	        if (!canImport(info)) {
	            return false;
	        }

	        JList list = (JList)info.getComponent();
	        DefaultListModel model = (DefaultListModel)list.getModel();
	        //Fetch the data -- bail if this fails
	        try {
	            data = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
	        } catch (UnsupportedFlavorException ufe) {
	            System.out.println("importData: unsupported data flavor");
	            return false;
	        } catch (IOException ioe) {
	            System.out.println("importData: I/O exception");
	            return false;
	        }

	        //only add objects that are not already in the list.
	        boolean AddToList = true;
	        for (int i = 0 ; i <model.getSize(); i++){
	        	if (model.get(i).toString().contentEquals(data)){
	        		AddToList = false;
	        	}
	        }
	        
	        if (AddToList){
		        if (info.isDrop()) { //This is a drop
		            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
		            int index = dl.getIndex();
		            if (dl.isInsert()) {
		                model.add(index, data);
		                return true;
		            } else {
		                model.set(index, data);
		                return true;
		            }
		        } else { //This is a paste
		            int index = list.getSelectedIndex();
		            // if there is a valid selection,
		            // insert data after the selection
		            if (index >= 0) {
		                model.add(list.getSelectedIndex()+1, data);
		            // else append to the end of the list
		            } else {
		                model.addElement(data);
		            }
		            return true;
		        }
	        } else {
	        	return false;
	        }
	        

	    }

	    /**
	     * Bundle up the data for export.
	     */
	    protected Transferable createTransferable(JComponent c) {
	        JList list = (JList)c;
	        int index = list.getSelectedIndex();
	        String value = (String)list.getSelectedValue();
	        return new StringSelection(value);
	    }

	    /**
	     * The list handles both copy and move actions.
	     */
	    public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }

	    /** 
	     * When the export is complete, remove the old list entry if the
	     * action was a move.
	     */
	    protected void exportDone(JComponent c, Transferable data, int action) {
	        if (action != MOVE) {
	            return;
	        }
	        JList list = (JList)c;
	        DefaultListModel model = (DefaultListModel)list.getModel();
	        int index = list.getSelectedIndex();
	        model.remove(index);
	    }

	    /**
	     * We only support importing strings.
	     */
	    public boolean canImport(TransferHandler.TransferSupport support) {
	        // we only import Strings
	        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	            return false;
	        }
	        return true;
	    }
	}
}
