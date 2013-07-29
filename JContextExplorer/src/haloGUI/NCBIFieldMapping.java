package haloGUI;

import java.util.LinkedList;

public class NCBIFieldMapping {

	//Fields
	public boolean ScreenResults = true;
	public LinkedList<String> Filters;
	public int RetMax = 200; 
	
	//Constructor
	public NCBIFieldMapping(){
		Filters = new LinkedList<String>();
		Filters.add("complete genome");
	}
}
