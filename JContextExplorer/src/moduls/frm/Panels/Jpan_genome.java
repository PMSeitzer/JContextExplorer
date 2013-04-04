package moduls.frm.Panels;

import genomeObjects.CSDisplayData;
import genomeObjects.ExtendedCRON;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;
import genomeObjects.OrganismSet;

import java.awt.Cursor;
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
		//add(btnViewAnnotations, c);
		
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
			
			//retrieve most current
			CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
			
			//Change all leaves to selected state
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				CL.setSelected(true);
			}
			
			//update
			fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
			
			//update displays
			fr.UpdateSelectedNodes();
			
			} catch (Exception e1){
				JOptionPane.showMessageDialog(null,"Please enter a query in the search bar (top left-hand corner).",
						"Submit Query",JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}
		}
		
		//deselect all button
		if (e.getSource() == btndeSelectAll){
			
			try {
			
			//retrieve most current
			CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
			
			//Change all leaves to selected state
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				CL.setSelected(false);
			}
			
			//update
			fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
			
			//update displays
			fr.UpdateSelectedNodes();
			
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,"Please enter a query in the search bar (top left-hand corner).",
						"Submit Query",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		//invoke view context frame
		if (e.getSource() == btnViewContexts){
		
				try {
					
					//retrieve most current + sort
					CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
					CSD.setCurrentlyViewedPanel(fr.getSelectedAnalysisType());
					
					//sort
					if (CSD.getCurrentlyViewedPanel().equals("Search Results")){
						
						//alphabetical order.
						Arrays.sort(CSD.getGraphicalContexts(), ContextLeaf.getAlphabeticalComparator());
						
					} else if (CSD.getCurrentlyViewedPanel().equals("Context Tree")){
						
						//based on context tree.
						Arrays.sort(CSD.getGraphicalContexts(), ContextLeaf.getContextTreeOrderComparator());
						
					} else if (CSD.getCurrentlyViewedPanel().equals("Phylogenetic Tree")) {
						
						//based on phylogenetic tree.
						Arrays.sort(CSD.getGraphicalContexts(), ContextLeaf.getPhylogeneticTreeOrderComparator());
					}
					
					//System.out.println("Currently Viewed: " + CSD.getCurrentlyViewedPanel());
					
					//remove empties from selection
					for (ContextLeaf CL : CSD.getGraphicalContexts()){
						if (CSD.getEC().getContexts().get(CL.getName()).isEmpty()){
							CL.setSelected(false);
						}
					}
					
					//count number selected
					int NumSelected = 0;
					for (ContextLeaf CL : CSD.getGraphicalContexts()){
						if (CL.isSelected()){
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
						//String Title =  "Context Viewer: " + fr.getCurrentFrame().getInternalPanel().getCSD().getEC().getName();
						String Title =  "Context Viewer: " + fr.getCurrentFrame().getInternalFrameData().getQD().getName();
						
						//Attempt: try an instance that will be forever disconnected.
						CSDisplayData CSDToContexts = new CSDisplayData();
						
						//map variables.
						CSDToContexts.setEC(CSD.getEC());
						CSDToContexts.setGraphicalContexts(CSD.getGraphicalContexts());
						
						//set wait cursor
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						new mainFrame(CSDToContexts, fr.getOS(), Title, fr);
						
						// return cursor to default
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					
				} catch (Exception e1){
//					e1.printStackTrace();
//					String exceptionString = "Select nodes of interest by clicking on the node name in the dendrogram." + "\n" +
//								"ctrl+click and shift+click can be used to select several nodes simultaneously." + "\n" + 
//								"You may select all or deselect all nodes by pushing the 'select all' and 'deselect all' buttons.";
//					JOptionPane.showMessageDialog(null,exceptionString);
				}
			
		}
		
		//select a subset of nodes
		if (e.getSource() == btnSelectNodes || e.getSource() == searchForNodes){
			
			try {
				
				//retrieve most current
				CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
				
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
						
						//Search basic node name
						for (int j = 0; j <Queries.length; j++){
							if (CL.getName().toUpperCase().contains(Queries[j].toUpperCase().replaceAll("\\s",""))){
								SelectNode = true;
							}
						}
						
						//check all genes associated with this context leaf
						LinkedList<GenomicElementAndQueryMatch> Genes = CSD.getEC().getContexts().get(CL.getName());
						
						//Search contexts
						for (int j = 0; j <Queries.length; j++){
							
							//search gene ids of genes
							if (Queries[j].toUpperCase().contains("GENEID:")){
								try {
									
									//retrieve Gene ID
									//int GeneIDNumber = Integer.parseInt(Queries[j].substring(7));
									String GeneID = Queries[j].substring(7);
									
									//if a gene matches, select this context
									for (GenomicElementAndQueryMatch GandE : Genes){
										if (GandE.getE().getGeneID().toUpperCase().equals(GeneID.toUpperCase())){
											SelectNode = true;
										}
									}
									
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(null,"GENEID values must be integers.",
											"GENEID value unreadable",JOptionPane.ERROR_MESSAGE);
								}

							}
							
							//search homology clusters
							if (Queries[j].toUpperCase().contains("CLUSTERID:")){
								try {
									
									//retrieve Id number
									int ClusterIDNumber = Integer.parseInt(Queries[j].substring(10));
									
									//if a gene matches, select this context
									for (GenomicElementAndQueryMatch GandE : Genes){
										if (GandE.getE().getClusterID() == ClusterIDNumber){
											SelectNode = true;
										}
									}
									
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(null,"CLUSTERID values must be integers.",
											"CLUSTERID value unreadable",JOptionPane.ERROR_MESSAGE);
								}
							}
							
							//search annotation
							if (Queries[j].toUpperCase().contains("ANNOTATION:")){
								try {
									
									//retrieve Id number
									String AnnotationFragment = Queries[j].substring(11).toUpperCase();
									
									//if a gene matches, select this context
									for (GenomicElementAndQueryMatch GandE : Genes){
										if (GandE.getE().getAnnotation().toUpperCase().contains(AnnotationFragment)){
											SelectNode = true;
										}
									}
									
								} catch (Exception ex) {
//									JOptionPane.showMessageDialog(null,"CLUSTERID values must be integers.",
//											"CLUSTERID value unreadable",JOptionPane.ERROR_MESSAGE);
								}
							}
							
							//search motif
							if (Queries[j].toUpperCase().contains("MOTIF:")){
								try {
									
									//retrieve motif name
									String MotifName = Queries[j].substring(6).toUpperCase();
									
									//if a gene matches, select this context
									for (GenomicElementAndQueryMatch GandE : Genes){
										if (GandE.getE().getAssociatedMotifNames().contains(MotifName)){
											SelectNode = true;
										}
									}
									
								} catch (Exception ex) {
//									JOptionPane.showMessageDialog(null,"There are no motifs of that na.",
//											"CLUSTERID value unreadable",JOptionPane.ERROR_MESSAGE);
								}
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
					//e1.printStackTrace();
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
