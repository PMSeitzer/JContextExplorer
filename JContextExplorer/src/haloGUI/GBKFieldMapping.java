package haloGUI;

import java.io.Serializable;

public class GBKFieldMapping implements Serializable {

	//this object instructs the parser about what to look for.
	
	//Fields
	public String Annotation = "/product=";
	public boolean GetTranslation = false;
	public String GeneID = "/locus_tag=";
	public boolean GetCluster = false;
	public String GetClusterTag = "/note=";
	
}
