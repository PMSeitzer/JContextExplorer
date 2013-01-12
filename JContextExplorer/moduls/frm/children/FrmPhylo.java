package moduls.frm.children;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class FrmPhylo extends JPanel{

	//fields
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	
	//constructor
	public FrmPhylo(FrmPrincipalDesk f, CSDisplayData CSD){
		this.fr = f;
		this.CSD = CSD;
	}
}
