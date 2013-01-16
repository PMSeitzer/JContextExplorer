package moduls.frm.Panels;

import genomeObjects.CSDisplayData;
import genomeObjects.ExtendedCRON;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;
import genomeObjects.OrganismSet;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import contextViewer.AnnotationFrame;
import contextViewer.mainFrame;

import moduls.frm.ContextLeaf;
import moduls.frm.FrmInternalFrame;
import moduls.frm.FrmPrincipalDesk;

public class Jpan_genome extends JPanel implements ActionListener,
	FocusListener, InternalFrameListener{

	//parent frame
	private final FrmPrincipalDesk fr;
	
	//display-related
	private OrganismSet OS;
	private Jpan_genome jg;
	private JButton btnSelectAll, btndeSelectAll, btnViewContexts, btnViewAnnotations, btnSelectNodes;
	private String strSelectAll = "Select All";
	private String strdeSelectall = "Deselect All";
	private String strViewContexts = "View Contexts";
	private String strViewAnnotations = "View Annotations";
	private String strSelectNodes = "Select Nodes";
	private JTextField searchForNodes;
	
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private Font fontSearch = new Font("Dialog", Font.PLAIN, 14);
	
	// Internal frame currently active
	private FrmInternalFrame currentInternalFrame = null;
	
	// Information relevant for context information
	private boolean[] SelectedNodeNumbers;
	private CSDisplayData CSD;

	//context view warning
	private boolean ProceedWithContextView;
	private int ViewingThreshold = 100;
	
	public Jpan_genome(FrmPrincipalDesk fr) {
		super();
		this.fr = fr;
		this.CSD = fr.getCSD();
		this.jg = this;
		this.getPanel();
		this.setVisible(true);
		this.OS = fr.getOS();
	}

	private void getPanel() {
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Multiple Genome Browser Tool"));
		final GridBagConstraints c = new GridBagConstraints();
		int gridx = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		//Select All
		c.gridx = gridx;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		btnSelectAll = new JButton(strSelectAll);
		btnSelectAll.addActionListener(this);
		btnSelectAll.setFont(fontStandard);
		add(btnSelectAll, c);
		gridx++;
		
		//Deselect All
		c.gridx = gridx;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		btndeSelectAll = new JButton(strdeSelectall);
		btndeSelectAll.addActionListener(this);
		btndeSelectAll.setFont(fontStandard);
		add(btndeSelectAll, c);
		gridx++;
		
		//View Contexts
		c.gridx = gridx;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		btnViewContexts = new JButton(strViewContexts);
		btnViewContexts.addActionListener(this);
		btnViewContexts.setFont(fontStandard);
		add(btnViewContexts, c);
		gridx++;
		
		//View Annotations
		c.gridx = gridx;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		btnViewAnnotations = new JButton(strViewAnnotations);
		btnViewAnnotations.addActionListener(this);
		btnViewAnnotations.setFont(fontStandard);
		add(btnViewAnnotations, c);
		
		//search for nodes bar
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.ipady = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		searchForNodes = new JTextField("");
		searchForNodes.setFont(fontSearch);
		searchForNodes.addActionListener(this);
		add(searchForNodes, c);
		
		//search for nodes button
		c.ipady = 5;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		btnSelectNodes = new JButton(strSelectNodes);
		btnSelectNodes.setFont(fontStandard);
		btnSelectNodes.addActionListener(this);
		add(btnSelectNodes, c);
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		
		currentInternalFrame = (FrmInternalFrame) e.getSource();
		//fr.setCurrentFrame(currentInternalFrame);
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//select all button
		if (e.getSource()==btnSelectAll){
			
			try {
			//retrieve values
			boolean[] UpdatedNodeNumbers = new boolean [fr.getCurrentFrame().getInternalPanel().getRectanglesSurroundingLabels().length];
			
			//set all to selected
			Arrays.fill(UpdatedNodeNumbers, Boolean.TRUE);
			
			//update
			fr.getCurrentFrame().getInternalPanel().setSelectedNodeNumbers(UpdatedNodeNumbers);
			fr.UpdateSelectedNodes();
			
			} catch (Exception e1){
				JOptionPane.showMessageDialog(null,"Please enter a query in the search bar (top left-hand corner).",
						"Submit Query",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		//deselect all button
		if (e.getSource() == btndeSelectAll){
			
			try {
			//retrieve values
			boolean[] UpdatedNodeNumbers = new boolean [fr.getCurrentFrame().getInternalPanel().getRectanglesSurroundingLabels().length];
			
			//set all to unselected
			Arrays.fill(UpdatedNodeNumbers, Boolean.FALSE);
			
			//update
			fr.getCurrentFrame().getInternalPanel().setSelectedNodeNumbers(UpdatedNodeNumbers);
			fr.UpdateSelectedNodes();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,"Please enter a query in the search bar (top left-hand corner).",
						"Submit Query",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		//invoke view context frame
		if (e.getSource() == btnViewContexts){
		
				try {
					//set CSD to appropriate value
					this.setCSD(fr.getCurrentFrame().getInternalPanel().getCSD());
			
					//count number selected
					int NumSelected = 0;
					for (int i = 0; i <CSD.getSelectedNodes().length; i++){
						if (CSD.getSelectedNodes()[i] == true){
							NumSelected++;
						}
					}
					
					//issue warning if the number is very high
					if (NumSelected >= ViewingThreshold ) {
						String SureYouWantToView = "You are attempting to view a large number (" + NumSelected +
								") of contexts simultaneously." + "\n"
								+ "Proceeding may cause this program to crash." + "\n"
								+ "Are you sure you would like to proceed?" + "\n";
						
						//ask question, and maybe proceed with search
						int ViewCheck = JOptionPane.showConfirmDialog(null,SureYouWantToView,
								"Proceed with context viewing", JOptionPane.YES_NO_CANCEL_OPTION);
						
						if (ViewCheck == JOptionPane.YES_OPTION){
							this.ProceedWithContextView  = true;
						} else {
							this.ProceedWithContextView = false;
						}
					} else {
						ProceedWithContextView = true;
					}

					//proceed with context viewer
					if (ProceedWithContextView == true){
					
						//open context viewer frame
						String Title =  "Context Viewer: " + fr.getCurrentFrame().getInternalPanel().getCSD().getEC().getName();
						new mainFrame(CSD, OS, Title);
						
					}
					
				} catch (Exception e1){
					String exceptionString = "Select nodes of interest by clicking on the node name in the dendrogram." + "\n" +
								"ctrl+click and shift+click can be used to select several nodes simultaneously." + "\n" + 
								"You may select all or deselect all nodes by pushing the 'select all' and 'deselect all' buttons.";
					JOptionPane.showMessageDialog(null,exceptionString);
				}
			
		}
		
		//invoke view annotations frame
		if (e.getSource() == btnViewAnnotations){

			try {
			
			//set CSD to appropriate value
			this.setCSD(fr.getCurrentFrame().getInternalPanel().getCSD());
				
			//retrieve hash map of entries
			HashMap<String, LinkedList<GenomicElementAndQueryMatch>> ContextEntries = fr.getCurrentFrame().getInternalPanel().getCSD().getEC().getContexts();
			
			//initialize strings
			String[] Headers = new String[CSD.getSelectedNodes().length];
			String[] Annotations = new String[CSD.getSelectedNodes().length];

			JTextArea textArea = new JTextArea(20, 80);
		    textArea.setEditable(false);	
 
		    int NodeCounter = 0;
		    boolean NodeSelected = false;
			//determine EC from selected
			for (int i = 0; i < CSD.getSelectedNodes().length; i++){
				if (CSD.getSelectedNodes()[i] == true){
					
					//increment counter
					NodeCounter++;
					
					//determine if node already selected
					NodeSelected = false;
					
					//isolate node name
					String NodeName = CSD.getNodeNames()[i];

					//retrieve list of genomic elements
					List<GenomicElementAndQueryMatch> LL = ContextEntries.get(NodeName);
					
					//check for cluster number or annotation query
					for (int j = 0; j < LL.size(); j++){
						if (CSD.getEC().getSearchType().equals("annotation")){
							if (NodeSelected == false){
								for (int k = 0; k < CSD.getEC().getQueries().length; k++){
									if (LL.get(j).getE().getAnnotation().toUpperCase().contains(CSD.getEC().getQueries()[k].toUpperCase().trim())){
										
										//write to array
										Headers[i] = NodeName + ": ";
										Annotations[i] = LL.get(j).getE().getAnnotation() + "\n";
										NodeSelected = true;
									}
								}
							}
						} else if (CSD.getEC().getSearchType().equals("cluster")){
							if (NodeSelected == false){
								for (int k = 0; k < CSD.getEC().getClusterNumbers().length; k++){
									if (LL.get(j).getE().getClusterID() == CSD.getEC().getClusterNumbers()[k]){
										
										//write to array
										Headers[i] = NodeName + ": ";
										Annotations[i] = LL.get(j).getE().getAnnotation() + "\n";
										NodeSelected = true;
									}
								}
							}
						}
						
					}
					
				}
			}

			// create a JTextPane + add settings
			JTextPane jtp = new JTextPane();
			jtp.setEditable(false);
			
			//retrieve document, and add styles
	        StyledDocument doc = jtp.getStyledDocument();
	        
	        Style def = StyleContext.getDefaultStyleContext().
                    getStyle(StyleContext.DEFAULT_STYLE);

	        Style regular = doc.addStyle("regular", def);
	        StyleConstants.setFontFamily(def, "SansSerif");
	        
	        Style s = doc.addStyle("bold", regular);
	        StyleConstants.setBold(s, true);
	        if (Headers.length > 1){
		        String MatchHits = "Annotations for " + NodeCounter + " selected nodes:\n";
		        doc.insertString(doc.getLength(), MatchHits, doc.getStyle("regular"));
		        doc.insertString(doc.getLength(), "-------------------------------\n", doc.getStyle("regular"));
	        }

            for (int i=0; i <Headers.length; i++) {
                try {
					doc.insertString(doc.getLength(), Headers[i], doc.getStyle("bold"));
					doc.insertString(doc.getLength(), Annotations[i], doc.getStyle("regular"));
				} catch (BadLocationException e1) {
					System.out.println("bad location exception");
				}
           
            }
            
            //open new frame with results
		    new AnnotationFrame(jtp, "Annotation Results", fr);
		    
			} catch (Exception e1){
				String exceptionString = "Select nodes of interest by clicking on the node name in the dendrogram." + "\n" +
						"ctrl+click and shift+click can be used to select several nodes simultaneously." + "\n" + 
						"You may select all or deselect all nodes by pushing the 'select all' and 'deselect all' buttons.";
				JOptionPane.showMessageDialog(null,exceptionString);
			}
		}
		
		//select a subset of nodes
		if (e.getSource() == btnSelectNodes || e.getSource() == searchForNodes){
			try {
				if (searchForNodes.getText().equals("")){
					String MessageString;
					MessageString = "Please enter either one or more genera or species, separated by commas." + "\n" 
									+ "This will select all appropriate nodes in the tree.";
					JOptionPane.showMessageDialog(null, MessageString);
				} else {

					//recover query, split by semicolon, comma, or white space
					String Query = searchForNodes.getText();
					String[] Queries = Query.split(";");
					if (Queries.length == 1){
						Queries = Query.split(",");
					}
					if (Queries.length == 1) {
						Queries = Query.split("\\s+");
					}
					
					//check queries against node name
					boolean SelectNode = false;
					for (ContextLeaf CL : CSD.getGraphicalContexts()){
						SelectNode = false;
						for (int j = 0; j <Queries.length; j++){
							if (CL.getName().toUpperCase().contains(Queries[j].toUpperCase().replaceAll("\\s",""))){
								SelectNode = true;
							}
						}
						CL.setSelected(SelectNode);
					}
					
					//update CSD + frame
					fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
					fr.UpdateSelectedNodes();

				}

				
				
			} catch (Exception e1){
					JOptionPane.showMessageDialog(null,"Please enter a query in the search bar (top left-hand corner).",
							"Submit Query",JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

}
