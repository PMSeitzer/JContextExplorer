package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import moduls.frm.FrmPrincipalDesk;

public class ContextForestWindow extends JFrame implements ActionListener{

	//Fields
	private FrmPrincipalDesk f;
	
	//Constructor
	public ContextForestWindow(FrmPrincipalDesk f){
		
		//Initializations
		this.f = f;
		
		//Methods
		getFrame();
		
		//final step - show visibility
		this.setVisible(true);
	}
	
	// ===== GUI- related ======= //
	
	//Frame
	public void getFrame(){
		
		this.setSize(600,500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Context Forest Window");
		this.setResizable(true);
		this.setLocationRelativeTo(null);

	}
	
	//Actions
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}



	public FrmPrincipalDesk getF() {
		return f;
	}



	public void setF(FrmPrincipalDesk f) {
		this.f = f;
	}
	
	//Setters and Getters

}
