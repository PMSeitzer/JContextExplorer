package moduls.frm.children;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class FrmTabbed extends JTabbedPane{
	
	//Fields
	//panels
	private JScrollPane ContextTreePanel;
	private JScrollPane ContextGraphPanel;
	private JScrollPane SearchResultsPanel;
	private JScrollPane PhyloTreePanel;
	
	//display variables
	private boolean ShowContextTree;
	private boolean ShowContextGraph;
	private boolean ShowSearchResults;
	private boolean ShowPhyloTree;
	
	//Constructor
	public FrmTabbed(JScrollPane fPizSP, JScrollPane fGraphSP, JScrollPane fResultsSP, JScrollPane fPhyloSP,
			boolean ShowContextTree, boolean ShowContextGraph, boolean ShowSearchResults, boolean ShowPhyloTree){
		
		//panels
		this.ContextTreePanel = fPizSP;
		this.ContextGraphPanel = fGraphSP;
		this.SearchResultsPanel = fResultsSP;
		this.PhyloTreePanel = fPhyloSP;
		
		//display options
		this.ShowContextTree = ShowContextTree;
		this.ShowContextGraph = ShowContextGraph;
		this.ShowSearchResults = ShowSearchResults;
		this.ShowPhyloTree = ShowPhyloTree;
		
		//retrieve panel
		this.getPanel();
	}
	
	//Methods
	public void getPanel(){
		
		//Add tabs, if specified
		//search results
		if (this.ShowSearchResults){
			this.addTab("Search Results", null,SearchResultsPanel);
		}
		
		//context tree
		if (this.ShowContextTree){
			this.addTab("Context Tree", null, ContextTreePanel);
		}
		
		//context graph
		if (this.ShowContextGraph){
			this.addTab("Context Graph", null, ContextGraphPanel);
		}
		
		//phylogenetic tree
		if (this.ShowPhyloTree){
			this.addTab("Phylogenetic Tree", null, PhyloTreePanel);
		}
	}
}
