package moduls.frm.children;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import genomeObjects.CSDisplayData;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.biojava3.core.sequence.Strand;

import contextViewer.GeneColorLegendFrame;

import moduls.frm.ContextLeaf;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.Panels.Jpan_btn_NEW;

public class FrmSearchResults extends JPanel implements ActionListener, TreeSelectionListener{

	//Fields
	//Management
	private FrmPrincipalDesk fr;	//master CSD available here
	private CSDisplayData CSD;	//The local CSD

	//Tree components
	private JTree SearchResults;
	private DefaultMutableTreeNode Query;
	private LinkedHashMap<String, DefaultMutableTreeNode> TreeNodeMapping;
	private boolean SelectedbyMouse = true;
	
	//GUI Components
	private JPanel TreeDisplay;
	private JPanel ButtonPanel;
	private JButton btnExpandAll;
	private JButton btnCollapseAll;
	private String strExpandAll = "Expand All";
	private String strCollapseAll = "Collapse All";
	
	//data retrieval
	public LinkedHashMap<String, GenomicElement> LeafData = new LinkedHashMap<String, GenomicElement>();
	public LinkedHashMap<String, String> LeafSource = new LinkedHashMap<String, String>();
	//pop-up window
	private JPopupMenu ExportMenu;	//export frame information
	
	//constructor
	public FrmSearchResults(final FrmPrincipalDesk f, CSDisplayData CSD){
		//base variables
		this.fr = f;
		this.CSD = CSD;
		
		//initialize
		this.InitializeSequenceExportMenu();
		
		//get panel
		this.getPanel();


	}
	
	//dummy constructor
	public FrmSearchResults(){

	}
	
	// ====== Export-Related ======= //
	
	//create the pop-up menu object
	private void InitializeSequenceExportMenu(){
		
		//Strings
		final String ExportDNASeqs = "Export Genes (DNA Sequences)";
		final String ExportProtSeqs = "Export Protein Sequences";
		final String ExportSegments = "Export Whole Segments";
		final String ExportDataAsShortTable = "Export Data as Table (short)";
		final String ExportDataAsLongTable = "Export Data as Table (long)";
		
		//create action listener
		ActionListener exportAction = new ActionListener(){
			
			public void actionPerformed(final ActionEvent evt) {
				
				/*
				 * EXPORT SEQUENCES
				 */
				
				//export DNA sequence of whole segment
				if (evt.getActionCommand().equals(ExportSegments)){
					
				}
				
				//export DNA segment of selected genes
				if (evt.getActionCommand().equals(ExportDNASeqs)) {
					
				}
				
				//export protein sequence
				if (evt.getActionCommand().equals(ExportProtSeqs)){
					
				}
				
				/*
				 * EXPORT DATA TABLE
				 */
				
				//export data table - short version
				if (evt.getActionCommand().equals(ExportDataAsShortTable)){
					ExportShortTable();
				}
				
				//export data table - long version
				if (evt.getActionCommand().equals(ExportDataAsLongTable)){
					ExportLongTable();
				}
			}

		};
		
		//set export menu
		this.ExportMenu = new JPopupMenu();
		
		//create menu items
		final JMenuItem me0 = new JMenuItem(ExportDNASeqs);
		final JMenuItem me1 = new JMenuItem(ExportProtSeqs);
		final JMenuItem me2 = new JMenuItem(ExportSegments);
		final JMenuItem me3 = new JMenuItem(ExportDataAsShortTable);
		final JMenuItem me4 = new JMenuItem(ExportDataAsLongTable);
		
		//add action listeners
		me0.addActionListener(exportAction);
		me1.addActionListener(exportAction);
		me2.addActionListener(exportAction);
		me3.addActionListener(exportAction);
		me4.addActionListener(exportAction);
		
		//build menu
		ExportMenu.add(me0);
		ExportMenu.add(me1);
		ExportMenu.add(me2);
		ExportMenu.addSeparator();
		ExportMenu.add(me3);
		ExportMenu.add(me4);

	}
	
	//export short table
	private void ExportShortTable(){
		
		//Create + Show file dialog window
		final FileDialog fd = new FileDialog(fr, "Export Search Results Data", FileDialog.SAVE);
		fd.setDirectory(fr.getFileChooserSource().getAbsolutePath());
		fd.setFile(".txt");
		fd.setVisible(true);
		
		//if a file is specified, export the data to file.
		if (fd.getFile() != null){
			
			//recover data for file
			String sPath = fd.getDirectory() + fd.getFile();
			final File OutputFile = new File(sPath);
			
			//update file chooser
			fr.setFileChooserSource(OutputFile.getParentFile());
			
			//a data structure to hold the source data
			LinkedList<String> SourceData = new LinkedList<String>();
			
			//iterate through tree nodes, retrieve selected, export
			int[] SelectedElements = SearchResults.getSelectionRows();
			for (int i = 0; i < SelectedElements.length; i++){
				
				//retrieve the appropriate node
				DefaultMutableTreeNode TN = (DefaultMutableTreeNode) SearchResults.getPathForRow(SelectedElements[i]).getLastPathComponent();
				
				//an individual gene / genes
				if (TN.isLeaf() && !TN.getAllowsChildren()){
					String s = TN.toString();
					if (!SourceData.contains(s)){
						SourceData.add(s);
					}
				//a whole set of genes
				} else {
					int ChildCount = TN.getChildCount();
					for (int j = 0; j < ChildCount; j++){
						String s = TN.getChildAt(j).toString();
						if (!SourceData.contains(s)){
							SourceData.add(s);
						}
					}
				}
				
			}
			
			try {
				
				//open file stream
				BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));

				String Header = "#GeneID\tClusterID\tAnnotation\n";
				bw.write(Header);
				bw.flush();

				
				//print output to table
				for (String s : SourceData){
					
					//overall string to output to file
					String str = "";
					
					//split line appropriately
					String[] ChopOne = s.split("ANNOTATION:");
					String[] ChopTwo = ChopOne[0].split("CLUSTERID:");
					String[] ChopThree = ChopTwo[0].split("GENEID:");
					
					//export as tab-delimited
					str = 	ChopThree[1].trim()
							+ "\t" + ChopTwo[1].trim() 
							+ "\t" + ChopOne[1].trim() 
							+ "\n";
					
					bw.write(str);
					bw.flush();
				}
				
				//close file stream
				bw.close();
				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "The data in the table could not be exported.",
						"Table Export Error",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	//export long table
	public void ExportLongTable(){
	
		//Create + Show file dialog window
		final FileDialog fd = new FileDialog(fr, "Export Search Results Data", FileDialog.SAVE);
		fd.setDirectory(fr.getFileChooserSource().getAbsolutePath());
		fd.setFile(".txt");
		fd.setVisible(true);
		
		//if a file is specified, export the data to file.
		if (fd.getFile() != null){
			
			//recover data for file
			String sPath = fd.getDirectory() + fd.getFile();
			final File OutputFile = new File(sPath);
			
			//update file chooser
			fr.setFileChooserSource(OutputFile.getParentFile());
			
			//a data structure to hold the source data
			LinkedList<String> SourceData = new LinkedList<String>();
			
			//iterate through tree nodes, retrieve selected, export
			int[] SelectedElements = SearchResults.getSelectionRows();
			for (int i = 0; i < SelectedElements.length; i++){
				
				//retrieve the appropriate node
				DefaultMutableTreeNode TN = (DefaultMutableTreeNode) SearchResults.getPathForRow(SelectedElements[i]).getLastPathComponent();
				
				//an individual gene / genes
				if (TN.isLeaf() && !TN.getAllowsChildren()){
					String s = TN.toString();
					if (!SourceData.contains(s)){
						SourceData.add(s);
					}
				//a whole set of genes
				} else {
					int ChildCount = TN.getChildCount();
					for (int j = 0; j < ChildCount; j++){
						String s = TN.getChildAt(j).toString();
						if (!SourceData.contains(s)){
							SourceData.add(s);
						}
					}
				}
				
			}
			
			try {
				
				//open file stream
				BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));

				String Header = "#Organism\tContig\tStart\tStop\tStrand\tAnnotation\tClusterID\tGeneID\n";
				bw.write(Header);
				bw.flush();
				
				//print output to table
				for (String s : SourceData){
					
					//retrieve all bioinfo.
					GenomicElement E = LeafData.get(s);
					String OrgName = LeafSource.get(s);
					
					String ClusterID = "";
					if (E.getClusterID() != -1){
						ClusterID = String.valueOf(E.getClusterID());
					} else {
						ClusterID = "none";
					}
					
					String GeneID = "";
					if (E.getGeneID() != null){
						GeneID = E.getGeneID();
					} else{
						GeneID = "none";
					}
					
					String TheStrand = "";
					if (E.getStrand().equals(Strand.POSITIVE)){
						TheStrand = "1";
					} else{
						TheStrand = "-1";
					}
					
					//export all the bioinfo.
					String str = OrgName + "\t" + E.getContig() + "\t"
							+ String.valueOf(E.getStart()) + "\t"
							+ String.valueOf(E.getStop()) + "\t" 
							+ TheStrand + "\t"
							+ E.getAnnotation() + "\t"
							+ ClusterID + "\t"
							+ GeneID + "\n";
					
					bw.write(str);
					bw.flush();
					
				}
				
				//close file stream
				bw.close();
				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "The data in the table could not be exported.",
						"Table Export Error",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	//create panel
	public void getPanel(){
		//create top panel
		TreeDisplay = new JPanel();
		TreeDisplay.setLayout(new GridLayout(1,0));
		
        //Create the nodes.
        Query = new DefaultMutableTreeNode(CSD.getEC().getName());
        
        //create nodes method
        this.CreateNodes(Query);
        
        SearchResults = new JTree(Query);
        SearchResults.addTreeSelectionListener(this);
        
        //adapter for export menu
        MouseAdapter ml = new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
        		//right-clicking on panel
        		if (SwingUtilities.isRightMouseButton(e)){
        			
        			//trigger pop-up menu display
        			ExportMenu.show(e.getComponent(),
        					e.getXOnScreen(), e.getYOnScreen());
        			
        			//reposition appropriately
        			ExportMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
        			
        		}
        	}
        };
        SearchResults.addMouseListener(ml);
        
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(SearchResults);
        Dimension D = treeView.getPreferredSize();
        treeView.setPreferredSize(new Dimension(D.height-20,D.width));
        
        //add to top panel
        TreeDisplay.add(treeView);
        
        //create bottom panel
        ButtonPanel = new JPanel();
        ButtonPanel.setLayout(new GridLayout(1,2));
        
        //Expand/Collapse buttons
        btnExpandAll = new JButton(strExpandAll);
        btnExpandAll.addActionListener(this);
        ButtonPanel.add(btnExpandAll);
        btnCollapseAll = new JButton(strCollapseAll);
        btnCollapseAll.addActionListener(this);
        ButtonPanel.add(btnCollapseAll);
        
        this.setLayout(new BorderLayout());
        this.add(TreeDisplay, BorderLayout.CENTER);
        this.add(ButtonPanel, BorderLayout.SOUTH);
	}

	//add all hits (nodes)
	public void CreateNodes(DefaultMutableTreeNode root){
		
		String GeneInfo;
		String GeneIDNum;
		String ClusterIDNum;
		
		TreeNodeMapping = new LinkedHashMap<String, DefaultMutableTreeNode>();
		
		//iterate through all contexts
		//for (String S : CSD.getEC().getContexts().keySet()){
		for (ContextLeaf CL : CSD.getGraphicalContexts()){	
			
			//create a new node, with the consistent name
			DefaultMutableTreeNode SM = new DefaultMutableTreeNode(CL.getName());
			CL.setSearchResultsTreeNode(SM);
			
			//Retrieve individual gene information
			LinkedList<GenomicElementAndQueryMatch> Genes = CSD.getEC().getContexts().get(CL.getName());
			
			//store mapping
			//TreeNodeMapping.put(S,SM);
			
			for (GenomicElementAndQueryMatch GandE : Genes){

				//Retrieve Gene ID number
				if (GandE.getE().getGeneID() == ""){
					GeneIDNum = "none";
				} else {
					GeneIDNum = GandE.getE().getGeneID();
				}

				//Retrieve Cluster ID number
				if (GandE.getE().getClusterID() == 0){
					ClusterIDNum = "none";
				} else {
					ClusterIDNum = Integer.toString(GandE.getE().getClusterID());
				}
				
				//Retrieve Annotation
				GeneInfo = "GENEID: " + GeneIDNum + " CLUSTERID: " 
						+ ClusterIDNum + " ANNOTATION: " + GandE.getE().getAnnotation();
				
				//add node to tree
				DefaultMutableTreeNode GM = new DefaultMutableTreeNode(GeneInfo);
				SM.add(GM);
				
				//store in hash map
				LeafData.put(GeneInfo, GandE.getE());
				LeafSource.put(GeneInfo, CSD.getEC().getSourceSpeciesNames().get(CL.getName()));
				String GeneInfoWithSource = "SOURCE: " + CL.getName() + ": " + GeneInfo;
				//TreeNodeMapping.put(GeneInfoWithSource, GM);
			}
			
			//add tree node to root
			root.add(SM);
		}
		
	}

	//actions: expand/contract
	@Override
	public void actionPerformed(ActionEvent evt) {
		//expand rows
		if (evt.getSource().equals(btnExpandAll)){
			for (int i = 0; i < SearchResults.getRowCount(); i++){
				SearchResults.expandRow(i);
			}
		}

		//collapse rows
		if (evt.getSource().equals(btnCollapseAll)){
			for (int i = 1; i < SearchResults.getRowCount(); i++){
				SearchResults.collapseRow(i);
			}
		}
		
		//re-draw figure (adjust button sizes)
		this.repaint();
	}

	public void UpdateNodes(){

		//This method is only called when not invoked by mouse event.
		SelectedbyMouse = false;
		
		//retrieve updated CSD
		this.CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
		
		//mark selected nodes
		for (ContextLeaf CL : CSD.getGraphicalContexts()){
			if (CL.isSelected()){
				SearchResults.addSelectionPath(new TreePath(CL.getSearchResultsTreeNode().getPath()));
			} else {
				SearchResults.removeSelectionPath(new TreePath(CL.getSearchResultsTreeNode().getPath()));
			}
		}
		
		//Now that nodes no longer affected, revert to old protocol
		SelectedbyMouse = true;

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		//only do things when selected by mouse.
		if (SelectedbyMouse){
			
			//retrieve updated CSD
			this.CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
			
//			//debugging.
//			System.out.println("Debugging, tree nodes:");
//			for (ContextLeaf CL : CSD.getGraphicalContexts()){
//				System.out.println(CL.getName());
//			}
			
			//Update selected/deselected
			TreePath[] SelectionChanges = e.getPaths();
			
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				for (int i = 0; i < SelectionChanges.length; i++){
					if (SelectionChanges[i].equals(new TreePath(CL.getSearchResultsTreeNode().getPath()))){
						if (CL.isSelected()){
							CL.setSelected(false);
						} else {
							CL.setSelected(true);
						}
						break;
					}
				}
			}
			
			//update master CSD
			fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
			
			//call main frame to update this and all other panels.
			this.fr.UpdateSelectedNodes();
		}

	}
	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}
	public CSDisplayData getCSD() {
		return CSD;
	}

}
