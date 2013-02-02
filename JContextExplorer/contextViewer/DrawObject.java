package contextViewer;

import java.awt.Paint;

public class DrawObject {

	//Fields
	private boolean StrRevChange; 		//true if Coordinates != StrRevCoordinates
	
	//graphical/display info
	private Paint Color;				//motif color

	private Integer Membership = -1;	//Before = -1, CS = 0, After = 1;
	private int StartCS;				//pixel x coordinate where CS range starts
	private int StopCS;					//pixel x coordinate where CS range stops
	
	//Constructor
	public DrawObject(){
		
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
