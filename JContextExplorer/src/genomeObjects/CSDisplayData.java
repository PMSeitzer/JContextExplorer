package genomeObjects;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedHashMap;

import moduls.frm.ContextLeaf;

public class CSDisplayData {

	//fields
	private String[] NodeNames;				//--Contexts under investigation
	private Rectangle2D[] Coordinates;		//
	private boolean[] SelectedNodes;		//
	private LinkedHashMap<String,Boolean> CurrentlySelectedNodes; 
	private String CurrentlyViewedPanel = "";
	
	private Rectangle2D[] PhyloCoordinates;
	private String[] PhyloNodeNames;
	
	
	private ExtendedCRON EC;				//--Context Set information-----
	private ContextLeaf[] GraphicalContexts;
	
	//constructor
	public CSDisplayData(){
		super();
	}
	
	//-----Getters and Setters--------------------------------------//
	
	public String[] getNodeNames() {
		return NodeNames;
	}
	public void setNodeNames(String[] nodeNames) {
		NodeNames = nodeNames;
	}
	public Rectangle2D[] getCoordinates() {
		return Coordinates;
	}
	public void setCoordinates(Rectangle2D[] coordinates) {
		Coordinates = coordinates;
	}
	public boolean[] getSelectedNodes() {
		return SelectedNodes;
	}
	public void setSelectedNodes(boolean[] selectedNodes) {
		SelectedNodes = selectedNodes;
	}
	public ExtendedCRON getEC() {
		return EC;
	}
	public void setEC(ExtendedCRON eC){
		EC = eC;
	}
	
	public void setECandInitializeTreeLeaves(ExtendedCRON eC) {
		//set ec value
		EC = eC;
		
		//initialize contexts
		this.GraphicalContexts = new ContextLeaf[EC.getContexts().keySet().size()];
		int Counter = 0;
		for (String s : EC.getContexts().keySet()){
			ContextLeaf CL = new ContextLeaf();
			CL.setName(s);
			
			//determine source species + write to context leaf structure
			String Species = "";
			
			String[] ParsedLine = s.split("-");
			for (int i = 0 ; i < ParsedLine.length-1; i++){
				Species = Species + ParsedLine[i];
			}
			
			CL.setSourceSpecies(Species);
			CL.setSelected(false);
			GraphicalContexts[Counter] = CL;
			Counter++;
		}
		
//		int Counter = 0;
//		
//		//initialize node mapping
//		CurrentlySelectedNodes = new LinkedHashMap<String,Boolean>();
//		for (String s : EC.getContexts().keySet()){
//			CurrentlySelectedNodes.put(s, false);
//			Counter++;
//		}
//		
//		//initialize selected nodes
//		SelectedNodes = new boolean[Counter];
//		Arrays.fill(SelectedNodes, false);
		
	}

	public LinkedHashMap<String,Boolean> getCurrentlySelectedNodes() {
		return CurrentlySelectedNodes;
	}

	public void setCurrentlySelectedNodes(LinkedHashMap<String,Boolean> currentlySelectedNodes) {
		CurrentlySelectedNodes = currentlySelectedNodes;
	}

	public ContextLeaf[] getGraphicalContexts() {
		return GraphicalContexts;
	}

	public void setGraphicalContexts(ContextLeaf[] graphicalContexts) {
		GraphicalContexts = graphicalContexts;
	}

	public Rectangle2D[] getPhyloCoordinates() {
		return PhyloCoordinates;
	}

	public void setPhyloCoordinates(Rectangle2D[] phyloCoordinates) {
		PhyloCoordinates = phyloCoordinates;
	}

	public String[] getPhyloNodeNames() {
		return PhyloNodeNames;
	}

	public void setPhyloNodeNames(String[] phyloNodeNames) {
		PhyloNodeNames = phyloNodeNames;
	}

	public String getCurrentlyViewedPanel() {
		return CurrentlyViewedPanel;
	}

	public void setCurrentlyViewedPanel(String currentlyViewedPanel) {
		CurrentlyViewedPanel = currentlyViewedPanel;
	}
	
}
