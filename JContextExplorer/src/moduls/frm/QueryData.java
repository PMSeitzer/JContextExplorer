package moduls.frm;

import java.io.Serializable;
import java.util.LinkedHashMap;
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
	
	//AND-statement fields
	public LinkedList<String> ANDStatements = new LinkedList<String>();
	public LinkedList<LinkedList<Integer>> ParsedANDStatementsCluster;
	public LinkedList<LinkedList<String>> ParsedANDStatementsAnnotation;
	public boolean ANDStatementsParsed = false;
	public boolean ifAndOnlyif = false;
	
	public LinkedList<Integer> ClustersAsList;
	public LinkedList<String> QueriesAsList;
	
	//update AND statments parsing
	public LinkedHashMap<Integer,Integer> ANDClustersHash = new LinkedHashMap<Integer,Integer>();
	public LinkedHashMap<String,Integer> ANDAnnotationsHash = new LinkedHashMap<String,Integer>();
	
	//Constructor
	public QueryData(){
		
	}

	// === Method ====//
	public void BuildANDStatements(String Type){

		//transform each unparsed AND statement into a list of associated items
		for (String s : ANDStatements){
				
			//split the string by components
			String[] Comps = s.split("\\$\\$");
				
			//parse statements into linked lists.
			for (String s1 : Comps){
				if (Type.equals("cluster")){
					
					//check hash + initialize count
					int Key = Integer.parseInt(s1.trim());
					int Count = 1;
					
					//determine appropriate count + increment counter
					if (ANDClustersHash.get(Key) != null){
						Count = ANDClustersHash.get(Key);
						Count++;
					}
					ANDClustersHash.put(Key,Count);
					
				} else {

					//check hash + initialize count
					String Key = s1.trim().toUpperCase();
					int Count = 1;
					
					//determine appropriate count + increment counter
					if (ANDAnnotationsHash.get(Key) != null){
						Count = ANDAnnotationsHash.get(Key);
						Count++;
					}
					ANDAnnotationsHash.put(Key,Count);
				}
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

	public void setQueriesAndList(String[] queries) {
		Queries = queries;
		
		//build list
		if (queries != null){
			QueriesAsList = new LinkedList<String>();
			for (String s : Queries){
				QueriesAsList.add(s);
			}
		}

	}

	public int[] getClusters() {
		return Clusters;
	}

	public void setClustersAndList(int[] clusters) {
		Clusters = clusters;
		
		//build list
		if (clusters != null){
			ClustersAsList = new LinkedList<Integer>();
			for (Integer x : Clusters){
				ClustersAsList.add(x);
			}
		}

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
