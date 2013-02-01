package contextViewer;

import genomeObjects.SequenceMotif;

import java.awt.Paint;
import java.awt.geom.Ellipse2D;

public class DrawMotif {
	
	//fields: before, after, and current
	private Ellipse2D Coordinates;		//coordinates of each motif
	private Ellipse2D StrRevCoordinates;//coordinates of gene, flipped around
	private boolean StrRevChange; 		//true if Coordinates != StrRevCoordinates
	
	//graphical/display info
	private Paint Color;				//motif color
	private SequenceMotif BioInfo;		//Information delivered on click
	private Integer Membership = -1;	//Before = -1, CS = 0, After = 1;
	private int StartCS;				//pixel x coordinate where CS range starts
	private int StopCS;					//pixel x coordinate where CS range stops
		
	//Constructor
	public DrawMotif(){
		super();
	}

	public Ellipse2D getCoordinates() {
		return Coordinates;
	}

	public void setCoordinates(Ellipse2D coordinates) {
		Coordinates = coordinates;
	}

	public Ellipse2D getStrRevCoordinates() {
		return StrRevCoordinates;
	}

	public void setStrRevCoordinates(Ellipse2D strRevCoordinates) {
		StrRevCoordinates = strRevCoordinates;
	}

	public boolean isStrRevChange() {
		return StrRevChange;
	}

	public void setStrRevChange(boolean strRevChange) {
		StrRevChange = strRevChange;
	}

	public Paint getColor() {
		return Color;
	}

	public void setColor(Paint color) {
		Color = color;
	}

	public SequenceMotif getBioInfo() {
		return BioInfo;
	}

	public void setBioInfo(SequenceMotif bioInfo) {
		BioInfo = bioInfo;
	}

	public Integer getMembership() {
		return Membership;
	}

	public void setMembership(Integer membership) {
		Membership = membership;
	}

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
}
