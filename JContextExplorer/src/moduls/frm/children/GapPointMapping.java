package moduls.frm.children;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class GapPointMapping implements Serializable{

	//Fields
	public LinkedHashMap<Integer, Double> Mapping = new LinkedHashMap<Integer, Double>();
	public int MaxGapLimit;
	public int MinGaplimit;
	public double MaxDissimilarity;
	
}
