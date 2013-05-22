package moduls.frm.children;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutJCE extends JDialog implements ActionListener{

	//Fields
	private JTextArea Info;
	private JPanel jp;
	
	//constructor
	public AboutJCE(){
		
		this.getInfo();
		this.getFrame();
		
		this.pack();
		this.setVisible(true);
		
	}
	
	//Methods
	
	//Create frame
	public void getFrame(){
								//width, height
		this.setSize(new Dimension(300, 300));
		
		this.setTitle("About JContextExplorer");
		this.setLocationRelativeTo(null);
		
	}
	
	//Create info
	public void getInfo(){
		
		String strInfo =
				"JContextExplorer genome context interrogation tool.\n\n"+
				"Version: 2.0\n" +
				"Release Date: May 22, 2013\n\n"+
				"JContextExplorer is free software.\n" +
				"The source code is available at\n" +
				"https://github.com/PMSeitzer/JContextExplorer\n\n" +
				"Questions?\n" +
				"Contact: Phillip Seitzer (pmseitzer@ucdavis.edu)\n\n"+
				"This software was developed by the Facciotti Laboratory,\n"+
				"at UC Davis.\n" +
				"website: " +
				"http://www.bme.ucdavis.edu/facciotti/";
						
		
		Info = new JTextArea(strInfo);
		
		jp = new JPanel();
		jp.add(Info);
		this.add(jp);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

}
