package contextViewer;

import genomeObjects.SequenceMotif;

import java.awt.Paint;
import java.awt.geom.Ellipse2D;

public class DrawMotif extends DrawObject{
	
	//fields: before, after, and current
	private Ellipse2D Coordinates;		//coordinates of each motif
	private Ellipse2D StrRevCoordinates;//coordinates of gene, flipped around
	
	//graphical/display info
	private SequenceMotif BioInfo;		//Information delivered on click
		
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

	public SequenceMotif getBioInfo() {
		return BioInfo;
	}

	public void setBioInfo(SequenceMotif bioInfo) {
		BioInfo = bioInfo;
	}

}
