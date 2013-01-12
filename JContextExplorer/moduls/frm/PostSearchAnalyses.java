package moduls.frm;

public class PostSearchAnalyses {

	//Fields
	private boolean OptionDisplaySearches;
	private boolean OptionComputeDendrogram;
	private boolean OptionComputeContextGraph;
	private boolean OptionRenderPhylogeny;
	
	//Constructor
	public PostSearchAnalyses(boolean Searches, boolean Dendrogram, boolean ContextGraph, boolean Phylogeny){
		this.OptionDisplaySearches = Searches;
		this.OptionComputeDendrogram = Dendrogram;
		this.OptionComputeContextGraph = ContextGraph;
		this.OptionRenderPhylogeny = Phylogeny;
	}

	//setters + getters
	public boolean isOptionDisplaySearches() {
		return OptionDisplaySearches;
	}

	public void setOptionDisplaySearches(boolean optionDisplaySearches) {
		OptionDisplaySearches = optionDisplaySearches;
	}

	public boolean isOptionComputeDendrogram() {
		return OptionComputeDendrogram;
	}

	public void setOptionComputeDendrogram(boolean optionComputeDendrogram) {
		OptionComputeDendrogram = optionComputeDendrogram;
	}

	public boolean isOptionComputeContextGraph() {
		return OptionComputeContextGraph;
	}

	public void setOptionComputeContextGraph(boolean optionComputeContextGraph) {
		OptionComputeContextGraph = optionComputeContextGraph;
	}

	public boolean isOptionRenderPhylogeny() {
		return OptionRenderPhylogeny;
	}

	public void setOptionRenderPhylogeny(boolean optionRenderPhylogeny) {
		OptionRenderPhylogeny = optionRenderPhylogeny;
	}
	

}
