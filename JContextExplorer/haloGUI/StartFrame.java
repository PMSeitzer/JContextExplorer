package haloGUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class StartFrame extends JFrame implements ActionListener{
	
	private final LoadGenomesPanelv2 lgp;
	
	//constructor
	public StartFrame(final String title){
		
		//INITALIZATIONS
		super(title);
		
		//ADD START PANEL
		lgp = new LoadGenomesPanelv2(this);
		
		//PANEL ADDING
		this.add(lgp, BorderLayout.NORTH);
		
		//SET PROPERTIES OF DESKTOP FRAME
		JFrame.setDefaultLookAndFeelDecorated(false);
		this.setResizable(true);
		this.setSize(450,210);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		//this.pack();


	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(lgp.getBtnSubmit())){
			System.out.println("keystrike");
		}
	}

}
