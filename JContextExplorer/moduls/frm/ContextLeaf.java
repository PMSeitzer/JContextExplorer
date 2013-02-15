package moduls.frm;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
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

	private int ContextTreeNodeNameNumber;
	private int PhyloTreeNodeNameNumber = -1;
	private boolean Selected;
	
	//constructor
	public ContextLeaf(){
		
	}

	//--------- Comparators ------------------------------------//
	
	public static Comparator<ContextLeaf> getAlphabeticalComparator(){
		return new Comparator<ContextLeaf>(){

			@Override
			public int compare(ContextLeaf CL1, ContextLeaf CL2) {
				
				if (CL1.Name != null && CL2.Name != null){
					return CL1.Name.compareToIgnoreCase(CL2.Name);
				}
				return 0;
			}
			
		};
	}
	
	public static Comparator<ContextLeaf> getContextTreeOrderComparator(){
		return new Comparator<ContextLeaf>(){

			@Override
			public int compare(ContextLeaf CL1, ContextLeaf CL2) {
				
				return CL1.ContextTreeNodeNameNumber - CL2.ContextTreeNodeNameNumber;
			}
			
		};
	}
	
	public static Comparator<ContextLeaf> getPhylogeneticTreeOrderComparator(){
		return new Comparator<ContextLeaf>(){
			
			public int compare(ContextLeaf CL1, ContextLeaf CL2){
				
				return CL1.PhyloTreeNodeNameNumber - CL2.PhyloTreeNodeNameNumber;
			}
		};
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


	public int getContextTreeNodeNameNumber() {
		return ContextTreeNodeNameNumber;
	}

	public void setContextTreeNodeNameNumber(int contextTreeNodeNameNumber) {
		ContextTreeNodeNameNumber = contextTreeNodeNameNumber;
	}

	public int getPhyloTreeNodeNameNumber() {
		return PhyloTreeNodeNameNumber;
	}

	public void setPhyloTreeNodeNameNumber(int phyloTreeNodeNameNumber) {
		PhyloTreeNodeNameNumber = phyloTreeNodeNameNumber;
	}


}
