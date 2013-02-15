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

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.PostSearchAnalyses;
import moduls.frm.Panels.Jpan_btn_NEW;

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
	
	//communication
	private FrmPrincipalDesk fr;
	private LinkedHashMap<Integer, String> SelectedPaneMapping = new LinkedHashMap<Integer, String>();

	//Constructor
	public FrmTabbed(JScrollPane fPizSP, JScrollPane fGraphSP, JScrollPane fResultsSP, JScrollPane fPhyloSP,
			PostSearchAnalyses AnalysesList, FrmPrincipalDesk fr){
		
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
		
		//communication with outside world
		this.fr = fr;

		
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
		
		int TabCode = -1;
		
		//Add tabs, if specified
		//search results
		if (this.ShowSearchResults){
			this.addTab("Search Results", null,SearchResultsPanel);
			TabCode++;
			SelectedPaneMapping.put(TabCode, "Search Results");
		}
		
		//context tree
		if (this.ShowContextTree){
			this.addTab("Context Tree", null, ContextTreePanel);
			TabCode++;
			SelectedPaneMapping.put(TabCode, "Context Tree");
		}
		
		//phylogenetic tree
		if (this.ShowPhyloTree){
			this.addTab("Phylogenetic Tree", null, PhyloTreePanel);
			TabCode++;
			SelectedPaneMapping.put(TabCode, "Phylogenetic Tree");
		}
		
		//context graph
		if (this.ShowContextGraph){
			this.addTab("Context Graph", null, ContextGraphPanel);
			TabCode++;
			SelectedPaneMapping.put(TabCode, "Context Graph");
		}

		//Retrieve currently selected frame
		String CurrentlySelected = fr.getSelectedAnalysisType();
		for (int i : SelectedPaneMapping.keySet()){
			if (SelectedPaneMapping.get(i).equals(CurrentlySelected)){
				this.setSelectedIndex(i);
				//System.out.println("Currently Selected: " + CurrentlySelected);
				break;
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		
//		//determine index
//		if (this.getTitleAt(this.getSelectedIndex()).equals("Search Results")){
//			//System.out.println("Search Results");
//		}
//		
//		if (this.getTitleAt(this.getSelectedIndex()).equals("Context Tree")){
//			//System.out.println("Context Tree");
//		}
//		
//		if (this.getTitleAt(this.getSelectedIndex()).equals("Context Graph")){
//			//System.out.println("Context Graph");
//		}
//		
//		if (this.getTitleAt(this.getSelectedIndex()).equals("Phylogenetic Tree")){
//			//System.out.println("Phylo Tree");
//		}
		
		//update index
		this.fr.setSelectedAnalysisType(getTitleAt(this.getSelectedIndex()));

		//System.out.println("Currently Selected: " + fr.getSelectedAnalysisType());
		
	}

}
