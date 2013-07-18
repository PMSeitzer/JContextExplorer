package ContextForest;

import java.util.LinkedList;

public class DissimilarityMatrixData {

	//Fields
	private LinkedList<Double> Dissimilarities;
	private LinkedList<String> FormattedDissimilarities;
	private LinkedList<String> MatrixFormattedDissimilarities;
	private String MethodName;
	private int NumLeaves;
	
	//constructor
	public DissimilarityMatrixData(){
		FormattedDissimilarities = new LinkedList<String>();
	}
	
	//Getters and Setters
	public LinkedList<Double> getDissimilarities() {
		return Dissimilarities;
	}
	public void setDissimilarities(LinkedList<Double> dissimilarity) {
		Dissimilarities = dissimilarity;
	}
	public String getMethodName() {
		return MethodName;
	}
	public void setMethodName(String methodName) {
		MethodName = methodName;
	}
	public LinkedList<String> getFormattedDissimilarities() {
		return FormattedDissimilarities;
	}
	public void setFormattedDissimilarities(LinkedList<String> formattedDissimilarities) {
		FormattedDissimilarities = formattedDissimilarities;
	}

	public int getNumLeaves() {
		return NumLeaves;
	}

	public void setNumLeaves(int numLeaves) {
		NumLeaves = numLeaves;
	}

	public LinkedList<String> getMatrixFormattedDissimilarities() {
		return MatrixFormattedDissimilarities;
	}

	public void setMatrixFormattedDissimilarities(
			LinkedList<String> matrixFormattedDissimilarities) {
		MatrixFormattedDissimilarities = matrixFormattedDissimilarities;
	}
}
