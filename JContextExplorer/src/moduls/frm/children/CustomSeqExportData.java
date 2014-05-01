package moduls.frm.children;

public class CustomSeqExportData {

	//coordinate ranges
	public int start_Before;	//how much to start before a site
	public int stop_Before;		//how much to stop before a site
	
	//which to use?
	public boolean start_Start;	//start relative to the start site
	public boolean stop_Stop;	//stop relative to the stop site
	
	//constructor
	public CustomSeqExportData(){

	}
	
	//display data
	public void Display(){
		
		String str = "Starting " + String.valueOf(start_Before);
		
		if (start_Start){
			str = str + " before start site, ending ";
		} else{
			str = str + " before stop site, ending ";
		}
		
		if (stop_Stop){
			str = str + String.valueOf(stop_Before) + " before stop site.";
		} else{
			str = str + String.valueOf(stop_Before) + " before start site.";
		}
		
		System.out.println(str);
	}
	
}
