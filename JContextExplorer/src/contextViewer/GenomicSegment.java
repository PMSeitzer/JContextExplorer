package contextViewer;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.sound.sampled.Line;

public class GenomicSegment {

	//fields
	private LinkedList<DrawGene> dg; 		//individual gene information for display
	private LinkedList<DrawMotif> dm;		//individual motif information for display
	private String Label;					//Node name
	private Rectangle2D boundingRect;		//Size information of whole segment
	
	//nucleotide range information
	private int StartBeforeBuffer;
	private int StartCS;
	private int CenterofCS;
	private int StartAfterBuffer;
	private int EndRange;
	
	//in the event of a strand-display reversal, should all genes be flipped??
	private boolean StrRevFlipGenes = false;
	
	//drawing bars
	private LinkedList<Integer> BarPositions;		//list of coordinates as to where to draw bars
	private LinkedList<Integer> BarPositionsRev;	//for strand-reversed case
	private LinkedList<Integer> BarValues;			//genomic coordinates associated with bars
	private LinkedList<Integer> ContigBoundaries;	//Coordinates designating start/end of a contig
	private LinkedList<Integer> ContigBoundariesRev;//Coordinates designating start/end of a contig, reverse
	
	//Getters + Setters
	public LinkedList<DrawGene> getDg() {
		return dg;
	}
	public void setDg(LinkedList<DrawGene> dg) {
		this.dg = dg;
	}
	public String getLabel() {
		return Label;
	}
	public void setLabel(String label) {
		Label = label;
	}
	public Rectangle2D getBoundingRect() {
		return boundingRect;
	}
	public void setBoundingRect(Rectangle2D boundingRect) {
		this.boundingRect = boundingRect;
	}
	public int getStartBeforeBuffer() {
		return StartBeforeBuffer;
	}
	public void setStartBeforeBuffer(int startBeforeBuffer) {
		StartBeforeBuffer = startBeforeBuffer;
	}
	public int getStartCS() {
		return StartCS;
	}
	public void setStartCS(int startCS) {
		StartCS = startCS;
	}
	public int getStartAfterBuffer() {
		return StartAfterBuffer;
	}
	public void setStartAfterBuffer(int startAfterBuffer) {
		StartAfterBuffer = startAfterBuffer;
	}
	public int getEndRange() {
		return EndRange;
	}
	public void setEndRange(int endRange) {
		EndRange = endRange;
	}
	public int getCenterofCS() {
		return CenterofCS;
	}
	public void setCenterofCS(int centerofCS) {
		CenterofCS = centerofCS;
	}
	public boolean isStrRevFlipGenes() {
		return StrRevFlipGenes;
	}
	public void setStrRevFlipGenes(boolean strRevFlipGenes) {
		StrRevFlipGenes = strRevFlipGenes;
	}
	public LinkedList<Integer> getBarPositions() {
		return BarPositions;
	}
	public void setBarPositions(LinkedList<Integer> barPositions) {
		BarPositions = barPositions;
	}
	public LinkedList<Integer> getBarValues() {
		return BarValues;
	}
	public void setBarValues(LinkedList<Integer> barValues) {
		BarValues = barValues;
	}
	public LinkedList<Integer> getBarPositionsRev() {
		return BarPositionsRev;
	}
	public void setBarPositionsRev(LinkedList<Integer> barPositionsRev) {
		BarPositionsRev = barPositionsRev;
	}
	public LinkedList<DrawMotif> getDm() {
		return dm;
	}
	public void setDm(LinkedList<DrawMotif> dm) {
		this.dm = dm;
	}
	public LinkedList<Integer> getContigBoundaries() {
		return ContigBoundaries;
	}
	public void setContigBoundaries(LinkedList<Integer> contigBoundaries) {
		ContigBoundaries = contigBoundaries;
	}
	public LinkedList<Integer> getContigBoundariesRev() {
		return ContigBoundariesRev;
	}
	public void setContigBoundariesRev(LinkedList<Integer> contigBoundariesRev) {
		ContigBoundariesRev = contigBoundariesRev;
	}
	
}
