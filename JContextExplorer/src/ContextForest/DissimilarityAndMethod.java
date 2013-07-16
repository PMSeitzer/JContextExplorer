package ContextForest;

import java.util.LinkedList;

public class DissimilarityAndMethod {

	//Fields
	private LinkedList<Double> Dissimilarity;
	private String MethodName;
	
	//Getters and Setters
	public LinkedList<Double> getDissimilarity() {
		return Dissimilarity;
	}
	public void setDissimilarity(LinkedList<Double> dissimilarity) {
		Dissimilarity = dissimilarity;
	}
	public String getMethodName() {
		return MethodName;
	}
	public void setMethodName(String methodName) {
		MethodName = methodName;
	}
}
