package genomeObjects;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class OSCreationInstructions {

	//Fields
	private LinkedList<String> AllNames;
	private LinkedHashMap<String, Boolean> GenomesLoaded;
	private LinkedHashMap<String, Boolean> GroupChunks; 
	private LinkedHashMap<String, Boolean> MotifsLoaded;
	private LinkedHashMap<String, RetrieveGenomeInstructions> GenomeSeeds;
	//private LinkedHashMap<>
	//object reuse!!!
	
	//Basic info about the OS
	private String Name;
	private String Notes;
	
	//Constructor
	public OSCreationInstructions(){
		GenomeSeeds = new LinkedHashMap<String, RetrieveGenomeInstructions>();
	}
	
	// ------- Methods ---------------------------------------------//
	
	//Expand a genome
	public AnnotatedGenome GrowGenome(RetrieveGenomeInstructions Instructions){
		
		//Initialize return type
		AnnotatedGenome AG = new AnnotatedGenome();
		
		if (Instructions.getSource().equals("GFFFile")){
			AG.importFromGFFFile(Instructions.getSourcePath());
		}
		
		//return genome
		return AG;
	}
	
	public OrganismSet GrowOS(OSCreationInstructions Instructions){
		
		//Initialize return type
		OrganismSet OS = new OrganismSet();
		
		//return organism set
		return OS;
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
