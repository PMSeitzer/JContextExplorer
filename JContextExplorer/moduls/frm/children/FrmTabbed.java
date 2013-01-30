package moduls.frm.children;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moduls.frm.PostSearchAnalyses;

public class FrmTabbed extends JTabbedPane implements ChangeListener{
	
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
			PostSearchAnalyses AnalysesList){
		
		//panels
		this.ContextTreePanel = fPizSP;
		this.ContextGraphPanel = fGraphSP;
		this.SearchResultsPanel = fResultsSP;
		this.PhyloTreePanel = fPhyloSP;
		
		//display options
		this.ShowContextTree = AnalysesList.isOptionComputeDendrogram();
		this.ShowContextGraph = AnalysesList.isOptionComputeContextGraph();
		this.ShowSearchResults = AnalysesList.isOptionDisplaySearches();
		this.ShowPhyloTree = AnalysesList.isOptionRenderPhylogeny();
		
//		//optional print statements (for debugging)
//		System.out.println("FrmTabbed Show search results:" + this.ShowSearchResults);
//		System.out.println("FrmTabbed Show dendrogram:" + this.ShowContextTree);
		
		//add a change listener to this
		this.addChangeListener(this);
		
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

	@Override
	public void stateChanged(ChangeEvent evt) {

		//perform action for each tab selected
		if (this.getTitleAt(this.getSelectedIndex()).equals("Search Results")){
			//System.out.println("Search");
		}
		
		if (this.getTitleAt(this.getSelectedIndex()).equals("Context Tree")){
			//System.out.println("Context Tree");
		}
		
		if (this.getTitleAt(this.getSelectedIndex()).equals("Context Graph")){
			//System.out.println("Context Graph");
		}
		
		if (this.getTitleAt(this.getSelectedIndex()).equals("Phylogenetic Tree")){
			//System.out.println("Phylo Tree");
		}
		
	}

}
