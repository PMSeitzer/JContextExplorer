package moduls.frm;

import genomeObjects.CSDisplayData;

public class QueryData {

	//Fields
	private boolean AnnotationSearch;
	private String[] Queries = null;
	private int[] Clusters = null;
	private String Name;
	private String ContextSetName;
	private String DissimilarityType;
	private String ClusteringType;
	private PostSearchAnalyses AnalysesList;
	private CSDisplayData CSD;
	private String OSName;
	
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
}
