package contextViewer;

import genomeObjects.GenomicElement;
import java.awt.Rectangle;


public class DrawGene extends DrawObject{

	//fields: before, after, and current
	private Rectangle Coordinates;		//coordinates of each gene
	private Rectangle StrRevCoordinates;//coordinates of gene, flipped around

	private GenomicElement BioInfo;		//Information delivered on click
	
	//Constructor
	public DrawGene(){
		super();
	}

	//Getters + Setters
 	public Rectangle getCoordinates() {
		return Coordinates;
	}
	public void setCoordinates(Rectangle coordinates) {
		Coordinates = coordinates;
	}
	public Rectangle getStrRevCoordinates() {
		return StrRevCoordinates;
	}
	public void setStrRevCoordinates(Rectangle strRevCoordinates) {
		StrRevCoordinates = strRevCoordinates;
	}

	public GenomicElement getBioInfo() {
		return BioInfo;
	}

	public void setBioInfo(GenomicElement bioInfo) {
		BioInfo = bioInfo;
	}

}
