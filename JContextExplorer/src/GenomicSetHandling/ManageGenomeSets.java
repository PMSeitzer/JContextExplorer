package GenomicSetHandling;

import genomeObjects.OrganismSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RootPaneContainer;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import moduls.frm.FrmPrincipalDesk;

public class ManageGenomeSets extends JDialog implements ActionListener, ListSelectionListener{

	//fields
	//data/base
	private FrmPrincipalDesk f;
	
	//add remove lists
	//Add list
	private TransferHandler lh;
	private DefaultListModel<String> IncludeListModel;
	private JList<String> IncludeList;
	private JScrollPane IncludePane;
	private JPanel IncludeInternalPanel;
	private String strIncludeInternalPanel = "Genome Sets to Retain";
	
	// List GUI stuff
	//Add list
	private LinkedList<String> DisplayOnlyTypes;
	private DefaultListModel<String> DisplayOnlyListModel;
	private JList<String> DisplayOnlyList;
	private JScrollPane DisplayOnlyPane;
	private JPanel DisplayOnlyInternalPanel;
	private String strDisplayOnlyInternalPanel = "Genome Sets to Remove";
	private JTextField DisplayOnlyTextField;
	private JButton DisplayOnlyAddButton;
	private JButton DisplayOnlyRemoveButton;
	
	//remove list
	private JLabel LblAvailableSets, LblCurrentSet;
	private JPanel jp, jp2, jpEnclosing;
	private JTextField LblName, TxtName, LblNotes, LblGenomes;
	private JTextArea OrganismSetNotes, LblInfo;
	private JComboBox menuGenomes;
	private JScrollPane ptsscroll, ptsscroll2;
	private JButton btnOK;
	private int ScrollPaneInset = 15;
	private int HeightInset = 160;
	
	private LinkedHashMap<String,Component> GSMap 
		= new LinkedHashMap<String, Component>();
	private String strGenomes = "Genomes";
	private String strSelectGenome = "Select Genome";
	private GSInfo GI;
	
	//Constructor
	public ManageGenomeSets(FrmPrincipalDesk f){
		//
		this.f = f;
		lh = new ListTransferHandler();
		
		this.getFrame();
		this.getPanel();
		this.getOSData(f.getOS().getName());
		this.pack();
		this.setLocationRelativeTo(null);
		this.setMinimumSize(this.getSize());
		
		//set default to selected.
		for (int i = 0; i < IncludeListModel.getSize(); i++){
			if (IncludeListModel.get(i).equals(GI.getGSName())){
				IncludeList.setSelectedIndex(i);
			}
		}
		
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setVisible(true);

	}
	
	//frame
	public void getFrame(){
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Manage Genome Sets");
		this.setResizable(false);
	}
	
	//panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(2,2,2,2);
		
        /*
         * Include List
         */
		//Initialize list + list elements
		IncludeListModel = new DefaultListModel<String>();
		
		for (String s : f.getGenomeSets().keySet()){
			IncludeListModel.addElement(s);
		}

		//GUI list settings
		IncludeList = new JList<String>(IncludeListModel);
		IncludeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
		IncludeList.setDragEnabled(true);
		IncludeList.setDropMode(DropMode.INSERT);
		IncludeList.setTransferHandler(lh);
		IncludeList.addListSelectionListener(this);

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
        
        c.gridx = 0;
        c.gridwidth = 2;
        jp.add(IncludeInternalPanel,c);
	
        /*
         * Display List
         */
        
        //Initialize list + list elements
        DisplayOnlyListModel = new DefaultListModel<String>();
     
        //GUI list settings
        DisplayOnlyList = new JList<String>(DisplayOnlyListModel);
        DisplayOnlyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);      
        DisplayOnlyList.setDragEnabled(true);
		DisplayOnlyList.setDropMode(DropMode.INSERT);    
		DisplayOnlyList.setTransferHandler(lh);
		DisplayOnlyList.addListSelectionListener(this);
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
        
		c.gridx = 2;
		c.gridwidth = 2;
        jp.add(DisplayOnlyInternalPanel,c);

        gridy++;
        
		/*
		 *  Other components
		 */
        
		//central frame
		jp2 = new JPanel();
		jp2.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridx = 0;
		c.weightx = 1;
		c.gridy = gridy;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gridy = 0;
        
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2,5,2,2);
		LblCurrentSet = new JLabel("SELECTED GENOME SET INFORMATION:");
		LblCurrentSet.setBackground(Color.GRAY);
		LblCurrentSet.setOpaque(true);
		jp2.add(LblCurrentSet, c);
		
		gridy++;
        
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.insets = new Insets(2,2,2,2);
		LblName = new JTextField("Name:");
		LblName.setEditable(false);
		jp2.add(LblName, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		TxtName = new JTextField("");
		TxtName.setEditable(false);
		TxtName.setColumns(30);
		jp2.add(TxtName, c);
		
		gridy++;
		
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblNotes = new JTextField("Notes:");
		LblNotes.setEditable(false);
		jp2.add(LblNotes, c);
		
		//Enter notes here
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		OrganismSetNotes = new JTextArea("");
		OrganismSetNotes.setEditable(false);
		ptsscroll = new JScrollPane(OrganismSetNotes);
		//System.out.println(TxtName.getPreferredSize().width);
		//ptsscroll.setPreferredSize(new Dimension(TxtName.getPreferredSize().width,50));
		//ptsscroll.setPreferredSize(new Dimension(TxtName.getColumns()*12+8, 50));
		ptsscroll.setPreferredSize(new Dimension(374+150, 50));
		jp2.add(ptsscroll, c);
		gridy++;
		
		//Genomes 
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblGenomes = new JTextField(strGenomes);
		LblGenomes.setEditable(false);
		jp2.add(LblGenomes, c);
		
		//Pull-down menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		String[] Empty = {"<none>"};
		menuGenomes = new JComboBox<String>(Empty);
		jp2.add(menuGenomes, c);
		gridy++;
		
		//information bar
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.ipady = 7;
		LblInfo = new JTextArea("");
		LblInfo.setEditable(false);
		ptsscroll2 = new JScrollPane(LblInfo);
		ptsscroll2.setPreferredSize(new Dimension(50, 100));
		jp2.add(ptsscroll2, c);
		gridy++;

		//OK button
		JPanel jp3 = new JPanel();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = gridy;
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		jp3.add(btnOK, c);
		
		//add to frame
		jpEnclosing = new JPanel();
		jpEnclosing.setLayout(new BorderLayout());
		jpEnclosing.add(jp, BorderLayout.NORTH);
		jpEnclosing.add(jp2, BorderLayout.CENTER);
		jpEnclosing.add(jp3,BorderLayout.SOUTH);
		this.add(jpEnclosing);
		
	}

	//OS Data for menu
	public void getOSData(String OSName){
		
		//retrieve GI information
		GI = f.getGenomeSets().get(OSName);
		
		TxtName.setText(OSName);
		OrganismSetNotes.setText(GI.getGSNotes());
		
		//initialize
		String strGenomesUpdated;
		
		//Update genomes
		strGenomesUpdated = strGenomes + " (" + String.valueOf(GI.getGSGenomeDescriptions().keySet().size()) + "):";
		LblGenomes.setText(strGenomesUpdated);
		
		//add genomes
		if (GI.getGSGenomeDescriptions().keySet().size() > 0){
			menuGenomes.removeItemAt(0);
			menuGenomes.addItem(strSelectGenome);
			for (String s : GI.getGSGenomeDescriptions().keySet()){
				menuGenomes.addItem(s);
			}
			menuGenomes.addActionListener(this);
		}
	}
	
	//reset OS Data
	public void clearOSData(){
		 
		menuGenomes.removeAllItems();
		menuGenomes.addItem("<none>");
		menuGenomes.removeActionListener(this);
		LblInfo.setText("");
	}
	
	//Actions
	@Override
	public void actionPerformed(ActionEvent e) {

		//View Statistics, Update
		if (e.getSource().equals(btnOK)){

			//Update fields
			f.getOS().setNotes(OrganismSetNotes.getText());
			
			//close
			this.dispose();
		}
		
		//Show individual genome data for each organism
		if (e.getSource().equals(menuGenomes)){
			try {
			for (String s : GI.getGSGenomeDescriptions().keySet()){
				
				//find appropriate species
				if (menuGenomes.getSelectedItem().equals(s)){
					showGenomeInfo(s);
				}
			}
			
			if (menuGenomes.getSelectedItem().equals(strSelectGenome)){
				LblInfo.setText("");
			}
			} catch (Exception ex) {}
			
		}
		
	}
	
	//Show info about individual genomes
	public void showGenomeInfo(String GenomeName){
		LblInfo.setText(GI.getGSGenomeDescriptions().get(GenomeName));
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


	//Update info
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		//Determine selection
		String Selection = "";
		
		//
		if (e.getSource() == IncludeList){
			Selection = IncludeList.getSelectedValue();
			DisplayOnlyList.setSelectedIndices(new int[0]);
		} 
		
		if (e.getSource() == DisplayOnlyList){
			Selection = DisplayOnlyList.getSelectedValue();
			IncludeList.setSelectedIndices(new int[0]);
		}
				
		//Update display information
		if (Selection != ""){
			clearOSData();
			getOSData(Selection);
			this.repaint();
		}
	}
}
