package moduls.frm;

import java.io.Serializable;
import java.util.LinkedList;

import importExport.DadesExternes;
import moduls.frm.children.FrmSearchResults;
import definicions.Cluster;
import definicions.MatriuDistancies;
import genomeObjects.CSDisplayData;

public class QueryData implements Serializable{

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
	private transient FrmSearchResults SRF;
	private DadesExternes de;
	private Cluster OutputCluster = null;
	
	//OR-statement fields
	public LinkedList<String> ANDStatements = new LinkedList<String>();
	public LinkedList<LinkedList> ParsedStatements;
	public LinkedList<LinkedList<Integer>> ParsedANDStatementsCluster;
	public LinkedList<LinkedList<String>> ParsedANDStatementsAnnotation;
	public boolean ANDStatementsParsed = false;
	
	//Constructor
	public QueryData(){
		
	}

	// === Method ====//
	public void BuildANDStatements(String Type){
		
		//Initialize
		ParsedStatements = new LinkedList<LinkedList>();
		
		//transform each unparsed AND statement into a list of associated items
		for (String s : ANDStatements){
				
			//split the string by components
			String[] Comps = s.split("\\$\\$");
				
			LinkedList<Integer> ANDClusters = new LinkedList<Integer>();
			LinkedList<String> ANDAnnotations = new LinkedList<String>();
				
			//parse statements into linked lists.
			for (String s1 : Comps){
				if (Type.equals("cluster")){
					ANDClusters.add(Integer.parseInt(s1.trim()));
				} else {
					ANDAnnotations.add(s1.trim().toUpperCase());
				}
			}
			
			//store the list in parsed form.
			if (Type.equals("cluster")){
				ParsedStatements.add(ANDClusters);
			} else {
				ParsedStatements.add(ANDAnnotations);
			}
		}
		
		//note that these statements have been parsed
		ANDStatementsParsed = true;
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

	public Cluster getOutputCluster() {
		return OutputCluster;
	}

	public void setOutputCluster(Cluster outputCluster) {
		OutputCluster = outputCluster;
	}
}
