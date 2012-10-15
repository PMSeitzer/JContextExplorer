package contextViewer;

import java.awt.Color;
import java.awt.Paint;

public class SharedHomology {

	//Fields
	//Source-related
	private String ECRONType;				//"annotation" or "cluster"
	private int ClusterID;					//cluster ID number
	private String Annotation;				//Annotation
	private boolean GeneGroupingMember;		//used to build the tree;
	
	//color-related
	private Integer Frequency;				//Number of matches across organisms compared
	private Color TheColor;					//associated color
	private Color[] BaseColors = new Color[9]; //effective color-mapping scheme
	
	//constructor
	public SharedHomology(){
		
		//base color scheme - for gene coloring
		this.BaseColors[0] = Color.GREEN;
		this.BaseColors[1] = Color.BLUE;
		this.BaseColors[2] = Color.RED;
		this.BaseColors[3] = Color.CYAN;
		this.BaseColors[4] = Color.MAGENTA;
		this.BaseColors[5] = Color.ORANGE;
		this.BaseColors[6] = Color.YELLOW;
		this.BaseColors[7] = Color.WHITE;
		this.BaseColors[8] = Color.PINK;
				
	}
	
	//Getters + Setters;
	public String getAnnotation() {
		return Annotation;
	}
	public void setAnnotation(String annotation) {
		Annotation = annotation;
	}
	public Integer getFrequency() {
		return Frequency;
	}
	public void setFrequency(Integer frequency) {
		Frequency = frequency;
	}
	public Color getColor() {
		return TheColor;
	}
	public void setColor(Color color) {
		TheColor = color;
	}
	public void addColor(int EntryNumber) {
		
		//determine base color
		Color TheColor = BaseColors[(EntryNumber % BaseColors.length)];

		//determine brighter/darker based on actual value
		int ColorFactor = EntryNumber/BaseColors.length;
		
//		//optional print statements
//		System.out.println("EntryNumber: " + EntryNumber);
//		System.out.println("Code: " + (EntryNumber % BaseColors.length) + " Color: " + TheColor);
//		System.out.println("Color Factor: " + ColorFactor);
		
		if (ColorFactor != 0){
			if (ColorFactor%2 == 0){
				//make brighter
				while (ColorFactor > 0){
					TheColor = TheColor.brighter();
					ColorFactor = ColorFactor - 2;
				}
			} else {
				//make darker
				while (ColorFactor > 0){
					TheColor = TheColor.darker();
					ColorFactor = ColorFactor - 2;
				}
			}
		}
		
		//finally, set color
		this.setColor(TheColor);
		
	}

	public String getECRONType() {
		return ECRONType;
	}

	public void setECRONType(String eCRONType) {
		ECRONType = eCRONType;
	}

	public int getClusterID() {
		return ClusterID;
	}

	public void setClusterID(int clusterID) {
		ClusterID = clusterID;
	}

	public boolean isGeneGroupingMember() {
		return GeneGroupingMember;
	}

	public void setGeneGroupingMember(boolean geneGroupingMember) {
		GeneGroupingMember = geneGroupingMember;
	}

}