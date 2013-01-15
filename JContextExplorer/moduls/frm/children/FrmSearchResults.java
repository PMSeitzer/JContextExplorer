package moduls.frm.children;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import genomeObjects.CSDisplayData;
import genomeObjects.GenomicElementAndQueryMatch;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import moduls.frm.FrmPrincipalDesk;

public class FrmSearchResults extends JPanel implements ActionListener{

	//Fields
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	private JTree SearchResults;
	private DefaultMutableTreeNode Query;
	private LinkedHashMap<String, DefaultMutableTreeNode> TreeNodeMapping;
	
	//GUI Components
	private JPanel TreeDisplay;
	private JPanel ButtonPanel;
	private JButton btnExpandAll;
	private JButton btnCollapseAll;
	private String strExpandAll = "Expand All";
	private String strCollapseAll = "Collapse All";
	
	//constructor
	public FrmSearchResults(final FrmPrincipalDesk f, CSDisplayData CSD){
		//base variables
		this.fr = f;
		this.CSD = CSD;
		
		//get panel
		this.getPanel();
	}

	//create panel
	public void getPanel(){
		//create top panel
		TreeDisplay = new JPanel();
		TreeDisplay.setLayout(new GridLayout(1,0));
		
        //Create the nodes.
        Query = new DefaultMutableTreeNode(CSD.getEC().getName());
        
        //create nodes method
        CreateNodes(Query);
        
        SearchResults = new JTree(Query);
        
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(SearchResults);
        
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
		for (String S : CSD.getEC().getContexts().keySet()){
			
			//create a new node, with the consistent name
			DefaultMutableTreeNode SM = new DefaultMutableTreeNode(S);
			
			//Retrieve individual gene information
			LinkedList<GenomicElementAndQueryMatch> Genes = CSD.getEC().getContexts().get(S);
			
			//store mapping
			TreeNodeMapping.put(S,SM);
			
			for (GenomicElementAndQueryMatch GandE : Genes){

				//Retrieve Gene ID number
				if (GandE.getE().getGeneID() == 0){
					GeneIDNum = "none";
				} else {
					GeneIDNum = Integer.toString(GandE.getE().getGeneID());
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
				
				String GeneInfoWithSource = "SOURCE: " + S + ": " + GeneInfo;
				//TreeNodeMapping.put(GeneInfoWithSource, GM);
			}
			
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
	}
	
	 public DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, String search) {
		    Enumeration nodeEnumeration = root.breadthFirstEnumeration();
		    while( nodeEnumeration.hasMoreElements() ) {
		      DefaultMutableTreeNode node =
		        (DefaultMutableTreeNode)nodeEnumeration.nextElement();
		      String found = (String)node.getUserObject();
		      if( search.equals( found ) ) {
		        return node;
		      }
		    }
		    return null;
		  }
	
	public void UpdateNodes(){

		//update CSD
		this.CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
		
		//initialize path arrays
		TreePath[] SelectedNodes;
		LinkedList<DefaultMutableTreeNode> SelectedNodesList = new LinkedList<DefaultMutableTreeNode>();
		boolean SelectNode = false;
		//update selected nodes - strings, names of nodes
		
		//check every node
		for (String NodeName: TreeNodeMapping.keySet()){
			DefaultMutableTreeNode Node = TreeNodeMapping.get(NodeName);
			if (CSD.getCurrentlySelectedNodes().get(NodeName)){
				SearchResults.addSelectionPath(new TreePath(Node.getPath()));
			} else {
				SearchResults.removeSelectionPath(new TreePath(Node.getPath()));
			}
		}
		
//		
//		for (String s : CSD.getCurrentlySelectedNodes().keySet()){
//			if (CSD.getCurrentlySelectedNodes().get(s) == true){
//				//SelectedNodesList.add(TreeNodeMapping.get(s));
//			} else {
//				
//			}
//		}
//		
//		
//		
//		//select nodes that ought to be selected
//		for (DefaultMutableTreeNode node : SelectedNodesList){
//			//check all nodes
//		    Enumeration nodeEnumeration = Query.breadthFirstEnumeration();
//		    while(nodeEnumeration.hasMoreElements() ) {
//			      DefaultMutableTreeNode NextNode =
//					        (DefaultMutableTreeNode)nodeEnumeration.nextElement();
//			      if (NextNode.equals(node)){
//			    	  SearchResults.addSelectionPath(new TreePath(node.getPath()));
//			      }
//		    }
//		}
		

		
//		//iterate through, find all appropriate nodes
//		for (int i = 0; i < SelectedNodesList.size(); i++){
//			DefaultMutableTreeNode Node = TreeNodeMapping.get(SelectedNodesList.get(i));
//			for (int j = 0; i < SearchResults.getRowCount(); j++){
//				if (SearchResults.getPathForRow(j).getLastPathComponent().equals(Node)){
//					SearchResults.addSelectionRow(j);
//				}
//			}
//		}
		

//		//create array
//		SelectedNodes = new TreePath[SelectedNodesList.size()];
//		for (int i = 0; i < SelectedNodes.length; i++){
//			//SelectedNodes[i] = SearchResults.getRowForPath(SelectedNodesList.get(i).getPath()); 
//			//SearchResults.addSelectionPaths((TreePath[]) SelectedNodesList.get(i).getPath());
//			DefaultMutableTreeNode Node = SelectedNodesList.get(i);
//			Node.getPath();
//			TreePath TP = new TreePath(Node);
//			SearchResults.addSelectionPath(TP);
//		}
		
		//update selection
		//SearchResults.setSelectionPaths(SelectedNodesList.get(i).getPath());

	}
}
