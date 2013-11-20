package contextViewer;

public class ContextViewerSettings {

	/*
	 * This class stores parameters critical for context display
	 */
	
	//Size of segments displayed
	public int RangeLimit;
	public int SplitLimit;
	public int DefaultRangeAround;
	
	//pop-up information options
	public boolean cbStart, cbStop, cbSize, cbType, cbClusterID, cbAnnotation;
	
	//genomic segment display options
	public boolean cbCoordinates;
	public boolean cbShowSurrounding;
	public boolean cbColorSurrounding;
	public boolean cbStrandNormalize;
	
	//Constructor
	public ContextViewerSettings(){
		RevertToDefaults();
	}
	
	//Revert to defaults
	public void RevertToDefaults(){
		
		//auto-segmentation
		RangeLimit = 50000;
		SplitLimit = 15000;
		
		//range around
		DefaultRangeAround = 2000;
		
		//genomic segment manipulation
		cbCoordinates = true;
		cbShowSurrounding = true;
		cbColorSurrounding = false;
		cbStrandNormalize = true;
		
		//pop-up information
		cbStart = false;
		cbStop = false;
		cbSize = true;
		cbType = false;
		cbClusterID = true;
		cbAnnotation = true;
	}

}
