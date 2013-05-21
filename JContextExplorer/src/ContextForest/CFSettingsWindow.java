package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import moduls.frm.FrmPrincipalDesk;

public class CFSettingsWindow extends JFrame implements ActionListener {

	//fields
	private FrmPrincipalDesk f;
	
	//Constructor
	public CFSettingsWindow(FrmPrincipalDesk f){
		this.f = f;
		this.getFrame();
		
		//adjust appearance prior to display
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// ===== GUI- related ======= //
	
	//Frame
	public void getFrame(){
		
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Construct a Context Forest");
		this.setResizable(true);

	}

	// ===== Action-listener ===== //
	
	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
