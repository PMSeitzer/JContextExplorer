package genomeObjects;

import java.util.LinkedList;

public class MotifGroupDescription {

	//Fields
	private String Name;
	private LinkedList<String> Species;
	private String Source;
	
	//Constructor
	public MotifGroupDescription(){
		
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public LinkedList<String> getSpecies() {
		return Species;
	}

	public void setSpecies(LinkedList<String> species) {
		Species = species;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}
}
