package moduls.frm;

import importExport.DadesExternes;
import moduls.frm.children.FrmSearchResults;
import definicions.MatriuDistancies;
import genomeObjects.CSDisplayData;

public class QueryData {

	//Fields
	private boolean AnnotationSearch;
	private String[] Queries = null;
	private String Name;
	private String ContextSetName;
	private String DissimilarityType;
	private String ClusteringType;
	private PostSearchAnalyses AnalysesList;
	private CSDisplayData CSD;
	private String OSName;
	
	//Output- type fields
	private int[] Clusters = null;
	private MatriuDistancies multiDendro;
	private FrmSearchResults SRF;
	private DadesExternes de;
	
	//Constructor
	public QueryData(){
		
	}

	//=== SETTERS AND GETTERS ==========//
	
	public String getOSName() {
		return OSName;
	}

	public void setOSName(String oSName) {
		OSName = oSName;
	}
	
	public boolean isAnnotationSearch() {
		return AnnotationSearch;
	}

	public void setAnnotationSearch(boolean annotationSearch) {
		AnnotationSearch = annotationSearch;
	}

	public String[] getQueries() {
		return Queries;
	}

	public void setQueries(String[] queries) {
		Queries = queries;
	}

	public int[] getClusters() {
		return Clusters;
	}

	public void setClusters(int[] clusters) {
		Clusters = clusters;
	}

	public String getContextSetName() {
		return ContextSetName;
	}

	public void setContextSetName(String contextSetName) {
		ContextSetName = contextSetName;
	}

	public String getDissimilarityType() {
		return DissimilarityType;
	}

	public void setDissimilarityType(String dissimilarityType) {
		DissimilarityType = dissimilarityType;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public PostSearchAnalyses getAnalysesList() {
		return AnalysesList;
	}

	public void setAnalysesList(PostSearchAnalyses analysesList) {
		AnalysesList = analysesList;
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

	public String getClusteringType() {
		return ClusteringType;
	}

	public void setClusteringType(String clusteringType) {
		ClusteringType = clusteringType;
	}

	public MatriuDistancies getMultiDendro() {
		return multiDendro;
	}

	public void setMultiDendro(MatriuDistancies multiDendro) {
		this.multiDendro = multiDendro;
	}

	public FrmSearchResults getSRF() {
		return SRF;
	}

	public void setSRF(FrmSearchResults sRF) {
		SRF = sRF;
	}

	public DadesExternes getDe() {
		return de;
	}

	public void setDe(DadesExternes de) {
		this.de = de;
	}
}
