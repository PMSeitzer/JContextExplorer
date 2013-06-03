package ContextForest;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class ManageQuerySets extends JDialog implements ActionListener{

	//FIELDS
	
	//master
	private FrmPrincipalDesk f;
	
	//gui
	private JPanel jp;
	
	//CONSTRUCTOR
	public ManageQuerySets(FrmPrincipalDesk f){
		this.f = f;
		
		//create components
		this.getPanel();
		this.getFrame();
		
		
		this.setVisible(true);
	}

	// ======= GUI Methods ====== //
	
	//panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(2,2,2,2);
		
		this.add(jp);
		
	}
	
	//frame
	public void getFrame(){
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Manage Query Sets");
		this.setResizable(true);
	}
	
	// ======= Listeners ====== //

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
