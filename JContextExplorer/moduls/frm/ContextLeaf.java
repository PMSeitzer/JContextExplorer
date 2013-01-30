package moduls.frm;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;

public class ContextLeaf {

	//fields
	private String Name;
	private String SourceSpecies;
	private DefaultMutableTreeNode SearchResultsTreeNode;
	private Rectangle2D ContextTreeCoordinates;
	private LinkedList<Rectangle2D> ContextGraphCoordinates;
	private Rectangle2D PhyloTreeCoordinates;					//Source organism node (not actual context leaf)

	private boolean Selected;
	
	//constructor
	public ContextLeaf(){
		
	}

	//---------------- GETTERS AND SETTERS--------------------- //
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public boolean isSelected() {
		return Selected;
	}
	public void setSelected(boolean selected) {
		Selected = selected;
	}

	public LinkedList<Rectangle2D> getContextGraphCoordinates() {
		return ContextGraphCoordinates;
	}

	public void setContextGraphCoordinates(LinkedList<Rectangle2D> contextGraphCoordinates) {
		ContextGraphCoordinates = contextGraphCoordinates;
	}

	public Rectangle2D getPhyloTreeCoordinates() {
		return PhyloTreeCoordinates;
	}

	public void setPhyloTreeCoordinates(Rectangle2D phyloTreeCoordinates) {
		PhyloTreeCoordinates = phyloTreeCoordinates;
	}
	public Rectangle2D getContextTreeCoordinates() {
		return ContextTreeCoordinates;
	}
	public void setContextTreeCoordinates(Rectangle2D contextTreeCoordinates) {
		ContextTreeCoordinates = contextTreeCoordinates;
	}

	public DefaultMutableTreeNode getSearchResultsTreeNode() {
		return SearchResultsTreeNode;
	}

	public void setSearchResultsTreeNode(DefaultMutableTreeNode searchResultsTreeNode) {
		SearchResultsTreeNode = searchResultsTreeNode;
	}

	public String getSourceSpecies() {
		return SourceSpecies;
	}

	public void setSourceSpecies(String sourceSpecies) {
		SourceSpecies = sourceSpecies;
	}


}
