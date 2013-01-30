package genomeObjects;

import java.util.LinkedList;

public class MotifGroup {

	//Fields
	private LinkedList<SequenceMotif> MotifInstances;
	private String Name;
	private String Notes;
	private String fileName;
	
	//Constructor
 	public MotifGroup(){
		
	}
	
	//Import
	public void ImportElements(String fileName){
		
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	//Setters + Getters
	public LinkedList<SequenceMotif> getMotifInstances() {
		return MotifInstances;
	}

	public void setMotifInstances(LinkedList<SequenceMotif> motifInstances) {
		MotifInstances = motifInstances;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}


}