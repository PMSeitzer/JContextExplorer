package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import moduls.frm.FrmPrincipalDesk;

public class TreeSimilarityScanWindow extends JFrame implements ActionListener {

	//Fields
	private FrmPrincipalDesk f;
	private QuerySet QS;
	
	//Constructor
	public TreeSimilarityScanWindow(FrmPrincipalDesk f, QuerySet QS){
		
		//Initializations
		this.f = f;
		this.QS = QS;
		
		//Methods
		getFrame();
		
		//final step - show visibility
		this.setVisible(true);
	}
	
	
	// ===== GUI- related ======= //
	
	//Frame
	public void getFrame(){
		
		this.setSize(700,550);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Tree Similarity Scan Results");
		this.setResizable(true);
		this.setLocationRelativeTo(null);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}


