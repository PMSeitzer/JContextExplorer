package ContextForest;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.SwingWorker;

import definicions.MatriuDistancies;

import moduls.frm.QueryData;

public class QuerySet {
	
	// ==================================================//
	// ======= Fields ===================================//
	// ==================================================//
	
	//Logistics
	private String Name;								//Name of this Query Set
	
	//Processing
	
	//Context Trees (all data is in QueryData structure)
	private LinkedList<QueryData> ContextTrees;		//Component Trees
	
	//Context Forest-related data
	private ContextForestParameters CFParams;			//Parameters for Context Forest computation
	private MatriuDistancies ContextForest;				//Computed Context Forest
	
	//Comparison-related data
	private CompareTreeParameters CTParams;				//To another tree
	private ComparePhenotypeDataParameters	CPParams;	//To phenotype data
	
	// ==================================================//	
	// ======= Classes ==================================//
	// ==================================================//
	
	//Generate all Context Trees
	public class ContextTreeWorker extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			
			return null;
		}
		
		
		
		
		//done with all processes
		public void done(){
			
		}
	}

	//Assemble Context Trees into a Context Forest
	public class ContextForestBuilder extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {

			return null;
		}
		
	}
		
	//Constructor
	public QuerySet(){
		
	}

	// ==================================================//
	// ======= Getters and Setters ======================//
	// ==================================================//
	
	public LinkedList<QueryData> getContextTrees() {
		return ContextTrees;
	}

	public void setContextTrees(LinkedList<QueryData> contextTrees) {
		ContextTrees = contextTrees;
	}

	public ContextForestParameters getCFParams() {
		return CFParams;
	}

	public void setCFParams(ContextForestParameters cFParams) {
		CFParams = cFParams;
	}

	public MatriuDistancies getContextForest() {
		return ContextForest;
	}

	public void setContextForest(MatriuDistancies contextForest) {
		ContextForest = contextForest;
	}

	public CompareTreeParameters getCTParams() {
		return CTParams;
	}

	public void setCTParams(CompareTreeParameters cTParams) {
		CTParams = cTParams;
	}

	public ComparePhenotypeDataParameters getCPParams() {
		return CPParams;
	}

	public void setCPParams(ComparePhenotypeDataParameters cPParams) {
		CPParams = cPParams;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	
}
