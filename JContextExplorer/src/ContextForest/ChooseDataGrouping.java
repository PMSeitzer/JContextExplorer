package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import moduls.frm.FrmPrincipalDesk;

public class ChooseDataGrouping extends JDialog implements ActionListener, PropertyChangeListener{

	//Fields
	private FrmPrincipalDesk f;
	
	//constructor
	public ChooseDataGrouping(FrmPrincipalDesk f){
		this.f = f;
		
		this.getPanel();
		this.getFrame();
		
		//Last step: make window visible
		this.setVisible(true);
	}

	public void getPanel(){
		
	}
	
	public void getFrame(){
		this.setSize(600,350);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Select Data Grouping and Analysis Parameters");
		this.setResizable(true);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	// Getters and Setters
	
	public FrmPrincipalDesk getF() {
		return f;
	}

	public void setF(FrmPrincipalDesk f) {
		this.f = f;
	}

}
