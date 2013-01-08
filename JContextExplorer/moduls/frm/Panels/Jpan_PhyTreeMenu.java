package moduls.frm.Panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class Jpan_PhyTreeMenu extends JPanel implements ActionListener {

	//fields
	private FrmPrincipalDesk f;
	
	//constructor
	public Jpan_PhyTreeMenu(FrmPrincipalDesk fr){
		this.f = fr;
		
		//build components
		this.getPanel();
	}
	
	//build components
	public void getPanel(){
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Correlate to Phylogeny")); // title
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		//c.weighty = 1;
		c.insets = new Insets(1,1,1,1);

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

}
