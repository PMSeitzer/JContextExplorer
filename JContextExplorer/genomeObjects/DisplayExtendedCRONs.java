package genomeObjects;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class DisplayExtendedCRONs extends JFrame {
	
	//add fields
	public ExtendedCRON ECRON;
	
	//constructor
	public DisplayExtendedCRONs(){
		
		//set size
		setTitle("Extended CRON viewer");
		
	    // screen size options
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(2*(width/(3)), 2*(height/(3)));
		
		//visibility, exit on close, location options
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		
	}

	//draw rectangles for operons
//	public void addECRONS(){
//		
//		if (this.ECRON != null){
//			for (int i = 0; i < this.ECRON.Operons.keySet().size(); i++){
//
//			}
//		}
//		
//	}
//	
	//create a single operon
	public void paintOperon(Graphics g){
		

	}

	//setters and getters
	public ExtendedCRON getECRON() {
		return ECRON;
	}
	public void setECRON(ExtendedCRON eCRON) {
		ECRON = eCRON;
	}
}
