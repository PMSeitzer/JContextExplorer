package GenomicSetHandling;

import java.util.LinkedHashMap;

public class GSInfo {

	//Fields
	private String GSName;
	private String GSNotes;
	private LinkedHashMap<String, String> GSGenomeDescriptions;
	
	//Constructor
	public GSInfo(){
		
	}

	public String getGSName() {
		return GSName;
	}

	public void setGSName(String gSName) {
		GSName = gSName;
	}

	public String getGSNotes() {
		return GSNotes;
	}

	public void setGSNotes(String gSNotes) {
		GSNotes = gSNotes;
	}

	public LinkedHashMap<String, String> getGSGenomeDescriptions() {
		return GSGenomeDescriptions;
	}

	public void setGSGenomeDescriptions(
			LinkedHashMap<String, String> gSGenomeDescriptions) {
		GSGenomeDescriptions = gSGenomeDescriptions;
	}
}
