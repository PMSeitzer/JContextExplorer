package genomeObjects;

import java.io.Serializable;
import java.util.LinkedList;

public class MotifGroupDescription implements Serializable {

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
