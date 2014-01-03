package genomeObjects;

//import importExport.DadesExternes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import operonClustering.*;

public class ExtendedCRON implements Serializable{

	//fields
	private String Name;
	private String ContextSetName;
	private String SearchType;
	private String ContextType;
	private LinkedHashMap<String, String> SourceSpeciesNames;
	private LinkedHashMap<String, HashSet<String>> SourceContigNames;
	private LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> Contexts; //<species-num, list of genomic elements
	private LinkedList<Double> Distances; 
	private int NumberOfEntries = 0;
	private LinkedList<String> Dissimilarities;
	private LinkedList<String> DissimilaritiesAsMatrix;
	private String DissimilarityType;
	private String[] Queries;
	private int[] ClusterNumbers;
	private LinkedList<CustomDissimilarity> CustomDissimilarities;
	
	// this linked list relates elements returned by Keys() in this way:
	//(0,1), (0,2) , ... , (0,n), (1,2), (1,3), ... (1,n), (2,3), (2,4), ... (n-1,n)	
	
	//public DadesExternes de; 
	/*
	 * This data type comes from the MultiDendrograms software package
	 * TODO: negotiate DadesExterna, ReadTXT, JPan_btn, and FrmPrincipalDesk to figure out
	 * how to appropriately modify those files and this one for direct ECRON loading
	 */
	
	//constructor
	public ExtendedCRON() {
		super();
	}
	
	// ----------------------- Hierarchical Clustering ------------------//

	//compute pairwise distances, using an external distance method
	public void computePairwiseDistances(String DissimilarityMethod){
		
		//retrieve distances method
		//uberDistancesAnnotation distMethod = new uberDistancesAnnotation();
		boolean UseCustomMethod = false;
		OperonDissimilarityMeasure distMethod = null;
		
		//select appropriate operon dissimilarity measure
		if (DissimilarityMethod.equals("Common Genes - Dice")){
		
			if (SearchType.equals("annotation")){
				distMethod = new DicebyAnnotation();
			} else if (SearchType.equals("cluster")){
				distMethod = new DicebyClusterID();
			}
			
		} else if (DissimilarityMethod.equals("Common Genes - Jaccard")){
			
			if (SearchType.equals("annotation")){
				distMethod = new JaccardbyAnnotation();
			} else if (SearchType.equals("cluster")){
				distMethod = new JaccardbyClusterID();
			}
			
		} else if (DissimilarityMethod.equals("Total Length")){
				
			distMethod = new TotalSize();
				
		} else if (DissimilarityMethod.equals("Moving Distances")){
			
			if (SearchType.equals("annotation")){
				distMethod = new MovingDistancesbyAnnotation();
			} else if (SearchType.equals("cluster")){
				distMethod = new MovingDistancesbyClusterID();
			}

		} else {
			UseCustomMethod = true;
		}
		

		//do not use a custom method
		if (!UseCustomMethod){
			
			//initialize output list
			LinkedList<Double> D = new LinkedList<Double>();
			
			//retrieve key set
			Object[] Keys = this.Contexts.keySet().toArray();
					
			//iterate over keys
			for (int i = 0; i < Keys.length; i++){
				
				//debugging variable
				String str = "";
				
				for (int j = i+1; j < Keys.length; j++){
					
					double dist = distMethod.computeDissimilarity(this.Contexts.get(Keys[i]),this.Contexts.get(Keys[j]));
					
					//print statements - also reveals the order of keys (debugging)
					//System.out.println("Distance between " + Keys[i] + " and " + Keys[j] + ": " + "(" + i + "," + j + "): "+ dist);
					str = str + String.valueOf(dist) + " ";
					
					//add value to linked list
					D.add(dist);
				}
				
				//debugging - view matrix
				//System.out.println(str);
			}

			//set list to ECRON structure.
			this.setDistances(D);
			
		} else { //Uses a custom method.
			
			//initialize output list
			LinkedList<Double> D = new LinkedList<Double>();
			
			//retrieve key set
			Object[] Keys = this.Contexts.keySet().toArray();
			
			//retrieve dissimilarity measure
			CustomDissimilarity CustomDistMethod = null;
			for (CustomDissimilarity CD : this.CustomDissimilarities){
				if (CD.getName().equals(DissimilarityMethod)){
					CustomDistMethod = CD;
					break;
				}
			}

			//iterate over keys
			for (int i = 0; i < Keys.length; i++){
				
				String str = "";
				for (int j = i+1; j < Keys.length; j++){
					
					double dist = CustomDistMethod.TotalDissimilarity(this.Contexts.get(Keys[i]), this.Contexts.get(Keys[j]), SearchType);

					//print statements - also reveals the order of keys
//					if (Keys[i].equals("Halococcus_saccharolyticus-1")){
//						System.out.println("Distance between " + Keys[i] + " and " + Keys[j] + ": " + "(" + i + "," + j + "): " + dist);
//					}
					str = str + String.valueOf(dist) + " ";
					
					//add value to linked list
					D.add(dist);
				}
				
				//debugging - view matrix
				//System.out.println(str);
			}

			//set list to ECRON structure.
			this.setDistances(D);
			
		}

	}
	
	//Export dissimilarity set into a format MultiDendrogram readable format
	public void exportDistances(File dissimilarityMatrix){
		
		//create file if it doesn't exist
	    if(!dissimilarityMatrix.exists()){
	    	try {
				dissimilarityMatrix.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    //import file writer, and write info to file.
		try {
			//import file writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(dissimilarityMatrix.getPath()));
			
			//retrieve key set
			Object[] Keys = this.Contexts.keySet().toArray();
			
			//initialize counter
			int Counter = -1;
			
			//iterate through to export the values to a text file.
			for (int i = 0; i < Keys.length; i++){
				for (int j = i+1; j < Keys.length; j++){
					
					//increment counter
					Counter++;
					
					//formatting - updated 7/18/2012
//					String First = (String) Keys[i];
//					String Second = (String) Keys[j];
//					
//					DecimalFormat df = new DecimalFormat("#.##");
//					String Distance = df.format(this.Distances.get(Counter)).toString();
//
//					String Row = First + ";" + Second + ";" + Distance;

					//Create each row
					String Row = (String) Keys[i] + ";" + (String) Keys[j] + ";" + this.Distances.get(Counter).toString();
					
					//write row to file writer, followed by new line
					bw.write(Row);
					bw.newLine();
					bw.flush();
					
				}
			}

			//close output stream
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Export dissimilarity set into an array of strings
	public void exportDistancesToField(){

		//initialize relationships field
		LinkedList<String> PairwiseRelationships = new LinkedList<String>();
		
		//initialize counter
		int Counter = -1;
		
		//retrieve key set
		Object[] Keys = this.Contexts.keySet().toArray();
			
		//iterate through to export the values to a text file.
		for (int i = 0; i < Keys.length; i++){
			for (int j = i+1; j < Keys.length; j++){
					
				//increment counter
				Counter++;
					
				//Create each row
				String Row = (String) Keys[i] + ";" + (String) Keys[j] + ";" + this.Distances.get(Counter).toString();
					
				//add this row to the linked list.
				PairwiseRelationships.add(Row);
			}
		}

		//set field
		setDissimilarities(PairwiseRelationships);
		
		//also make the matrix
		exportDissimilaritiesAsMatrix();
	}
	
	//Export a dissimilarity set into a matrix
	public void exportDissimilaritiesAsMatrix(){
		
		//initialize matrix
		DissimilaritiesAsMatrix = new LinkedList<String>();
		
		//initialize counter
		int Counter = -1;	//counter
		Object[] Keys = this.Contexts.keySet().toArray(); //retrieve keys
		int L = Keys.length; //width of array
		//System.out.println("L = " + L + "\n");
		
		//create object array
		Object[][] Matrix = new Object[L][L+1];
			
		//build matrix
		for (int i = 0; i < Keys.length; i++){
			
			Matrix[i][0] = Keys[i];		//row name
			Matrix[i][i+1] = 0.0;		//anything vs itself has diss of 0
			
			for (int j = i+1; j < Keys.length; j++){
					
				//increment counter
				Counter++;
					
				//print statement
				//System.out.println("(i,j) = (" + i +"," + j + "): " + this.Distances.get(Counter));
				
				//write values
				Matrix[i][j+1] =  this.Distances.get(Counter); //serves as D(i,j);
				Matrix[j][i+1] =  this.Distances.get(Counter);
			}
		}
		
		//blank line
		//System.out.println();
		
		//write rows to matrix
		for (int i = 0; i < Matrix.length; i++){
			String str = "";
			for (int j = 0; j < Matrix[i].length; j++){
				str = str + String.valueOf(Matrix[i][j]) + "\t";
			}
			//System.out.println(str);
			DissimilaritiesAsMatrix.add(str);
		}
		
	}

	
	// ---- Cosmetic ----- //
	
	public void displayDistancesToscreen(){
		for(int i = 0; i<Dissimilarities.size(); i++){
			System.out.println(Dissimilarities.get(i));
		}
	}
	
	
	//----------------------- Getters and Setters -----------------------//

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public void setContexts(LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> operons) {
		Contexts = operons;
	}
	public HashMap<String, LinkedList<GenomicElementAndQueryMatch>> getContexts() {
		return Contexts;
	}
	public LinkedList<Double> getDistances() {
		return Distances;
	}
	public void setDistances(LinkedList<Double> distances) {
		Distances = distances;
	}
	public String getContextSetName() {
		return ContextSetName;
	}

	public void setContextSetName(String contextSetName) {
		ContextSetName = contextSetName;
	}

	public int getNumberOfEntries() {
		return NumberOfEntries;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		NumberOfEntries = numberOfEntries;
	}

	
	public LinkedList<String> getDissimilarities() {
		return Dissimilarities;
	}

	public void setDissimilarities(LinkedList<String> dissimilarities) {
		Dissimilarities = dissimilarities;
	}

	public String getSearchType() {
		return SearchType;
	}

	public void setSearchType(String type) {
		SearchType = type;
	}

	public LinkedHashMap<String, String> getSourceSpeciesNames() {
		return SourceSpeciesNames;
	}

	public void setSourceSpeciesNames(
			LinkedHashMap<String, String> sourceSpeciesNames) {
		SourceSpeciesNames = sourceSpeciesNames;
	}

	public LinkedHashMap<String, HashSet<String>> getSourceContigNames() {
		return SourceContigNames;
	}

	public void setSourceContigNames(LinkedHashMap<String, HashSet<String>> sourceContigNames) {
		SourceContigNames = sourceContigNames;
	}

	public String getDissimilarityType() {
		return DissimilarityType;
	}

	public void setDissimilarityType(String dissimilarityType) {
		DissimilarityType = dissimilarityType;
	}

	public String getContextType() {
		return ContextType;
	}

	public void setContextType(String contextType) {
		ContextType = contextType;
	}

	public String[] getQueries() {
		return Queries;
	}

	public void setQueries(String[] queries) {
		Queries = queries;
	}

	public int[] getClusterNumbers() {
		return ClusterNumbers;
	}

	public void setClusterNumbers(int[] clusterNumbers) {
		ClusterNumbers = clusterNumbers;
	}

	public LinkedList<CustomDissimilarity> getCustomDissimilarities() {
		return CustomDissimilarities;
	}

	public void setCustomDissimilarities(LinkedList<CustomDissimilarity> customDissimilarities) {
		CustomDissimilarities = customDissimilarities;
	}

	public LinkedList<String> getDissimilaritiesAsMatrix() {
		return DissimilaritiesAsMatrix;
	}

	public void setDissimilaritiesAsMatrix(LinkedList<String> dissimilaritiesAsMatrix) {
		DissimilaritiesAsMatrix = dissimilaritiesAsMatrix;
	}

}
