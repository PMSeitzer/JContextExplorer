package contextViewer;

import genomeObjects.GenomicElement;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

public class DrawGene {

	//fields: before, after, and current
	private Rectangle Coordinates;		//coordinates of each gene
	private Rectangle StrRevCoordinates;//coordinates of gene, flipped around
	private boolean StrRevChange; 		//true if Coordinates != StrRevCoordinates
	
	//graphical/display info
	private Paint Color;				//gene color
	private GenomicElement BioInfo;		//Information delivered on click
	private Integer Membership;			//Before = -1, CS = 0, After = 1;
	private int StartCS;				//pixel x coordinate where CS range starts
	private int StopCS;					//pixel x coordinate where CS range stops
		
	//Constructor
	public DrawGene(){
		super();
	}

	//Getters + Setters
	public int getStartCS() {
		return StartCS;
	}

	public void setStartCS(int startCS) {
		StartCS = startCS;
	}

	public int getStopCS() {
		return StopCS;
	}

	public void setStopCS(int stopCS) {
		StopCS = stopCS;
	}
	
 	public Rectangle getCoordinates() {
		return Coordinates;
	}
	public void setCoordinates(Rectangle coordinates) {
		Coordinates = coordinates;
	}
	public Paint getColor() {
		return Color;
	}
	public void setColor(Paint color) {
		Color = color;
	}
	public GenomicElement getBioInfo() {
		return BioInfo;
	}
	public void setBioInfo(GenomicElement e) {
		BioInfo = e;
	}
	public Integer getMembership() {
		return Membership;
	}
	public void setMembership(Integer membership) {
		Membership = membership;
	}
	public Rectangle getStrRevCoordinates() {
		return StrRevCoordinates;
	}
	public void setStrRevCoordinates(Rectangle strRevCoordinates) {
		StrRevCoordinates = strRevCoordinates;
	}

	public boolean isStrRevChange() {
		return StrRevChange;
	}

	public void setStrRevChange(boolean strRevChange) {
		StrRevChange = strRevChange;
	}
}
