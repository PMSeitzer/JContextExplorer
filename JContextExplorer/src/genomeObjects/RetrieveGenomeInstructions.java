package genomeObjects;

import java.io.Serializable;

public class RetrieveGenomeInstructions implements Serializable {

	//Fields
	private String Source;	
	private String SourcePath;
	
	//TODO: motif / functional data instructions	
	//private LinkedHashMap<String, RetrieveMotifInstructions>
	
	//constructor
	public RetrieveGenomeInstructions(){
		
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getSourcePath() {
		return SourcePath;
	}

	public void setSourcePath(String sourcePath) {
		SourcePath = sourcePath;
	}
}
